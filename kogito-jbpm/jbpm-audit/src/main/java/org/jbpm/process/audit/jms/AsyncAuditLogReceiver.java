package org.jbpm.process.audit.jms;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.ProcessInstanceLog;

import com.thoughtworks.xstream.XStream;

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
    
    public AsyncAuditLogReceiver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
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
                XStream xstram = new XStream();
                Object event = xstram.fromXML(messageContent);
                
                switch (eventType) {
   
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
