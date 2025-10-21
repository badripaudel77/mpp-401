package model;

// This class holds the information for Project and Client (many-to-many)

public class ProjectClient {
    private Long projectId;
    private Long clientId;

    public ProjectClient() {

    }

    public ProjectClient(Long projectId, Long clientId) {
        this.projectId = projectId;
        this.clientId = clientId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}

