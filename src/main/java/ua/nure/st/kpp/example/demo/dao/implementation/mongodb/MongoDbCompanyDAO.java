package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.MongoNotPrimaryException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * @author Stanislav Hlova
 */
public class MongoDbCompanyDAO implements CompanyDAO, MongoDbDAO {
    private final MongoTimeoutProperties mongoTimeoutProperties;

    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> companyCollection;

    public MongoDbCompanyDAO(MongoTimeoutProperties mongoTimeoutProperties, MongoDatabase mongoDatabase) {
        this.mongoTimeoutProperties = mongoTimeoutProperties;
        this.mongoDatabase = mongoDatabase;
        this.companyCollection = mongoDatabase.getCollection("company");
    }

    @Override
    public Company create(Company company) throws DAOException {
        Document document = mapToDocument(company);
        int count = 0;
        while (true) {
            try {
                companyCollection.insertOne(document);
                return read(document.getObjectId("_id").toString());
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
    public List<Company> readAll() throws DAOException {
        List<Company> companyList = new ArrayList<>();
        FindIterable<Document> documents = companyCollection.find();
        try (MongoCursor<Document> cursor = documents.cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                companyList.add(mapToCompany(document));
            }
        }
        return companyList;
    }

    private Company mapToCompany(Document document) {
        return new Company.Builder()
                .id(document.getObjectId("_id").toString())
                .name(document.getString("name"))
                .address(document.getString("address"))
                .email(document.getString("email"))
                .build();
    }

    @Override
    public boolean update(String id, Company company) throws DAOException {
        int count = 0;
        while (true) {
            try {
                UpdateResult updateResult = companyCollection.updateOne(eq("_id", new ObjectId(id)),
                        combine(set("name", company.getName()),
                                set("email", company.getEmail()),
                                set("address",company.getAddress())));
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

    private Document mapToDocument(Company company) {
        return new Document()
                .append("name", company.getName())
                .append("email", company.getEmail())
                .append("address", company.getAddress());
    }

    @Override
    public Company read(String id) throws DAOException {
        FindIterable<Document> documents = companyCollection.find(eq("_id", new ObjectId(id)));
        if (documents.cursor().hasNext()) {
            return mapToCompany(documents.cursor().next());
        }
        return null;
    }

    @Override
    public List<String> readAllAvailableId() throws DAOException {
        return readAll().stream()
                .map(Company::getId)
                .collect(Collectors.toList());
    }
}
