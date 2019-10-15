/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.kie.dmn.feel.lang.ast.ASTNode;

public class ASTHeuristicCheckEvent implements FEELEvent {
    private final Severity severity;
    private final String message;
    private final ASTNode astNode;
    private final Throwable sourceException;

    public ASTHeuristicCheckEvent(Severity severity, String message, ASTNode astNode, Throwable sourceException) {
        this.severity = severity;
        this.message = message;
        this.astNode = astNode;
        this.sourceException = sourceException;
    }

    public ASTHeuristicCheckEvent(Severity severity, String message, ASTNode astNode) {
        this(severity, message, astNode, null);
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
        StringBuilder builder = new StringBuilder();
        builder.append("ASTEventBase [severity=").append(severity)
        .append(", message=").append(message)
        .append(", sourceException=").append(sourceException)
        .append(", astNode=").append(astNode)
        .append("]");
        return builder.toString();
    }

    
}
