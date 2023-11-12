package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.MySqlDAOConfig;
import ua.nure.st.kpp.example.demo.dao.Factory;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;

public class MySqlDAOFactory implements Factory {
    private final MySqlConnectionUtils mySqlConnectionUtils;

    public MySqlDAOFactory(MySqlDAOConfig config) {
        this.mySqlConnectionUtils = new MySqlConnectionUtils(config);
    }

    @Override
    public ItemDAO createItemDAO() {
        return new MySqlItemDAO(mySqlConnectionUtils);
    }

    @Override
    public CompanyDAO createCompanyDAO() {
        return new MySqlCompanyDAO(mySqlConnectionUtils);
    }

    @Override
    public IncomeJournalDAO createIncomeJournalDAO() {
        return new MySqlIncomeJournalDAO(mySqlConnectionUtils);
    }

    @Override
    public OutcomeJournalDAO createOutcomeJournalDAO() {
        return new MySqlOutcomeJournalDAO(mySqlConnectionUtils);
    }

}
