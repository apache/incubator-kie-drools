/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class DefaultDMNMessagesManager
        implements DMNMessageManager {

    // should we use a sorted set instead?
    private List<DMNMessage> messages;

    public DefaultDMNMessagesManager() {
        this.messages = new ArrayList<>();
    }

    @Override
    public List<DMNMessage> getMessages() {
        return messages;
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        List<DMNMessage.Severity> severities = Arrays.asList( sevs );
        return messages.stream().filter( m -> severities.contains( m.getSeverity() ) ).collect( Collectors.toList() );
    }

    @Override
    public boolean hasErrors() {
        return messages.stream().anyMatch( m -> DMNMessage.Severity.ERROR.equals( m.getSeverity() ) );
    }

    @Override
    public void addAll(List<? extends DMNMessage> messages) {
        for (DMNMessage message : messages) {
            addMessage( message );
        }
    }

    @Override
    public void addAllUnfiltered(List<? extends DMNMessage> messages) {
        for (DMNMessage message : messages) {
            this.messages.add(message);
        }
    }

    @Override
    public DMNMessage addMessage(DMNMessage newMessage) {
        for( DMNMessage existingMessage : messages ) {
            if( isDuplicate( existingMessage, newMessage ) ) {
                return existingMessage;
            }
        }
        this.messages.add( newMessage );
        return newMessage;
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source );
        return addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source, exception );
        return addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source, feelEvent );
        return addMessage( msg );
    }

    private boolean isDuplicate(DMNMessage existingMsg, DMNMessage newMessage) {
        return existingMsg.getMessageType().equals( newMessage.getMessageType() ) &&
               existingMsg.getSourceReference() == newMessage.getSourceReference();
    }

}
