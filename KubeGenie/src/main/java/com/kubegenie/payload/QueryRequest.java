package com.kubegenie.payload;

import jakarta.validation.constraints.NotBlank;

public class QueryRequest {
    @NotBlank
    private String query;

    public QueryRequest() {}

    public QueryRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
