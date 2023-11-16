package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

/**
 * @author Stanislav Hlova
 */

public class MongoTimeoutProperties {
    private long waitReconnectDuration;
    private int numberOfReconnect;

    public MongoTimeoutProperties(long waitReconnectDuration, int numberOfReconnect) {
        this.waitReconnectDuration = waitReconnectDuration;
        this.numberOfReconnect = numberOfReconnect;
    }

    public long getWaitReconnectDuration() {
        return waitReconnectDuration;
    }

    public int getNumberOfReconnect() {
        return numberOfReconnect;
    }
}
