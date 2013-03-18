package org.jbpm.process.audit;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jbpm.process.audit.jms.AsyncAuditLogProducer;
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
     * Creates new isntances of audit logger based on given type and parameters and 
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
    
    
}
