package org.kie.internal.task.api.model;

import java.util.Map;



public interface EmailNotification extends Notification {


    Map<? extends Language, ? extends EmailNotificationHeader> getEmailHeaders();

    void setEmailHeaders(Map<? extends Language, ? extends EmailNotificationHeader> emailHeaders);

}
