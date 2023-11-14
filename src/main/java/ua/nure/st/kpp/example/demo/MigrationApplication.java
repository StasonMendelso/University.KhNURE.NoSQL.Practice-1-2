package ua.nure.st.kpp.example.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import ua.nure.st.kpp.example.demo.dao.*;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoDbDaoFactory;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.MySqlDAOFactory;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stanislav Hlova
 */
public class MigrationApplication {
    private static Factory mySqlDaoFactory;
    private static Factory mongoDbDaoFactory;
    static Logger root = (Logger) LoggerFactory
            .getLogger(Logger.ROOT_LOGGER_NAME);

    static {
        root.setLevel(Level.OFF);
    }

    static {
        MySqlDAOConfig config = new MySqlDAOConfig("jdbc:mysql://localhost:3306/warehousecpp", "root", "root");
        mySqlDaoFactory = new MySqlDAOFactory(config);

        MongoDbDAOConfig mongoDbDAOConfig = new MongoDbDAOConfig("mongodb://localhost:27001", "warehouse");
        mongoDbDaoFactory = new MongoDbDaoFactory(mongoDbDAOConfig);
    }


    public static void main(String[] args) throws DAOException {
        //    migrateToMongoDB();
        migrateToMySql();
    }

    private static void migrateToMongoDB() throws DAOException {
        System.out.println("Migration to MongoDB started\n");

        System.out.println("Migrating company started");
        CompanyDAO mySqlCompanyDao = mySqlDaoFactory.createCompanyDAO();
        CompanyDAO mongoDbCompanyDao = mongoDbDaoFactory.createCompanyDAO();

        List<Company> companies = mySqlCompanyDao.readAll();
        Map<String, String> companyNewIdMap = new HashMap<>();
        for (Company company : companies) {
            Company newCompany = mongoDbCompanyDao.create(company);
            companyNewIdMap.put(company.getId(), newCompany.getId());
        }
        System.out.println("Migrating company finished");

        System.out.println("Migrating item started");
        ItemDAO mySqlItemDao = mySqlDaoFactory.createItemDAO();
        IncomeJournalDAO mySqlIncomeJournalDAO = mySqlDaoFactory.createIncomeJournalDAO();
        OutcomeJournalDAO mySqlOutcomeJournalDAO = mySqlDaoFactory.createOutcomeJournalDAO();
        ItemDAO mongoDbItemDao = mongoDbDaoFactory.createItemDAO();

        List<Item> itemListFromMySQL = mySqlItemDao.readAll();
        List<Record> incomeRecords = mySqlIncomeJournalDAO.readAll().getRecords();
        List<Record> outcomeRecords = mySqlOutcomeJournalDAO.readAll().getRecords();
        Map<String, String> itemNewIdMap = new HashMap<>();

        for (Item item : itemListFromMySQL) {
            int positiveAmount = getAmount(item.getId(), outcomeRecords);
            int negativeAmount = getAmount(item.getId(), incomeRecords);

            int deltaAmount = negativeAmount - positiveAmount;
            int initialAmount = item.getAmount() - deltaAmount;

            item.setAmount(initialAmount);
            Item newItem = mongoDbItemDao.create(item);
            itemNewIdMap.put(item.getId(), newItem.getId());
        }
        System.out.println("Migrating item finished");


        System.out.println("Migrating income journal started");
        IncomeJournalDAO mySqlIncomeJournalDao = mySqlDaoFactory.createIncomeJournalDAO();
        IncomeJournalDAO mongoDbIncomeJournalDao = mongoDbDaoFactory.createIncomeJournalDAO();

        for (Record record : mySqlIncomeJournalDao.readAll().getRecords()) {
            record.setId(new ObjectId().toString());
            record.getItem().setId(itemNewIdMap.get(record.getItem().getId()));
            record.getCompany().setId(companyNewIdMap.get(record.getCompany().getId()));
            mongoDbIncomeJournalDao.createRecord(record);
        }
        System.out.println("Migrating income journal finished");


        System.out.println("Migrating outcome journal started");
        OutcomeJournalDAO mySqlOutcomeJournalDao = mySqlDaoFactory.createOutcomeJournalDAO();
        OutcomeJournalDAO mongoDbOutcomeJournalDao = mongoDbDaoFactory.createOutcomeJournalDAO();

        for (Record record : mySqlOutcomeJournalDao.readAll().getRecords()) {
            record.setId(new ObjectId().toString());
            record.getItem().setId(itemNewIdMap.get(record.getItem().getId()));
            record.getCompany().setId(companyNewIdMap.get(record.getCompany().getId()));
            mongoDbOutcomeJournalDao.createRecord(record);
        }
        System.out.println("Migrating outcome journal finished");

        System.out.println("\nMigration to MongoDB finished");
    }

