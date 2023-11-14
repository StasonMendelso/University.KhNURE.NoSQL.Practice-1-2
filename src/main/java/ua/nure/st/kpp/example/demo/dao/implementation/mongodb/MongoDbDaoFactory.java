package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ua.nure.st.kpp.example.demo.dao.*;

/**
 * @author Stanislav Hlova
 */
public class MongoDbDaoFactory implements Factory {
    private final MongoDbItemDAO mongoDbItemDAO;
    private final MongoDbCompanyDAO mongoDbCompanyDAO;
    private final MongoDbIncomeJournalDAO mongoDbIncomeJournalDAO;
    private final MongoDbOutcomeJournalDAO mongoDbOutcomeJournalDAO;

    public MongoDbDaoFactory(MongoDbDAOConfig mongoDbDAOConfig) {
        ConnectionString connectionString = new ConnectionString(mongoDbDAOConfig.getConnectionString());
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDAOConfig.getName());

        this.mongoDbItemDAO = new MongoDbItemDAO(mongoDatabase);
        this.mongoDbCompanyDAO = new MongoDbCompanyDAO(mongoDatabase);
        this.mongoDbIncomeJournalDAO = new MongoDbIncomeJournalDAO(mongoClient, mongoDatabase);
        this.mongoDbOutcomeJournalDAO = new MongoDbOutcomeJournalDAO(mongoClient, mongoDatabase);

        //При завершенні програми закриваємо mongoClient з'єднання
        //Примітка: дана реалізація може спрацювати не завжди в силу реалізації виконання програми
        Runtime.getRuntime().addShutdownHook(new Thread(mongoClient::close));
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
