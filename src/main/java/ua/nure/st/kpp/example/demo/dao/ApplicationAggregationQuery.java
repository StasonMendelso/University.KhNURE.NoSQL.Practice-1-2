package ua.nure.st.kpp.example.demo.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.Decimal128;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stanislav Hlova
 */
public class ApplicationAggregationQuery implements AggregationQuery {
    private final MongoCollection<Document> itemCollection;

    public ApplicationAggregationQuery(MongoCollection<Document> itemCollection) {
        this.itemCollection = itemCollection;
    }

    @Override
    public Long findTotalItemsNumber() {
        long result = 0L;
        FindIterable<Document> documents = itemCollection.find();
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Integer amount = document.getInteger("amount");
                result += amount.longValue();
            }
        }
        return result;
    }

    @Override
    public Map<String, Long> findTotalItemsNumberGroupByUnit() {
        Map<String, Long> result = new HashMap<>();

        FindIterable<Document> documents = itemCollection.find();
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String unit = document.getString("unit");
                Long amount = document.getInteger("amount").longValue();
                result.compute(unit, (key, value) -> (value == null ? amount : value + amount));
            }
        }

        return result;
    }

    @Override
    public List<Item> findItemsByOneCompanyWithEmail(String email) {
        List<Item> result = new ArrayList<>();
        FindIterable<Document> documents = itemCollection.find();
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                List<Document> incomeJournal = (List<Document>) document.get("income_journal");
                boolean anyMatch = incomeJournal.stream().anyMatch(record -> ((Document) record.get("company")).get("email").equals(email));
                if (anyMatch) {
                    result.add(mapToItem(document));
                }
            }
        }

        return result;
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
    public BigDecimal findTotalPaidPriceForItemByVendor(String vendor) {
        FindIterable<Document> documents = itemCollection.find();
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                if (document.get("vendor").equals(vendor)) {
                    List<Document> incomeJournal = (List<Document>) document.get("income_journal");

                    double sum = incomeJournal.stream()
                            .mapToDouble(value -> value.get("price", Decimal128.class).doubleValue() * value.getInteger("amount"))
                            .sum();

                    return BigDecimal.valueOf(sum).setScale(4,RoundingMode.FLOOR);

                }
            }
        }
        throw new RuntimeException("Can't find item by vendor");
    }

    @Override
    public BigDecimal findTotalPaidPriceForItems() {
        BigDecimal result = BigDecimal.ZERO.setScale(4,RoundingMode.FLOOR);
        FindIterable<Document> documents = itemCollection.find();
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                List<Document> incomeJournal = (List<Document>) document.get("income_journal");

                double sum = incomeJournal.stream()
                        .mapToDouble(value -> value.get("price", Decimal128.class).doubleValue() * value.getInteger("amount"))
                        .sum();

                result = result.add(BigDecimal.valueOf(sum));

            }
        }
        return result;
    }
}
