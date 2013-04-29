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

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.api.Command;
import org.jbpm.executor.api.CommandCallback;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.ExecutionResults;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;

/**
 *
 * @author salaboy
 */
public class ExecutorRunnable implements Runnable {

    @Inject
    private Logger logger;
    @Inject
    private JbpmServicesPersistenceManager pm;
   
    //@Inject
    //private Event<RequestInfo> requestEvents;
    //@Inject
    //private Event<ErrorInfo> errorEvents;
    @Inject
    private ExecutorQueryService queryService;
    private final Map<String, Command> commandCache = new HashMap<String, Command>();
    private final Map<String, CommandCallback> callbackCache = new HashMap<String, CommandCallback>();

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setQueryService(ExecutorQueryService queryService) {
        this.queryService = queryService;
    }

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
                pm.merge(r);
                // CDI Contexts are not propagated to new threads
                //requestEvents.select(new AnnotationLiteral<Running>(){}).fire(r); 
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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable e) {
                e.printStackTrace();
                exception = e;
            }

            if (exception != null) {
                logger.log(Level.SEVERE, "{0} >>> Before - Error Handling!!!{1}", new Object[]{System.currentTimeMillis(), exception.getMessage()});



                ErrorInfo errorInfo = new ErrorInfo(exception.getMessage(), ExceptionUtils.getFullStackTrace(exception.fillInStackTrace()));
                errorInfo.setRequestInfo(r);
                // CDI Contexts are not propagated to new threads
                //requestEvents.select(new AnnotationLiteral<OnError>(){}).fire(r);
                //errorEvents.select(new AnnotationLiteral<OnError>(){}).fire(errorInfo);
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

                pm.merge(r);


                logger.severe(" >>> After - Error Handling!!!");


            } else {

                r.setStatus(STATUS.DONE);
                pm.merge(r);
                // CDI Contexts are not propagated to new threads
                //requestEvents.select(new AnnotationLiteral<Completed>(){}).fire(r);
            }
        }
    }

//    /*
//     * following are supporting methods to allow execution on application startup
//     * as at that time RequestScoped entity manager cannot be used so instead
//     * use EntityManagerFactory and manage transaction manually
//     */
//    protected EntityManager getEntityManager() {
//        try {
//            this.em.toString();          
//            return this.em;
//        } catch (ContextNotActiveException e) {
//            EntityManager em = this.emf.createEntityManager();
//            return em;
//        }
//    }
    private Command findCommand(String name) {
        synchronized (commandCache) {
            
                if (!commandCache.containsKey(name)) {
                    try {
                        Command commandInstance = (Command) Class.forName(name).newInstance();
                        commandCache.put(name, commandInstance);
                    } catch (Exception ex) {
                        logger.severe(" EEE: Unknown Command implemenation with name '" + name + "'");
                        throw new IllegalArgumentException("Unknown Command implemenation with name '" + name + "'");
                    }

                }

       
        }
        return commandCache.get(name);
    }

    private CommandCallback findCommandCallback(String name) {
        synchronized (callbackCache) {
            
                    if (!callbackCache.containsKey(name)) {
                        try {
                            CommandCallback commandCallbackInstance = (CommandCallback) Class.forName(name).newInstance();
                            callbackCache.put(name, commandCallbackInstance);
                        } catch (Exception ex) {
                            logger.severe(" EEE: Unknown CommandCallback implemenation with name '" + name + "'");
                            throw new IllegalArgumentException("Unknown Command implemenation with name '" + name + "'");
                        }

                    }

        }
        return callbackCache.get(name);
    }
}
