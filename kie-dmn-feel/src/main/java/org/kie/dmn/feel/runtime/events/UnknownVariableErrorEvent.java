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
 * An event class to report an unknown variable error as returned by the parser
 */
public class UnknownVariableErrorEvent
        extends FEELEventBase
        implements FEELEvent {

    private final int    line;
    private final int    column;
    private final Object offendingSymbol;

    public UnknownVariableErrorEvent(Severity severity, String msg, int line, int charPositionInLine, Object offendingSymbol) {
        super( severity, msg, null );
        this.line = line;
        this.column = charPositionInLine;
        this.offendingSymbol = offendingSymbol;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Object getOffendingSymbol() {
        return offendingSymbol;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "severity=" + getSeverity() +
               ", line=" + line +
               ", column=" + column +
               ", offendingSymbol='" + offendingSymbol +
               "', message='" + getMessage() + '\'' +
               '}';
    }
}
