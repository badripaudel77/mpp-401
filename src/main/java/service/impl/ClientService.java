package service.impl;

import model.Client;
import repository.ClientRepository;
import service.CrudService;

import java.sql.SQLException;
import java.util.List;

public class ClientService implements CrudService<Client, Long> {
    private final ClientRepository repository;

    public ClientService() {
        this.repository = new ClientRepository();
    }

    @Override
    public void add(Client entity) throws Exception {
        repository.insert(entity);
    }

    @Override
    public Client findById(Long id) throws SQLException {
        return repository.findById(id);
    }

    @Override
    public List<Client> findAll() throws SQLException {
        return repository.findAll();
    }

    @Override
    public void update(Client entity, Long id) throws SQLException {
        repository.update(entity, id);
    }

    @Override
    public void delete(Long id) throws SQLException {
        repository.delete(id);
    }
}
