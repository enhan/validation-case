package eu.enhan.validation.scala

import com.google.common.net.HostAndPort
import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import cats._
import cats.data._
import cats.implicits._
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._

case class BusinessConfig(thresholdA: Int, thresholdB: Int, thresholdC: Int)

case class KafkaConfig(applicationId: String, bootstrapServers: List[HostAndPort])

case class ApplicationConfig(kafkaConfig: KafkaConfig, businessConfig: BusinessConfig)

object ScalaSampleWithEither extends App {

  type ValidationRes[A] = ValidatedNel[ConfigError, A]


  val log = LoggerFactory.getLogger("ScalaCatsSample")


  def get[A](path: String, extractor: String => A): Either[ConfigError, A] = try {
    Right(extractor(path))
  } catch {
    case e:ConfigException.Missing => Left(ParameterIsMissing(path))
    case e:ConfigException.WrongType => Left(CouldNotParse)
  }

  val config = ConfigFactory.load()


  def validateBusiness(config: Config): ValidatedNel[ConfigError, BusinessConfig] = {
    val unvalidatedTAE = get("app.thresholdA", p => config.getInt(p))
    val tAe = unvalidatedTAE.flatMap{ notValidated =>
    if (notValidated < 0)
    Left(ThresholdATooLow(notValidated, 0))
    else
    Right(notValidated)
  }

    val unvalidatedTCE = get("app.thresholdC", p => config.getInt(p))
    val tCe = unvalidatedTCE.flatMap{ notValidated =>
    if (notValidated > 10000)
    Left(ThresholdCTooHigh(notValidated, 10000))
    else
    Right(notValidated)
  }


    val unvalidatedTBE = get("app.thresholdB", p => config.getInt(p))

    val tBe: Either[ConfigError, Int] = (for {
    a <- unvalidatedTAE
    b <- unvalidatedTBE
    c <- unvalidatedTCE
  } yield {
    if (a < b && b < c)
    Right[ConfigError, Int](b)
    else
    Left[ConfigError, Int](ThresholdBNotInBetween(b, a, c))
  }).flatMap(identity)

    val tAV: ValidatedNel[ConfigError, Int] = tAe.toValidatedNel
    val tBV: ValidatedNel[ConfigError, Int] = tBe.toValidatedNel
    val tCV: ValidatedNel[ConfigError, Int] = tCe.toValidatedNel

    //IntelliJ does not like this syntax :/
    //  val businessConfValidationResult: ValidatedNel[ConfigError, BusinessConfig] = (tAV, tBV, tCV).mapN(BusinessConfig)
    Applicative[ValidationRes].map3(tAV, tBV, tCV)(BusinessConfig)
  }

  def validateHost(rawString: String, index: Int): ValidatedNel[ConfigError, HostAndPort] = try {
    HostAndPort.fromString(rawString).withDefaultPort(9092).validNel
  } catch {
    case e: IllegalArgumentException => InvalidHost(rawString, index).invalidNel
  }

  def validateKafkaConfig(config: Config): ValidatedNel[ConfigError, KafkaConfig] = {
    val applicationIdV = get("kafka.applicationId", p => config.getString(p)).toValidatedNel
    val serversE: Either[ConfigError, List[String]] = get("kafka.bootstrapServers", p => config.getStringList(p).asScala.toList)

    val serverV = serversE.map { rawList =>
      if (rawList.isEmpty){
        NoBootstrapServers.invalidNel
      } else {
        rawList.zipWithIndex.traverse[ValidationRes, HostAndPort]{ t =>
          validateHost(t._1, t._2)
        }(Applicative[ValidationRes])
      }
    }.fold(err => err.invalidNel, identity)

    Applicative[ValidationRes].map2(applicationIdV, serverV)(KafkaConfig)

  }

  val finalValidation = Applicative[ValidationRes].map2(validateKafkaConfig(config), validateBusiness(config))(ApplicationConfig)

  val toLog = finalValidation.fold({ errors =>
    "Invalid: " + errors
  }, {conf =>
    "Valid: " + conf
  })

  log.info(toLog)

}
