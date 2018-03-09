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

package org.jbpm.process.audit.jms;

import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.variable.ProcessIndexerManager;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import static org.kie.soup.commons.xstream.XStreamUtils.createXStream;

/**
 * Asynchronous log producer that puts audit log events into JMS queue.
 * It expects to have following objects available before it is fully operational:
 * <ul>
 *  <li>ConnectionFactory - used to create jMS objects required to send a message</li>
 *  <li>Queue - JMS destination where messages should be placed</li>
 * </ul>
 * 
 * It sends TextMessages with content of *Log classes (ProcessInstanceLog,
 * NodeInstanceLog, VaraiableInstanceLog) serialized by Xstream. 
 * Such serialization allows:
 * <ul>
 *  <li>use of message selectors to filter which types of events should be processed by different consumer</li>
 *  <li>use any consumer to process messages - does not have to be default one</li>
 *  <li>use content based routing in more advanced scenarios</li>
 * </ul>
 * 
 * Default receiver is <code>AsyncAuditLogReceiver</code> class
 */
public class AsyncAuditLogProducer extends AbstractAuditLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncAuditLogProducer.class);

    private ConnectionFactory connectionFactory;    
    private Queue queue;
    private boolean transacted = true;
    private XStream xstream;
    
    private ProcessIndexerManager indexManager = ProcessIndexerManager.get();

    public AsyncAuditLogProducer() {
        initXStream();
    }
    
    public AsyncAuditLogProducer(KieSession session, boolean transacted) {
        super(session);
        this.transacted = transacted;
        session.addEventListener(this);
        initXStream();
    }

    private void initXStream() {
        if(xstream==null) {
            xstream = createXStream();
            String[] voidDeny = {"void.class", "Void.class"};
            xstream.denyTypes(voidDeny);
        }
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }
    
    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        NodeInstanceLog log = (NodeInstanceLog) builder.buildEvent(event);
        sendMessage(log, BEFORE_NODE_ENTER_EVENT_TYPE);
        ((NodeInstanceImpl) event.getNodeInstance()).getMetaData().put("NodeInstanceLog", log);
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        NodeInstanceLog log = (NodeInstanceLog) builder.buildEvent(event, null);
        sendMessage(log, AFTER_NODE_LEFT_EVENT_TYPE);   
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> variables = indexManager.index(getBuilder(), event);
        for (org.kie.api.runtime.manager.audit.VariableInstanceLog log : variables) {  
            sendMessage(log, AFTER_VAR_CHANGE_EVENT_TYPE);   
        }
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        ProcessInstanceLog log = (ProcessInstanceLog) builder.buildEvent(event);
        sendMessage(log, BEFORE_START_EVENT_TYPE);
        
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        ProcessInstanceLog log = (ProcessInstanceLog) builder.buildEvent(event, null);
        sendMessage(log, AFTER_COMPLETE_EVENT_TYPE);
    }
    
    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
    	// trigger this to record some of the data (like work item id) after activity was triggered
    	NodeInstanceLog log = (NodeInstanceLog) ((NodeInstanceImpl) event.getNodeInstance()).getMetaData().get("NodeInstanceLog");
    	NodeInstanceLog logUpdated = (NodeInstanceLog) builder.buildEvent(event, log);
    	if (logUpdated != null) {
    		sendMessage(log, AFTER_NODE_ENTER_EVENT_TYPE);
    	}
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {

        
    }
    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        
    }
    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
    }
    
    protected void sendMessage(Object messageContent, Integer eventType) {
        if (connectionFactory == null && queue == null) {
            throw new IllegalStateException("ConnectionFactory and Queue cannot be null");
        }
        Connection queueConnection = null;
        Session queueSession = null;
        MessageProducer producer = null;
        try {
            queueConnection = connectionFactory.createConnection();
            queueSession = queueConnection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            String eventXml = xstream.toXML(messageContent);
            TextMessage message = queueSession.createTextMessage(eventXml);
            message.setIntProperty("EventType", eventType);
            producer = queueSession.createProducer(queue);            
            producer.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error when sending JMS message with working memory event", e);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing producer", e);
                }
            }
            
            if (queueSession != null) {
                try {
                    queueSession.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue session", e);
                }
            }
            
            if (queueConnection != null) {
                try {
                    queueConnection.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue connection", e);
                }
            }
        }
    }

    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }


}
