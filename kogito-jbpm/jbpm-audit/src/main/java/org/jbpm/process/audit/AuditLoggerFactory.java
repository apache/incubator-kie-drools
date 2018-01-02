/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.audit;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jbpm.process.audit.jms.AsyncAuditLogProducer;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

/**
 * Factory for producing support audit loggers. Currently two types are available:
 * <ul>
 *  <li>JPA - synchronous logger that is bound to the engine transaction and 
 *  persists audit events as part of runtime engine transaction</li>
 *  <li>JMS - asynchronous logger that can be configured to place messages on the queue
 *  either with respect to active transaction (only after transaction is committed) or 
 *  place them directly as they are generated</li>
 * </ul>
 */
public class AuditLoggerFactory {

    public enum Type {
        JPA,
        JMS
    }
    
    /**
     * Creates new instance of audit logger based on given type and parameters and 
     * registers it directly in given ksession to receive its events.
     * Depending on the types several properties are supported:
     * <bold>JPA</bold>
     * No properties are supported
     * 
     * <bold>JMS</bold>
     * <ul>
     * <li>jbpm.audit.jms.transacted - determines if JMS session is transacted or not - default true - type Boolean</li>
     * <li>jbpm.audit.jms.connection.factory - connection factory instance - type javax.jms.ConnectionFactory</li>
     * <li>jbpm.audit.jms.queue - JMS queue instance - type javax.jms.Queue</li>
     * <li>jbpm.audit.jms.connection.factory.jndi - JNDI name of the connection factory to look up - type String</li>
     * <li>jbpm.audit.jms.queue.jndi - JNDI name of the queue to look up - type String</li>
     * </ul>
     * @param type - type of the AuditLoger to create (JPA or JMS)
     * @param ksession - ksession that the logger will be attached to
     * @param properties - optional properties for the type of logger to initialize it
     * @return new instance of AbstractAuditLogger
     */
    public static AbstractAuditLogger newInstance(Type type, KieSession ksession, Map<String, Object> properties) {
        AbstractAuditLogger logger = null;
        switch (type) {
            case JPA:
                logger = new JPAWorkingMemoryDbLogger(ksession);
                break;
            case JMS:
                boolean transacted = true;
                if (properties.containsKey("jbpm.audit.jms.transacted")) {
                    transacted = (Boolean) properties.get("jbpm.audit.jms.transacted");
                }
                
                logger = new AsyncAuditLogProducer(ksession, transacted);
                // set connection factory and queue if given as property
                if (properties.containsKey("jbpm.audit.jms.connection.factory")) {
                    ConnectionFactory connFactory = (ConnectionFactory) properties.get("jbpm.audit.jms.connection.factory"); 
                    ((AsyncAuditLogProducer) logger).setConnectionFactory(connFactory);
                }                
                if (properties.containsKey("jbpm.audit.jms.queue")) {
                    Queue queue = (Queue) properties.get("jbpm.audit.jms.queue"); 
                    ((AsyncAuditLogProducer) logger).setQueue(queue);
                }
                try {
                    // look up connection factory and queue if given as property
                    if (properties.containsKey("jbpm.audit.jms.connection.factory.jndi")) {
                        ConnectionFactory connFactory = (ConnectionFactory) InitialContext.doLookup(
                                (String)properties.get(properties.get("jbpm.audit.jms.connection.factory.jndi"))); 
                        ((AsyncAuditLogProducer) logger).setConnectionFactory(connFactory);
                    }                
                    if (properties.containsKey("jbpm.audit.jms.queue.jndi")) {
                        Queue queue = (Queue) InitialContext.doLookup((String)properties.get("jbpm.audit.jms.queue.jndi")); 
                        ((AsyncAuditLogProducer) logger).setQueue(queue);
                    }
                } catch (NamingException e) {
                    throw new RuntimeException("Error when looking up ConnectionFactory/Queue", e);
                }
                break;
            default:
                break;
        }
        
        return logger;
    }
    
    /**
     * Creates new instance of JPA audit logger
     * NOTE: this will build the logger but it is not registered directly on a session: once received, 
     * it will need to be registered as an event listener
     * @return new instance of JPA audit logger
     */
    public static AbstractAuditLogger newJPAInstance() {
        return new JPAWorkingMemoryDbLogger();
    }
    
    /**
     * Creates new instance of JPA audit logger with given Environment
     * NOTE: this will build the logger but it is not registered directly on a session: once received, 
     * it will need to be registered as an event listener
     * @param env Environment instance to be used
     * @return new instance of JPA audit logger
     */
    public static AbstractAuditLogger newJPAInstance(Environment env) {
        return new JPAWorkingMemoryDbLogger(env);
    }
    
    /**
     * Creates new instance of JMS audit logger based on given parameters.
     * Supported parameters are as follows:
     * <ul>
     * <li>jbpm.audit.jms.transacted - determines if JMS session is transacted or not - default true - type Boolean</li>
     * <li>jbpm.audit.jms.connection.factory - connection factory instance - type javax.jms.ConnectionFactory</li>
     * <li>jbpm.audit.jms.queue - JMS queue instance - type javax.jms.Queue</li>
     * <li>jbpm.audit.jms.connection.factory.jndi - JNDI name of the connection factory to look up - type String</li>
     * <li>jbpm.audit.jms.queue.jndi - JNDI name of the queue to look up - type String</li>
     * </ul>
     * NOTE: this will build the logger but it is not registered directly on a session: once received, 
     * it will need to be registered as an event listener
     * @param properties - optional properties for the type of logger to initialize it
     * @return new instance of JMS audit logger
     */
    public static AbstractAuditLogger newJMSInstance(Map<String, Object> properties) {
        AsyncAuditLogProducer logger = new AsyncAuditLogProducer();
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
     * Creates new instance of JMS audit logger based on given connection factory and queue.
     * NOTE: this will build the logger but it is not registered directly on a session: once received, 
     * it will need to be registered as an event listener
     * @param transacted determines if JMS session is transacted or not
     * @param connFactory connection factory instance
     * @param queue JMS queue instance
     * @return new instance of JMS audit logger
     */
    public static AbstractAuditLogger newJMSInstance(boolean transacted, ConnectionFactory connFactory, Queue queue) {
        AsyncAuditLogProducer logger = new AsyncAuditLogProducer();
        logger.setTransacted(transacted);
        logger.setConnectionFactory(connFactory);
        logger.setQueue(queue);
        
        return logger;
    }
    
}
