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

package org.jbpm.casemgmt.impl.jms;

import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.AFTER_CASE_DATA_ADDED_EVENT_TYPE;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.AFTER_CASE_DATA_REMOVED_EVENT_TYPE;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.AFTER_CASE_REOPEN_EVENT_TYPE;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.AFTER_CASE_ROLE_ASSIGNMENT_ADDED_EVENT_TYPE;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.AFTER_CASE_ROLE_ASSIGNMENT_REMOVED_EVENT_TYPE;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.AFTER_CASE_STARTED_EVENT_TYPE;
import static org.kie.soup.commons.xstream.XStreamUtils.createTrustingXStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jbpm.casemgmt.api.audit.CaseFileData;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.event.CaseDataEvent;
import org.jbpm.casemgmt.api.event.CaseEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.event.CaseReopenEvent;
import org.jbpm.casemgmt.api.event.CaseRoleAssignmentEvent;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.casemgmt.impl.audit.CaseFileDataLog;
import org.jbpm.casemgmt.impl.audit.CaseIndexerManager;
import org.jbpm.casemgmt.impl.audit.CaseRoleAssignmentLog;
import org.jbpm.casemgmt.impl.model.AuditCaseInstanceData;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.task.api.TaskModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class AsyncCaseInstanceAuditEventProducer implements CaseEventListener, Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCaseInstanceAuditEventProducer.class);
    private ConnectionFactory connectionFactory;    
    private Queue queue;
    private boolean transacted = true;
    private XStream xstream;
    
    private CaseIndexerManager indexManager = CaseIndexerManager.get();
    
    public AsyncCaseInstanceAuditEventProducer() {
        initXStream();
    }
    
    private void initXStream() {
        if(xstream==null) {
            xstream = createTrustingXStream();
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
    
    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    @Override
    public void afterCaseStarted(CaseStartEvent event) {
        
        CaseFileInstance caseFile = event.getCaseFile();
        if (caseFile == null) {
            return;
        }
        List<CaseRoleAssignmentLog> caseRoleAssignmentsLogs = new ArrayList<>();
        Collection<CaseRoleInstance> caseRoleAssignments = ((CaseFileInstanceImpl)caseFile).getAssignments();
        if (caseRoleAssignments != null && !caseRoleAssignments.isEmpty()) {
            for (CaseRoleInstance roleAssignment : caseRoleAssignments) {
                logger.debug("Role {} has following assignments {}", roleAssignment.getRoleName(), roleAssignment.getRoleAssignments());
                
                if (roleAssignment.getRoleAssignments() != null && !roleAssignment.getRoleAssignments().isEmpty()) {
                    
                    roleAssignment.getRoleAssignments().forEach(entity -> {
                        CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(event.getProcessInstanceId(), event.getCaseId(), roleAssignment.getRoleName(), entity);
                        
                        caseRoleAssignmentsLogs.add(assignmentLog);
                    });
                    
                }
            }
        } else {
            // add public role so it can be found by queries that take assignments into consideration
            CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(event.getProcessInstanceId(), event.getCaseId(), "*", TaskModelProvider.getFactory().newGroup(AuthorizationManager.PUBLIC_GROUP));
            caseRoleAssignmentsLogs.add(assignmentLog);
        }
        
        Map<String, Object> initialData = caseFile.getData();
        List<CaseFileData> caseFileDataLogs = new ArrayList<>();
        if (!initialData.isEmpty()) {
        
        
            initialData.forEach((name, value) -> {
                
                if (value != null) {
                    List<CaseFileData> indexedValues = indexManager.index(event, name, value); 
                                                        
                    caseFileDataLogs.addAll(indexedValues);                    
                }
            });
        }
        sendMessage(AFTER_CASE_STARTED_EVENT_TYPE, new AuditCaseInstanceData(event.getCaseId(), caseFileDataLogs, caseRoleAssignmentsLogs), 8);
    }
    
    @Override
    public void afterCaseReopen(CaseReopenEvent event) {
        List<CaseFileData> logs = updateCaseFileItems(event, event.getData(), event.getCaseId(), event.getCaseDefinitionId(), event.getUser());
                
        sendMessage(AFTER_CASE_REOPEN_EVENT_TYPE, new AuditCaseInstanceData(event.getProcessInstanceId(), event.getCaseId(), logs, null), 4);    
    }    

    @Override
    public void afterCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event) {
                
        CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(-1L, event.getCaseId(), event.getRole(), event.getEntity());
        sendMessage(AFTER_CASE_ROLE_ASSIGNMENT_ADDED_EVENT_TYPE, new AuditCaseInstanceData(event.getCaseId(), null, Collections.singletonList(assignmentLog)), 4);

    }

    @Override
    public void afterCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event) {
        
        CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(-1L, event.getCaseId(), event.getRole(), event.getEntity());
        
        sendMessage(AFTER_CASE_ROLE_ASSIGNMENT_REMOVED_EVENT_TYPE, new AuditCaseInstanceData(event.getCaseId(), null, Collections.singletonList(assignmentLog)), 4);
    }   

    @Override
    public void afterCaseDataAdded(CaseDataEvent event) {
        List<CaseFileData> logs = updateCaseFileItems(event, event.getData(), event.getCaseId(), event.getDefinitionId(), event.getUser());
        
        if (logs != null && !logs.isEmpty()) {
            sendMessage(AFTER_CASE_DATA_ADDED_EVENT_TYPE, new AuditCaseInstanceData(event.getCaseId(), logs, null), 4);
        }
    }

    @Override
    public void afterCaseDataRemoved(CaseDataEvent event) {
        List<CaseFileData> logs = event.getData().keySet()
                .stream()
                .map(name -> new CaseFileDataLog(event.getCaseId(), event.getDefinitionId(), name))
                .collect(Collectors.toList());
        
        if (logs != null && !logs.isEmpty()) {
            sendMessage(AFTER_CASE_DATA_REMOVED_EVENT_TYPE, new AuditCaseInstanceData(event.getCaseId(), logs, null), 4);
        }
    }

    @Override
    public void close() {
        // no-op
    }

    
    /*
     * Helper methods
     */
    
    protected void sendMessage(Integer eventType, AuditCaseInstanceData eventData, int priority) {
        if (connectionFactory == null && queue == null) {
            throw new IllegalStateException("ConnectionFactory and Queue cannot be null");
        }
        Connection queueConnection = null;
        Session queueSession = null;
        MessageProducer producer = null;
        try {
            queueConnection = connectionFactory.createConnection();
            queueSession = queueConnection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            String eventXml = xstream.toXML(eventData);
            TextMessage message = queueSession.createTextMessage(eventXml);
            message.setStringProperty("LogType", "Case");
            message.setIntProperty("EventType", eventType);
            producer = queueSession.createProducer(queue);  
            producer.setPriority(priority);
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
    
    protected List<CaseFileData> updateCaseFileItems(CaseEvent event, Map<String, Object> addedData, String caseId, String caseDefinitionId, String user) {
        
        if (addedData.isEmpty()) {
            return null;
        }
        List<CaseFileData> logs = new ArrayList<>();
        
        addedData.forEach((name, value) -> {
            
            if (value != null) {
                List<CaseFileData> indexedValues = indexManager.index(event, name, value); 
                
                logs.addAll(indexedValues); 
                
            }
        });
        return logs;
    }
}
