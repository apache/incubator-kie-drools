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

package org.jbpm.kie.services.impl;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.jms.AsyncAuditLogReceiver;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.audit.jms.AsyncTaskLifeCycleEventReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompositeAsyncAuditLogReceiver implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(CompositeAsyncAuditLogReceiver.class);
    
    private EntityManagerFactory entityManagerFactory;
    
    private MessageListener processLogsReceiver;
    private MessageListener taskLogReceiver;
    
    private MessageListener caseInstanceLogReceiver;
    
    
    public CompositeAsyncAuditLogReceiver() {
    }
    
    public CompositeAsyncAuditLogReceiver(EntityManagerFactory emf) {
        this.entityManagerFactory = emf;        
    }
    
    @PostConstruct
    public void init() {
        if (entityManagerFactory == null) {
            this.entityManagerFactory = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain"); 
        }
        this.processLogsReceiver = new AsyncAuditLogReceiver(entityManagerFactory);
        this.taskLogReceiver = new AsyncTaskLifeCycleEventReceiver(entityManagerFactory);
        this.caseInstanceLogReceiver = createCaseEventReceiver();
        
    }
    
    @Override
    public void onMessage(Message message) {
       
        logger.debug("Audit log message received {}", message);
        try {
            String logType = message.getStringProperty("LogType");
            logger.debug("LogType property on message set to {}", logType);
            
            if ("Process".equals(logType)) {
                processLogsReceiver.onMessage(message);
            } else if ("Task".equals(logType)) {
                taskLogReceiver.onMessage(message);
            } else if ("Case".equals(logType) && caseInstanceLogReceiver != null) {
                caseInstanceLogReceiver.onMessage(message);
            } else {
                logger.warn("Unexpected message {} with log type {}, consuming and ignoring", message, logType);
            }
            
        } catch (JMSException e) {
            logger.error("Unexpected JMS exception while processing audit log message", e);
        }

    }
    
    /*
     * Helper methods
     */
    
    @SuppressWarnings("unchecked")
    protected MessageListener createCaseEventReceiver() {
        try {
            Class<MessageListener> caseEventReceiverClass = (Class<MessageListener>) Class.forName("org.jbpm.casemgmt.impl.jms.AsyncCaseInstanceAuditEventReceiver");
            
            return caseEventReceiverClass.getConstructor(EntityManagerFactory.class).newInstance(entityManagerFactory);
        } catch (Exception e) {
            logger.debug("No message listener found for case instance event receiver", e);
            
            return null;
        }
    }

}
