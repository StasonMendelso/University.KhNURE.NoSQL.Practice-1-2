package ua.nure.st.kpp.example.demo.dao;

import org.springframework.stereotype.Repository;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.List;
@Repository
public interface ItemDAO {
    boolean create(Item item) throws DAOException;

    boolean updateQuantity(int id, int quantity) throws DAOException;

    boolean update(int id, Item item) throws DAOException;

    List<Item> readAll() throws DAOException;

    Item read(String vendor) throws DAOException;

    Item read(int id) throws DAOException;

    boolean delete(int id) throws DAOException;

    List<Integer> readAllAvailableId() throws DAOException;
}
