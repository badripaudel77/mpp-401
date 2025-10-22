package controller;

import model.Client;
import model.Project;
import service.impl.ClientService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ClientController {

    private static final ClientService clientService = new ClientService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        mainMenu();
    }

    public static void mainMenu() {
        while (true) {
            System.out.println("\n==== Client Management ====");
            System.out.println("1. Add Client");
            System.out.println("2. Update Client");
            System.out.println("3. Delete Client");
            System.out.println("4. View Client By ID");
            System.out.println("5. View All Clients");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            try {
                switch (choice) {
                    case 1 -> addClient();
                    case 2 -> updateClient();
                    case 3 -> deleteClient();
                    case 4 -> viewClientById();
                    case 5 -> viewAllClients();
                    case 0 -> {
                        System.out.println("Exiting Client Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addClient() throws Exception {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter industry: ");
        String industry = scanner.nextLine().trim();

        System.out.print("Enter contact name: ");
        String contactName = scanner.nextLine().trim();

        System.out.print("Enter contact phone: ");
        String contactPhone = scanner.nextLine().trim();

        System.out.print("Enter contact email: ");
        String contactEmail = scanner.nextLine().trim();

        Client client = new Client();
        client.setName(name);
        client.setIndustry(industry);
        client.setContactPerson(contactName);
        client.setContactPhone(contactPhone);
        client.setContactEmail(contactEmail);

        clientService.add(client);
        System.out.println("Client added successfully!");
    }

    private static void updateClient() throws SQLException {
        System.out.print("Enter client ID to update: ");
        long id = getLongInput();

        Client existing = clientService.findById(id);
        if (existing == null) {
            System.out.println("No client found with ID: " + id);
            return;
        }

        System.out.print("Enter new name (" + existing.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            existing.setName(name);
        }

        System.out.print("Enter new industry (" + existing.getIndustry() + "): ");
        String industry = scanner.nextLine().trim();
        if (!industry.isEmpty()) {
            existing.setIndustry(industry);
        }

        System.out.print("Enter new contact name (" + existing.getContactPerson() + "): ");
        String contactName = scanner.nextLine().trim();
        if (!contactName.isEmpty()) {
            existing.setContactPerson(contactName);
        }

        System.out.print("Enter new contact phone (" + existing.getContactPhone() + "): ");
        String contactPhone = scanner.nextLine().trim();
        if (!contactPhone.isEmpty()) {
            existing.setContactPhone(contactPhone);
        }

        System.out.print("Enter new contact email (" + existing.getContactEmail() + "): ");
        String contactEmail = scanner.nextLine().trim();
        if (!contactEmail.isEmpty()) {
            existing.setContactEmail(contactEmail);
        }
        clientService.update(existing, id);
        System.out.println("Client updated successfully!");
    }

    private static void deleteClient() throws SQLException {
        System.out.print("Enter client ID to delete: ");
        long id = getLongInput();
        clientService.delete(id);
        System.out.println("Client deleted successfully!");
    }

    private static void viewClientById() throws SQLException {
        System.out.print("Enter client ID: ");
        long id = getLongInput();

        Client client = clientService.findById(id);
        if (client == null) {
            System.out.println("No client found with ID: " + id);
            return;
        }

        System.out.println("\n--- Client Details ---");
        System.out.println("ID: " + client.getId());
        System.out.println("Name: " + client.getName());
        System.out.println("Industry: " + client.getIndustry());
        System.out.println("Contact Name: " + client.getContactPerson());
        System.out.println("Contact Phone: " + client.getContactPhone());
        System.out.println("Contact Email: " + client.getContactEmail());

        System.out.println("\nProjects:");
        Optional.ofNullable(client.getProjects()).orElse(Collections.emptyList())
                        .stream()
                        .map(Project::getName)
                        .forEach(p -> System.out.println("-" + p));
    }

    private static void viewAllClients() throws SQLException {
        List<Client> clients = clientService.findAll();
        if (clients.isEmpty()) {
            System.out.println("No clients available.");
            return;
        }

        System.out.println("\n--- All Clients ---");
        clients.forEach(c ->
                System.out.printf("ID: %d | Name: %s | Industry: %s | Contact Person : %s | Contact Phone: %s | Contact Email %s %n",
                        c.getId(), c.getName(), c.getIndustry(), c.getContactPerson(), c.getContactPhone(), c.getContactEmail()));
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
}