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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ForExpressionNode
        extends BaseNode {

    private List<IterationContextNode> iterationContexts;
    private BaseNode                   expression;

    public ForExpressionNode(ParserRuleContext ctx, ListNode iterationContexts, BaseNode expression) {
        super( ctx );
        this.iterationContexts = new ArrayList<>(  );
        this.expression = expression;
        for( BaseNode n : iterationContexts.getElements() ) {
            this.iterationContexts.add( (IterationContextNode) n );
        }
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
        try {
            ctx.enterFrame();
            List results = new ArrayList(  );
            ForIteration[] ictx = initializeContexts( ctx, iterationContexts);

            while ( nextIteration( ctx, ictx ) ) {
                Object result = expression.evaluate( ctx );
                results.add( result );
            }
            return results;
        } finally {
            ctx.exitFrame();
        }
    }

    private boolean nextIteration( EvaluationContext ctx, ForIteration[] ictx ) {
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

    private void setValueIntoContext(EvaluationContext ctx, ForIteration forIteration) {
        ctx.setValue( forIteration.getName(), forIteration.getNextValue() );
    }

    private ForIteration[] initializeContexts(EvaluationContext ctx, List<IterationContextNode> iterationContexts) {
        ForIteration[] ictx = new ForIteration[iterationContexts.size()];
        int i = 0;
        for ( IterationContextNode icn : iterationContexts ) {
            ictx[i] = createQuantifiedExpressionIterationContext( ctx, icn );
            if( i < iterationContexts.size()-1 && ictx[i].hasNextValue() ) {
                setValueIntoContext( ctx, ictx[i] );
            }
            i++;
        }
        return ictx;
    }

    private ForIteration createQuantifiedExpressionIterationContext(EvaluationContext ctx, IterationContextNode icn) {
        String name = icn.evaluateName( ctx );
        Object result = icn.evaluate( ctx );
        Iterable values = result instanceof Iterable ? (Iterable) result : Collections.singletonList( result );
        ForIteration fi = new ForIteration( name, values );
        return fi;
    }

    private static class ForIteration {
        private String   name;
        private Iterable values;
        private Iterator iterator;

        public ForIteration(String name, Iterable values) {
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
