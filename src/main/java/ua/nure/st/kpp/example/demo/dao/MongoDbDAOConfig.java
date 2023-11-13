package ua.nure.st.kpp.example.demo.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Stanislav Hlova
 */

@ConfigurationProperties(prefix = "database.mongodb")
public class MongoDbDAOConfig {
    private String connectionString;
    private String name;

    public MongoDbDAOConfig() {
    }

    public MongoDbDAOConfig(String connectionString, String name) {
        this.connectionString = connectionString;
        this.name = name;
    }

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
}
