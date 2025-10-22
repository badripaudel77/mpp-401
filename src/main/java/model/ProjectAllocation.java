package model;

public class ProjectAllocation {
    private Employee employee;
    private Project project;
    private double allocationPercentage;

    // Constructors
    public ProjectAllocation() {}

    public ProjectAllocation(Employee employee, Project project, double allocationPercentage) {
        this.employee = employee;
        this.project = project;
        this.allocationPercentage = allocationPercentage;
    }

    // Getters and Setters
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public double getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(double allocationPercentage) { this.allocationPercentage = allocationPercentage; }
}
