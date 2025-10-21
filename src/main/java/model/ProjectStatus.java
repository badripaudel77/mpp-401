package model;

public enum ProjectStatus {
    ACTIVE("ongoing"),
    COMPLETED("finished");
    final String status;

    ProjectStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
