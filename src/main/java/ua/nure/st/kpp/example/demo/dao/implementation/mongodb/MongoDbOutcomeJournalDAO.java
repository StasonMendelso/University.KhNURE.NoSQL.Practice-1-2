package ua.nure.st.kpp.example.demo.dao.implementation.mongodb;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;

/**
 * @author Stanislav Hlova
 */
public class MongoDbOutcomeJournalDAO implements OutcomeJournalDAO {
    @Override
    public boolean createRecord(Record record) throws DAOException {
        return false;
    }

    @Override
    public Journal readAll() throws DAOException {
        return null;
    }

    @Override
    public boolean updateRecord(int id, Record record) throws DAOException {
        return false;
    }

    @Override
    public boolean deleteRecord(int id) throws DAOException {
        return false;
    }

    @Override
    public Record read(int id) throws DAOException {
        return null;
    }
}
