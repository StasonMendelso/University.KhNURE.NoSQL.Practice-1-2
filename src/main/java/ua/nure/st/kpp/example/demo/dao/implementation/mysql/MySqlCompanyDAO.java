package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class MySqlCompanyDAO implements CompanyDAO {
    private final MySqlConnectionUtils mySqlConnectionUtils;

    public MySqlCompanyDAO(MySqlConnectionUtils mySqlConnectionUtils) {

        this.mySqlConnectionUtils = mySqlConnectionUtils;
    }

    private static class Query {
        public static final String INSERT_COMPANY = "INSERT INTO companies(name,email,address) VALUES (?,?,?);";
        public static final String UPDATE_COMPANY_BY_ID = "UPDATE companies SET name = ?, email = ?, address = ? WHERE id = ?;";
        public static final String GET_ALL_COMPANIES = "SELECT * FROM companies ORDER BY id;";
        public static final String GET_COMPANY_BY_ID = "SELECT * FROM companies WHERE id = ?;";
        public static final String GET_ALL_COMPANIES_ID = "SELECT id FROM companies ORDER BY id";
    }

    @Override
    public Company create(Company company) throws DAOException {
        try (Connection connection = mySqlConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query.INSERT_COMPANY, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            preparedStatement.setString(index++, company.getName());
            preparedStatement.setString(index++, company.getEmail());
            preparedStatement.setString(index, company.getAddress());

            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            return read(generatedKeys.getString(1));
        } catch (SQLException exception) {
            throw new DAOException(exception);
        }
    }

    @Override
    public List<Company> readAll() throws DAOException {
        List<Company> companies = new LinkedList<>();
        try (Connection connection = mySqlConnectionUtils.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(Query.GET_ALL_COMPANIES)) {
                while (resultSet.next()) {
                    Company company = mapCompany(resultSet);
                    companies.add(company);
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        }
        return companies;
    }

    @Override
    public Company read(String id) throws DAOException {
        try (Connection connection = mySqlConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_COMPANY_BY_ID)) {
            preparedStatement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCompany(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        }
        return null;
    }

    @Override
    public List<String> readAllAvailableId() throws DAOException {
        List<String> idList = new LinkedList<>();
        try (Connection connection = mySqlConnectionUtils.getConnection();
            Statement statement = connection.createStatement()){
            try(ResultSet resultSet = statement.executeQuery(Query.GET_ALL_COMPANIES_ID)){
                while(resultSet.next()){
                    idList.add(String.valueOf(resultSet.getInt("id")));
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        }
        return idList;
    }

    private Company mapCompany(ResultSet resultSet) throws SQLException {
        String id = String.valueOf(resultSet.getInt("id"));
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String address = resultSet.getString("address");
        return new Company.Builder<>()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();
    }

    @Override
    public boolean update(String id, Company company) throws DAOException {
        try (Connection connection = mySqlConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query.UPDATE_COMPANY_BY_ID)) {
            int index = 1;
            preparedStatement.setString(index++, company.getName());
            preparedStatement.setString(index++, company.getEmail());
            preparedStatement.setString(index++, company.getAddress());
            preparedStatement.setInt(index, Integer.parseInt(id));

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DAOException(exception);
        }
    }


}
