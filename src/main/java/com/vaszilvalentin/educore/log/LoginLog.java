package com.vaszilvalentin.educore.log;

import java.time.LocalDateTime;

/**
 * Represents a login or logout event by a user.
 */
public class LoginLog {
    private String id;
    private String userId;
    private LocalDateTime timestamp;
    private ActionType action;

    public enum ActionType {
        LOGIN,
        LOGOUT
    }

    public LoginLog(String id, String userId, LocalDateTime timestamp, ActionType action) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ActionType getAction() {
        return action;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "LoginLog{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", action=" + action +
                '}';
    }
}