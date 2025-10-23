package controller;

import model.Project;
import model.Project;
import model.ProjectStatus;
import service.impl.ProjectService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ProjectController {

    private static final ProjectService projectService = new ProjectService();

    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) throws SQLException {
//        List<Project> projects = new ProjectService().getProjectsByDepartment(1, "description");
//        projects.forEach(p -> System.out.println(p.getName() + " " + p.getDescription() + " " + p.getEndDate() + " " + p.getBudget()));

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
                System.out.println("6. View Projects By Status");
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
                        case 6 -> viewProjectsByStatus();
                        case 0 -> {
                            System.out.println("Exiting Project Menu...");
                            return;
                        }
                        default -> System.out.println("Invalid choice! Try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        // ------------------- CRUD METHODS -------------------

        private static void addProject() throws Exception {
            System.out.print("Enter project name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter start date (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Enter end date (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Enter budget: ");
            double budget = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter status: ");
            ProjectStatus status = ProjectStatus.valueOf(scanner.nextLine().trim().toUpperCase());

            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
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
            String description = scanner.nextLine().trim();
            if (!description.isEmpty()) existing.setDescription(description);

            System.out.print("Enter new start date (" + existing.getStartDate() + "): ");
            String start = scanner.nextLine().trim();
            if (!start.isEmpty()) existing.setStartDate(LocalDate.parse(start));

            System.out.print("Enter new end date (" + existing.getEndDate() + "): ");
            String end = scanner.nextLine().trim();
            if (!end.isEmpty()) existing.setEndDate(LocalDate.parse(end));

            System.out.print("Enter new budget (" + existing.getBudget() + "): ");
            String budgetInput = scanner.nextLine().trim();
            if (!budgetInput.isEmpty()) existing.setBudget(Double.parseDouble(budgetInput));

            System.out.print("Enter new status (" + existing.getStatus() + "): ");
            String statusInput = scanner.nextLine().trim();
            if (!statusInput.isEmpty())
                existing.setStatus(ProjectStatus.valueOf(statusInput.toUpperCase()));

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
            if (project == null) {
                System.out.println("No project found with ID: " + id);
                return;
            }

            System.out.println("\n--- Project Details ---");
            System.out.println("ID: " + project.getId());
            System.out.println("Name: " + project.getName());
            System.out.println("Description: " + project.getDescription());
            System.out.println("Start Date: " + project.getStartDate());
            System.out.println("End Date: " + project.getEndDate());
            System.out.println("Budget: $" + project.getBudget());
            System.out.println("Status: " + project.getStatus());

            System.out.println("\nDepartments:");
            Optional.ofNullable(project.getDepartments())
                    .orElse(Collections.emptyList())
                    .forEach(d -> System.out.println("- " + d.getName()));

            System.out.println("\nClients:");
            Optional.ofNullable(project.getClients())
                    .orElse(Collections.emptyList())
                    .forEach(c -> System.out.println("- " + c.getName()));

            System.out.println("\nWorkers:");
            Optional.ofNullable(project.getWorkers())
                    .orElse(Collections.emptyList())
                    .forEach(a -> System.out.println("- " + a.getEmployee().getFullName()
                            + " (Hours: " + a.getAllocationPercentage() + ")"));
        }

        private static void viewAllProjects() throws SQLException {
            List<Project> projects = projectService.findAll();
            if (projects.isEmpty()) {
                System.out.println("No projects available.");
                return;
            }

            System.out.println("\n--- All Projects ---");
            projects.forEach(p ->
                    System.out.printf("ID: %d | Name: %s | Status: %s | Start: %s | End: %s | Budget: %.2f%n",
                            p.getId(), p.getName(), p.getStatus(),
                            p.getStartDate(), p.getEndDate(), p.getBudget()));
        }

        private static void viewProjectsByStatus() throws SQLException {
            System.out.print("Enter status to filter by (ACTIVE, COMPLETED, etc.): ");
            String input = scanner.nextLine().trim().toUpperCase();
            ProjectStatus status = ProjectStatus.valueOf(input);

            List<Project> projects = projectService.findAll().stream()
                    .filter(project -> project.getStatus().equals(status))
                            .toList();

            if (projects.isEmpty()) {
                System.out.println("No projects found with status: " + status);
                return;
            }

            System.out.println("\n--- Projects with Status: " + status + " ---");
            projects.forEach(p ->
                    System.out.printf("ID: %d | Name: %s | Budget: %.2f | Start: %s | End: %s%n",
                            p.getId(), p.getName(), p.getBudget(),
                            p.getStartDate(), p.getEndDate()));
        }

        // ------------------- UTILITY METHODS -------------------

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


