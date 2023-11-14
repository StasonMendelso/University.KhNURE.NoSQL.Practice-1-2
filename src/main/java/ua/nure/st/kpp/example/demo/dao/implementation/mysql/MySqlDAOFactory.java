package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.MySqlDAOConfig;
import ua.nure.st.kpp.example.demo.dao.Factory;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;

public class MySqlDAOFactory implements Factory {
    private final MySqlItemDAO mySqlItemDAO;
    private final MySqlCompanyDAO mySqlCompanyDAO;
    private final MySqlIncomeJournalDAO mySqlIncomeJournalDAO;
    private final MySqlOutcomeJournalDAO mySqlOutcomeJournalDAO;

    public MySqlDAOFactory(MySqlDAOConfig config) {
        MySqlConnectionUtils mySqlConnectionUtils = new MySqlConnectionUtils(config);
        this.mySqlItemDAO = new MySqlItemDAO(mySqlConnectionUtils);
        this.mySqlCompanyDAO = new MySqlCompanyDAO(mySqlConnectionUtils);
        this.mySqlIncomeJournalDAO = new MySqlIncomeJournalDAO(mySqlConnectionUtils);
        this.mySqlOutcomeJournalDAO = new MySqlOutcomeJournalDAO(mySqlConnectionUtils);
    }

    @Override
    public ItemDAO createItemDAO() {
        return mySqlItemDAO;
    }

    @Override
    public CompanyDAO createCompanyDAO() {
        return mySqlCompanyDAO;
    }

    @Override
    public IncomeJournalDAO createIncomeJournalDAO() {
        return mySqlIncomeJournalDAO;
    }

    @Override
    public OutcomeJournalDAO createOutcomeJournalDAO() {
        return mySqlOutcomeJournalDAO;
    }

}
