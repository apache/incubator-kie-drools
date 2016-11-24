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

import org.kie.dmn.core.api.DMNContext;
import org.kie.dmn.core.api.DMNDecisionResult;
import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.core.api.DMNResult;
import org.kie.dmn.feel.runtime.events.FEELEvent;

import java.util.*;
import java.util.stream.Collectors;

public class DMNResultImpl implements DMNResult {
    private DMNContext context;
    private List<DMNMessage> messages;
    private Map<String, DMNDecisionResult> decisionResults;

    public DMNResultImpl() {
        messages = new ArrayList<>(  );
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
        return messages;
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        List<DMNMessage.Severity> severities = Arrays.asList( sevs );
        return messages.stream().filter( m -> severities.contains( m.getSeverity() ) ).collect( Collectors.toList());
    }

    @Override
    public boolean hasErrors() {
        return messages.stream().anyMatch( m -> DMNMessage.Severity.ERROR.equals( m.getSeverity() ) );
    }

    public void addMessage( DMNMessage msg ) {
        this.messages.add( msg );
    }

    public DMNMessage addMessage( DMNMessage.Severity severity, String message, String sourceId ) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, sourceId );
        this.messages.add( msg );
        return msg;
    }

    public void addMessage( DMNMessage.Severity severity, String message, String sourceId, Throwable exception ) {
        this.messages.add( new DMNMessageImpl( severity, message, sourceId, exception ) );
    }

    public void addMessage( DMNMessage.Severity severity, String message, String sourceId, FEELEvent feelEvent ) {
        this.messages.add( new DMNMessageImpl( severity, message, sourceId, feelEvent ) );
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
