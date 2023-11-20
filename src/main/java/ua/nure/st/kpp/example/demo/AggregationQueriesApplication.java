package ua.nure.st.kpp.example.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.slf4j.LoggerFactory;
import ua.nure.st.kpp.example.demo.dao.AggregationQuery;
import ua.nure.st.kpp.example.demo.dao.ApplicationAggregationQuery;
import ua.nure.st.kpp.example.demo.dao.MongoDbDAOConfig;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoDbItemDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoTimeoutProperties;

import java.util.List;
import java.util.Map;

/**
 * @author Stanislav Hlova
 */
public class AggregationQueriesApplication {

    public static void main(String[] args) {
        Logger logger = (Logger) LoggerFactory
                .getLogger("org.mongodb.driver.protocol.command");
        logger.setLevel(Level.OFF);

        MongoDbDAOConfig mongoDbDAOConfig = new MongoDbDAOConfig();
        mongoDbDAOConfig.setName("warehouse");
        mongoDbDAOConfig.setReplicaSet(true);
        mongoDbDAOConfig.setServerAddress(List.of(
                new MongoDbDAOConfig.ServerAddressProperties("localhost", 27001),
                new MongoDbDAOConfig.ServerAddressProperties("localhost", 27002),
                new MongoDbDAOConfig.ServerAddressProperties("localhost", 27003)));
        MongoTimeoutProperties mongoTimeoutProperties = new MongoTimeoutProperties(1000, 3);
        List<ServerAddress> serverAddresses = mongoDbDAOConfig.getServerAddress().stream()
                .map(serverAddressProperties -> new ServerAddress(serverAddressProperties.getHost(), serverAddressProperties.getPort()))
                .toList();
        MongoClient mongoClient = new MongoClient(serverAddresses, MongoClientOptions.builder().retryWrites(false).build());
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDAOConfig.getName());


        AggregationQuery mongoDbItemDAO = new MongoDbItemDAO(mongoTimeoutProperties, mongoDatabase);
        AggregationQuery applicationAggregation = new ApplicationAggregationQuery(mongoDatabase.getCollection("item"));

        System.out.println(mongoDbItemDAO.findTotalItemsNumber());
        System.out.println(applicationAggregation.findTotalItemsNumber());

        System.out.println("=============================");

        Map<String, Long> totalItemsNumberGroupByUnit = mongoDbItemDAO.findTotalItemsNumberGroupByUnit();
        totalItemsNumberGroupByUnit.entrySet().stream()
                .forEach(entry -> System.out.println("unit: " + entry.getKey() + ", amount: " + entry.getValue()));

        Map<String, Long> totalItemsNumberGroupByUnitApp = applicationAggregation.findTotalItemsNumberGroupByUnit();
        totalItemsNumberGroupByUnitApp.entrySet().stream()
                .forEach(entry -> System.out.println("unit: " + entry.getKey() + ", amount: " + entry.getValue()));

        System.out.println("=============================");
        System.out.println(mongoDbItemDAO.findItemsByOneCompanyWithEmail("metal_company@gmail.com"));
        System.out.println(applicationAggregation.findItemsByOneCompanyWithEmail("metal_company@gmail.com"));
        System.out.println("=============================");
        System.out.println(mongoDbItemDAO.findTotalPaidPriceForItemByVendor("038-123-53-365-234"));
        System.out.println(applicationAggregation.findTotalPaidPriceForItemByVendor("038-123-53-365-234"));
        System.out.println("=============================");

        System.out.println(mongoDbItemDAO.findTotalPaidPriceForItems());
        System.out.println(applicationAggregation.findTotalPaidPriceForItems());
    }

}
