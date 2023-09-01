package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.List;


public interface Escalation extends Externalizable {

    long getId();

    void setId(long id);

    String getName();

    void setName(String name);

    List<BooleanExpression> getConstraints();

    void setConstraints(List<BooleanExpression> constraints);

    List<Notification> getNotifications();

    void setNotifications(List<Notification> notifications);

    List<Reassignment> getReassignments();

    void setReassignments(List<Reassignment> reassignments);
}
