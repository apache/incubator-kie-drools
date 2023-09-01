package org.drools.quarkus.quickstart.test.model;

import java.util.Objects;

public class Alert {
    private final String notification;

    public Alert(String notification) {
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notification);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Alert other = (Alert) obj;
        return Objects.equals(notification, other.notification);
    }

    @Override
    public String toString() {
        return "Alert [notification=" + notification + "]";
    }
}
