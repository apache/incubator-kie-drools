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

package org.kie.dmn.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.kie.dmn.lang.Type;
import org.kie.dmn.lang.impl.EvaluationContextImpl;
import org.kie.dmn.lang.types.BuiltInType;

public class BaseNode
        implements ASTNode {
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
        this.setStartChar( ctx.getStart().getStartIndex() );
        this.setStartLine( ctx.getStart().getLine() );
        this.setStartColumn( ctx.getStart().getCharPositionInLine() );
        this.setEndChar( ctx.getStop().getStopIndex() );
        this.setEndLine( ctx.getStop().getLine() );
        this.setEndColumn( ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() );
        this.setText( getOriginalText( ctx ) );
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

    @Override
    public Object evaluate(EvaluationContextImpl ctx) {
        return null;
    }

    private String getOriginalText( ParserRuleContext ctx ) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        Interval interval = new Interval(a,b);
        return ctx.getStart().getInputStream().getText(interval);
    }

}
