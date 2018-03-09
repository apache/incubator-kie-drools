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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;

import com.thoughtworks.xstream.XStream;

import static org.kie.soup.commons.xstream.XStreamUtils.createXStream;

/**
 * Asynchronous audit event receiver. Receives messages from JMS queue
 * that it is attached to as <code>MessageListener</code>.
 * This is the second part of asynchronous BAM support backed by JMS
 * (producer is provide by <code>AsyncAuditLogProducer</code> class).
 * Thus it shares the same message format that is TextMessage with 
 * Xstream serialized *Log classes (ProcessInstanceLog,
 * NodeInstanceLog, VaraiableInstanceLog) as content.
 * 
 * by default it uses entity manager factory and creates entity manager for each message
 * although it provides getEntityManager method that can be overloaded by extensions to supply 
 * entity managers instead of creating it for every message.
 * 
 * For more enterprise based solution this class can be extended by MDB implementations to 
 * provide additional details that are required by MDB such as:
 * <ul>
 *  <li>annotations - @MessageDriven, @ActivationConfigurationProperty</li>
 *  <li>dependency injection - inject entity manager factory or entity manager by annotating methods</li>
 * </ul>
 */
public class AsyncAuditLogReceiver implements MessageListener {
    
    private EntityManagerFactory entityManagerFactory;
    private XStream xstream;
    
    public AsyncAuditLogReceiver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        initXStream();
    }

    private void initXStream() {
        if(xstream==null) {
            xstream = createXStream();
            String[] voidDeny = {"void.class", "Void.class"};
            xstream.denyTypes(voidDeny);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            EntityManager em = getEntityManager();
            TextMessage textMessage = (TextMessage) message;
            try {
                String messageContent = textMessage.getText();
                Integer eventType = textMessage.getIntProperty("EventType");
                Object event = xstream.fromXML(messageContent);
                
                switch (eventType) {
                case AbstractAuditLogger.AFTER_NODE_ENTER_EVENT_TYPE:
                    NodeInstanceLog nodeAfterEnterEvent = (NodeInstanceLog) event;
                    if (nodeAfterEnterEvent.getWorkItemId() != null) {
                    List<NodeInstanceLog> result = em.createQuery(
                            "from NodeInstanceLog as log where log.nodeInstanceId = :nodeId and log.type = 0")
                            .setParameter("nodeId", nodeAfterEnterEvent.getNodeInstanceId()).getResultList();
                            
                            if (result != null && result.size() != 0) {
                            	NodeInstanceLog log = result.get(result.size() - 1);
                               log.setWorkItemId(nodeAfterEnterEvent.getWorkItemId());
                               
                               
                               em.merge(log);   
                           }
                    }
                    break;
                
                case AbstractAuditLogger.AFTER_COMPLETE_EVENT_TYPE:
                    ProcessInstanceLog processCompletedEvent = (ProcessInstanceLog) event;
                    List<ProcessInstanceLog> result = em.createQuery(
                            "from ProcessInstanceLog as log where log.processInstanceId = :piId and log.end is null")
                            .setParameter("piId", processCompletedEvent.getProcessInstanceId()).getResultList();
                            
                            if (result != null && result.size() != 0) {
                               ProcessInstanceLog log = result.get(result.size() - 1);
                               log.setOutcome(processCompletedEvent.getOutcome());
                               log.setStatus(processCompletedEvent.getStatus());
                               log.setEnd(processCompletedEvent.getEnd());
                               log.setDuration(processCompletedEvent.getDuration());
                               
                               em.merge(log);   
                           }
                    break;
                default:
                    em.persist(event);
                    break;
                }
                em.flush();
                em.close();
            } catch (JMSException e) {
                e.printStackTrace();
                throw new RuntimeException("Exception when receiving audit event event", e);
            }
        }

    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

}
