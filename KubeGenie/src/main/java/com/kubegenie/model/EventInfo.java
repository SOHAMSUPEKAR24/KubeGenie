package com.kubegenie.model;

import java.time.OffsetDateTime;

public class EventInfo {
    private String reason;
    private String type;
    private String message;
    private String involvedObject;
    private OffsetDateTime eventTime;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getInvolvedObject() { return involvedObject; }
    public void setInvolvedObject(String involvedObject) { this.involvedObject = involvedObject; }
    public OffsetDateTime getEventTime() { return eventTime; }
    public void setEventTime(OffsetDateTime eventTime) { this.eventTime = eventTime; }
}
