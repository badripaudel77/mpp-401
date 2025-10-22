package model;

public class DepartmentProject {
    private int departmentId;
    private int projectId;

    public DepartmentProject() {}

    public DepartmentProject(int departmentId, int projectId) {
        this.departmentId = departmentId;
        this.projectId = projectId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "DepartmentProject{" +
                "departmentId=" + departmentId +
                ", projectId=" + projectId +
                '}';
    }
}
