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

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.Msg;

import java.util.*;

public class QuantifiedExpressionNode
        extends BaseNode {

    public static enum Quantifier {
        SOME, EVERY;

        public static Quantifier resolve(String text) {
            if ( "some".equals( text ) ) {
                return SOME;
            } else {
                return EVERY;
            }
        }

        public Boolean positiveTest() {
            return this == SOME;
        }

        public Boolean defaultValue() {
            return this == EVERY;
        }
    }

    private Quantifier                 quantifier;
    private List<IterationContextNode> iterationContexts;
    private BaseNode                   expression;

    public QuantifiedExpressionNode(ParserRuleContext ctx, Quantifier quantifier, ListNode list, BaseNode expression) {
        super( ctx );
        this.quantifier = quantifier;
        this.iterationContexts = new ArrayList<>();
        this.expression = expression;
        for ( BaseNode n : list.getElements() ) {
            this.iterationContexts.add( (IterationContextNode) n );
        }
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(Quantifier quantifier) {
        this.quantifier = quantifier;
    }

    public List<IterationContextNode> getIterationContexts() {
        return iterationContexts;
    }

    public void setIterationContexts(List<IterationContextNode> iterationContexts) {
        this.iterationContexts = iterationContexts;
    }

    public BaseNode getExpression() {
        return expression;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    @Override
    public Boolean evaluate(EvaluationContext ctx) {
        if( quantifier == Quantifier.SOME || quantifier == Quantifier.EVERY ) {
            return iterateContexts( ctx, iterationContexts, expression, quantifier );
        }
        ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "Quantifier")) );
        return null;
    }

    private Boolean iterateContexts(EvaluationContext ctx, List<IterationContextNode> iterationContexts, BaseNode expression, Quantifier quantifier ) {
        try {
            ctx.enterFrame();
            QEIteration[] ictx = initializeContexts(ctx, iterationContexts);

            while ( nextIteration( ctx, ictx ) ) {
                Boolean result = (Boolean) expression.evaluate( ctx );
                if ( result != null && result.equals( quantifier.positiveTest() ) ) {
                    return quantifier.positiveTest();
                }
            }
            return quantifier.defaultValue();
        } finally {
            ctx.exitFrame();
        }
    }

    private boolean nextIteration( EvaluationContext ctx, QEIteration[] ictx ) {
        int i = ictx.length-1;
        while ( i >= 0 && i < ictx.length ) {
            if ( ictx[i].hasNextValue() ) {
                setValueIntoContext( ctx, ictx[i] );
                i++;
            } else {
                i--;
            }
        }
        return i >= 0;
    }

    private void setValueIntoContext(EvaluationContext ctx, QEIteration qeIteration) {
        ctx.setValue( qeIteration.getName(), qeIteration.getNextValue() );
    }

    private QEIteration[] initializeContexts(EvaluationContext ctx, List<IterationContextNode> iterationContexts) {
        QEIteration[] ictx = new QEIteration[iterationContexts.size()];
        int i = 0;
        for ( IterationContextNode icn : iterationContexts ) {
            ictx[i++] = createQuantifiedExpressionIterationContext( ctx, icn );
        }
        return ictx;
    }

    private QEIteration createQuantifiedExpressionIterationContext(EvaluationContext ctx, IterationContextNode icn) {
        String name = icn.evaluateName( ctx );
        Object result = icn.evaluate( ctx );
        Iterable values = result instanceof Iterable ? (Iterable) result : Collections.singletonList( result );
        QEIteration qei = new QEIteration( name, values );
        return qei;
    }

    private static class QEIteration {
        private String name;
        private Iterable values;
        private Iterator iterator;

        public QEIteration(String name, Iterable values) {
            this.name = name;
            this.values = values;
        }

        public boolean hasNextValue() {
            if( iterator == null ) {
                iterator = values.iterator();
            }
            boolean hasValue = this.iterator.hasNext();
            if( ! hasValue ) {
                this.iterator = null;
            }
            return hasValue;
        }

        public Object getNextValue() {
            return iterator != null ? iterator.next() : null;
        }

        public String getName() {
            return name;
        }
    }
}
