package ua.nure.st.kpp.example.demo.dao;

import org.springframework.stereotype.Service;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.util.List;
@Service
public interface CompanyDAO {
    boolean create(Company company) throws DAOException;
    List<Company> readAll() throws DAOException;
    boolean update(int id, Company company) throws DAOException;
    Company read(int id) throws DAOException;

    List<Integer> readAllAvailableId() throws DAOException;
}
