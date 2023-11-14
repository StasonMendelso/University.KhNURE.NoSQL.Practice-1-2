package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

/**
 * @author Stanislav Hlova
 */
public class MongoDbItemDAO implements ItemDAO {

    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> itemCollection;

    public MongoDbItemDAO(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
        this.itemCollection = mongoDatabase.getCollection("item");
    }


    @Override
    public Item create(Item item) throws DAOException {
        Document document = mapToDocument(item);
        itemCollection.insertOne(document);
        return readById(document.getObjectId("_id").toString());
    }

    private Document mapToDocument(Item item) {
        return new Document()
                .append("vendor", item.getVendor())
                .append("name", item.getName())
                .append("unit", item.getUnit())
                .append("weight", item.getWeight().doubleValue())
                .append("amount", item.getAmount())
                .append("reserve_rate", item.getReserveRate())
                .append("income_journal", new ArrayList<>())
                .append("outcome_journal", new ArrayList<>());
    }

    @Override
    public boolean updateQuantity(String id, int quantity) {
        UpdateResult updateResult = itemCollection.updateOne(eq("_id", new ObjectId(id)), set("amount", quantity));
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean update(String id, Item item) throws DAOException {
        UpdateResult updateResult = itemCollection.updateOne(eq("_id", new ObjectId(id)), combine(
                set("vendor", item.getVendor()),
                set("name", item.getName()),
                set("unit", item.getUnit()),
                set("weight", item.getWeight().doubleValue()),
                set("amount", item.getAmount()),
                set("reserve_rate", item.getReserveRate())));
        return updateResult.getModifiedCount() > 0;
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
        FindIterable<Document> documents = itemCollection.find(and(
                regex("name", name),
                gte("amount", minAmount),
                lte("amount", maxAmount)
        ));
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
                .weight(BigDecimal.valueOf(document.getDouble("weight")).setScale(4, RoundingMode.FLOOR))
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
        UpdateResult updateResult = itemCollection.updateOne(eq("_id", new ObjectId(id)), unset("amount"));
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public List<String> readAllAvailableId() throws DAOException {
        return readAll().stream()
                .map(Item::getId)
                .collect(Collectors.toList());
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
}
