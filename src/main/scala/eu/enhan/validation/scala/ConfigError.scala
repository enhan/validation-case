package eu.enhan.validation.scala

abstract sealed class ConfigError
object CouldNotParse extends ConfigError
case class ParameterIsMissing(parameterName: String) extends ConfigError
object NoBootstrapServers extends ConfigError
case class InvalidHost(incorrectValue: String, positionInArray: Int) extends ConfigError
case class ThresholdATooLow(incorrectValue: Int, minAllowedValue: Int) extends ConfigError
case class ThresholdCTooHigh(incorrectValue: Int, maxAllowedValue: Int) extends ConfigError
case class ThresholdBNotInBetween(incorrectValue: Int, suppliedA: Int, suppliedC: Int) extends ConfigError

