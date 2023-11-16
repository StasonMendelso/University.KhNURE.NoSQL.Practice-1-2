package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.client.ClientSession;

/**
 * @author Stanislav Hlova
 */
public interface MongoDbDAO {
    default void sleep(long waitReconnectDuration) {
        try {
            Thread.sleep(waitReconnectDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    default void abortTransactionAndCloseSession(ClientSession clientSession) {
        clientSession.abortTransaction();
        clientSession.close();
    }
}
