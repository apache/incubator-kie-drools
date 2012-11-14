/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.audit;

import java.util.Date;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowLogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.kie.event.KnowledgeRuntimeEventManager;
import org.hibernate.Session;

/**
 * This class has been deprecated in favor of {@link JPAWorkingMemoryDbLogger}. 
 * 
 * Please, please use that class instead of this. 
 */
@Deprecated
public class WorkingMemoryDbLogger extends WorkingMemoryLogger {
    
    public WorkingMemoryDbLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }
    
    public WorkingMemoryDbLogger(KnowledgeRuntimeEventManager session) {
    	super(session);
    }

    public void logEventCreated(LogEvent logEvent) {
        switch (logEvent.getType()) {
            case LogEvent.BEFORE_RULEFLOW_CREATED:
                RuleFlowLogEvent processEvent = (RuleFlowLogEvent) logEvent;
                addProcessLog(processEvent.getProcessInstanceId(), processEvent.getProcessId());
                break;
            case LogEvent.AFTER_RULEFLOW_COMPLETED:
            	processEvent = (RuleFlowLogEvent) logEvent;
                updateProcessLog(processEvent.getProcessInstanceId());
                break;
            case LogEvent.BEFORE_RULEFLOW_NODE_TRIGGERED:
            	RuleFlowNodeLogEvent nodeEvent = (RuleFlowNodeLogEvent) logEvent;
            	addNodeEnterLog(nodeEvent.getProcessInstanceId(), nodeEvent.getProcessId(), nodeEvent.getNodeInstanceId(), nodeEvent.getNodeId(), nodeEvent.getNodeName());
                break;
            case LogEvent.BEFORE_RULEFLOW_NODE_EXITED:
            	nodeEvent = (RuleFlowNodeLogEvent) logEvent;
            	addNodeExitLog(nodeEvent.getProcessInstanceId(), nodeEvent.getProcessId(), nodeEvent.getNodeInstanceId(), nodeEvent.getNodeId(), nodeEvent.getNodeName());
                break;
            default:
                // ignore all other events
        }
    }

    private void addProcessLog(long processInstanceId, String processId) {
        ProcessInstanceLog log = new ProcessInstanceLog(processInstanceId, processId);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(log);
        session.getTransaction().commit();
    }
    
    private void updateProcessLog(long processInstanceId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
        "from ProcessInstanceLog as log where log.processInstanceId = ? and log.end is null")
            .setLong(0, processInstanceId).list();
        if (result != null && result.size() != 0) {
        	ProcessInstanceLog log = result.get(result.size() - 1);
	        log.setEnd(new Date());
	        session.update(log);
        }
        session.getTransaction().commit();
    }
    
    private void addNodeEnterLog(long processInstanceId, String processId, String nodeInstanceId, String nodeId, String nodeName) {
        NodeInstanceLog log = new NodeInstanceLog(NodeInstanceLog.TYPE_ENTER, processInstanceId, processId, nodeInstanceId, nodeId, nodeName);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(log);
        session.getTransaction().commit();
    }
    
    private void addNodeExitLog(long processInstanceId, String processId, String nodeInstanceId, String nodeId, String nodeName) {
        NodeInstanceLog log = new NodeInstanceLog(NodeInstanceLog.TYPE_EXIT, processInstanceId, processId, nodeInstanceId, nodeId, nodeName);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(log);
        session.getTransaction().commit();
    }
    
    public void dispose() {
    	HibernateUtil.getSessionFactory().getCurrentSession().close();
    	HibernateUtil.close();
    }

}
