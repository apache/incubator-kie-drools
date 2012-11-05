/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.PresentationElement;


/**
 * Experimental
 *  The Task Presentation Service is intended to handle
 *  all the informaiton related with the Task Rendering Phase.
 *  All the meta information about how to render a Task will be handled
 *  and stored by this service implementation.
 */
public interface TaskPresentationService {

    long addPresentationElement(long taskId, PresentationElement element);

    void removePresentationElement(long taskId, long elementId);

    List<PresentationElement> getPresentationElements(long taskId);
}
