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
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.DELETE_CASE_DATA_BY_NAME_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.DELETE_CASE_ROLE_ASSIGNMENT_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.FIND_CASE_DATA_BY_NAME_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.FIND_CASE_DATA_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.FIND_CASE_PROCESS_INST_ID_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.UPDATE_CASE_PROCESS_INST_ID_QUERY;
import static org.kie.soup.commons.xstream.XStreamUtils.createTrustingXStream;

import java.util.List;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.jbpm.casemgmt.api.audit.CaseFileData;
import org.jbpm.casemgmt.impl.audit.CaseFileDataLog;
import org.jbpm.casemgmt.impl.audit.CaseRoleAssignmentLog;
import org.jbpm.casemgmt.impl.model.AuditCaseInstanceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class AsyncCaseInstanceAuditEventReceiver implements MessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncCaseInstanceAuditEventReceiver.class);
    
    private EntityManagerFactory entityManagerFactory;
    private XStream xstream;
    
    public AsyncCaseInstanceAuditEventReceiver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        initXStream();
    }

    private void initXStream() {
        if(xstream==null) {
            xstream = createTrustingXStream();
            String[] voidDeny = {"void.class", "Void.class"};
            xstream.denyTypes(voidDeny);
        }
    }
    
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            EntityManager em = getEntityManager();
            TextMessage textMessage = (TextMessage) message;
            try {
                String messageContent = textMessage.getText();
                Integer eventType = textMessage.getIntProperty("EventType");
                
                logger.debug("Processing message with event type {} and content {}", eventType, messageContent);
                
                AuditCaseInstanceData event = (AuditCaseInstanceData) xstream.fromXML(messageContent);
                
                switch (eventType) {
                case AFTER_CASE_STARTED_EVENT_TYPE:
                    
                    if (event.getCaseRoleAssignments() != null) {
                        for (CaseRoleAssignmentLog roleAssignment : event.getCaseRoleAssignments()) {
                            em.persist(roleAssignment);
                        }
                    }
                    
                    if (event.getCaseFileData() != null) {
                        for (CaseFileData caseFileData : event.getCaseFileData()) {
                            em.persist(caseFileData);
                        }
                    }
                    
                    break;
                
                case AFTER_CASE_REOPEN_EVENT_TYPE:
                   
                    int updated = em.createQuery(UPDATE_CASE_PROCESS_INST_ID_QUERY)
                                    .setParameter("piID", event.getProcessInstanceId())
                                    .setParameter("caseId", event.getCaseId())
                                    .executeUpdate();
                    
                    logger.debug("Updated {} role assignment entries for case id {}", updated, event.getCaseId());
                    
                    updateCaseFileItems(event, em);
                    
                    break;
                case AFTER_CASE_ROLE_ASSIGNMENT_ADDED_EVENT_TYPE:
                    
                    try {
                        Long processInstanceId = (Long) em.createQuery(FIND_CASE_PROCESS_INST_ID_QUERY)
                                                        .setParameter("caseId", event.getCaseId())
                                                        .getSingleResult();
                        
                        if (event.getCaseRoleAssignments() != null) {
                            for (CaseRoleAssignmentLog roleAssignment : event.getCaseRoleAssignments()) {
                                roleAssignment.setProcessInstanceId(processInstanceId);
                                em.persist(roleAssignment);
                            }
                        }
                    } catch (NoResultException | NonUniqueResultException e) {
                        // no op
                    }
                    
                    break;                    
                case AFTER_CASE_ROLE_ASSIGNMENT_REMOVED_EVENT_TYPE:
                    if (event.getCaseRoleAssignments() != null) {
                        for (CaseRoleAssignmentLog roleAssignment : event.getCaseRoleAssignments()) {
                            em.createQuery(DELETE_CASE_ROLE_ASSIGNMENT_QUERY)
                                            .setParameter("role", roleAssignment.getRoleName())
                                            .setParameter("entity", roleAssignment.getEntityId())
                                            .setParameter("caseId", event.getCaseId())
                                            .executeUpdate();
                            
                            logger.debug("Removed {} role assignment for entity {} for case id {}", roleAssignment.getRoleName(), roleAssignment.getEntityId(), event.getCaseId());
                        }
                    }
                    
                    break;
                case AFTER_CASE_DATA_ADDED_EVENT_TYPE:
                    
                    updateCaseFileItems(event, em);
                    
                    break;
                case AFTER_CASE_DATA_REMOVED_EVENT_TYPE:
                    if (event.getCaseFileData() != null) {
                        
                        em.createQuery(DELETE_CASE_DATA_BY_NAME_QUERY)
                                        .setParameter("itemNames", event.getCaseFileData().stream().map(item -> item.getItemName()).collect(Collectors.toList()))
                                        .setParameter("caseId", event.getCaseId())
                                        .executeUpdate();                       
                    }
                    break;                    
                default:
                    logger.warn("Unsupported event type {}", eventType);
                    break;
                }
                em.flush();
                em.close();
            } catch (JMSException e) {                
                throw new RuntimeException("Exception when receiving case instance audit event ", e);
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
    
    /*
     * Helper methods
     */

    protected void updateCaseFileItems(AuditCaseInstanceData auditCaseInstanceData, EntityManager em) {
        
        if (auditCaseInstanceData.getCaseFileData() == null || auditCaseInstanceData.getCaseFileData().isEmpty()) {
            return;
        }

        List<String> currentCaseData = currentCaseData(auditCaseInstanceData.getCaseId(), em);
        auditCaseInstanceData.getCaseFileData().forEach((item) -> {
                        
            CaseFileDataLog caseFileDataLog = null;
            if (currentCaseData.contains(item.getItemName())) {
                logger.debug("Case instance {} has already stored log value for {} thus it's going to be updated", auditCaseInstanceData.getCaseId(), item.getItemName());
                caseFileDataLog = caseFileDataByName(auditCaseInstanceData.getCaseId(), item.getItemName(), em);
            
                caseFileDataLog.setItemType(item.getItemType());
                caseFileDataLog.setItemValue(item.getItemValue());
                caseFileDataLog.setLastModified(item.getLastModified());
                caseFileDataLog.setLastModifiedBy(item.getLastModifiedBy());
                em.merge(caseFileDataLog);
            } else {
                logger.debug("Case instance {} has no log value for {} thus it's going to be inserted", auditCaseInstanceData.getCaseId(), item.getItemName());                
                em.persist(item);
            }   
            
        });
    }


    @SuppressWarnings("unchecked")
    protected List<String> currentCaseData(String caseId, EntityManager em) {        
        List<String> caseDataLog = em.createQuery(FIND_CASE_DATA_QUERY)
                .setParameter("caseId", caseId)
                .getResultList();
        
        return caseDataLog;
    }
    
    protected CaseFileDataLog caseFileDataByName(String caseId, String name, EntityManager em) {
        
        return (CaseFileDataLog) em.createQuery(FIND_CASE_DATA_BY_NAME_QUERY)
                .setParameter("caseId", caseId)
                .setParameter("itemName", name)
                .getSingleResult();                
    }
}
