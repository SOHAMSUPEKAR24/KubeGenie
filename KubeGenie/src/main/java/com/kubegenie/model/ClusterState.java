package com.kubegenie.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ClusterState {
    private OffsetDateTime capturedAt;
    private List<PodInfo> pods;
    private List<NodeInfo> nodes;
    private List<DeploymentInfo> deployments;
    private List<EventInfo> recentEvents;

    // Optional resource usage (requires metrics-server)
    private Map<String, ResourceUsage> podUsageByKey;   // key: namespace/pod
    private Map<String, ResourceUsage> nodeUsageByName; // key: node name

    public OffsetDateTime getCapturedAt() { return capturedAt; }
    public void setCapturedAt(OffsetDateTime capturedAt) { this.capturedAt = capturedAt; }
    public List<PodInfo> getPods() { return pods; }
    public void setPods(List<PodInfo> pods) { this.pods = pods; }
    public List<NodeInfo> getNodes() { return nodes; }
    public void setNodes(List<NodeInfo> nodes) { this.nodes = nodes; }
    public List<DeploymentInfo> getDeployments() { return deployments; }
    public void setDeployments(List<DeploymentInfo> deployments) { this.deployments = deployments; }
    public List<EventInfo> getRecentEvents() { return recentEvents; }
    public void setRecentEvents(List<EventInfo> recentEvents) { this.recentEvents = recentEvents; }
    public Map<String, ResourceUsage> getPodUsageByKey() { return podUsageByKey; }
    public void setPodUsageByKey(Map<String, ResourceUsage> podUsageByKey) { this.podUsageByKey = podUsageByKey; }
    public Map<String, ResourceUsage> getNodeUsageByName() { return nodeUsageByName; }
    public void setNodeUsageByName(Map<String, ResourceUsage> nodeUsageByName) { this.nodeUsageByName = nodeUsageByName; }
}
