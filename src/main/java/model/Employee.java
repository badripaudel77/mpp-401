package model;

import java.time.LocalDate;
import java.util.List;

public class Employee {
    private int id;
    private String email;
    private String fullName;
    private String title;
    private LocalDate hireDate;
    private double salary;
    //Crucially every single member of the workforce must be permanently assigned to one, and only one, organizational
    //unit.
    private Department department;
    private List<ProjectAllocation> projects;  // Many-to-Many with extra field (allocation)

    // Constructors
    public Employee() {}

    public Employee(int id, String fullName, String email, String title,
                    LocalDate hireDate, double salary) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.title = title;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<ProjectAllocation> getProjects() { return projects; }
    public void setProjects(List<ProjectAllocation> projects) { this.projects = projects; }
}
