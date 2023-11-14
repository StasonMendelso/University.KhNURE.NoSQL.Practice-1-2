package ua.nure.st.kpp.example.demo.dao;

import org.springframework.stereotype.Service;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.util.List;
@Service
public interface CompanyDAO {
    Company create(Company company) throws DAOException;
    List<Company> readAll() throws DAOException;
    boolean update(String id, Company company) throws DAOException;
    Company read(String id) throws DAOException;

    List<String> readAllAvailableId() throws DAOException;
}
