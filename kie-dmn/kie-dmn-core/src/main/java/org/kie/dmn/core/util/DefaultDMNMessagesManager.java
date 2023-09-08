/**
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
package org.kie.dmn.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.io.Resource;
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
    private String path;

    public DefaultDMNMessagesManager(Resource resource) {
        this.messages = new ArrayList<>();
        this.path = resource != null ? resource.getSourcePath() : null;
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
        this.messages.addAll(messages);
    }

    @Override
    public DMNMessage addMessage(DMNMessage newMessage) {
        this.messages.add( newMessage );
        return newMessage;
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source ).withPath(path);
        return addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source, exception ).withPath(path);
        return addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source, feelEvent ).withPath(path);
        return addMessage( msg );
    }

}
