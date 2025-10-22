package controller;

import model.Project;
import service.impl.ProjectService;

import java.sql.SQLException;
import java.util.List;

public class ProjectController {
    public static void main(String[] args) throws SQLException {
        List<Project> projects = new ProjectService().getProjectsByDepartment(1, "description");
        projects.forEach(p-> System.out.println(p.getName() + " " + p.getDescription() + " " + p.getEndDate() + " " + p.getBudget()));
    }
}
