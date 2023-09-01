package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.List;


public interface Deadlines extends Externalizable {

    List<Deadline> getStartDeadlines();

    void setStartDeadlines(List<Deadline> startDeadlines);

    List<Deadline> getEndDeadlines();

    void setEndDeadlines(List<Deadline> endDeadlines);

}
