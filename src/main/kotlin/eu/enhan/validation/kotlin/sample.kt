package eu.enhan.validation.kotlin

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrHandle
import arrow.core.monad
import arrow.data.ListK
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedNel
import arrow.data.applicative
import arrow.data.fix
import arrow.data.invalidNel
import arrow.data.k
import arrow.data.semigroup
import arrow.data.validNel
import arrow.typeclasses.binding
import com.google.common.net.HostAndPort
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

/**
 *
 */

fun main(args: Array<String>) {

    val log = LoggerFactory.getLogger("KotlinArrowSample")

    val config = ConfigFactory.load()

    val businessValidation = validateBusinessConfig(config)
    val kafkaValidation = validateKafkaConfig(config)

    val finalValidation: ValidatedNel<ConfigError, ApplicationConfig> = Validated.applicative<Nel<ConfigError>>(Nel.semigroup())
            .map(kafkaValidation, businessValidation, { ApplicationConfig(it.a, it.b) }).fix()

    val toLog = finalValidation.fold({errors ->
        "Invalid : " + errors.show()
    }, { conf ->
        "Valid: $conf"
    })

    log.info(toLog)

}

fun <A> get(path: String, extractor: (String) -> A): Either<ConfigError, A> = try {
    Either.right(extractor(path))
} catch (e: ConfigException.Missing) {
    Either.left(ConfigError.ParameterIsMissing(path))
} catch (e: ConfigException.WrongType) {
    Either.left(ConfigError.CouldNotParse)
}

fun validateBusinessConfig(config: Config): ValidatedNel<ConfigError, BusinessConfig> = run {
    val unvalidatedTAE = get("app.thresholdA", { p -> config.getInt(p)})
    val tAe = unvalidatedTAE.flatMap { unvalidatedTa ->
        if (unvalidatedTa < 0 )
            Either.left(ConfigError.ThresholdATooLow(unvalidatedTa, 0))
        else
            Either.right(unvalidatedTa)
    }

    val unvalidatedTCE = get("app.thresholdC", { p -> config.getInt(p)})
    val tCe = unvalidatedTCE.flatMap { unvalidatedTc ->
        if (unvalidatedTc > 10000)
            Either.left(ConfigError.ThresholdCTooHigh(unvalidatedTc, 10000))
        else
            Either.right(unvalidatedTc)
    }

    val unvalidatedTBE = get("app.thresholdB", {p -> config.getInt(p)})


    val tBe = Either.monad<ConfigError>().binding{
        val ta = unvalidatedTAE.bind()
        val tb = unvalidatedTBE.bind()
        val tc = unvalidatedTCE.bind()
        if (ta < tb && tb < tc)
            Either.right(tb)
        else
            Either.left(ConfigError.ThresholdBNotInBetween(tb, ta, tc))

    }.flatMap { it }


    val tAV: ValidatedNel<ConfigError, Int> = Validated.fromEither(tAe).toValidatedNel()
    val tBV: ValidatedNel<ConfigError, Int> = Validated.fromEither(tBe).toValidatedNel()
    val tCV: ValidatedNel<ConfigError, Int> = Validated.fromEither(tCe).toValidatedNel()

    ValidatedNel.applicative(Nel.semigroup<ConfigError>()).map(tAV, tBV, tCV, {
        val a = it.a
        val b = it.b
        val c = it.c
        BusinessConfig(a, b, c)
    }).fix()
}

fun validateKafkaConfig(config: Config): ValidatedNel<ConfigError, KafkaConfig> = run {

    val applicationIdV = Validated.fromEither(get("kafka.applicationId", { config.getString(it) })).toValidatedNel()

    val serversE = get("kafka.bootstrapServers", {config.getStringList(it)})

    val serversV: ValidatedNel<ConfigError, ListK<HostAndPort>> = serversE.map { rawList ->
        if (rawList.isEmpty()){
            ConfigError.NoBootstrapServers.invalidNel<ConfigError, ListK<HostAndPort>>()
        } else {
            val hostsV = rawList.withIndex().map { validateHost(it.value, it.index) }
            hostsV.k().traverse(Validated.applicative<Nel<ConfigError>>(Nel.semigroup()), { it }).fix()
        }
    }.getOrHandle { it.invalidNel() }

    // Combine
    ValidatedNel.applicative<Nel<ConfigError>>(Nel.semigroup()).map(applicationIdV, serversV, { KafkaConfig(it.a, it.b.list) }).fix()
}

fun validateHost(rawString: String, index: Int): ValidatedNel<ConfigError, HostAndPort> = try {
    HostAndPort.fromString(rawString).withDefaultPort(9092).validNel<ConfigError, HostAndPort>()
} catch (e: IllegalArgumentException) {
    ConfigError.InvalidHost(rawString, index).invalidNel<ConfigError, HostAndPort>()
}