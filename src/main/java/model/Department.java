package model;

import java.util.List;

public class Department {
    private Long id;
    private String name;
    private String location;
    private double annualBudget;

    private List<Employee> employees;
    private List<Project> projects;

    public Department() {
    }

    public Department(Long id, String name, String location, double annualBudget,
                      List<Employee> employees, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.annualBudget = annualBudget;
        this.employees = employees;
        this.projects = projects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAnnualBudget() {
        return annualBudget;
    }

    public void setAnnualBudget(double annualBudget) {
        this.annualBudget = annualBudget;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
