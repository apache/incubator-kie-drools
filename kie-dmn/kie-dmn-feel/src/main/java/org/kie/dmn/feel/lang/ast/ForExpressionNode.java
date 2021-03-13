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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.Msg;

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
            ctx.setValue("partial", results);
            ForIteration[] ictx = initializeContexts( ctx, iterationContexts);

            while ( nextIteration( ctx, ictx ) ) {
                Object result = expression.evaluate( ctx );
                results.add( result );
            }
            return results;
        } catch (EndpointOfRangeNotOfNumberException e) {
            // ast error already reported
            return null;
        } finally {
            ctx.exitFrame();
        }
    }

    public static boolean nextIteration(EvaluationContext ctx, ForIteration[] ictx) {
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

    public static void setValueIntoContext(EvaluationContext ctx, ForIteration forIteration) {
        ctx.setValue( forIteration.getName(), forIteration.getNextValue() );
    }
    
    @Override
    public Type getResultType() {
        return BuiltInType.LIST;
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

    private static class EndpointOfRangeNotOfNumberException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private ForIteration createQuantifiedExpressionIterationContext(EvaluationContext ctx, IterationContextNode icn) {
        ForIteration fi = null;
        String name = icn.evaluateName( ctx );
        Object result = icn.evaluate( ctx );
        Object rangeEnd = icn.evaluateRangeEnd(ctx);
        if (rangeEnd == null) {
            Iterable values = result instanceof Iterable ? (Iterable) result : Collections.singletonList(result);
            fi = new ForIteration(name, values);
        } else {
            valueMustBeANumber(ctx, result);
            BigDecimal start = (BigDecimal) result;
            valueMustBeANumber(ctx, rangeEnd);
            BigDecimal end = (BigDecimal) rangeEnd;
            fi = new ForIteration(name, start, end);
        }
        return fi;
    }

    private void valueMustBeANumber(EvaluationContext ctx, Object value) {
        if (!(value instanceof BigDecimal)) {
            ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.VALUE_X_NOT_A_VALID_ENDPOINT_FOR_RANGE_BECAUSE_NOT_A_NUMBER, value), null));
            throw new EndpointOfRangeNotOfNumberException();
        }
    }

    public static class ForIteration {
        private String   name;
        private Iterable values;

        private Supplier<Iterator> iteratorGenerator;
        private Iterator iterator;

        public ForIteration(String name, Iterable values) {
            this.name = name;
            this.values = values;
            this.iteratorGenerator = () -> this.values.iterator();
        }

        public ForIteration(String name, final BigDecimal start, final BigDecimal end) {
            this.name = name;
            this.iteratorGenerator = () -> new BigDecimalRangeIterator(start, end);
        }

        public boolean hasNextValue() {
            if( iterator == null ) {
                iterator = iteratorGenerator.get();
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

    public static class BigDecimalRangeIterator implements Iterator<BigDecimal> {

        private enum Direction {
            ASCENDANT,
            DESCENDANT;
        }

        private final BigDecimal start;
        private final BigDecimal end;

        private BigDecimal cursor;
        private final Direction direction;
        private final BigDecimal increment;

        public BigDecimalRangeIterator(BigDecimal start, BigDecimal end) {
            this.start = start;
            this.end = end;
            this.direction = (start.compareTo(end) <= 0) ? Direction.ASCENDANT : Direction.DESCENDANT;
            this.increment = (direction == Direction.ASCENDANT) ? new BigDecimal(1, MathContext.DECIMAL128) : new BigDecimal(-1, MathContext.DECIMAL128);
        }

        @Override
        public boolean hasNext() {
            if (cursor == null) {
                return true;
            } else {
                BigDecimal lookAhead = cursor.add(increment);
                if (direction == Direction.ASCENDANT) {
                    return lookAhead.compareTo(end) <= 0;
                } else {
                    return lookAhead.compareTo(end) >= 0;
                }
            }
        }

        @Override
        public BigDecimal next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (cursor == null) {
                cursor = start;
            } else {
                cursor = cursor.add(increment);
            }
            return cursor;
        }

    }

    @Override
    public ASTNode[] getChildrenNode() {
        ASTNode[] children = new ASTNode[ iterationContexts.size() + 1 ];
        System.arraycopy(iterationContexts.toArray(new ASTNode[]{}), 0, children, 0, iterationContexts.size());
        children[ children.length-1 ] = expression;
        return children;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
