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
package org.kie.dmn.feel.lang.ast;

import java.util.Objects;
import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ParserHelper;
import org.kie.dmn.feel.runtime.events.ASTEventBase;
import org.kie.dmn.feel.util.Msg;

public class BaseNode
        implements ASTNode {
    protected final ASTNode[] EMPTY_CHILDREN = new ASTNode[0];
    private int startChar;
    private int endChar;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;

    private String text;

    public BaseNode() {
    }

    public BaseNode( ParserRuleContext ctx ) {
        // DO NOT keep the reference to `ParserRuleContext` to avoid unneeded retention of lexer structures.
        this.setStartChar( ctx.getStart().getStartIndex() );
        this.setStartLine( ctx.getStart().getLine() );
        this.setStartColumn( ctx.getStart().getCharPositionInLine() );
        this.setEndChar( ctx.getStop().getStopIndex() );
        this.setEndLine( ctx.getStop().getLine() );
        this.setEndColumn( ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() );
        this.setText( ParserHelper.getOriginalText( ctx ) );
    }

    public BaseNode copyLocationAttributesFrom(BaseNode from) {
        this.setStartChar(from.getStartChar());
        this.setStartLine(from.getStartLine());
        this.setStartColumn(from.getStartColumn());
        this.setEndChar(from.getEndChar());
        this.setEndLine(from.getEndLine());
        this.setEndColumn(from.getEndColumn());
        this.setText(from.getText());
        return this;
    }

    @Override
    public int getStartChar() {
        return startChar;
    }

    public void setStartChar(int startChar) {
        this.startChar = startChar;
    }

    @Override
    public int getEndChar() {
        return endChar;
    }

    public void setEndChar(int endChar) {
        this.endChar = endChar;
    }

    @Override
    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    @Override
    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" + text + "}";
    }

    @Override
    public Type getResultType() {
        return BuiltInType.UNKNOWN;
    }
    
    protected Supplier<FEELEvent> astEvent(Severity severity, String message) {
        return () -> new ASTEventBase(severity, message, this) ;    
    }
    protected Supplier<FEELEvent> astEvent(Severity severity, String message, Throwable throwable) {
        return () -> new ASTEventBase(severity, message, this, throwable) ;    
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.BASE_NODE_EVALUATE_CALLED) ) );
        return null;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return EMPTY_CHILDREN;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseNode baseNode)) {
            return false;
        }
        return Objects.equals(this.getClass(), o.getClass()) && Objects.equals(text, baseNode.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
