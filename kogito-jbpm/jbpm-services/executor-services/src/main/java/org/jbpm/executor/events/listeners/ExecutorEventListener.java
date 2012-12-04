/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.events.listeners;

import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;

/**
 *
 * @author salaboy
 */
public interface ExecutorEventListener {
    public void onRequestPending(RequestInfo r);
    public void onRequestRunning(RequestInfo r);
    public void onRequestCancelled(RequestInfo r);
    public void onRequestOnError(RequestInfo r);
    public void onError(ErrorInfo e);
}
