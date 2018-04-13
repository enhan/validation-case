package eu.enhan.validation.kotlin

import arrow.data.Nel
import com.google.common.net.HostAndPort

/**
 *
 */

data class KafkaConfig(val applicationId: String, val bootstrapServers: List<HostAndPort>)

data class BusinessConfig(val thresholdA: Int, val thresholdB: Int, val thresholdC: Int)

data class ApplicationConfig(val kafka: KafkaConfig, val businessConfig: BusinessConfig)

sealed class ConfigError {

    object CouldNotParse: ConfigError()

    data class ParameterIsMissing(val parameterName: String): ConfigError()

    object NoBootstrapServers: ConfigError()

    data class InvalidHost(val incorrectValue: String, val positionInArray: Int) : ConfigError()

    data class ThresholdATooLow(val incorrectValue: Int, val minAllowedValue: Int): ConfigError()

    data class ThresholdCTooHigh(val incorrectValue: Int, val maxAllowedValue: Int): ConfigError()

    data class ThresholdBNotInBetween(val incorrectValue: Int, val suppliedA: Int, val suppliedC: Int): ConfigError()

}