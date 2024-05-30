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
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class InfixOpNode
        extends BaseNode {

    private InfixOperator operator;
    private BaseNode left;
    private BaseNode right;

    public InfixOpNode(ParserRuleContext ctx, BaseNode left, String op, BaseNode right) {
        super(ctx);
        this.left = left;
        this.operator = InfixOperator.determineOperator(op);
        this.right = right;
    }

    public InfixOpNode(InfixOperator operator, BaseNode left, BaseNode right, String text) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        this.setText(text);
    }

    public InfixOperator getOperator() {
        return operator;
    }

    public void setOperator(InfixOperator operator) {
        this.operator = operator;
    }

    public boolean isBoolean() {
        return this.operator.isBoolean();
    }

    public BaseNode getLeft() {
        return left;
    }

    public void setLeft(BaseNode left) {
        this.left = left;
    }

    public BaseNode getRight() {
        return right;
    }

    public void setRight(BaseNode right) {
        this.right = right;
    }

    @Override
    public Type getResultType() {
        // see FEEL spec Table 45.
        if (operator.isBoolean()) {
            return BuiltInType.BOOLEAN;
        }
        switch (operator) {
            case ADD:
            case SUB: {
                if (left.getResultType() == BuiltInType.NUMBER && right.getResultType() == BuiltInType.NUMBER) {
                    return BuiltInType.NUMBER;
                } else if (left.getResultType() == BuiltInType.DATE_TIME && right.getResultType() == BuiltInType.DATE_TIME) {
                    return BuiltInType.DATE_TIME;
                } else if (left.getResultType() == BuiltInType.TIME && right.getResultType() == BuiltInType.TIME) {
                    return BuiltInType.TIME;
                } else if (left.getResultType() == BuiltInType.DURATION || right.getResultType() == BuiltInType.DURATION) {
                    if (left.getResultType() == BuiltInType.DATE_TIME || right.getResultType() == BuiltInType.DATE_TIME) {
                        return BuiltInType.DATE_TIME;
                    } else if (left.getResultType() == BuiltInType.TIME || right.getResultType() == BuiltInType.TIME) {
                        return BuiltInType.TIME;
                    } else if (left.getResultType() == BuiltInType.DURATION && right.getResultType() == BuiltInType.DURATION) {
                        return BuiltInType.DURATION;
                    }
                } else if (left.getResultType() == BuiltInType.STRING && right.getResultType() == BuiltInType.STRING) {
                    return BuiltInType.STRING;
                }
            }
            case MULT:
            case DIV: {
                if (left.getResultType() == BuiltInType.NUMBER && right.getResultType() == BuiltInType.NUMBER) {
                    return BuiltInType.NUMBER;
                } else if (left.getResultType() == BuiltInType.DURATION || right.getResultType() == BuiltInType.DURATION) {
                    if (left.getResultType() == BuiltInType.NUMBER || right.getResultType() == BuiltInType.NUMBER) {
                        return BuiltInType.NUMBER;
                    }
                }
            }
            case POW: {
                if (left.getResultType() == BuiltInType.NUMBER && right.getResultType() == BuiltInType.NUMBER) {
                    return BuiltInType.NUMBER;
                }
            }
            default:
                return BuiltInType.UNKNOWN;
        }
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if (this.left == null) return null;
        return operator.evaluate(this, ctx);
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[]{left, right};
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
