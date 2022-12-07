package com.capstone.lifesabit.gateguard.notifications;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    UUID notificationID;
    UUID passID;
    UUID userID;
    NotificationType type;
    String title;
    String description;
    LocalDateTime timestamp;

    public Notification(UUID notificationID, UUID passID, UUID userID, NotificationType type, String title, String description, LocalDateTime timestamp) {
        this.notificationID = notificationID;
        this.passID = passID;
        this.userID = userID;
        this.type = type;
        this.title = title;
        this.description = description;
        // TODO Should timestamp be set to now?
        this.timestamp = timestamp;
    }

    public Notification(String title, UUID passID, UUID userID, String description, LocalDateTime timestamp) {
        notificationID = UUID.randomUUID();
        this.passID = passID;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public UUID getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(UUID notificationID) {
        this.notificationID = notificationID;
    }

    public UUID getPassID() {
        return passID;
    }

    public void setPassID(UUID passID) {
        this.passID = passID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
