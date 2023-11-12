package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Stanislav Hlova
 */
public class MongoDbIncomeJournalDAO implements IncomeJournalDAO {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> itemCollection;
    private final MongoCollection<Document> companyCollection;

    public MongoDbIncomeJournalDAO(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;
        this.itemCollection = mongoDatabase.getCollection("item");
        this.companyCollection = mongoDatabase.getCollection("company");
    }

    @Override
    public boolean createRecord(Record record) throws DAOException {
        //Note: A ClientSession instance can not be used concurrently in multiple operations.
        ClientSession clientSession = mongoClient.startSession();
        try {
            clientSession.startTransaction();

            UpdateResult updateResult = itemCollection.updateOne(clientSession, eq("_id", new ObjectId(record.getItem().getId())), Updates.inc("amount", record.getAmount()));
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }
            updateResult = itemCollection.updateOne(clientSession, eq("_id", new ObjectId(record.getItem().getId())),
                    Updates.push("income_journal", mapToDocument(record)));
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }

            clientSession.commitTransaction();
        } catch (MongoException mongoException) {
            clientSession.abortTransaction();
            throw new DAOException(mongoException);
        } finally {
            clientSession.close();
        }

        return false;
    }

    private Document mapToDocument(Record record) {
        return new Document()
                .append("_id", new ObjectId())
                .append("document_number", record.getDocumentNumber())
                .append("date", Date.from(record.getDate().atZone(ZoneId.systemDefault())
                        .toInstant()))
                .append("price", record.getPrice())
                .append("amount", record.getAmount())
                .append("company", mapToDocument(record.getCompany()));
    }

    private Document mapToDocument(Company company) {
        FindIterable<Document> documents = companyCollection.find(eq("_id", new ObjectId(company.getId())));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            if (!cursor.hasNext()) {
                throw new MongoException("Can't found company with passed id " + company.getId());
            }
            Document document = cursor.next();
            return new Document()
                    .append("company_id", new ObjectId(document.getObjectId("_id").toString()))
                    .append("name", document.getString("name"))
                    .append("email", document.getString("email"))
                    .append("address", document.getString("address"));
        }

    }

    @Override
    public Journal readAll() throws DAOException {
        List<Record> recordList = new ArrayList<>();
        FindIterable<Document> documents = itemCollection.find().projection(Document.parse("{'outcome_journal':0}"));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                recordList.addAll(mapToRecordList(document));

            }
        }

        return new Journal(recordList);
    }

    private List<Record> mapToRecordList(Document document) {
        List<Record> result = new ArrayList<>();
        Item item = mapToItem(document);

        List<Document> documentList = document.getList("income_journal", Document.class);
        Iterator<Document> iterator = documentList.iterator();
        while (iterator.hasNext()) {
            Record record = getRecord(iterator.next());
            record.setItem(item);
            result.add(record);
        }

        return result;
    }

    private Record getRecord(Document document) {
        return new Record.Builder()
                .id(document.getObjectId("_id").toString())
                .documentNumber(document.getString("document_number"))
                .date(document.getDate("date").toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .amount(document.getInteger("amount"))
                .price(document.get("price", Decimal128.class).bigDecimalValue())
                .company(mapToCompany(document.get("company", Document.class)))
                .build();
    }

    private Item mapToItem(Document document) {
        return new Item.Builder()
                .id(document.getObjectId("_id").toString())
                .vendor(document.getString("vendor"))
                .unit(document.getString("unit"))
                .weight(BigDecimal.valueOf(document.getDouble("weight")))
                .amount(document.getInteger("amount"))
                .name(document.getString("name"))
                .reserveRate(document.getInteger("reserve_rate"))
                .build();
    }

    private Company mapToCompany(Document document) {
        return new Company.Builder()
                .id(document.getObjectId("company_id").toString())
                .name(document.getString("name"))
                .address(document.getString("address"))
                .email(document.getString("email"))
                .build();
    }

    @Override
    public boolean updateRecord(String id, Record record) throws DAOException {
        //Note: A ClientSession instance can not be used concurrently in multiple operations.
        ClientSession clientSession = mongoClient.startSession();
        try {
            clientSession.startTransaction();

            Record recordFromDb = read(id);
            UpdateResult updateResult = itemCollection.updateOne(clientSession, eq("_id", new ObjectId(recordFromDb.getItem().getId())), Updates.inc("amount", -recordFromDb.getAmount()));
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }
            updateResult = itemCollection.updateOne(clientSession,
                    Filters.eq("income_journal._id", new ObjectId(id)),
                    Updates.pull("income_journal", new Document("_id", new ObjectId(id)))
            );
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }

            updateResult = itemCollection.updateOne(clientSession, eq("_id", new ObjectId(record.getItem().getId())), Updates.inc("amount", record.getAmount()));
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }
            updateResult = itemCollection.updateOne(clientSession, eq("_id", new ObjectId(record.getItem().getId())),
                    Updates.push("income_journal", mapToDocument(record)));
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }


            clientSession.commitTransaction();
        } catch (MongoException mongoException) {
            clientSession.abortTransaction();
            throw new DAOException(mongoException);
        } finally {
            clientSession.close();
        }

        return false;
    }

    @Override
    public boolean deleteRecord(String id) throws DAOException {

        //Note: A ClientSession instance can not be used concurrently in multiple operations.
        ClientSession clientSession = mongoClient.startSession();
        try {
            clientSession.startTransaction();
            Record recordFromDb = read(id);
            UpdateResult updateResult = itemCollection.updateOne(clientSession, eq("_id", new ObjectId(recordFromDb.getItem().getId())), Updates.inc("amount", -recordFromDb.getAmount()));
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }
            updateResult = itemCollection.updateOne(clientSession,
                    Filters.eq("income_journal._id", new ObjectId(id)),
                    Updates.pull("income_journal", new Document("_id", new ObjectId(id)))
            );
            if (updateResult.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return false;
            }

            clientSession.commitTransaction();
        } catch (MongoException mongoException) {
            clientSession.abortTransaction();
            throw new DAOException(mongoException);
        } finally {
            clientSession.close();
        }

        return false;

    }

    @Override
    public Record read(String id) throws DAOException {
        FindIterable<Document> documents = itemCollection.find(Filters.elemMatch("income_journal", eq("_id", new ObjectId(id))))
                .projection(Projections.fields(
                        Projections.elemMatch("income_journal", eq("_id", new ObjectId(id))),
                        Projections.include("_id", "vendor", "name", "unit", "weight", "reserve_rate", "amount")));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            if (cursor.hasNext()) {
                Document document = cursor.next();
                Record record = getRecord((Document) document.get("income_journal", ArrayList.class).get(0));
                record.setItem(mapToItem(document));

                return record;
            }
        }

        return null;
    }
}
