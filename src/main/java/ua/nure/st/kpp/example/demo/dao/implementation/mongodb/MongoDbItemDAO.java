package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class MongoDbItemDAO implements ItemDAO {
    @Override
    public boolean create(Item item) throws DAOException {
        return false;
    }

    @Override
    public boolean updateQuantity(int id, int quantity) throws DAOException {
        return false;
    }

    @Override
    public boolean update(int id, Item item) throws DAOException {
        return false;
    }

    @Override
    public List<Item> readAll() throws DAOException {
        return null;
    }

    @Override
    public Item read(String vendor) throws DAOException {
        return null;
    }

    @Override
    public Item read(int id) throws DAOException {
        return null;
    }

    @Override
    public boolean delete(int id) throws DAOException {
        return false;
    }

    @Override
    public List<Integer> readAllAvailableId() throws DAOException {
        return null;
    }

    @Override
    public List<Item> readAllByName(String name) throws DAOException {
        return null;
    }
}
