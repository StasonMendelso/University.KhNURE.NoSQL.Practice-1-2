package ua.nure.st.kpp.example.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.MongoDbDAOConfig;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoDbItemDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoTimeoutProperties;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Stanislav Hlova
 */
public class MongoDbExperimentsApplication {


    public static void main(String[] args) throws DAOException, InterruptedException {
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

        DeleteResult deleteResult = mongoClient.getDatabase("warehouse").getCollection("item").deleteMany(new Document());
        System.out.println("Deleted " + deleteResult.getDeletedCount() + " items");
        Thread.sleep(10000L);

        MongoDbItemDAO mongoDbItemDAO = new MongoDbItemDAO(mongoTimeoutProperties, mongoDatabase, WriteConcern.MAJORITY);
        List<Item> itemList = new ArrayList<>();
        System.out.println("Started inserting");
        for (int i = 1; i <= 10000; i++) {
            Item item = new Item.Builder<>().vendor("vendor" + i).name("name" + i).reserveRate(i).weight(BigDecimal.TEN.setScale(4, RoundingMode.FLOOR)).unit("unit").amount(i)
                    .build();
            itemList.add(item);
            if (i % 30 == 0) {
                System.out.println();
            }
            System.out.print("Insert item " + i + "\t");
            mongoDbItemDAO.create(item);
        }
        System.out.println("\nFinished inserting");

        List<Item> itemsFromMongo = mongoDbItemDAO.readAll();
        itemsFromMongo.sort((o1, o2) -> o1.getVendor().compareTo(o2.getVendor()));
        itemList.sort((o1, o2) -> o1.getVendor().compareTo(o2.getVendor()));
        if (itemList.equals(itemsFromMongo)) {
            System.out.println("All records are equaled");
        } else {
            System.out.println("Some records don't equal");
        }
        System.out.println("The total number of readed from MongoDb: " + itemsFromMongo.size());


    }

}

