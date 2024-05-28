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

import com.github.javaparser.ast.expr.NameExpr;
import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.ASTEventBase;
import org.kie.dmn.feel.runtime.functions.ListContainsFunction;
import org.kie.dmn.feel.util.Msg;

public class InNode
        extends BaseNode {

    public static final NameExpr INNODE_N = new NameExpr(InNode.class.getCanonicalName());

    private BaseNode value;
    private BaseNode exprs;

    public InNode(ParserRuleContext ctx, BaseNode value, BaseNode exprs) {
        super( ctx );
        this.value = value;
        this.exprs = exprs;
    }

    public BaseNode getValue() {
        return value;
    }

    public void setValue(BaseNode value) {
        this.value = value;
    }

    public BaseNode getExprs() {
        return exprs;
    }

    public void setExprs(BaseNode exprs) {
        this.exprs = exprs;
    }

    public Boolean evaluate(EvaluationContext ctx) {
        return exprs == null ? null : staticEvaluation(ctx, this.value.evaluate(ctx), this.exprs.evaluate(ctx), this);
    }

    public static Boolean staticEvaluation(EvaluationContext ctx, Object value, Object expr, InNode inNode) {
        if ( expr != null ) {
            if ( expr instanceof Iterable ) {
                // evaluate in the collection
                for ( Object e : ((Iterable) expr) ) {
                    // have to compare to Boolean.TRUE because in() might return null
                    if (in(ctx, value, e, inNode) == Boolean.TRUE) {
                        return true;
                    }
                }
                return false;
            } else {
                // evaluate single entity
                return in(ctx, value, expr, inNode);
            }
        }
        ctx.notifyEvt(() -> new ASTEventBase(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "Expression"),
                                             null));
        return null;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.BOOLEAN;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[]{value, exprs};
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    private static Boolean in(EvaluationContext ctx, Object value, Object expr, InNode inNode) {
        // need to improve this to work with unary tests
        if ( expr == null ) {
            return value == expr;
        } else if ( expr instanceof UnaryTest ) {
            return ((UnaryTest) expr).apply( ctx, value );
        } else if ( expr instanceof Range ) {
            try {
                return ((Range) expr).includes( value );
            } catch ( Exception e ) {
                ctx.notifyEvt(() -> new ASTEventBase(FEELEvent.Severity.ERROR,
                                                     Msg.createMessage(Msg.EXPRESSION_IS_RANGE_BUT_VALUE_IS_NOT_COMPARABLE, value.toString(), expr.toString()),
                                                     inNode,
                                                     e));
                return null;
            }
        } else if ( value != null ) {
            return ListContainsFunction.itemEqualsSC(value, expr);
        } else {
            // value == null, expr != null
            return Boolean.FALSE;
        }
    }
}
