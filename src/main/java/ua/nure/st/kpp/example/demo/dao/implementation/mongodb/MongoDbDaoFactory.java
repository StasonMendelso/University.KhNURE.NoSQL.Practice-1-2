package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import ua.nure.st.kpp.example.demo.dao.*;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class MongoDbDaoFactory implements Factory {
    private final MongoDbItemDAO mongoDbItemDAO;
    private final MongoDbCompanyDAO mongoDbCompanyDAO;
    private final MongoDbIncomeJournalDAO mongoDbIncomeJournalDAO;
    private final MongoDbOutcomeJournalDAO mongoDbOutcomeJournalDAO;

    public MongoDbDaoFactory(MongoDbDAOConfig mongoDbDAOConfig) {
        MongoClient mongoClient;
        if (mongoDbDAOConfig.isReplicaSet()) {
            List<ServerAddress> serverAddresses = mongoDbDAOConfig.getServerAddress().stream()
                    .map(serverAddressProperties -> new ServerAddress(serverAddressProperties.getHost(), serverAddressProperties.getPort()))
                    .toList();
            mongoClient = new MongoClient(serverAddresses);
        } else {
            mongoClient = new MongoClient(new MongoClientURI(mongoDbDAOConfig.getConnectionString()));
        }

        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDAOConfig.getName());

        this.mongoDbItemDAO = new MongoDbItemDAO(mongoDatabase);
        this.mongoDbCompanyDAO = new MongoDbCompanyDAO(mongoDatabase);
        this.mongoDbIncomeJournalDAO = new MongoDbIncomeJournalDAO(mongoClient, mongoDatabase);
        this.mongoDbOutcomeJournalDAO = new MongoDbOutcomeJournalDAO(mongoClient, mongoDatabase);

        //При завершенні програми закриваємо mongoClient з'єднання
        //Примітка: дана реалізація може спрацювати не завжди в силу реалізації виконання програми
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing MongoClient: " + mongoClient);
            mongoClient.close();}));
    }

    @Override
    public ItemDAO createItemDAO() {
        return mongoDbItemDAO;
    }

    @Override
    public CompanyDAO createCompanyDAO() {
        return mongoDbCompanyDAO;
    }

    @Override
    public IncomeJournalDAO createIncomeJournalDAO() {
        return mongoDbIncomeJournalDAO;
    }

    @Override
    public OutcomeJournalDAO createOutcomeJournalDAO() {
        return mongoDbOutcomeJournalDAO;
    }
}
