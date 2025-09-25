package com.kubegenie.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class PodInfo {
    private String name;
    private String namespace;
    private String phase;
    private String nodeName;
    private List<String> containerImages;
    private Map<String, String> labels;
    private OffsetDateTime startTime;
    private List<String> conditions;
    private String qosClass;

    // getters/setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public List<String> getContainerImages() { return containerImages; }
    public void setContainerImages(List<String> containerImages) { this.containerImages = containerImages; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }
    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }
    public String getQosClass() { return qosClass; }
    public void setQosClass(String qosClass) { this.qosClass = qosClass; }
}
