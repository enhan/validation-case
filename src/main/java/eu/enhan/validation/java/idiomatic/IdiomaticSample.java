package eu.enhan.validation.java.idiomatic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

/**
 *
 */
public class IdiomaticSample {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("IdiomaticSample");

        Config config = ConfigFactory.load();

        List<ConfigErrors.ConfigError> errors = new ArrayList<>();

        List<String> rawBootstrapServers;
        try {
            rawBootstrapServers = config.getStringList("kafka.bootstrapServers");
        } catch (ConfigException.Missing | ConfigException.WrongType ex) {
            errors.add(new ConfigErrors.ParameterIsMissing("kafka.bootstrapServers"));
            rawBootstrapServers = null;
        }

        String applicationId;
        try {
            applicationId = config.getString("kafka.applicationId");
        } catch (ConfigException.Missing | ConfigException.WrongType ex) {
                errors.add(new ConfigErrors.ParameterIsMissing("kafka.applicationId"));
                applicationId = null;
        }


        Integer tA;
        try {
            tA =config.getInt("app.thresholdA");
        } catch (ConfigException.Missing | ConfigException.WrongType ex) {
            errors.add(new ConfigErrors.ParameterIsMissing("app.thresholdA"));
            tA = null;
        }

        Integer tB;
        try {
            tB = config.getInt("app.thresholdB");
        } catch (ConfigException.Missing | ConfigException.WrongType ex) {
            errors.add(new ConfigErrors.ParameterIsMissing("app.thresholdB"));
            tB = null;
        }
        Integer tC;
        try{
            tC = config.getInt("app.thresholdC");
        }catch (ConfigException.Missing | ConfigException.WrongType ex) {
            errors.add(new ConfigErrors.ParameterIsMissing("app.thresholdC"));
            tC = null;
        }


        List<HostAndPort> bootstrapServers = new ArrayList<>();

        if (rawBootstrapServers.isEmpty()) {
            errors.add(new ConfigErrors.NoBootstrapServers());
        } else {
            String rawBootstrapServer;
            for (int i = 0; i < rawBootstrapServers.size(); i++) {
                rawBootstrapServer = rawBootstrapServers.get(i);
                try {
                    bootstrapServers.add(HostAndPort.fromString(rawBootstrapServer).withDefaultPort(9092));
                } catch (IllegalArgumentException e) {
                    errors.add(new ConfigErrors.InvalidHost(rawBootstrapServer, i));
                }
            }
        }

        // tA
        if (tA != null && tA < 0) {
            errors.add(new ConfigErrors.ThresholdATooLow(tA, 0));
        }

        if (tC != null && tC > 10000) {
            errors.add(new ConfigErrors.ThresholdCTooHigh(tC, 10000));
        }

        if (tA!= null && tB != null && tC != null && (tB > tC || tB < tA)) {
            errors.add(new ConfigErrors.ThresholdBNotInBetween(tB, tA, tC));
        }

        if (errors.isEmpty()) {
            log.info("All good");
        } else {
            for (ConfigErrors.ConfigError error : errors) {
                log.error(error.toString());
            }
        }

    }



}
