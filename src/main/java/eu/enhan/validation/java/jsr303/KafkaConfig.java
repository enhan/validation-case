package eu.enhan.validation.java.jsr303;

import java.util.List;

import com.google.common.net.HostAndPort;

/**
 *
 */
public class KafkaConfig {

    public List<HostAndPort> bootstrapServers;
    public String applicationId;

    public KafkaConfig(List<HostAndPort> bootstrapServers, String applicationId) {
        this.bootstrapServers = bootstrapServers;
        this.applicationId = applicationId;
    }

    public List<HostAndPort> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<HostAndPort> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
