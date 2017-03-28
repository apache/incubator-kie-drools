/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.*;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.util.DefaultDMNMessagesManager;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.*;

public class DMNResultImpl implements DMNResult, DMNMessageManager {
    private DMNContext context;
    private DMNMessageManager messages;
    private Map<String, DMNDecisionResult> decisionResults;

    public DMNResultImpl() {
        messages = new DefaultDMNMessagesManager();
        decisionResults = new HashMap<>(  );
    }

    public void setContext(DMNContext context) {
        this.context = context;
    }

    @Override
    public DMNContext getContext() {
        return context;
    }

    @Override
    public List<DMNMessage> getMessages() {
        return messages.getMessages();
    }

    @Override
    public void addAll(List<DMNMessage> messages) {
        this.messages.addAll( messages );
    }

    @Override
    public DMNMessage addMessage(DMNMessage msg) {
        return messages.addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        return messages.addMessage( severity, message, messageType, source );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        return messages.addMessage( severity, message, messageType, source, exception );
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        return messages.getMessages( sevs );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        return messages.addMessage( severity, message, messageType, source, feelEvent );
    }

    @Override
    public boolean hasErrors() {
        return messages.hasErrors();
    }

    public List<DMNDecisionResult> getDecisionResults() {
        return new ArrayList<>( decisionResults.values() );
    }

    public DMNDecisionResult getDecisionResultByName( String name ) {
        return decisionResults.values().stream().filter( dr -> dr.getDecisionName().equals( name ) ).findFirst().get();
    }

    public DMNDecisionResult getDecisionResultById( String id ) {
        return decisionResults.get( id );
    }

    public void setDecisionResult( String id, DMNDecisionResult result ) {
        this.decisionResults.put( id, result );
    }

    @Override
    public String toString() {
        return "DMNResultImpl{" +
               "context=" + context +
               ", messages=" + messages +
               '}';
    }

}
