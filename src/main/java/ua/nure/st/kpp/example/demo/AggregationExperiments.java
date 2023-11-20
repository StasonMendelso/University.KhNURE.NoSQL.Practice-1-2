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
import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.MongoDbDAOConfig;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoDbDaoFactory;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoTimeoutProperties;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Stanislav Hlova
 */
public class AggregationExperiments {
    public static void main(String[] args) throws DAOException {
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
        mongoDbDAOConfig.setWaitReconnectDuration(mongoTimeoutProperties.getWaitReconnectDuration());
        mongoDbDAOConfig.setNumberOfReconnect(mongoTimeoutProperties.getNumberOfReconnect());
        MongoClient mongoClient = new MongoClient(serverAddresses, MongoClientOptions.builder().retryWrites(false).build());
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDAOConfig.getName());

        MongoDbDaoFactory mongoDbDaoFactory = new MongoDbDaoFactory(mongoDbDAOConfig);

        AggregationQuery mongoDbItemDAO = (AggregationQuery) mongoDbDaoFactory.createItemDAO();
        AggregationQuery applicationAggregation = new ApplicationAggregationQuery(mongoDatabase.getCollection("item"));


        //insert 100.000 records
        //  insertRecords(mongoDbDaoFactory, 100_000);

        runExperiments(mongoDbItemDAO, applicationAggregation);
    }

    private static final String AGGREGATION_FRAMEWORK = "[AGGREGATION FRAMEWORK] - ";
    private static final String STANDARD_JAVA = "[STANDARD JAVA] - ";

    private static void runExperiments(AggregationQuery mongoDbItemDAO, AggregationQuery applicationAggregation) {
        System.out.println("Query № 1");
        System.out.println(AGGREGATION_FRAMEWORK + measureTime(mongoDbItemDAO::findTotalItemsNumber));
        System.out.println(STANDARD_JAVA + measureTime(applicationAggregation::findTotalItemsNumber));

        System.out.println("Query № 2");
        System.out.println(AGGREGATION_FRAMEWORK + measureTime(mongoDbItemDAO::findTotalItemsNumberGroupByUnit));
        System.out.println(STANDARD_JAVA + measureTime(applicationAggregation::findTotalItemsNumberGroupByUnit));

        System.out.println("Query № 3");
        final String email = "company_address3";
        System.out.println(AGGREGATION_FRAMEWORK + measureTime(() -> mongoDbItemDAO.findItemsByOneCompanyWithEmail(email)));
        System.out.println(STANDARD_JAVA + measureTime(() -> applicationAggregation.findItemsByOneCompanyWithEmail(email)));

        System.out.println("Query № 4");
        final String vendor = "vendor56003";
        System.out.println(AGGREGATION_FRAMEWORK + measureTime(() -> mongoDbItemDAO.findTotalPaidPriceForItemByVendor(vendor)));
        System.out.println(STANDARD_JAVA + measureTime(() -> applicationAggregation.findTotalPaidPriceForItemByVendor(vendor)));

        System.out.println("Query № 5");
        System.out.println(AGGREGATION_FRAMEWORK + measureTime(mongoDbItemDAO::findTotalPaidPriceForItems));
        System.out.println(STANDARD_JAVA + measureTime(applicationAggregation::findTotalPaidPriceForItems));
    }

    private static Long measureTime(MeasureFunction measureFunction) {
        long startTime = System.currentTimeMillis();

        measureFunction.apply();

        long endTime = System.currentTimeMillis();

        return (endTime - startTime) / 1000;
    }

    private interface MeasureFunction {
        void apply();
    }


    private static void insertRecords(MongoDbDaoFactory mongoDbDaoFactory, int recordNumber) throws DAOException {
        ItemDAO itemDAO = mongoDbDaoFactory.createItemDAO();
        CompanyDAO companyDAO = mongoDbDaoFactory.createCompanyDAO();
        IncomeJournalDAO incomeJournalDAO = mongoDbDaoFactory.createIncomeJournalDAO();
        List<Company> companies = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            Company company = companyDAO.create(new Company.Builder()
                    .email("company_email@gmail.com")
                    .name("company_name " + i)
                    .address("company_address" + i)
                    .build());
            companies.add(company);
        }

        Random random = new Random();
        List<String> units = List.of("kg", "apiece", "sq.metres");

        for (int i = 1; i <= recordNumber; i++) {
            Item item = itemDAO.create(new Item.Builder()
                    .vendor("vendor" + i)
                    .reserveRate(random.nextInt(10, 200))
                    .unit(units.get(random.nextInt(units.size())))
                    .weight(BigDecimal.valueOf(random.nextDouble(5)))
                    .amount(random.nextInt(1, 11))
                    .name("item_name" + i)
                    .build());

            for (int j = 1; j <= random.nextInt(1, 15); j++) {
                incomeJournalDAO.createRecord(new Record.Builder()
                        .item(item)
                        .price(BigDecimal.valueOf(random.nextDouble(50, 300)))
                        .amount(random.nextInt(1, 40))
                        .company(companies.get(random.nextInt(companies.size())))
                        .documentNumber("document_number" + i + j)
                        .date(LocalDateTime.now())
                        .build());
            }
        }

    }
}
