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
import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.core.api.DMNResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DMNResultImpl implements DMNResult {
    private DMNContext context;
    private List<DMNMessage> messages;

    public DMNResultImpl() {
        messages = new ArrayList<>(  );
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

    public void addMessage( DMNMessage.Severity severity, String message ) {
        this.messages.add( new DMNMessageImpl( severity, message ) );
    }

    public void addMessage( DMNMessage.Severity severity, String message, Throwable exception ) {
        this.messages.add( new DMNMessageImpl( severity, message, exception ) );
    }

    @Override
    public String toString() {
        return "DMNResultImpl{" +
               "context=" + context +
               ", messages=" + messages +
               '}';
    }
}
