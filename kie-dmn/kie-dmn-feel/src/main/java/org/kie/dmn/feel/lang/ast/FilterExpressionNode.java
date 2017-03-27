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
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.Msg;

import java.util.*;

public class FilterExpressionNode
        extends BaseNode {

    private BaseNode expression;
    private BaseNode filter;

    public FilterExpressionNode(ParserRuleContext ctx, BaseNode expression, BaseNode filter) {
        super( ctx );
        this.expression = expression;
        this.filter = filter;
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
        List list = value instanceof List ? (List) value : Arrays.asList( value );

        try {
            if( filter.getResultType() != BuiltInType.BOOLEAN ) {
                // check if index
                Object f = filter.evaluate( ctx );
                if ( f != null && f instanceof Number ) {
                    // what to do if Number is not an integer??
                    int i = ((Number) f).intValue();
                    if ( i > 0 && i <= list.size() ) {
                        return list.get( i - 1 );
                    } else if ( i < 0 && Math.abs( i ) <= list.size() ) {
                        return list.get( list.size() + i );
                    } else {
                        ctx.notifyEvt( astEvent( Severity.ERROR, Msg.createMessage( Msg.INDEX_OUT_OF_BOUND ) ) );
                        return null;
                    }
                } else {
                    List results = new ArrayList(  );
                    for( Object v : list ) {
                        evaluateExpressionInContext( ctx, results, v );
                    }
                    return results;
                }
            } else {
                List results = new ArrayList(  );
                for( Object v : list ) {
                    evaluateExpressionInContext( ctx, results, v );
                }
                return results;
            }
        } catch ( Exception e ) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.ERROR_EXECUTING_LIST_FILTER, getText()), e) );
        }

        return null;
    }

    private void evaluateExpressionInContext(EvaluationContext ctx, List results, Object v) {
        try {
            ctx.enterFrame();
            // handle it as a predicate
            ctx.setValue( "item", v );
            // if it is a Map, need to add all string keys as variables in the context
            if( v instanceof Map ) {
                Set<Map.Entry> set = ((Map) v).entrySet();
                for( Map.Entry ce : set ) {
                    if( ce.getKey() instanceof String ) {
                        ctx.setValue( (String) ce.getKey(), ce.getValue() );
                    }
                }
            }

            Object r = this.filter.evaluate( ctx );
            if( r instanceof Boolean && ((Boolean)r) == Boolean.TRUE ) {
                results.add( v );
            }
        } finally {
            ctx.exitFrame();
        }
    }
}
