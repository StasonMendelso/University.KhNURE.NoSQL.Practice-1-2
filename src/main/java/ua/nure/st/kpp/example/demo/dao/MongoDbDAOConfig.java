package ua.nure.st.kpp.example.demo.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Stanislav Hlova
 */

@ConfigurationProperties(prefix = "database.mongodb")
public class MongoDbDAOConfig {
    private String connectionString;
    private String name;
    private boolean replicaSet;
    private long waitReconnectDuration;
    private int numberOfReconnect;

    private List<ServerAddressProperties> serverAddress;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReplicaSet() {
        return replicaSet;
    }

    public void setReplicaSet(boolean replicaSet) {
        this.replicaSet = replicaSet;
    }

    public List<ServerAddressProperties> getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(List<ServerAddressProperties> serverAddress) {
        this.serverAddress = serverAddress;
    }

    public long getWaitReconnectDuration() {
        return waitReconnectDuration;
    }

    public void setWaitReconnectDuration(long waitReconnectDuration) {
        this.waitReconnectDuration = waitReconnectDuration;
    }

    public int getNumberOfReconnect() {
        return numberOfReconnect;
    }

    public void setNumberOfReconnect(int numberOfReconnect) {
        this.numberOfReconnect = numberOfReconnect;
    }

    public static class ServerAddressProperties{
        private String host;
        private int port;

        public ServerAddressProperties() {
        }

        public ServerAddressProperties(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
