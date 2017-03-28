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

package org.kie.dmn.feel.runtime.events;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * A base class with common functionality to all events
 */
public class FEELEventBase implements FEELEvent {
    private final Severity severity;
    private final String message;
    private final Throwable sourceException;

    public FEELEventBase(Severity severity, String message, Throwable sourceException) {
        this.severity = severity;
        this.message = message;
        this.sourceException = sourceException;
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
    public Throwable getSourceException() {
        return sourceException;
    }

    @Override
    public int getLine() {
        return -1;
    }

    @Override
    public int getColumn() {
        return -1;
    }

    @Override
    public Object getOffendingSymbol() {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "severity=" + severity +
               ", message='" + message + '\'' +
               ", sourceException=" + sourceException +
               '}';
    }
}