    private static void migrateToMySql() throws DAOException {
        System.out.println("Migration to MySQL started\n");

        System.out.println("Migrating company started");
        CompanyDAO mySqlCompanyDao = mySqlDaoFactory.createCompanyDAO();
        CompanyDAO mongoDbCompanyDao = mongoDbDaoFactory.createCompanyDAO();

        List<Company> companies = mongoDbCompanyDao.readAll();
        Map<String, String> companyNewIdMap = new HashMap<>();
        for (Company company : companies) {
            Company newCompany = mySqlCompanyDao.create(company);
            companyNewIdMap.put(company.getId(), newCompany.getId());
        }
        System.out.println("Migrating company finished");

        System.out.println("Migrating item started");
        ItemDAO mySqlItemDao = mySqlDaoFactory.createItemDAO();
        IncomeJournalDAO mongoDbIncomeJournalDAO = mongoDbDaoFactory.createIncomeJournalDAO();
        OutcomeJournalDAO mongoDbOutcomeJournalDAO = mongoDbDaoFactory.createOutcomeJournalDAO();
        ItemDAO mongoDbItemDao = mongoDbDaoFactory.createItemDAO();

        List<Item> itemListFromMongoDB = mongoDbItemDao.readAll();
        List<Record> incomeRecords = mongoDbIncomeJournalDAO.readAll().getRecords();
        List<Record> outcomeRecords = mongoDbOutcomeJournalDAO.readAll().getRecords();
        Map<String, String> itemNewIdMap = new HashMap<>();

        for (Item item : itemListFromMongoDB) {
            int positiveAmount = getAmount(item.getId(), outcomeRecords);
            int negativeAmount = getAmount(item.getId(), incomeRecords);

            int deltaAmount = negativeAmount - positiveAmount;
            int initialAmount = item.getAmount() - deltaAmount;

            item.setAmount(initialAmount);
            Item newItem = mySqlItemDao.create(item);
            itemNewIdMap.put(item.getId(), newItem.getId());
        }
        System.out.println("Migrating item finished");


        System.out.println("Migrating income journal started");
        IncomeJournalDAO mySqlIncomeJournalDao = mySqlDaoFactory.createIncomeJournalDAO();
        IncomeJournalDAO mongoDbIncomeJournalDao = mongoDbDaoFactory.createIncomeJournalDAO();

        for (Record record : mongoDbIncomeJournalDao.readAll().getRecords()) {
            record.getItem().setId(itemNewIdMap.get(record.getItem().getId()));
            record.getCompany().setId(companyNewIdMap.get(record.getCompany().getId()));
            mySqlIncomeJournalDao.createRecord(record);
        }
        System.out.println("Migrating income journal finished");


        System.out.println("Migrating outcome journal started");
        OutcomeJournalDAO mySqlOutcomeJournalDao = mySqlDaoFactory.createOutcomeJournalDAO();
        OutcomeJournalDAO mongoDbOutcomeJournalDao = mongoDbDaoFactory.createOutcomeJournalDAO();

        for (Record record : mongoDbOutcomeJournalDao.readAll().getRecords()) {
            record.getItem().setId(itemNewIdMap.get(record.getItem().getId()));
            record.getCompany().setId(companyNewIdMap.get(record.getCompany().getId()));
            mySqlOutcomeJournalDao.createRecord(record);
        }
        System.out.println("Migrating outcome journal finished");

        System.out.println("\nMigration to MySQL finished");
    }

    private static int getAmount(String itemId, List<Record> incomeRecords) {
        return incomeRecords.stream()
                .filter(record -> record.getItem().getId().equals(itemId))
                .mapToInt(Record::getAmount)
                .sum();
    }
}
