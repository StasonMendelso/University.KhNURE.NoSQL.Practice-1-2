package ua.nure.st.kpp.example.demo.dao.implementation.mysql.util;

import ua.nure.st.kpp.example.demo.dao.DAOConfig;
import ua.nure.st.kpp.example.demo.dao.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlConnectionUtils {
    private final String url;
    private final Properties databaseProperties = new Properties();
    public MySqlConnectionUtils(DAOConfig config) {
        this.url = config.getUrl();
        databaseProperties.setProperty("user", config.getUser());
        databaseProperties.setProperty("password", config.getPassword());
    }

    public Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    public Connection getConnection(boolean transaction) throws SQLException {
        Connection connection = DriverManager.getConnection(url, databaseProperties);
        if (transaction) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);// because we can have a lost update in the update method of the quantity in the item table, because we have read
            //REPEATABLE_READ - При этом никакая другая транзакция не может изменять данные, читаемые текущей транзакцией, пока та не окончена.
            //READ_COMMITTED- На этом уровне обеспечивается защита от чернового, «грязного» чтения, тем не менее, в процессе работы одной транзакции другая может быть успешно завершена и сделанные ею изменения зафиксированы. В итоге первая транзакция будет работать с другим набором данных.
        }
        return connection;
    }
    public void rollback(Connection connection) throws DAOException {
        if(connection!=null){
            try {
                connection.rollback();
            } catch (SQLException exception) {
                throw new DAOException(exception);
            }
        }
    }
    public void close(Connection connection) throws DAOException {
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException exception) {
                throw new DAOException(exception);
            }
        }
    }
}
