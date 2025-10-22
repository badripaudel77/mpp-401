package service.impl;

import model.Department;
import repository.DepartmentRepository;
import service.CrudService;

import java.sql.SQLException;
import java.util.List;

public class DepartmentService implements CrudService<Department, Long> {
    private final DepartmentRepository repository;

    public DepartmentService() {
        this.repository = new DepartmentRepository();
    }

    @Override
    public void add(Department entity) throws Exception {
        if(entity.getName().isEmpty()) {
            throw new IllegalArgumentException("Name can't be empty.");
        }
        repository.insert(entity);
    }

    @Override
    public Department findById(Long id) throws SQLException {
        return repository.findById(id);
    }

    @Override
    public List<Department> findAll() throws SQLException {
        return repository.findAll();
    }

    @Override
    public void update(Department entity, Long id) throws SQLException {
        this.repository.update(entity, id);
    }

    @Override
    public void delete(Long id) throws SQLException {
        this.repository.delete(id);
    }
}
