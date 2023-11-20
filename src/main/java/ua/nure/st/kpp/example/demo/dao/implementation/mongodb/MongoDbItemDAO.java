package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.MongoNotPrimaryException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import ua.nure.st.kpp.example.demo.dao.AggregationQuery;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

/**
 * @author Stanislav Hlova
 */
public class MongoDbItemDAO implements ItemDAO, MongoDbDAO, AggregationQuery {
    private final MongoTimeoutProperties mongoTimeoutProperties;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> itemCollection;

    public MongoDbItemDAO(MongoTimeoutProperties mongoTimeoutProperties, MongoDatabase mongoDatabase) {
        this.mongoTimeoutProperties = mongoTimeoutProperties;
        this.mongoDatabase = mongoDatabase;
        this.itemCollection = mongoDatabase.getCollection("item");
    }

    public MongoDbItemDAO(MongoTimeoutProperties mongoTimeoutProperties, MongoDatabase mongoDatabase, WriteConcern writeConcern) {
        this.mongoTimeoutProperties = mongoTimeoutProperties;
        this.mongoDatabase = mongoDatabase;
        this.itemCollection = mongoDatabase.getCollection("item").withWriteConcern(writeConcern);
    }

    @Override
    public Item create(Item item) throws DAOException {
        Document document = mapToDocument(item);
        int count = 0;
        while (true) {
            try {
                itemCollection.insertOne(document);
            } catch (MongoWriteConcernException | MongoNotPrimaryException | MongoWriteException exception) {
                if (count == mongoTimeoutProperties.getNumberOfReconnect()) {
                    throw new DAOException(exception);
                }
                count++;
                sleep(mongoTimeoutProperties.getWaitReconnectDuration());
                System.out.println("Trying to insert an item again!!");
                continue;
            }
            return readById(document.getObjectId("_id").toString());
        }
    }

    protected Document mapToDocument(Item item) {
        return new Document().append("vendor", item.getVendor()).append("name", item.getName()).append("unit", item.getUnit()).append("weight", item.getWeight().doubleValue()).append("amount", item.getAmount()).append("reserve_rate", item.getReserveRate()).append("income_journal", new ArrayList<>()).append("outcome_journal", new ArrayList<>());
    }

    @Override
    public boolean updateQuantity(String id, int quantity) throws DAOException {
        int count = 0;
        while (true) {
            try {
                UpdateResult updateResult = itemCollection.updateOne(eq("_id", new ObjectId(id)), set("amount", quantity));
                return updateResult.getModifiedCount() > 0;
            } catch (MongoWriteConcernException | MongoNotPrimaryException | MongoWriteException exception) {
                if (count == mongoTimeoutProperties.getNumberOfReconnect()) {
                    throw new DAOException(exception);
                }
                count++;
                sleep(mongoTimeoutProperties.getWaitReconnectDuration());
            }
        }

    }

    @Override
    public boolean update(String id, Item item) throws DAOException {
        int count = 0;
        while (true) {
            try {
                UpdateResult updateResult = itemCollection.updateOne(eq("_id", new ObjectId(id)), combine(set("vendor", item.getVendor()), set("name", item.getName()), set("unit", item.getUnit()), set("weight", item.getWeight().doubleValue()), set("amount", item.getAmount()), set("reserve_rate", item.getReserveRate())));
                return updateResult.getModifiedCount() > 0;
            } catch (MongoWriteConcernException | MongoNotPrimaryException | MongoWriteException exception) {
                if (count == mongoTimeoutProperties.getNumberOfReconnect()) {
                    throw new DAOException(exception);
                }
                count++;
                sleep(mongoTimeoutProperties.getWaitReconnectDuration());
            }
        }
    }

