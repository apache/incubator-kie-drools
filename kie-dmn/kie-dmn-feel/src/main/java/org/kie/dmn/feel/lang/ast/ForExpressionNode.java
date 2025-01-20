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

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.exceptions.EndpointOfForIterationNotValidTypeException;
import org.kie.dmn.feel.exceptions.EndpointOfForIterationDifferentTypeException;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIteration;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.getForIteration;

public class ForExpressionNode
        extends BaseNode {

    private static final Logger LOG = LoggerFactory.getLogger(ForExpressionNode.class);

    private List<IterationContextNode> iterationContexts;
    private BaseNode expression;

    public ForExpressionNode(ParserRuleContext ctx, ListNode iterationContexts, BaseNode expression) {
        super(ctx);
        this.iterationContexts = new ArrayList<>();
        this.expression = expression;
        for (BaseNode n : iterationContexts.getElements()) {
            this.iterationContexts.add((IterationContextNode) n);
        }
    }

    public ForExpressionNode(List<IterationContextNode> iterationContexts, BaseNode expression, String text) {
        this.iterationContexts = iterationContexts;
        this.expression = expression;
        this.setText(text);
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
            List<Object> toReturn = new ArrayList<>();
            ctx.setValue("partial", toReturn);
            populateToReturn(0, ctx, toReturn);
            LOG.trace("returning {}", toReturn);
            return toReturn;
        } catch (EndpointOfForIterationNotValidTypeException | EndpointOfForIterationDifferentTypeException e) {
            // ast error already reported
            return null;
        } finally {
            ctx.exitFrame();
        }
    }

    private void populateToReturn(int k, EvaluationContext ctx, List<Object> toPopulate) {
        LOG.trace("populateToReturn at index {}", k);
        if (k > iterationContexts.size() - 1) {
            LOG.trace("Index {} out of range, returning", k);
            return;
        }
        IterationContextNode iterationContextNode = iterationContexts.get(k);
        ForIteration forIteration = createForIteration(ctx, iterationContextNode);
        while (forIteration.hasNextValue()) {
            LOG.trace("{} has next value", forIteration);
            ctx.enterFrame(); // open loop scope frame, for every iter ctx, except last one as guarded by if clause
            // above
            setValueIntoContext(ctx, forIteration.getName(), forIteration.getNextValue());
            if (k == iterationContexts.size() - 1) {
                LOG.trace("i == iterationContexts.size() -1: this is the last iteration context; evaluating {}",
                          expression);
                Object result = expression.evaluate(ctx);
                LOG.trace("add {} to toReturn", result);
                toPopulate.add(result);
            } else if (k < iterationContexts.size() - 1) {
                populateToReturn(k + 1, ctx, toPopulate);
            }
        }
        ctx.exitFrame();
    }

    static void setValueIntoContext(EvaluationContext ctx, String name, Object value) {
        ctx.setValue(name, value);
    }

    @Override
    public Type getResultType() {
        return BuiltInType.LIST;
    }

    private ForIteration createForIteration(EvaluationContext ctx, IterationContextNode iterationContextNode) {
        LOG.trace("Creating ForIteration for {}", iterationContextNode);
        ForIteration toReturn = null;
        String name = iterationContextNode.evaluateName(ctx);
        Object result = iterationContextNode.evaluate(ctx);
        Object rangeEnd = iterationContextNode.evaluateRangeEnd(ctx);
        if (rangeEnd == null) {
            if (result instanceof Iterable iterable) {
                toReturn = new ForIteration(name, iterable);
            } else if (result instanceof Range) {
                toReturn = getForIteration(ctx, name, ((Range) result).getStart(), ((Range) result).getEnd());
            } else {
                toReturn = new ForIteration(name, Collections.singletonList(result));
            }
        } else {
            toReturn = getForIteration(ctx, name, result, rangeEnd);
        }
        return toReturn;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        ASTNode[] children = new ASTNode[iterationContexts.size() + 1];
        System.arraycopy(iterationContexts.toArray(new ASTNode[]{}), 0, children, 0, iterationContexts.size());
        children[children.length - 1] = expression;
        return children;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
