/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.PresentationElement;


/**
 *
 */
public interface TaskPresentationService {

    long addPresentationElement(long taskId, PresentationElement element);

    void removePresentationElement(long taskId, long elementId);

    List<PresentationElement> getPresentationElements(long taskId);
}
