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

import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.feel.runtime.events.FEELEvent;

public class DMNMessageImpl implements DMNMessage {
    private Severity  severity;
    private String    message;
    private String    sourceId;
    private Throwable exception;
    private FEELEvent feelEvent;

    public DMNMessageImpl() {
    }

    public DMNMessageImpl(Severity severity, String message, String sourceId) {
        this( severity, message, sourceId, (Throwable) null);
    }

    public DMNMessageImpl(Severity severity, String message, String sourceId, Throwable exception) {
        this.severity = severity;
        this.message = message;
        this.sourceId = sourceId;
        this.exception = exception;
    }

    public DMNMessageImpl(Severity severity, String message, String sourceId, FEELEvent feelEvent) {
        this.severity = severity;
        this.message = message;
        this.sourceId = sourceId;
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
    public String getSourceId() {
        return sourceId;
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
               ", message='" + message + '\'' +
               ", sourceId='" + sourceId + '\'' +
               ", exception='" + ( exception != null ? ( exception.getClass().getSimpleName() + " : " + exception.getMessage() ) : "" ) + "'" +
               ", feelEvent='" + ( feelEvent != null ? ( feelEvent.getClass().getSimpleName() + " : " + feelEvent.getMessage() ) : "" ) + "'" +
               "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof DMNMessageImpl) ) return false;

        DMNMessageImpl that = (DMNMessageImpl) o;

        if ( severity != that.severity ) return false;
        if ( message != null ? !message.equals( that.message ) : that.message != null ) return false;
        if ( sourceId != null ? !sourceId.equals( that.sourceId ) : that.sourceId != null ) return false;
        if ( exception != null ? !exception.equals( that.exception ) : that.exception != null ) return false;
        return feelEvent != null ? feelEvent.equals( that.feelEvent ) : that.feelEvent == null;

    }

    @Override
    public int hashCode() {
        int result = severity != null ? severity.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (exception != null ? exception.hashCode() : 0);
        result = 31 * result + (feelEvent != null ? feelEvent.hashCode() : 0);
        return result;
    }
}
