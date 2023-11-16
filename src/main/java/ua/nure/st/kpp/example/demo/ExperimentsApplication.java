package ua.nure.st.kpp.example.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.MongoDbDAOConfig;
import ua.nure.st.kpp.example.demo.dao.MySqlDAOConfig;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoDbDaoFactory;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.MySqlDAOFactory;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Stanislav Hlova
 */
public class ExperimentsApplication {
    private static final String MONGO_DB = "[MONGO_DB] - ";
    private static final String MY_SQL = "[MY_SQL] - ";

    private static final ItemDAO mySqlItemDao;
    private static final ItemDAO mongoDbItemDao;
    private static final MySqlConnectionUtils mySqlConnectionUtils;
    private static final MongoClient mongoClient;
    static Logger root = (Logger) LoggerFactory
            .getLogger(Logger.ROOT_LOGGER_NAME);

    static {
        root.setLevel(Level.OFF);
    }

    static {
        MySqlDAOConfig config = new MySqlDAOConfig("jdbc:mysql://localhost:3306/warehousecpp", "root", "root");
        MySqlDAOFactory mySqlDAOFactory = new MySqlDAOFactory(config);
        mySqlConnectionUtils = new MySqlConnectionUtils(config);
        mySqlItemDao = mySqlDAOFactory.createItemDAO();

        MongoDbDAOConfig mongoDbDAOConfig = new MongoDbDAOConfig();
        mongoDbDAOConfig.setConnectionString("mongodb://localhost:27017");
        mongoDbDAOConfig.setName("warehouse");
        MongoDbDaoFactory mongoDbDaoFactory = new MongoDbDaoFactory(mongoDbDAOConfig);
        mongoClient = MongoClients.create(new ConnectionString(mongoDbDAOConfig.getConnectionString()));
        mongoDbItemDao = mongoDbDaoFactory.createItemDAO();
    }

    public static void main(String[] args) throws SQLException {
        clearDatabases();
        int[] sizes = {100,1000,10000,50000,100000,500000};

        for (int size : sizes) {
            System.out.println("==========================================");
            String name = "Nails";
            int minAmount = 20;
            int maxAmount = 50;
            System.out.println("[MAIN] - " + "Dataset size is " + size + ".Search with parameters: name = " + name + ", minAmount = " + minAmount + ", maxAmount = " + maxAmount);
            List<Item> itemList = experimentInsert(size);
            experimentSelectByNameAndAmount(itemList, name, minAmount, maxAmount);

            clearDatabases();
        }
    }

    private static void clearDatabases() throws SQLException {
        DeleteResult deleteResult = mongoClient.getDatabase("warehouse").getCollection("item").deleteMany(new Document());
        System.out.println(MONGO_DB + "Deleted " + deleteResult.getDeletedCount() + " items");

        Connection connection = mySqlConnectionUtils.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM items");
            int deletedNumber = statement.getUpdateCount();

            System.out.println(MY_SQL + "Deleted " + deletedNumber + " items");
        }
    }

    private static void experimentSelectByNameAndAmount(List<Item> itemList, String name, int minAmount, int maxAmount) throws DAOException {
        //prepare filtered itemList;
        List<Item> filteredItems = itemList.stream().filter(item -> item.getName().contains(name)
                        && item.getAmount() >= minAmount
                        && item.getAmount() <= maxAmount)
                .toList();

        List<Item> selectFromMySQL = selectFromMySQL(name, minAmount, maxAmount);
        List<Item> differences = filteredItems.stream()
                .filter(element -> !selectFromMySQL.contains(element))
                .collect(Collectors.toList());
        if (!differences.isEmpty()) {
            System.out.println(MY_SQL + "Return wrong list! ERROR-ERROR-ERROR");
        }
        List<Item> selectFromMongoDb = selectFromMongoDB(name, minAmount, maxAmount);
        differences = filteredItems.stream()
                .filter(element -> !selectFromMongoDb.contains(element))
                .collect(Collectors.toList());
        if (!differences.isEmpty()) {
            System.out.println(MONGO_DB + "Return wrong list! ERROR-ERROR-ERROR");
        }

    }

    private static List<Item> selectFromMongoDB(String name, int minAmount, int maxAmount) throws DAOException {
        List<Item> result;
        long startTime = System.currentTimeMillis();
        result = mongoDbItemDao.readByNameAndAmount(name, minAmount, maxAmount);
        long endTime = System.currentTimeMillis();
        System.out.println(MONGO_DB + " Select operation by parameters took " + (endTime - startTime) / 1000.0d + " seconds");
        return result;
    }

    private static List<Item> selectFromMySQL(String name, int minAmount, int maxAmount) throws DAOException {
        List<Item> result;
        long startTime = System.currentTimeMillis();
        result = mySqlItemDao.readByNameAndAmount(name, minAmount, maxAmount);
        long endTime = System.currentTimeMillis();
        System.out.println(MY_SQL + " Select operation by parameters took " + (endTime - startTime) / 1000.0d + " seconds");
        return result;
    }

    private static List<Item> experimentInsert(int size) throws DAOException {
        List<Item> itemList = generateItemSet(size);
        insertIntoMySQL(itemList);
        insertIntoMongoDB(itemList);
        return itemList;
    }

    private static void insertIntoMySQL(List<Item> itemList) throws DAOException {
        long startTime = System.currentTimeMillis();
        for (Item item : itemList) {
            mySqlItemDao.create(item);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(MY_SQL + " Insert operations took " + (endTime - startTime) / 1000.0d + " seconds");
    }

    private static void insertIntoMongoDB(List<Item> itemList) throws DAOException {
        long startTime = System.currentTimeMillis();
        for (Item item : itemList) {
            mongoDbItemDao.create(item);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(MONGO_DB + " Insert operations took " + (endTime - startTime) / 1000.0d + " seconds");
    }


    private static final String[] nameSamples = {"Nails 35.0x1.0", "Cookies with chocolate", "Milky cookies", "Nuts", "Bananas from Africa", "Lemons from Turkey"};
    public static final String[] units = {"kg", "box", "gr", "lbs"};

    //generate unique vendor, repeated names and weight.
    private static List<Item> generateItemSet(int size) {
        Random random = new Random();
        List<Item> itemList = new ArrayList<>();
        int maxAmount = 100;
        for (int i = 1; i <= size; i++) {
            itemList.add(new Item.Builder<>()
                    .vendor("vendor" + i)
                    .name(nameSamples[random.nextInt(nameSamples.length)] + "[" + i + "]")
                    .amount(random.nextInt(maxAmount) + 1)
                    .unit(units[random.nextInt(units.length)])
                    .weight(BigDecimal.valueOf(random.nextDouble(10d) + 1).setScale(4, RoundingMode.FLOOR))
                    .reserveRate(random.nextInt(maxAmount) + 1)
                    .build());
        }
        return itemList;
    }

}
