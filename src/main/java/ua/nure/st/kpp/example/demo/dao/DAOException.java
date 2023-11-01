package ua.nure.st.kpp.example.demo.dao;

import java.sql.SQLException;
public class DAOException extends SQLException {
    public DAOException(Throwable exception){
        super(exception);
    }

    @Override
    public String toString() {
        return "DBException{" +
                super.toString()+
                "}";
    }
}
