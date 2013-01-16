package org.jbpm.process.audit.jms;

import java.util.Arrays;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.drools.WorkingMemory;
import org.drools.audit.event.LogEvent;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.kie.event.KnowledgeRuntimeEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * Asynchronous log producer that puts audit log events into JMS queue.
 * It expects to have following objects available before it is fully operational:
 * <ul>
 *  <li>ConnectionFactory - used to create jMS objects required to send a message</li>
 *  <li>Queue - JMS destination where messages should be placed</li>
 * </ul>
 * 
 * It sends TextMessages with content of RuleFlowEvent serialized by Xstream. 
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
    
    private static Logger logger = LoggerFactory.getLogger(AsyncAuditLogProducer.class);
    
    private static final List<Integer> SUPPORTED_EVENTS = Arrays.asList(new Integer[]{LogEvent.BEFORE_RULEFLOW_CREATED,
                                                    LogEvent.AFTER_RULEFLOW_COMPLETED,
                                                    LogEvent.BEFORE_RULEFLOW_NODE_TRIGGERED,
                                                    LogEvent.BEFORE_RULEFLOW_NODE_EXITED,
                                                    LogEvent.AFTER_VARIABLE_INSTANCE_CHANGED}); 

    private ConnectionFactory connectionFactory;    
    private Queue queue;
    private boolean transacted = true;
    
    public AsyncAuditLogProducer(WorkingMemory workingMemory, boolean transacted) {
        super(workingMemory);
        this.transacted = transacted;
    }

    public AsyncAuditLogProducer(KnowledgeRuntimeEventManager session, boolean transacted) {
        super(session);
        this.transacted = transacted;
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
    public void logEventCreated(LogEvent logEvent) {
        if (SUPPORTED_EVENTS.contains(logEvent.getType())) {
            sendMessage(logEvent);
        }
        
    }
    
    protected void sendMessage(Object messageContent) {
        if (connectionFactory == null && queue == null) {
            throw new IllegalStateException("ConnectionFactory and Queue cannot be null");
        }
        Connection queueConnection = null;
        Session queueSession = null;
        MessageProducer producer = null;
        try {
            queueConnection = connectionFactory.createConnection();
            queueSession = queueConnection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
           
            XStream xstream = new XStream();
            String eventXml = xstream.toXML(messageContent);
            TextMessage message = queueSession.createTextMessage(eventXml);
            
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


}
