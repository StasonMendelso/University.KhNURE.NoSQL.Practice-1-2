package ua.nure.st.kpp.example.demo.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "database.mysql")
public class MySqlDAOConfig {
    private String url;
    private String user;
    private String password;

    public MySqlDAOConfig() {
    }


    public MySqlDAOConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "MySqlDAOConfig{" +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
