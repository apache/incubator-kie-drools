/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.events.listeners;


import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;

import org.jbpm.executor.annotations.Cancelled;
import org.jbpm.executor.annotations.Pending;
import org.jbpm.executor.annotations.Running;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultExecutorEventListener extends JbpmServicesEventListener<RequestInfo> implements ExecutorEventListener{

    private static final Logger logger = LoggerFactory.getLogger(DefaultExecutorEventListener.class);
    
    public DefaultExecutorEventListener() {
        
    }

    public void onRequestPending(@Observes(notifyObserver= Reception.ALWAYS) @Pending RequestInfo r) {
        logger.info("New Pending Request {}", r.getId());
    }

    public void onRequestRunning(@Observes(notifyObserver= Reception.ALWAYS) @Running RequestInfo r) {
        logger.info(" Request Running {}", r.getId());
    }

    public void onRequestCancelled(@Observes(notifyObserver= Reception.ALWAYS) @Cancelled RequestInfo r) {
        logger.info("The Request {} has being Cancelled", r.getId());
    }
//
//    public void onRequestOnError(@Observes(notifyObserver= Reception.ALWAYS) @OnError RequestInfo r) {
//        logger.info("The Request {} reported an Error", r.getId());
//    }
//
//    public void onError(@Observes(notifyObserver= Reception.ALWAYS) @OnError ErrorInfo e) {
//        logger.info("The Request {} reported an Error with id {}", e.getRequestInfo().getId(), e.getId());
//    }

   
}
