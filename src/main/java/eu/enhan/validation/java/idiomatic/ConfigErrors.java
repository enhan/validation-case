package eu.enhan.validation.java.idiomatic;

/**
 *
 */
public class ConfigErrors {

    private ConfigErrors() {

    }

    interface ConfigError {
    }

    static class CouldNotParse implements ConfigError {

    }

    static class ParameterIsMissing implements ConfigError {
        public final String parameterName;

        public ParameterIsMissing(String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public String toString() {
            return "Parameter '" + parameterName + "' is missing";
        }
    }

    static class NoBootstrapServers implements ConfigError {

        @Override
        public String toString() {
            return "Bootstrap servers must be a non empty list of valid hosts and port";
        }
    }

    static class InvalidHost implements ConfigError{
        public final String incorrectValue;
        public final int positionInArray;

        public InvalidHost(String incorrectValue, int positionInArray) {
            this.incorrectValue = incorrectValue;
            this.positionInArray = positionInArray;
        }

        @Override
        public String toString() {
            return "'" + incorrectValue + "'is not a valid host in 'bootstrapServers' at index " + positionInArray;
        }
    }

    static class ThresholdATooLow implements ConfigError {
        public final int incorrectValue;
        public final int minAllowedValue;

        public ThresholdATooLow(int incorrectValue, int minAllowedValue) {
            this.incorrectValue = incorrectValue;
            this.minAllowedValue = minAllowedValue;
        }

        @Override
        public String toString() {
            return "thresholdA : " + incorrectValue + " is not above " + minAllowedValue;
        }
    }

    static class ThresholdCTooHigh implements ConfigError {
        public final int incorrectValue;
        public final int maxAllowedValue;

        public ThresholdCTooHigh(int incorrectValue, int maxAllowedValue) {
            this.incorrectValue = incorrectValue;
            this.maxAllowedValue = maxAllowedValue;
        }

        @Override
        public String toString() {
            return "thresholdC: " + incorrectValue + "is not under " + maxAllowedValue;
        }
    }

    static class ThresholdBNotInBetween implements ConfigError {
        public final int incorrectValue;
        public final int suppliedA;
        public final int suppliedC;

        public ThresholdBNotInBetween(int incorrectValue, int suppliedA, int suppliedC) {
            this.incorrectValue = incorrectValue;
            this.suppliedA = suppliedA;
            this.suppliedC = suppliedC;
        }

        @Override
        public String toString() {
            return "thresholdB must be between thresholdA and thresholdC: " + incorrectValue + "is not between " + suppliedA + " and " + suppliedC;
        }
    }


}
