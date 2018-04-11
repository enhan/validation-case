package eu.enhan.validation.java.jsr303;

/**
 *
 */
public class WholeConfig {

    private BusinessConfig businessConfig;
    private KafkaConfig kafkaConfig;

    public WholeConfig(BusinessConfig businessConfig, KafkaConfig kafkaConfig) {
        this.businessConfig = businessConfig;
        this.kafkaConfig = kafkaConfig;
    }

    public BusinessConfig getBusinessConfig() {
        return businessConfig;
    }

    public void setBusinessConfig(BusinessConfig businessConfig) {
        this.businessConfig = businessConfig;
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }

    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }
}
