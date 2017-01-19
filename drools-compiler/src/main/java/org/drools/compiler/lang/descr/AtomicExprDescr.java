/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.lang.descr;

public class AtomicExprDescr extends BaseDescr implements ExpressionDescr {
    private static final long serialVersionUID = 510l;

    private String            expression;
    private String            rewrittenExpression;
    private boolean           literal;

    public AtomicExprDescr() { }

    public AtomicExprDescr(final String expression) {
        this( expression, false );
    }

    public AtomicExprDescr(final String expression, final boolean isLiteral ) {
        this.expression = expression;
        this.literal = isLiteral;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression( final String expression ) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return expression;
    }

    public boolean isLiteral() {
        return literal;
    }

    public void setLiteral( boolean literal ) {
        this.literal = literal;
    }

    public String getRewrittenExpression() {
        return rewrittenExpression != null ? rewrittenExpression : expression;
    }

    public boolean hasRewrittenExpression() {
        return rewrittenExpression != null;
    }

    public void setRewrittenExpression(String rewrittenExpression) {
        this.rewrittenExpression = rewrittenExpression;
    }

    @Override
    public AtomicExprDescr replaceVariable(String oldVar, String newVar) {
        expression = expression.replace( oldVar, newVar );
        return this;
    }
}
