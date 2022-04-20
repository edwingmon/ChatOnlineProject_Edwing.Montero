package com.example.damxat.Model;

public class PushNotification {
    private String tokken;
    private NotificationModel notification;


    public PushNotification(String tokken, NotificationModel notification) {
        this.tokken = tokken;
        this.notification = notification;
    }

    public String getTokken() {
        return tokken;
    }

    public void setTokken(String tokken) {
        this.tokken = tokken;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

}
