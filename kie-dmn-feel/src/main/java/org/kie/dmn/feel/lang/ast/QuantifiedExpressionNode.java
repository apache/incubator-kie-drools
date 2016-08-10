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
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.types.VariableSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    public Object evaluate(EvaluationContext ctx) {
        switch ( quantifier ) {
            case SOME:
                ctx.enterFrame();
                Iterable[] iterables = new Iterable[iterationContexts.size()];
                Iterator[] iterators = new Iterator[iterables.length];
                String[] names = new String[iterators.length];
                int i = 0;
                for ( IterationContextNode icn : iterationContexts ) {
                    names[i] = icn.evaluateName( ctx );
                    Object result = icn.evaluate( ctx );
                    iterables[i] = result instanceof Iterable ? (Iterable) result : Collections.singletonList( result );
                    iterators[i] = iterables[i].iterator();
                }
                while ( i >= 0 ) {
                    while ( i < iterators.length ) {
                        iterators[i] = iterables[i].iterator();
                        Object value = iterators[i].hasNext() ? iterators[i].next() : null;
                        ctx.setValue( names[i], value );
                        i++;
                    }
                    Boolean result = (Boolean) expression.evaluate( ctx );
                    if ( result != null && result.equals( Boolean.TRUE ) ) {
                        // then "some" evaluates to true
                        return Boolean.TRUE;
                    }
                    i--;
                    while ( i >= 0 && i < iterators.length ) {
                        if ( iterators[i] == null ) {
                            iterators[i] = iterables[i].iterator();
                        }
                        if ( iterators[i].hasNext() ) {
                            Object value = iterators[i].hasNext() ? iterators[i].next() : null;
                            ctx.setValue( names[i], value );
                            i++;
                        } else {
                            iterators[i] = null;
                            i--;
                        }
                    }
                }
                return Boolean.FALSE;
            case EVERY:
                break;
        }
        return null;
    }
}
