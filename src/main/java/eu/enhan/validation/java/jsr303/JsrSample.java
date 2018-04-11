package eu.enhan.validation.java.jsr303;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 */
public class JsrSample {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger("JsrSample");

        log.info("Starting sample for JSR 303");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();


        // Extracting all the config
        Config config = ConfigFactory.load();
        List<String> rawBootstrapServers = config.getStringList("kafka.bootstrapServers");
        String applicationId = config.getString("kafka.applicationId");

        int tA = config.getInt("app.thresholdA");
        int tB = config.getInt("app.thresholdB");
        int tC = config.getInt("app.thresholdC");

        // We need to do the HostAndPort validation by hand.
        List<HostAndPort> bootstrapServers = new ArrayList<>();
        List<String> invalidValidHosts = new ArrayList<>();

        for (String rawBootstrapServer : rawBootstrapServers) {
            try {
                bootstrapServers.add(HostAndPort.fromString(rawBootstrapServer).withDefaultPort(9092));
            } catch (IllegalArgumentException e) {
                invalidValidHosts.add(rawBootstrapServer);
            }
        }

        KafkaConfig kafkaConfig = new KafkaConfig(bootstrapServers, applicationId);
        BusinessConfig businessConfig = new BusinessConfig(tA, tB, tC);

        WholeConfig wholeConfig = new WholeConfig(businessConfig, kafkaConfig);

        Set<ConstraintViolation<BusinessConfig>> constraintViolations = validator.validate(businessConfig);

        // Displaying errors.
        for (ConstraintViolation<BusinessConfig> violation : constraintViolations) {
            log.info("Constraint violation {}", violation);
        }

        if (constraintViolations.isEmpty()) {
            log.info("All good");
        }

    }

}
