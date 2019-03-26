/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.audit;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.audit.jms.AsyncTaskLifeCycleEventProducer;

public class TaskAuditLoggerFactory {

    /**
     * Creates new instance of JPA task audit logger
     * @return new instance of JPA task audit logger
     */
    public static JPATaskLifeCycleEventListener newJPAInstance() {
        return new JPATaskLifeCycleEventListener(true);
    }
    
    /**
     * Creates new instance of JPA task audit logger with given entity manager factory
     * @param enf EntityManagerFactory instance to be used
     * @return new instance of JPA task audit logger
     */
    public static JPATaskLifeCycleEventListener newJPAInstance(EntityManagerFactory emf) {
        return new JPATaskLifeCycleEventListener(emf);
    }
    
    /**
     * Creates new instance of JMS task audit logger based on given parameters.
     * Supported parameters are as follows:
     * <ul>
     * <li>jbpm.audit.jms.transacted - determines if JMS session is transacted or not - default true - type Boolean</li>
     * <li>jbpm.audit.jms.connection.factory - connection factory instance - type javax.jms.ConnectionFactory</li>
     * <li>jbpm.audit.jms.queue - JMS queue instance - type javax.jms.Queue</li>
     * <li>jbpm.audit.jms.connection.factory.jndi - JNDI name of the connection factory to look up - type String</li>
     * <li>jbpm.audit.jms.queue.jndi - JNDI name of the queue to look up - type String</li>
     * </ul>
     * @param properties - optional properties for the type of logger to initialize it
     * @return new instance of JMS task audit logger
     */
    public static AsyncTaskLifeCycleEventProducer newJMSInstance(Map<String, Object> properties) {
        AsyncTaskLifeCycleEventProducer logger = new AsyncTaskLifeCycleEventProducer();
        boolean transacted = true;
        if (properties.containsKey("jbpm.audit.jms.transacted")) {
            Object transactedObj = properties.get("jbpm.audit.jms.transacted");
            if (transactedObj instanceof Boolean) {
                transacted = (Boolean) properties.get("jbpm.audit.jms.transacted");
            } else {
                transacted = Boolean.parseBoolean(transactedObj.toString());
            }
        }
        
        logger.setTransacted(transacted);
        
        // set connection factory and queue if given as property
        if (properties.containsKey("jbpm.audit.jms.connection.factory")) {
            ConnectionFactory connFactory = (ConnectionFactory) properties.get("jbpm.audit.jms.connection.factory"); 
            logger.setConnectionFactory(connFactory);
        }                
        if (properties.containsKey("jbpm.audit.jms.queue")) {
            Queue queue = (Queue) properties.get("jbpm.audit.jms.queue"); 
            logger.setQueue(queue);
        }
        try {
            // look up connection factory and queue if given as property
            if (properties.containsKey("jbpm.audit.jms.connection.factory.jndi")) {
                ConnectionFactory connFactory = (ConnectionFactory) InitialContext.doLookup(
                        (String)properties.get("jbpm.audit.jms.connection.factory.jndi")); 
                logger.setConnectionFactory(connFactory);
            }                
            if (properties.containsKey("jbpm.audit.jms.queue.jndi")) {
                Queue queue = (Queue) InitialContext.doLookup((String)properties.get("jbpm.audit.jms.queue.jndi")); 
               logger.setQueue(queue);
            }
        } catch (NamingException e) {
            throw new RuntimeException("Error when looking up ConnectionFactory/Queue", e);
        }
        
        return logger;
    }
    
    /**
     * Creates new instance of JMS task audit logger based on given connection factory and queue.
     * @param transacted determines if JMS session is transacted or not
     * @param connFactory connection factory instance
     * @param queue JMS queue instance
     * @return new instance of JMS task audit logger
     */
    public static AsyncTaskLifeCycleEventProducer newJMSInstance(boolean transacted, ConnectionFactory connFactory, Queue queue) {
        AsyncTaskLifeCycleEventProducer logger = new AsyncTaskLifeCycleEventProducer();
        logger.setTransacted(transacted);
        logger.setConnectionFactory(connFactory);
        logger.setQueue(queue);
        
        return logger;
    }
}
