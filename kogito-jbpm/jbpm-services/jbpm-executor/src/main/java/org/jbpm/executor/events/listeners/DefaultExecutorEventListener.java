/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.events.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jbpm.executor.annotations.Cancelled;
import org.jbpm.executor.annotations.OnError;
import org.jbpm.executor.annotations.Pending;
import org.jbpm.executor.annotations.Running;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;


/**
 *
 */


public class DefaultExecutorEventListener extends JbpmServicesEventListener<RequestInfo> implements ExecutorEventListener{
    @Inject 
    private Logger logger;
    
    public DefaultExecutorEventListener() {
        
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    public void onRequestPending(@Observes(notifyObserver= Reception.ALWAYS) @Pending RequestInfo r) {
        logger.log(Level.INFO, " New Pending Request {0}", r.getId());
    }

    public void onRequestRunning(@Observes(notifyObserver= Reception.ALWAYS) @Running RequestInfo r) {
        logger.log(Level.INFO, " Request Running{0}", r.getId());
    }

    public void onRequestCancelled(@Observes(notifyObserver= Reception.ALWAYS) @Cancelled RequestInfo r) {
        logger.log(Level.INFO, " The Request {0} has being Cancelled", r.getId());
    }
//
//    public void onRequestOnError(@Observes(notifyObserver= Reception.ALWAYS) @OnError RequestInfo r) {
//        logger.log(Level.INFO, " The Request {0} reported an Error", r.getId());
//    }
//
//    public void onError(@Observes(notifyObserver= Reception.ALWAYS) @OnError ErrorInfo e) {
//        logger.log(Level.INFO, " The Request {0} reported an Error with id {1}", new Object[]{e.getRequestInfo().getId(), e.getId()});
//    }

   
}