    @Override
    public List<Item> readAll() throws DAOException {

        List<Item> itemList = new ArrayList<>();
        FindIterable<Document> documents = itemCollection.find(exists("amount"));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                itemList.add(mapToItem(document));
            }
        }
        return itemList;
    }

    @Override
    public List<Item> readByNameAndAmount(String name, int minAmount, int maxAmount) throws DAOException {
        List<Item> itemList = new ArrayList<>();
        FindIterable<Document> documents = itemCollection.find(and(regex("name", name), gte("amount", minAmount), lte("amount", maxAmount)));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                itemList.add(mapToItem(document));
            }
        }
        return itemList;
    }

    private Item mapToItem(Document document) {
        return new Item.Builder()
                .id(document.getObjectId("_id").toString())
                .vendor(document.getString("vendor"))
                .unit(document.getString("unit"))
                .weight(BigDecimal.valueOf(document.getDouble("weight"))
                        .setScale(4, RoundingMode.FLOOR))
                .amount(document.getInteger("amount"))
                .name(document.getString("name"))
                .reserveRate(document.getInteger("reserve_rate"))
                .build();
    }

    @Override
    public Item readByVendor(String vendor) throws DAOException {
        FindIterable<Document> documents = itemCollection.find(eq("vendor", vendor));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            if (cursor.hasNext()) {
                return mapToItem(cursor.next());
            }
        }
        return null;
    }

    @Override
    public Item readById(String id) throws DAOException {
        FindIterable<Document> documents = itemCollection.find(eq("_id", new ObjectId(id)));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            if (cursor.hasNext()) {
                return mapToItem(cursor.next());
            }
        }
        return null;
    }

    @Override
    public boolean delete(String id) throws DAOException {
        int count = 0;
        while (true) {
            try {
                UpdateResult updateResult = itemCollection.updateOne(eq("_id", new ObjectId(id)), unset("amount"));
                return updateResult.getModifiedCount() > 0;
            } catch (MongoWriteConcernException | MongoNotPrimaryException | MongoWriteException exception) {
                if (count == mongoTimeoutProperties.getNumberOfReconnect()) {
                    throw new DAOException(exception);
                }
                count++;
                sleep(mongoTimeoutProperties.getWaitReconnectDuration());
            }
        }

    }

    @Override
    public List<String> readAllAvailableId() throws DAOException {
        return readAll().stream().map(Item::getId).collect(Collectors.toList());
    }

    @Override
    public List<Item> readAllByName(String name) throws DAOException {
        List<Item> itemList = new ArrayList<>();
        FindIterable<Document> documents = itemCollection.find(combine(regex("name", name, "i"), exists("amount")));
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                itemList.add(mapToItem(document));
            }
        }
        return itemList;
    }

    @Override
    public Long findTotalItemsNumber() {
        AggregateIterable<Document> iterable = itemCollection.aggregate(List.of(
                Aggregates.group(null, Accumulators.sum("total_warehouse_items", "$amount"))
        ));
        try (MongoCursor<Document> cursor = iterable.cursor()) {
            if (cursor.hasNext()) {
                return cursor.next().getInteger("total_warehouse_items").longValue();
            }
            throw new RuntimeException("Can't find total items number");
        }
    }

    @Override
    public Map<String, Long> findTotalItemsNumberGroupByUnit() {
        Map<String, Long> result = new HashMap<>();
        AggregateIterable<Document> iterable = itemCollection.aggregate(List.of(
                Aggregates.group("$unit", Accumulators.sum("total_items", "$amount")),
                Aggregates.project(Projections.fields(
                        Projections.computed("unit", "$_id"),
                        Projections.computed("total_amount", "$total_items")
                ))
        ));
        try (MongoCursor<Document> cursor = iterable.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String unit = document.getString("unit");
                long amount = document.getInteger("total_amount").longValue();
                result.put(unit, amount);
            }
            return result;
        }
    }

    @Override
    public List<Item> findItemsByOneCompanyWithEmail(String email) {
        List<Item> result = new ArrayList<>();
        AggregateIterable<Document> iterable = itemCollection.aggregate(List.of(
                Aggregates.match(Filters.eq("income_journal.company.email", email))
        ));
        try (MongoCursor<Document> cursor = iterable.cursor()) {
            while (cursor.hasNext()) {
                result.add(mapToItem(cursor.next()));
            }
            return result;
        }
    }

    @Override
    public BigDecimal findTotalPaidPriceForItemByVendor(String vendor) {
        AggregateIterable<Document> iterable = itemCollection.aggregate(List.of(
                Aggregates.match(Filters.eq("vendor",vendor)),
                Aggregates.unwind("$income_journal"),
                Aggregates.project(Projections.computed("totalPrice",
                        new Document("$multiply", List.of("$income_journal.amount", "$income_journal.price")))),
                Aggregates.group("$_id",Accumulators.sum("totalSum","$totalPrice"))
        ));
        try (MongoCursor<Document> cursor = iterable.cursor()) {
            if (cursor.hasNext()) {
                return cursor.next().get("totalSum", Decimal128.class).bigDecimalValue();
            }
            throw new RuntimeException("Can't find total paid for item by vendor");
        }
    }

    @Override
    public BigDecimal findTotalPaidPriceForItems() {
        AggregateIterable<Document> iterable = itemCollection.aggregate(List.of(
                Aggregates.unwind("$income_journal"),
                Aggregates.project(Projections.computed("totalPrice",
                        new Document("$multiply", List.of("$income_journal.amount", "$income_journal.price")))),
                Aggregates.group(null,Accumulators.sum("total","$totalPrice"))
        ));
        try (MongoCursor<Document> cursor = iterable.cursor()) {
            if (cursor.hasNext()) {
                return cursor.next().get("total", Decimal128.class).bigDecimalValue();
            }
            throw new RuntimeException("Can't find total paid for item by vendor");
        }
    }
}
