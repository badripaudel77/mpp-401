package controller;

import model.Department;
import model.Employee;
import model.Project;
import service.impl.DepartmentService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class DepartmentController {

    private static final DepartmentService departmentService = new DepartmentService();
    private static final Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
        mainMenu();
    }

    public static void mainMenu() {
        while (true) {
            System.out.println("\n==== Department Management ====");
            System.out.println("1. Add Department");
            System.out.println("2. Update Department");
            System.out.println("3. Delete Department");
            System.out.println("4. View Department By ID");
            System.out.println("5. View All Departments");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            try {
                switch (choice) {
                    case 1 -> addDepartment();
                    case 2 -> updateDepartment();
                    case 3 -> deleteDepartment();
                    case 4 -> viewDepartmentById();
                    case 5 -> viewAllDepartments();
                    case 0 -> {
                        System.out.println("Exiting Department Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addDepartment() throws Exception {
        System.out.print("Enter department name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Enter annual budget: ");
        double budget = getDoubleInput();

        Department department = new Department();
        department.setName(name);
        department.setLocation(location);
        department.setAnnualBudget(budget);

        departmentService.add(department);
        System.out.println("Department added successfully!");
    }

    private static void updateDepartment() throws SQLException {
        System.out.print("Enter department ID to update: ");
        long id = getLongInput();

        Department existing = departmentService.findById(id);
        if (existing == null) {
            System.out.println("No department found with ID: " + id);
            return;
        }

        System.out.print("Enter new name (" + existing.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) existing.setName(name);

        System.out.print("Enter new location (" + existing.getLocation() + "): ");
        String location = scanner.nextLine().trim();
        if (!location.isEmpty()) existing.setLocation(location);

        System.out.print("Enter new annual budget (" + existing.getAnnualBudget() + "): ");
        String budgetInput = scanner.nextLine().trim();
        if (!budgetInput.isEmpty()) {
            existing.setAnnualBudget(Double.parseDouble(budgetInput));
        }
        departmentService.update(existing, id);
        System.out.println("Department updated successfully!");
    }

    private static void deleteDepartment() throws SQLException {
        System.out.print("Enter department ID to delete: ");
        long id = getLongInput();
        departmentService.delete(id);
        System.out.println("Department deleted successfully!");
    }

    private static void viewDepartmentById() throws SQLException {
        System.out.print("Enter department ID: ");
        long id = getLongInput();

        Department department = departmentService.findById(id);
        if (department == null) {
            System.out.println("No department found with ID: " + id);
            return;
        }

        System.out.println("\n--- Department Details ---");
        System.out.println("ID: " + department.getId());
        System.out.println("Name: " + department.getName());
        System.out.println("Location: " + department.getLocation());
        System.out.println("Annual Budget: " + department.getAnnualBudget());

        List<Employee> employees = department.getEmployees();
        List<Project> projects = department.getProjects();

        if (employees != null && !employees.isEmpty()) {
            System.out.println("\nEmployees:");
            employees.forEach(e -> System.out.println(" - " + e.getFullName()));
        } else {
            System.out.println("\nNo employees assigned.");
        }

        if (projects != null && !projects.isEmpty()) {
            System.out.println("\nProjects:");
            projects.forEach(p -> System.out.println(" - " + p.getName()));
        } else {
            System.out.println("\nNo projects assigned.");
        }
    }

    private static void viewAllDepartments() throws SQLException {
        List<Department> departments = departmentService.findAll();
        if (departments.isEmpty()) {
            System.out.println("No departments available.");
            return;
        }

        System.out.println("\n--- All Departments ---");
        departments.forEach(d ->
                System.out.printf("ID: %d | Name: %s | Location: %s | Budget: %.2f%n",
                        d.getId(), d.getName(), d.getLocation(), d.getAnnualBudget()));
    }

    // Utility Methods
    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
    }

    private static long getLongInput() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a valid ID: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Enter again: ");
            }
        }
    }
}
