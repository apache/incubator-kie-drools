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
import org.kie.dmn.feel.exceptions.EndpointOfRangeNotValidTypeException;
import org.kie.dmn.feel.exceptions.EndpointOfRangeOfDifferentTypeException;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIteration;
import org.kie.dmn.feel.lang.types.BuiltInType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.getForIteration;

public class ForExpressionNode
        extends BaseNode {


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
            List results = new ArrayList();
            ctx.setValue("partial", results);
            ForIteration[] ictx = initializeContexts(ctx, iterationContexts);

            while (nextIteration(ctx, ictx)) {
                Object result = expression.evaluate(ctx);
                results.add(result);
                ctx.exitFrame(); // last i-th scope unrolled, see also ForExpressionNode.nextIteration(...)
            }
            return results;
        } catch (EndpointOfRangeNotValidTypeException | EndpointOfRangeOfDifferentTypeException e) {
            // ast error already reported
            return null;
        } finally {
            ctx.exitFrame();
        }
    }

    public static boolean nextIteration(EvaluationContext ctx, ForIteration[] ictx) {
        int i = ictx.length - 1;
        while (i >= 0 && i < ictx.length) {
            if (ictx[i].hasNextValue()) {
                ctx.enterFrame(); // on first iter, open last scope frame; or new ones when prev unrolled
                setValueIntoContext(ctx, ictx[i]);
                i++;
            } else {
                if (i > 0) {
                    // end of iter loop for this i-th scope; i-th scope is always unrolled as part of the 
                    // for-loop cycle, so here must unroll the _prev_ scope;
                    // the if-guard for this code block makes sure NOT to unroll bottom one.
                    ctx.exitFrame();
                }
                i--;
            }
        }
        return i >= 0;
    }

    public static void setValueIntoContext(EvaluationContext ctx, ForIteration forIteration) {
        ctx.setValue(forIteration.getName(), forIteration.getNextValue());
    }

    @Override
    public Type getResultType() {
        return BuiltInType.LIST;
    }

    private ForIteration[] initializeContexts(EvaluationContext ctx, List<IterationContextNode> iterationContexts) {
        ForIteration[] ictx = new ForIteration[iterationContexts.size()];
        int i = 0;
        for (IterationContextNode icn : iterationContexts) {
            ictx[i] = createQuantifiedExpressionIterationContext(ctx, icn);
            if (i < iterationContexts.size() - 1 && ictx[i].hasNextValue()) {
                ctx.enterFrame(); // open loop scope frame, for every iter ctx, except last one as guarded by if clause above
                setValueIntoContext(ctx, ictx[i]);
            }
            i++;
        }
        return ictx;
    }

    private ForIteration createQuantifiedExpressionIterationContext(EvaluationContext ctx, IterationContextNode icn) {
        ForIteration fi;
        String name = icn.evaluateName(ctx);
        Object result = icn.evaluate(ctx);
        Object rangeEnd = icn.evaluateRangeEnd(ctx);
        if (rangeEnd == null) {
            Iterable values = result instanceof Iterable ? (Iterable) result : Collections.singletonList(result);
            fi = new ForIteration(name, values);
        } else {
            fi = getForIteration(ctx, name, result, rangeEnd);
        }
        return fi;
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
