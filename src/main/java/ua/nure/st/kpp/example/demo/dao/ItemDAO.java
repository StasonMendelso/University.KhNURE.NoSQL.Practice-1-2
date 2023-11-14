package ua.nure.st.kpp.example.demo.dao;

import org.springframework.stereotype.Repository;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.List;
@Repository
public interface ItemDAO {
    Item create(Item item) throws DAOException;

    boolean updateQuantity(String id, int quantity) throws DAOException;

    boolean update(String id, Item item) throws DAOException;

    List<Item> readAll() throws DAOException;
    List<Item> readByNameAndAmount(String name, int minAmount, int maxAmount) throws DAOException;

    Item readByVendor(String vendor) throws DAOException;

    Item readById(String id) throws DAOException;

    boolean delete(String id) throws DAOException;

    List<String> readAllAvailableId() throws DAOException;

    List<Item> readAllByName(String name) throws DAOException;
}
