package org.jbpm.process.audit.jms;

import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

public class BitronixHornetQXAConnectionFactory implements XAConnectionFactory {
    
    public static XAConnectionFactory connectionFactory;

    @Override
    public XAConnection createXAConnection() throws JMSException {
        
        return (XAConnection) connectionFactory.createXAConnection();
    }

    @Override
    public XAConnection createXAConnection(String userName, String password)
            throws JMSException {
        return (XAConnection) connectionFactory.createXAConnection();
    }

}
