package ua.nure.st.kpp.example.demo.dao;

import org.springframework.stereotype.Repository;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;
@Repository
public interface JournalDAO {
    boolean createRecord(Record record) throws DAOException;

    Journal readAll() throws DAOException;

    boolean updateRecord(String id, Record record) throws DAOException;

    boolean deleteRecord(String id) throws DAOException;

    Record read(String id) throws DAOException;
}
