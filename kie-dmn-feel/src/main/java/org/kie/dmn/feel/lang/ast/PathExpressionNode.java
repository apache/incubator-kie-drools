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
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PathExpressionNode
        extends BaseNode {

    private BaseNode expression;
    private BaseNode name;

    public PathExpressionNode(ParserRuleContext ctx, BaseNode expression, BaseNode name) {
        super( ctx );
        this.expression = expression;
        this.name = name;
    }

    public BaseNode getExpression() {
        return expression;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public BaseNode getName() {
        return name;
    }

    public void setName(BaseNode name) {
        this.name = name;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        try {
            Object o = this.expression.evaluate( ctx );
            if ( o instanceof List ) {
                List list = (List) o;
                // list of contexts/elements as defined in the spec, page 114
                List results = new ArrayList();
                for( Object element : list ) {
                    Object r = fetchValue( element );
                    if( r != null ) {
                        results.add( r );
                    }
                }
                return results;
            } else {
                return fetchValue( o );
            }
        } catch ( Exception e ) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.ERROR_EVALUATING_PATH_EXPRESSION, expression.getText(), name.getText()), e) );
        }
        return null;
    }

    private Object fetchValue(Object o)
            throws IllegalAccessException, InvocationTargetException {
        if ( name instanceof NameRefNode ) {
            o = EvalHelper.getValue( o, name.getText() );
        } else if ( name instanceof QualifiedNameNode ) {
            for ( NameRefNode nr : ((QualifiedNameNode) name).getParts() ) {
                o = EvalHelper.getValue( o, nr.getText() );
            }
        }
        return o;
    }
}
