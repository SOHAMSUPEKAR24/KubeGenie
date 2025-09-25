package com.kubegenie.model;

public class DeploymentInfo {
    private String name;
    private String namespace;
    private Integer replicas;
    private Integer availableReplicas;
    private Integer updatedReplicas;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public Integer getReplicas() { return replicas; }
    public void setReplicas(Integer replicas) { this.replicas = replicas; }
    public Integer getAvailableReplicas() { return availableReplicas; }
    public void setAvailableReplicas(Integer availableReplicas) { this.availableReplicas = availableReplicas; }
    public Integer getUpdatedReplicas() { return updatedReplicas; }
    public void setUpdatedReplicas(Integer updatedReplicas) { this.updatedReplicas = updatedReplicas; }
}
