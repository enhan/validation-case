package eu.enhan.validation.java.idiomatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

/**
 *
 */
public class Idiomatic2Sample {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("IdiomaticSample");

        Config config = ConfigFactory.load();

        List<ConfigErrors.ConfigError> errors = new ArrayList<>();

        Optional<List<String>> rawBootstrapServersOpt = get("kafka.bootstrapServers", errors, () -> config.getStringList("kafka.bootstrapServers"));


        Optional<String> applicationId = get("kafka.applicationId", errors, () -> config.getString("kafka.applicationId"));


        Optional<Integer> tA = get("app.thresholdA", errors, () -> config.getInt("app.thresholdA"));

        Optional<Integer> tB = get("app.thresholdB", errors, () -> config.getInt("app.thresholdB"));

        Optional<Integer> tC = get("app.thresholdC", errors, () -> config.getInt("app.thresholdC"));;

        List<HostAndPort> bootstrapServers = new ArrayList<>();

        rawBootstrapServersOpt.ifPresent(rawBootstrapServers -> {
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
        });


        // tA
        tA.ifPresent(p -> {
            if (p < 0 ){
                errors.add(new ConfigErrors.ThresholdATooLow(p, 0));
            }
        });

        tC.ifPresent(c -> {
            if (c > 10000){
                errors.add(new ConfigErrors.ThresholdCTooHigh(c, 10000));
            }
        });

        tA.ifPresent(a -> {
            tC.ifPresent(c -> {
                tB.ifPresent(b -> {
                    if (b > c || b < a){
                        errors.add(new ConfigErrors.ThresholdBNotInBetween(b, a, c));
                    }
                });
            });
        });



        if (errors.isEmpty()) {
            log.info("All good");
        } else {
            for (ConfigErrors.ConfigError error : errors) {
                log.error(error.toString());
            }
        }

    }

public static <A> Optional<A> get(String path, List<ConfigErrors.ConfigError> errors, Supplier<A> extractor) {
    try {
        return Optional.ofNullable(extractor.get());
    } catch (ConfigException.Missing | ConfigException.WrongType ex) {
        errors.add(new ConfigErrors.ParameterIsMissing(path));
        return Optional.empty();
    }
}



}
