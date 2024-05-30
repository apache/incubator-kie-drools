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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.SilentWrappingEvaluationContextImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.Msg;

public class FilterExpressionNode
        extends BaseNode {

    private BaseNode expression;
    private BaseNode filter;

    public FilterExpressionNode(ParserRuleContext ctx, BaseNode expression, BaseNode filter) {
        super( ctx );
        this.expression = expression;
        this.filter = filter;
    }

    public FilterExpressionNode(BaseNode expression, BaseNode filter, String text) {
        this.expression = expression;
        this.filter = filter;
        this.setText(text);
    }

    public BaseNode getExpression() {
        return expression;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public BaseNode getFilter() {
        return filter;
    }

    public void setFilter(BaseNode filter) {
        this.filter = filter;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if( expression == null ) {
            return null;
        }
        Object value = expression.evaluate( ctx );
        // spec determines single values should be treated as lists of one element
        List list = value instanceof List ? (List) value : Collections.singletonList(value);

        try {
            if( filter.getResultType() != BuiltInType.BOOLEAN ) {
                // check if index
                Object f = filter.evaluate(new SilentWrappingEvaluationContextImpl(ctx)); // I need to try evaluate filter first, ignoring errors; only if evaluation fails, or is not a Number, it delegates to try `evaluateExpressionsInContext`
                if (f instanceof Number) {
                    // what to do if Number is not an integer??
                    int i = ((Number) f).intValue();
                    if ( i > 0 && i <= list.size() ) {
                        return list.get( i - 1 );
                    } else if ( i < 0 && Math.abs( i ) <= list.size() ) {
                        return list.get( list.size() + i );
                    } else {
                        ctx.notifyEvt(astEvent(Severity.WARN, Msg.createMessage(Msg.INDEX_OUT_OF_BOUND, list.size(), i)));
                        return null;
                    }
                } else {
                    return evaluateExpressionsInContext(ctx, list);
                }
            } else {
                return evaluateExpressionsInContext(ctx, list);
            }
        } catch ( Exception e ) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.ERROR_EXECUTING_LIST_FILTER, getText()), e) );
        }

        return null;
    }

    private List evaluateExpressionsInContext(EvaluationContext ctx, List expressions) {
        List results = new ArrayList();
        expressions.forEach(expression -> evaluateExpressionInContext( ctx, results, expression));
        return results;
    }

    private void evaluateExpressionInContext(EvaluationContext ctx, List results, Object v) {
        try {
            ctx.enterFrame();
            // handle it as a predicate
            // Have the "item" variable set first, so to respect the DMN spec: The expression in square brackets can reference a list
            // element using the name item, unless the list element is a context that contains the key "item".
            ctx.setValue( "item", v );

            // using Root object logic to avoid having to eagerly inspect all attributes.
            ctx.setRootObject(v);

            // a filter would always return a list with all the elements for which the filter is true.
            // In case any element fails in there or the filter expression returns null, it will only exclude the element, but will continue to process the list.
            // In case all elements fail, the result will be an empty list.
            Object r = this.filter.evaluate(new SilentWrappingEvaluationContextImpl(ctx)); // evaluate filter, ignoring errors 
            if( r instanceof Boolean && r == Boolean.TRUE ) {
                results.add( v );
            }
        } finally {
            ctx.exitFrame();
        }
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { expression, filter };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
