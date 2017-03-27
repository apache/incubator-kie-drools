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

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

public class DMNMessageImpl
        implements DMNMessage {
    private Severity                 severity;
    private String                   message;
    private DMNMessageType           messageType;
    private DMNModelInstrumentedBase source;
    private Throwable                exception;
    private FEELEvent                feelEvent;

    public DMNMessageImpl() {
    }

    public DMNMessageImpl(Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        this( severity, message, messageType, source, (Throwable) null );
    }

    public DMNMessageImpl(Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        this.severity = severity;
        this.message = message;
        this.messageType = messageType;
        this.source = source;
        this.exception = exception;
    }

    public DMNMessageImpl(Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        this.severity = severity;
        this.message = message;
        this.messageType = messageType;
        this.source = source;
        this.feelEvent = feelEvent;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public DMNMessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public String getSourceId() {
        return source != null && source instanceof DMNElement ? ((DMNElement) source).getId() : null;
    }

    @Override
    public Object getSourceReference() {
        return source;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public FEELEvent getFeelEvent() {
        return feelEvent;
    }

    @Override
    public String toString() {
        return "DMNMessage{" +
               " severity=" + severity +
               ", type=" + messageType +
               ", message='" + (message!=null?message:"") + '\'' +
               ", sourceId='" + getSourceId() + '\'' +
               ", exception='" + (exception != null ? (exception.getClass().getSimpleName() + " : " + exception.getMessage()) : "") + "'" +
               ", feelEvent='" + (feelEvent != null ? (feelEvent.getClass().getSimpleName() + " : " + feelEvent.getMessage()) : "") + "'" +
               "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof DMNMessageImpl) ) return false;

        DMNMessageImpl that = (DMNMessageImpl) o;

        if ( severity != that.severity ) return false;
        if ( message != null ? !message.equals( that.message ) : that.message != null ) return false;
        if ( messageType != null ? !messageType.equals( that.messageType ) : that.messageType != null ) return false;
        if ( source != null ? !source.equals( that.source ) : that.source != null ) return false;
        if ( exception != null ? !exception.equals( that.exception ) : that.exception != null ) return false;
        return feelEvent != null ? feelEvent.equals( that.feelEvent ) : that.feelEvent == null;

    }

    @Override
    public int hashCode() {
        int result = severity != null ? severity.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (exception != null ? exception.hashCode() : 0);
        result = 31 * result + (feelEvent != null ? feelEvent.hashCode() : 0);
        return result;
    }
}
