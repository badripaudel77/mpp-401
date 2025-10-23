package controller;

import model.Client;
import model.Department;
import model.Project;
import model.ProjectStatus;
import service.impl.ProjectService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ProjectController {

    private static final ProjectService projectService = new ProjectService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        mainMenu();
    }

    public static void mainMenu() {
        while (true) {
            System.out.println("\n==== Project Management ====");
            System.out.println("1. Add Project");
            System.out.println("2. Update Project");
            System.out.println("3. Delete Project");
            System.out.println("4. View Project By ID");
            System.out.println("5. View All Projects");
            System.out.println("6. Calculate Project HR cost");
            System.out.println("7. Retrieve Projects By Department");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            try {
                switch (choice) {
                    case 1 -> addProject();
                    case 2 -> updateProject();
                    case 3 -> deleteProject();
                    case 4 -> viewProjectById();
                    case 5 -> viewAllProjects();
                    case 6 -> calcProjectHRCost();
                    case 7 -> getProjectsByDepartment();
                    case 0 -> {
                        System.out.println("Exiting Project Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Try again.");
                }
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addProject() throws Exception {
        System.out.print("Enter project name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter description: ");
        String desc = scanner.nextLine().trim();

        System.out.print("Enter start date (yyyy-mm-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter end date (yyyy-mm-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter project budget: ");
        double budget = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter status (active/completed): ");
        String statusInput = scanner.nextLine().trim().toLowerCase();
        ProjectStatus status = statusInput.equals("completed") ? ProjectStatus.COMPLETED : ProjectStatus.ACTIVE;

        Project project = new Project();
        project.setName(name);
        project.setDescription(desc);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setBudget(budget);
        project.setStatus(status);

        projectService.add(project);
        System.out.println("Project added successfully!");
    }

    private static void updateProject() throws SQLException {
        System.out.print("Enter project ID to update: ");
        long id = getLongInput();

        Project existing = projectService.findById(id);
        if (existing == null) {
            System.out.println("No project found with ID: " + id);
            return;
        }

        System.out.print("Enter new name (" + existing.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) existing.setName(name);

        System.out.print("Enter new description (" + existing.getDescription() + "): ");
        String desc = scanner.nextLine().trim();
        if (!desc.isEmpty()) existing.setDescription(desc);

        System.out.print("Enter new start date (" + existing.getStartDate() + "): ");
        String startInput = scanner.nextLine().trim();
        if (!startInput.isEmpty()) existing.setStartDate(LocalDate.parse(startInput));

        System.out.print("Enter new end date (" + existing.getEndDate() + "): ");
        String endInput = scanner.nextLine().trim();
        if (!endInput.isEmpty()) existing.setEndDate(LocalDate.parse(endInput));

        System.out.print("Enter new budget (" + existing.getBudget() + "): ");
        String budgetInput = scanner.nextLine().trim();
        if (!budgetInput.isEmpty()) existing.setBudget(Double.parseDouble(budgetInput));

        System.out.print("Enter new status (" + existing.getStatus() + "): ");
        String statusInput = scanner.nextLine().trim().toLowerCase();
        if (!statusInput.isEmpty()) {
            existing.setStatus(statusInput.equals("completed") ? ProjectStatus.COMPLETED : ProjectStatus.ACTIVE);
        }
        projectService.update(existing, id);
        System.out.println("Project updated successfully!");
    }

    private static void deleteProject() throws SQLException {
        System.out.print("Enter project ID to delete: ");
        long id = getLongInput();
        projectService.delete(id);
        System.out.println("Project deleted successfully!");
    }

    private static void viewProjectById() throws SQLException {
        System.out.print("Enter project ID: ");
        long id = getLongInput();

        Project project = projectService.findById(id);
        if (project != null) {
            System.out.println("\n--- Project Details ---");
            System.out.println("ID: " + project.getId());
            System.out.println("Name: " + project.getName());
            System.out.println("Description: " + project.getDescription());
            System.out.println("Start Date: " + project.getStartDate());
            System.out.println("End Date: " + project.getEndDate());
            System.out.println("Budget: " + project.getBudget());
            System.out.println("Status: " + project.getStatus().getStatus());

            // Get associated clients
            List<Client> clients = project.getClients();
            if (clients != null && !clients.isEmpty()) {
                System.out.println("\n--- Associated Clients ---");
                clients.forEach(c -> System.out.println(
                        "Client ID: " + c.getId() +
                                " | Name: " + c.getName() +
                                " | Email: " + c.getContactEmail()
                ));
            }
            else {
                System.out.println("\nNo clients associated with this project.");
            }
            // Get associated departments
            List<Department> departments = project.getDepartments();
            if (departments != null && !departments.isEmpty()) {
                System.out.println("\n--- Associated Departments ---");
                departments.forEach(d -> System.out.println(
                        "Department ID: " + d.getId() +
                                " | Name: " + d.getName() +
                                " | Location: " + d.getLocation()
                ));
            }
            else {
                System.out.println("\nNo departments linked to this project.");
            }
        }
        else {
            System.out.println("No project found with ID: " + id);
        }
    }

    private static void viewAllProjects() throws SQLException {
        List<Project> projects = projectService.findAll();
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }
        System.out.println("\n--- All Projects ---");
        projects.forEach(p ->
                System.out.printf("ID: %d | Name: %s | Budget: %.2f | Status: %s | Duration: %s to %s%n",
                        p.getId(), p.getName(), p.getBudget(), p.getStatus().getStatus(),
                        p.getStartDate(), p.getEndDate()));
    }

    private static void calcProjectHRCost() throws SQLException {
        System.out.println("Enter project ID to calculate cost");
        int projectId = getIntInput();
        double totalCost = projectService.calculateProjectHRCost(projectId);
        System.out.println("Total HR cost for project " + projectId + " = " + totalCost);
    }

    private static void getProjectsByDepartment() throws SQLException {
        System.out.println("Enter the department ID ");
        var depId = getIntInput();
        System.out.println("Enter the sorting type (eg: name, description)");
        var sortBy = new Scanner(System.in).nextLine();
        List<Project> projectsByDepartment = projectService.getProjectsByDepartment(depId, sortBy);
        projectsByDepartment.stream()
                .filter(Objects::nonNull)
                .map(project -> String.format("ID: %d | %s | %s | %s | %s | %f | %s ",
                 project.getId(), project.getName(), project.getDescription(), project.getStartDate(),
                        project.getEndDate(), project.getBudget(), project.getStatus().name()))
                .forEach(System.out::println);
    }

    // Utility methods
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
}
