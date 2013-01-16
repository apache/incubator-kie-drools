package org.jbpm.process.audit.jms;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowLogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.audit.event.RuleFlowVariableLogEvent;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.event.ExtendedRuleFlowLogEvent;

import com.thoughtworks.xstream.XStream;

/**
 * Asynchronous audit event receiver. Receives messages from JMS queue
 * that it is attached to as <code>MessageListener</code>.
 * This is the second part of asynchronous BAM support backed by JMS
 * (producer is provide by <code>AsyncAuditLogProducer</code> class).
 * Thus it shares the same message format that is TextMessage with 
 * Xstream serialized RuleFlowEvents as content.
 * 
 * by default it uses entity manager factory and creates entity manager for each message
 * although it provides getEntityManager method that van be overloaded by extensions to supply 
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
    
    public AsyncAuditLogReceiver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            EntityManager em = getEntityManager();
            TextMessage textMessage = (TextMessage) message;
            try {
                String messageContent = textMessage.getText();
                
                XStream xstram = new XStream();
                LogEvent event = (LogEvent) xstram.fromXML(messageContent);
                
                switch (event.getType()) {
                case LogEvent.BEFORE_RULEFLOW_CREATED:
                    RuleFlowLogEvent processCreatedEvent = (RuleFlowLogEvent) event;
                    ProcessInstanceLog logProcessCreated = new ProcessInstanceLog(processCreatedEvent.getProcessInstanceId(), processCreatedEvent.getProcessId());
                    if (processCreatedEvent instanceof ExtendedRuleFlowLogEvent) {
                        logProcessCreated.setParentProcessInstanceId(((ExtendedRuleFlowLogEvent) processCreatedEvent).getParentProcessInstanceId());
                    }
                    em.persist(logProcessCreated);
                    break;

                case LogEvent.AFTER_RULEFLOW_COMPLETED:
                    RuleFlowLogEvent processCompletedEvent = (RuleFlowLogEvent) event;
                    List<ProcessInstanceLog> result = em.createQuery(
                            "from ProcessInstanceLog as log where log.processInstanceId = ? and log.end is null")
                                .setParameter(1, processCompletedEvent.getProcessInstanceId()).getResultList();
                            
                            if (result != null && result.size() != 0) {
                               ProcessInstanceLog log = result.get(result.size() - 1);
                               log.setEnd(new Date());
                               if (processCompletedEvent instanceof ExtendedRuleFlowLogEvent) {
                                   log.setStatus(((ExtendedRuleFlowLogEvent) processCompletedEvent).getProcessInstanceState());
                                   log.setOutcome(((ExtendedRuleFlowLogEvent) processCompletedEvent).getOutcome());
                               }
                               
                               em.merge(log);   
                           }
                    break;
                case LogEvent.BEFORE_RULEFLOW_NODE_TRIGGERED:
                    RuleFlowNodeLogEvent nodeEnteredEvent = (RuleFlowNodeLogEvent) event;
                    NodeInstanceLog logNodeEnter = new NodeInstanceLog(
                            NodeInstanceLog.TYPE_ENTER, nodeEnteredEvent.getProcessInstanceId(), nodeEnteredEvent.getProcessId(), 
                            nodeEnteredEvent.getNodeInstanceId(), nodeEnteredEvent.getNodeId(), nodeEnteredEvent.getNodeName());
                    em.persist(logNodeEnter);
                    break;
                case LogEvent.BEFORE_RULEFLOW_NODE_EXITED:
                    RuleFlowNodeLogEvent nodeExitedEvent = (RuleFlowNodeLogEvent) event;
                    NodeInstanceLog logNodeExit = new NodeInstanceLog(
                            NodeInstanceLog.TYPE_EXIT, nodeExitedEvent.getProcessInstanceId(), nodeExitedEvent.getProcessId(),
                            nodeExitedEvent.getNodeInstanceId(), nodeExitedEvent.getNodeId(), nodeExitedEvent.getNodeName());
                    
                    em.persist(logNodeExit);
                    break;
                case LogEvent.AFTER_VARIABLE_INSTANCE_CHANGED:
                    RuleFlowVariableLogEvent variableEvent = (RuleFlowVariableLogEvent) event;
                    VariableInstanceLog logVariable = new VariableInstanceLog(
                            variableEvent.getProcessInstanceId(), variableEvent.getProcessId(), variableEvent.getVariableInstanceId(), 
                            variableEvent.getVariableId(), variableEvent.getObjectToString());
                    
                    em.persist(logVariable);
                    
                    break;
                default:
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
