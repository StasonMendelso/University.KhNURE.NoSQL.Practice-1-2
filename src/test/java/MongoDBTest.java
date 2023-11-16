import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.MongoDbDAOConfig;
import ua.nure.st.kpp.example.demo.dao.implementation.mongodb.MongoDbDaoFactory;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Stanislav Hlova
 */
class MongoDBTest {

    private MongoDbDaoFactory mongoDbDaoFactory;
    private ItemDAO mongoDbItemDAO;


    @BeforeEach
    void setUp() {
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

        MongoDbDaoFactory mongoDbDaoFactory = new MongoDbDaoFactory(mongoDbDAOConfig);
        mongoDbItemDAO = mongoDbDaoFactory.createItemDAO();
    }

    @Test
    void findException() throws DAOException {
        System.out.println("Started");
        for (int i = 1; i <= 10000; i++) {
            System.out.println("Insert item " + i);
            mongoDbItemDAO.create(new Item.Builder<>()
                    .vendor("vendor" + i)
                    .name("name")
                    .reserveRate(i)
                    .weight(BigDecimal.TEN)
                    .unit("unit")
                    .amount(i)
                    .build());
        }
        System.out.println("Finished");
    }
}
