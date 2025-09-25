package com.kubegenie.payload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryResponse {
    private String aiExplanation;
    private List<String> suggestedKubectlCommands = new ArrayList<>();
    private List<String> optimizationAdvice = new ArrayList<>();
    private Map<String, Object> clusterSummary; // small digest of the snapshot

    public QueryResponse() {}

    public String getAiExplanation() {
        return aiExplanation;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
    }

    public List<String> getSuggestedKubectlCommands() {
        return suggestedKubectlCommands;
    }

    public void setSuggestedKubectlCommands(List<String> suggestedKubectlCommands) {
        this.suggestedKubectlCommands = suggestedKubectlCommands;
    }

    public List<String> getOptimizationAdvice() {
        return optimizationAdvice;
    }

    public void setOptimizationAdvice(List<String> optimizationAdvice) {
        this.optimizationAdvice = optimizationAdvice;
    }

    public Map<String, Object> getClusterSummary() {
        return clusterSummary;
    }

    public void setClusterSummary(Map<String, Object> clusterSummary) {
        this.clusterSummary = clusterSummary;
    }
}
