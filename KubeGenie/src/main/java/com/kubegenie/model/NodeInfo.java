package com.kubegenie.model;

import java.util.List;
import java.util.Map;

public class NodeInfo {
    private String name;
    private String osImage;
    private String kubeletVersion;
    private Map<String, String> labels;
    private List<String> conditions;
    private String capacityCpu;
    private String capacityMem;
    private String allocatableCpu;
    private String allocatableMem;
    private String taints;

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOsImage() { return osImage; }
    public void setOsImage(String osImage) { this.osImage = osImage; }
    public String getKubeletVersion() { return kubeletVersion; }
    public void setKubeletVersion(String kubeletVersion) { this.kubeletVersion = kubeletVersion; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }
    public String getCapacityCpu() { return capacityCpu; }
    public void setCapacityCpu(String capacityCpu) { this.capacityCpu = capacityCpu; }
    public String getCapacityMem() { return capacityMem; }
    public void setCapacityMem(String capacityMem) { this.capacityMem = capacityMem; }
    public String getAllocatableCpu() { return allocatableCpu; }
    public void setAllocatableCpu(String allocatableCpu) { this.allocatableCpu = allocatableCpu; }
    public String getAllocatableMem() { return allocatableMem; }
    public void setAllocatableMem(String allocatableMem) { this.allocatableMem = allocatableMem; }
    public String getTaints() { return taints; }
    public void setTaints(String taints) { this.taints = taints; }
}
