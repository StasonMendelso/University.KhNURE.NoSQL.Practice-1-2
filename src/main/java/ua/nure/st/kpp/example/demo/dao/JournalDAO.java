package ua.nure.st.kpp.example.demo.dao;

import org.springframework.stereotype.Repository;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;
@Repository
public interface JournalDAO {
    boolean createRecord(Record record) throws DAOException;

    Journal readAll() throws DAOException;

    boolean updateRecord(int id, Record record) throws DAOException;

    boolean deleteRecord(int id) throws DAOException;

    Record read(int id) throws DAOException;
}
