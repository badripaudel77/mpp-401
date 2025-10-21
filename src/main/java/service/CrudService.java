package service;

import java.sql.SQLException;
import java.util.List;

// T class
// V ID
public interface CrudService<T, V> {
    // CREATE
    void add(T entity) throws Exception;

    // READ
    T findById(V id) throws SQLException;
    List<T> findAll() throws SQLException;

    // Update
    void update(T entity, V id) throws SQLException;

    // delete by ID
    void delete(V id) throws SQLException;
}
