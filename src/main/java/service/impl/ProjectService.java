package service.impl;

import model.Project;
import repository.ProjectRepository;
import service.CrudService;

import java.sql.SQLException;
import java.util.List;

public class ProjectService implements CrudService<Project, Long> {
    private final ProjectRepository repository;

    public ProjectService() {
        this.repository = new ProjectRepository();
    }

    @Override
    public void add(Project entity) throws Exception {
        repository.insert(entity);
    }

    @Override
    public Project findById(Long id) throws SQLException {
        return repository.findById(id);
    }

    @Override
    public List<Project> findAll() throws SQLException {
        return repository.findAll();
    }

    @Override
    public void update(Project entity, Long id) throws SQLException {
        repository.update(entity, id);
    }

    @Override
    public void delete(Long id) throws SQLException {
        repository.delete(id);
    }
}
