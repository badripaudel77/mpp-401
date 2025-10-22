package service.impl;

import model.Employee;
import repository.EmployeeRepository;
import service.CrudService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService implements CrudService<Employee, Integer> {
    private final EmployeeRepository repository;

    public EmployeeService() {
        this.repository = new EmployeeRepository();
    }

    @Override
    public void add(Employee employee) throws Exception {
        if (employee.getSalary() < 0) {
            throw new Exception("Salary cannot be negative");
        }
        this.repository.insert(employee);
    }

    @Override
    public void update(Employee employee, Integer id) throws SQLException {
        this.repository.update(employee, id);
    }

    @Override
    public void delete(Integer id) throws SQLException {
        this.repository.delete(id);
    }

    @Override
    public Employee findById(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        return this.repository.findAll();
    }

    public List<Employee> findByDepartment(Long id) throws SQLException {
        return this.repository.findByDepartment(id);
    }

}
