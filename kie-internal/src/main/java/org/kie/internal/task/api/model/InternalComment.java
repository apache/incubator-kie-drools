package org.kie.internal.task.api.model;

import java.util.Date;

import org.kie.api.task.model.Comment;
import org.kie.api.task.model.User;


public interface InternalComment extends Comment {

    void setId(long id);

    void setText(String text);

    void setAddedAt(Date addedDate);

    void setAddedBy(User addedBy);

}
