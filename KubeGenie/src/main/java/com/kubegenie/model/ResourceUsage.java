package com.kubegenie.model;

public class ResourceUsage {
    // Raw strings (e.g., "120m", "256Mi") to avoid unit conversion surprises.
    private String cpu;
    private String memory;

    public ResourceUsage() {}
    public ResourceUsage(String cpu, String memory) {
        this.cpu = cpu; this.memory = memory;
    }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
}
