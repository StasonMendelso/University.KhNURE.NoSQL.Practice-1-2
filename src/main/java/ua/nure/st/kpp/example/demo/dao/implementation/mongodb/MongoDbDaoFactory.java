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
    private final MongoDatabase mongoDatabase;

    public MongoDbDaoFactory(MongoDbDAOConfig mongoDbDAOConfig) {
        ConnectionString connectionString = new ConnectionString(mongoDbDAOConfig.getConnectionString());
        MongoClient mongoClient = MongoClients.create(connectionString);
        this.mongoDatabase = mongoClient.getDatabase(mongoDbDAOConfig.getName());

        //При завершенні програми закриваємо mongoClient з'єднання
        //Примітка: дана реалізація може спрацювати не завжди в силу реалізації виконання програми
        Runtime.getRuntime().addShutdownHook(new Thread(mongoClient::close));
    }

    @Override
    public ItemDAO createItemDAO() {
        return new MongoDbItemDAO();
    }

    @Override
    public CompanyDAO createCompanyDAO() {
        return new MongoDbCompanyDAO(mongoDatabase);
    }

    @Override
    public IncomeJournalDAO createIncomeJournalDAO() {
        return new MongoDbIncomeJournalDAO();
    }

    @Override
    public OutcomeJournalDAO createOutcomeJournalDAO() {
        return new MongoDbOutcomeJournalDAO();
    }
}
