/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process.core.correlation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.jbpm.process.instance.impl.ReturnValueEvaluator;
import org.jbpm.util.JbpmClassLoaderUtil;

public class CorrelationManager implements Serializable {

    private static final long serialVersionUID = -1557112455565607001L;

    private Map<String, Correlation> correlations;
    private Map<String, Message> messages;

    private transient ClassLoader classLoader;

    public CorrelationManager() {
        this.correlations = new HashMap<>();
        this.messages = new HashMap<>();
        this.classLoader = JbpmClassLoaderUtil.findClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void newCorrelation(String correlationRef, String correlationName) {
        if (correlations.containsKey(correlationRef)) {
            throw new IllegalStateException("Correlation " + correlationRef + "(" + correlationName + ") already exists");
        }
        Correlation correlation = new Correlation(correlationRef, correlationName);
        correlations.put(correlationRef, correlation);
    }

    public void newMessage(String id, String name, String type) {
        if (messages.containsKey(id)) {
            throw new IllegalStateException("Correlated messages " + id + " (" + name + ") already exists");
        }
        Message correlatedMessage = new Message(id, name, type);
        messages.put(id, correlatedMessage);
    }

    public boolean isSubscribe(String messageRef) {
        return correlations.values().stream().anyMatch(correlation -> correlation.hasCorrelationFor(messageRef));
    }

    public void subscribeTo(String correlationRef) {
        if (!correlations.containsKey(correlationRef)) {
            throw new IllegalStateException("Correlation " + correlationRef + " does not exist");
        }
        correlations.get(correlationRef).subscribe();
    }

    public CorrelationInstance computeCorrelationInstance(String messageRef, Object event) {
        if (event == null) {
            throw new IllegalArgumentException("cannot compute a correlation from a null object");
        }
        if (!messages.containsKey(messageRef)) {
            throw new IllegalArgumentException("Message ref " + messageRef + " is not a correlated message");
        }

        Message message = messages.get(messageRef);

        try {
            if (!classLoader.loadClass(message.getMessageType()).isInstance(event)) {
                throw new IllegalArgumentException("Object event type is not appropiate for this correlation. The message type was "
                        + event.getClass().getCanonicalName() + " and was expecting " + message.getMessageType() + " in message " + message.getMessageName() + "(" + message.getMessageName() + ")");
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Object event type is not found", e);
        }

        Correlation correlation = findCorrelationByMessageRef(messageRef);
        CorrelationInstance correlationInstance = new CorrelationInstance(correlation.getId(), correlation.getName());
        CorrelationProperties properties = correlation.getMessageCorrelationFor(messageRef);
        for (String name : properties.names()) {
            ReturnValueEvaluator evaluator = properties.getExpressionFor(name);
            Object val = evaluator.eval(event);
            if (val == null) {
                throw new IllegalArgumentException("Message property evaluated to null is not possible: " + messageRef + " property " + name);
            }
            correlationInstance.setProperty(name, val);
        }
        return correlationInstance;
    }

    public CorrelationInstance computeSubscription(String messageRef, Function<String, Object> resolver) {
        Correlation correlation = findCorrelationByMessageRef(messageRef);
        if (!correlation.isSubscribed()) {
            throw new IllegalArgumentException("There is no subscription for correlation by message ref " + messageRef + " is not subscribed");
        }
        CorrelationInstance correlationInstance = new CorrelationInstance(correlation.getId(), correlation.getName());
        CorrelationProperties properties = correlation.getProcessSubscription();

        for (String name : properties.names()) {
            ReturnValueEvaluator evaluator = properties.getExpressionFor(name);
            Object val = evaluator.eval(resolver);
            if (val == null) {
                throw new IllegalArgumentException("Process Subscription property evaluated to null is not possible: " + messageRef + " property " + name);
            }
            correlationInstance.setProperty(name, val);
        }

        return correlationInstance;
    }

    private Correlation findCorrelationByMessageRef(String messageRef) {
        for (Correlation correlation : correlations.values()) {
            if (correlation.hasCorrelationFor(messageRef)) {
                return correlation;
            }
        }
        throw new IllegalArgumentException("Correlation for message ref " + messageRef + " does not exist");
    }

    public void addMessagePropertyExpression(String correlationRef, String messageRef, String propertyName, ReturnValueEvaluator expression) {
        correlations.get(correlationRef).getMessageCorrelationFor(messageRef).addProperty(propertyName, expression);
    }

    public void addProcessSubscriptionPropertyExpression(String correlationRef, String propertyName, ReturnValueEvaluator expression) {
        if (!correlations.containsKey(correlationRef)) {
            return;
        }
        correlations.get(correlationRef).getProcessSubscription().addProperty(propertyName, expression);
    }

    public Set<String> getMessagesId() {
        return messages.keySet();
    }

    public Message findMessageById(String messageId) {
        return messages.get(messageId);
    }

    public Set<String> getCorrelationsId() {
        return correlations.keySet();
    }

    public Correlation findCorrelationById(String correlationId) {
        return correlations.get(correlationId);
    }

}