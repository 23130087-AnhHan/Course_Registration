package dao;

import java.sql.SQLException;
import java.util.List;

public interface IDao<T> {
    void insert(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(String id) throws SQLException;
    T getById(String id) throws SQLException;
    List<T> getAll() throws SQLException;
}
