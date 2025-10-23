package model;

public enum ProjectStatus {
    ACTIVE("active"),
    COMPLETED("completed"),
    PLANNED("planned");

    final String status;

    ProjectStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
