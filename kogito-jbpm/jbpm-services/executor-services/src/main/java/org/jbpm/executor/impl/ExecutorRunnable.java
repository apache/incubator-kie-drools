/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.annotations.Completed;
import org.jbpm.executor.annotations.OnError;
import org.jbpm.executor.annotations.Running;
import org.jbpm.executor.api.Command;
import org.jbpm.executor.api.CommandCallback;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.ExecutionResults;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;

/**
 *
 * @author salaboy
 */
public class ExecutorRunnable implements Runnable {
    @Inject 
    private Logger logger;
    @Inject
    private EntityManager em;
    @Inject
    private BeanManager beanManager;
    @Inject
    private Event<RequestInfo> requestEvents;
    @Inject
    private Event<ErrorInfo> errorEvents;
    @Inject 
    private ExecutorQueryService queryService;
    
    private final Map<String, Command> commandCache = new HashMap<String, Command>();
    private final Map<String, CommandCallback> callbackCache = new HashMap<String, CommandCallback>();

    @Transactional
    public void run() {
        logger.log(Level.INFO, " >>> Executor Thread {0} Waking Up!!!", this.toString());
        List<?> resultList = queryService.getPendingRequests();
        logger.log(Level.INFO, " >>> Pending Requests = {0}", resultList.size());
        if (resultList.size() > 0) {
            RequestInfo r = null;
            Throwable exception = null;
            try {
                r = (RequestInfo) resultList.get(0);
                r.setStatus(STATUS.RUNNING);
                em.merge(r);
                requestEvents.select(new AnnotationLiteral<Running>(){}).fire(r);
                logger.log(Level.INFO, " >> Processing Request Id: {0}", r.getId());
                logger.log(Level.INFO, " >> Request Status ={0}", r.getStatus());
                logger.log(Level.INFO, " >> Command Name to execute = {0}", r.getCommandName());


                Command cmd = this.findCommand(r.getCommandName());

                CommandContext ctx = null;
                byte[] reqData = r.getRequestData();
                if (reqData != null) {
                    try {
                        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(reqData));
                        ctx = (CommandContext) in.readObject();
                    } catch (IOException e) {
                        ctx = null;
                        e.printStackTrace();
                    }
                }
                ExecutionResults results = cmd.execute(ctx);
                if (ctx != null && ctx.getData("callbacks") != null) {
                    logger.log(Level.INFO, " ### Callback: {0}", ctx.getData("callbacks"));
                    String[] callbacksArray = ((String) ctx.getData("callbacks")).split(",");;
                    List<String> callbacks = (List<String>) Arrays.asList(callbacksArray);
                    for (String callbackName : callbacks) {
                        CommandCallback handler = this.findCommandCallback(callbackName);
                        handler.onCommandDone(ctx, results);
                    }
                } else {
                    logger.info(" ### Callbacks: NULL");
                }
                if (results != null) {
                    try {
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(bout);
                        out.writeObject(results);
                        byte[] respData = bout.toByteArray();
                        r.setResponseData(respData);
                    } catch (IOException e) {
                        r.setResponseData(null);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }
            if (exception != null) {
                logger.log(Level.SEVERE, "{0} >>> Before - Error Handling!!!{1}", new Object[]{System.currentTimeMillis(), exception.getMessage()});
                

                
                ErrorInfo errorInfo = new ErrorInfo(exception.getMessage(), ExceptionUtils.getFullStackTrace(exception.fillInStackTrace()));
                errorInfo.setRequestInfo(r);
                requestEvents.select(new AnnotationLiteral<OnError>(){}).fire(r);
                errorEvents.select(new AnnotationLiteral<OnError>(){}).fire(errorInfo);
                r.getErrorInfo().add(errorInfo);
                logger.log(Level.SEVERE, " >>> Error Number: {0}", r.getErrorInfo().size());
                if (r.getRetries() > 0) {
                    r.setStatus(STATUS.RETRYING);
                    r.setRetries(r.getRetries() - 1);
                    r.setExecutions(r.getExecutions() + 1);
                    logger.log(Level.SEVERE, " >>> Retrying ({0}) still available!", r.getRetries());
                } else {
                    logger.severe(" >>> Error no retries left!");
                    r.setStatus(STATUS.ERROR);
                    r.setExecutions(r.getExecutions() + 1);
                }

                em.merge(r);


                logger.severe(" >>> After - Error Handling!!!");


            } else {
                
                r.setStatus(STATUS.DONE);
                em.merge(r);
                requestEvents.select(new AnnotationLiteral<Completed>(){}).fire(r);
            }
        }
    }

    private Command findCommand(String name) {

        synchronized (commandCache) {
            if (!commandCache.containsKey(name)) {
                Set<Bean<?>> beans = beanManager.getBeans(name);
                if (!beans.iterator().hasNext()){
                    throw new IllegalArgumentException("Unknown Command implemenation with name '"+name+"'");
                }
                Bean<?> bean = beans.iterator().next();
                commandCache.put(name, (Command) beanManager.getReference(bean, Command.class, beanManager.createCreationalContext(bean)));
            }
        }
        
        return commandCache.get(name);
    }
    
    private CommandCallback findCommandCallback(String name) {

        synchronized (callbackCache) {
            if (!callbackCache.containsKey(name)) {
                Set<Bean<?>> beans = beanManager.getBeans(name);
                if (!beans.iterator().hasNext()){
                    throw new IllegalArgumentException("Unknown CommandCallback implemenation with name '"+name+"'");
                }
                Bean<?> bean = beans.iterator().next();
                callbackCache.put(name, (CommandCallback) beanManager.getReference(bean, CommandCallback.class, beanManager.createCreationalContext(bean)));
            }
        }
        
        return callbackCache.get(name);
    }
}
