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

package org.jbpm.process.workitem.jms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSSendTaskWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(JMSSendTaskWorkItemHandler.class);

    private String connectionFactoryName;
    private String destinationName;

    private ConnectionFactory connectionFactory;
    private Destination destination;

    private boolean transacted = false;

    public JMSSendTaskWorkItemHandler() {
        this.connectionFactoryName = "java:/JmsXA";
        this.destinationName = "queue/KIE.SIGNAL";
        init();
    }

    public JMSSendTaskWorkItemHandler(String connectionFactoryName,
                                      String destinationName) {
        this.connectionFactoryName = connectionFactoryName;
        this.destinationName = destinationName;
        init();
    }

    public JMSSendTaskWorkItemHandler(ConnectionFactory connectionFactory,
                                      Destination destination) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
        init();
    }

    public JMSSendTaskWorkItemHandler(String connectionFactoryName,
                                      String destinationName,
                                      boolean transacted) {
        this.connectionFactoryName = connectionFactoryName;
        this.destinationName = destinationName;
        this.transacted = transacted;
        init();
    }

    public JMSSendTaskWorkItemHandler(ConnectionFactory connectionFactory,
                                      Destination destination,
                                      boolean transacted) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
        this.transacted = transacted;
        init();
    }

    public JMSSendTaskWorkItemHandler(ConnectionFactory connectionFactory,
                                      Destination destination,
                                      boolean transacted,
                                      boolean doInit) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
        this.transacted = transacted;
        if(doInit) {
            init();
        }
    }

    protected void init() {
        try {
            InitialContext ctx = new InitialContext();
            if (this.connectionFactory == null) {
                this.connectionFactory = (ConnectionFactory) ctx.lookup(connectionFactoryName);
            }
            if (this.destination == null) {
                this.destination = (Destination) ctx.lookup(destinationName);
            }
            logger.info("JMS based work item handler successfully activated on destination {}",
                        destination);
        } catch (Exception e) {
            logger.error("Unable to initialize JMS send work item handler due to {}",
                         e.getMessage(),
                         e);
        }
    }

    protected Message createMessage(WorkItem workItem,
                                    Session session) throws JMSException {
        BytesMessage message = session.createBytesMessage();

        // set properties
        addPropertyIfExists("KIE_Signal",
                            workItem.getParameter("Signal"),
                            message);
        addPropertyIfExists("KIE_SignalProcessInstanceId",
                            workItem.getParameter("SignalProcessInstanceId"),
                            message);
        addPropertyIfExists("KIE_SignalWorkItemId",
                            workItem.getParameter("SignalWorkItemId"),
                            message);
        addPropertyIfExists("KIE_SignalDeploymentId",
                            workItem.getParameter("SignalDeploymentId"),
                            message);

        addPropertyIfExists("KIE_ProcessInstanceId",
                            workItem.getProcessInstanceId(),
                            message);
        addPropertyIfExists("KIE_DeploymentId",
                            ((WorkItemImpl) workItem).getDeploymentId(),
                            message);

        Object data = workItem.getParameter("Data");
        if (data != null) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);
                oout.writeObject(data);

                message.writeBytes(bout.toByteArray());
            } catch (IOException e) {
                logger.warn("Error serializing context data",
                            e);
            }
        }

        return message;
    }

    @Override
    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        if (connectionFactory == null || destination == null) {
            throw new RuntimeException("Connection factory and destination must be set for JMS send task handler");
        }

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(transacted,
                                               Session.AUTO_ACKNOWLEDGE);

            Message message = createMessage(workItem,
                                            session);
            producer = session.createProducer(destination);
            producer.send(message);

            manager.completeWorkItem(workItem.getId(),
                                     null);
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing producer",
                                e);
                }
            }

            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue session",
                                e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue connection",
                                e);
                }
            }
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // no-op
    }

    @Override
    public void close() {
        connectionFactory = null;
        destination = null;
    }

    protected void addPropertyIfExists(String propertyName,
                                       Object properyValue,
                                       Message msg) throws JMSException {
        if (properyValue != null) {
            msg.setObjectProperty(propertyName,
                                  properyValue);
        }
    }
}
