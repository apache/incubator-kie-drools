/**
 *
 */
package org.kie.internal.task.api.model;

import java.io.Externalizable;

public interface EmailNotificationHeader extends Externalizable {

    long getId();

    void setId(long id);

    String getLanguage();

    void setLanguage(String language);

    String getSubject();

    void setSubject(String subject);

    String getBody();

    void setBody(String body);

    String getReplyTo();

    void setReplyTo(String replyTo);

    String getFrom();

    void setFrom(String from);

}