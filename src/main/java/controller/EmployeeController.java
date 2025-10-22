package controller;

import model.Employee;
import model.Department;
import service.impl.EmployeeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EmployeeController {
     public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        EmployeeService service = new EmployeeService();

        while (true) {
            System.out.println("\n--- Employee Menu ---");
            System.out.println("1. Add Employee");
            System.out.println("2. List All Employees");
            System.out.println("3. Find Employee by ID");
            System.out.println("4. Delete Employee by ID");
            System.out.println("5. Transfer Employee to another department");
            System.out.println("6. Exit");
            System.out.print("Select option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Full Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Hire Date (yyyy-mm-dd): ");
                        LocalDate hireDate = LocalDate.parse(scanner.nextLine());
                        System.out.print("Salary: ");
                        double salary = Double.parseDouble(scanner.nextLine());

                        // For simplicity: input department manually (there is no UI)
                        System.out.print("Department ID: ");
                        int deptId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Department Name: ");
                        String deptName = scanner.nextLine();
                        Department dept = new Department();
                        dept.setId(deptId);
                        dept.setName(deptName);

                        Employee emp = new Employee();
                        emp.setFullName(name);
                        emp.setEmail(email);
                        emp.setTitle(title);
                        emp.setHireDate(hireDate);
                        emp.setSalary(salary);
                        emp.setDepartment(dept);

                        service.add(emp);
                        System.out.println("Employee added successfully!");
                        break;

                    case 2:
                        List<Employee> employees = service.findAll();
                        System.out.println("\nAll Employees:");
                        employees
                                .forEach(e -> System.out.println(
                                        e.getId() + " | " +
                                                e.getFullName() + " | " +
                                                e.getEmail() + " | " +
                                                e.getTitle() + " | " +
                                                e.getSalary() + " | " +
                                                (e.getDepartment() != null ? e.getDepartment().getId() : "N/A")
                                ));
                        break;

                    case 3:
                        System.out.print("Enter Employee ID: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        Employee found = service.findById(id);
                        Optional.ofNullable(found)
                                .ifPresentOrElse(
                                        e -> System.out.println(
                                                e.getId() + " | " +
                                                        e.getFullName() + " | " +
                                                        e.getEmail() + " | " +
                                                        e.getTitle() + " | " +
                                                        e.getSalary() + " | " +
                                                        Optional.ofNullable(e.getDepartment())
                                                                .map(Department::getId)
                                                                .orElse(-111)
                                        ),
                                        () -> System.out.println("Employee not found.")
                                );
                        break;

                    case 4:
                        System.out.print("Enter Employee ID: ");
                        int empId = Integer.parseInt(scanner.nextLine());
                        if (empId >= 0) {
                            service.delete(empId);
                        }
                        else {
                            System.out.println("Invalid ID");
                        }
                        break;

                    case 5:
                        System.out.print("Enter Employee ID to transfer: ");
                        int eId = Integer.parseInt(scanner.nextLine());

                        System.out.print("Enter Department ID to transfer to: ");
                        int dId = Integer.parseInt(scanner.nextLine());
                        if (eId >= 0 && dId>=0) {
                            boolean success = service.transferEmployeeToDepartment(eId, dId);
                            if (success) {
                                System.out.println("Employee transferred to another department");
                            } else {
                                System.out.println("Something went wrong, couldn't transfer employee : " + eId);
                            }
                        }
                        else {
                            System.out.println("Invalid IDs");
                        }
                        break;

                    case 6:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
