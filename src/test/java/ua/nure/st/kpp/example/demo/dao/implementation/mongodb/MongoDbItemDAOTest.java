package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import com.mongodb.MongoWriteConcernException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.entity.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Stanislav Hlova
 */
@ExtendWith(MockitoExtension.class)
class MongoDbItemDAOTest {
    private MongoDbItemDAO mongoDbItemDAO;
    private MongoTimeoutProperties mongoTimeoutProperties;
    @Mock
    private MongoCollection<Document> itemCollection;
    @Mock
    private Item item;
    @Mock
    private Document document;
    @Mock
    private ObjectId objectId;
    @Mock
    private MongoWriteConcernException mongoException;

    @BeforeEach
    void setUp() {
        mongoTimeoutProperties = mock(MongoTimeoutProperties.class);
        when(mongoTimeoutProperties.getWaitReconnectDuration()).thenReturn(100L);
        when(mongoTimeoutProperties.getNumberOfReconnect()).thenReturn(3);
        MongoDatabase mongoDatabase = mock(MongoDatabase.class);
        when(mongoDatabase.getCollection("item")).thenReturn(itemCollection);
        mongoDbItemDAO = spy(new MongoDbItemDAO(mongoTimeoutProperties, mongoDatabase));
    }

    @Test
    void shouldExecuteThreeRequest() throws DAOException {
        final String id = "23523fs323r";
        doReturn(document).when(mongoDbItemDAO).mapToDocument(item);
        when(document.getObjectId("_id")).thenReturn(objectId);
        when(objectId.toString()).thenReturn(id);
        doReturn(item).when(mongoDbItemDAO).readById(id);
        doThrow(mongoException)
                .doThrow(mongoException)
                .doNothing()
                .when(itemCollection).insertOne(document);

        mongoDbItemDAO.create(item);

        verify(mongoDbItemDAO, times(2)).sleep(mongoTimeoutProperties.getWaitReconnectDuration());
        verify(itemCollection, times(3)).insertOne(document);
    }
    @Test
    void shouldExecuteThreeRequest_andThrowException() {
        doReturn(document).when(mongoDbItemDAO).mapToDocument(item);
        doThrow(mongoException)
                .doThrow(mongoException)
                .doThrow(mongoException)
                .doThrow(mongoException)
                .when(itemCollection).insertOne(document);

        DAOException exception = assertThrows(DAOException.class, () -> mongoDbItemDAO.create(item));
        assertEquals(mongoException,exception.getCause());

        verify(mongoDbItemDAO, times(3)).sleep(mongoTimeoutProperties.getWaitReconnectDuration());
        verify(itemCollection, times(4)).insertOne(document);
    }
}