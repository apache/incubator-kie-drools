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
package org.kie.dmn.feel.codegen.feel11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.CTypeNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.visitor.DefaultedVisitor;

public class ASTUnaryTestTransform extends DefaultedVisitor<ASTUnaryTestTransform.UnaryTestSubexpr> {

    @Override
    public UnaryTestSubexpr defaultVisit(ASTNode n) {
        return propagateWildcard(n);
    }

    @Override
    public UnaryTestSubexpr visit(UnaryTestListNode n) {
        List<BaseNode> collect =
                new ArrayList<>();
        for (BaseNode e : n.getElements()) {
            UnaryTestSubexpr accept = e.accept(this);
            if (accept.isWildcard()) {
                collect.add(rewriteToUnaryTestExpr(accept.node));
            } else if (!accept.isUnaryTest()) {
                collect.add(rewriteToUnaryEqInExpr(accept.node));
            } else {
                collect.add(accept.node);
            }
        }
        return new TopLevel(new UnaryTestListNode(collect, n.getState()).copyLocationAttributesFrom(n));
    }

    private BaseNode rewriteToUnaryTestExpr(BaseNode node) {
        return new UnaryTestNode("test", node).copyLocationAttributesFrom(node);
    }

    public BaseNode rewriteToUnaryEqInExpr(BaseNode node) {
        if (node instanceof ListNode || node instanceof RangeNode) {
            return new UnaryTestNode("in", node).copyLocationAttributesFrom(node);
        } else {
            return new UnaryTestNode("=", node).copyLocationAttributesFrom(node);
        }
    }

    @Override
    public UnaryTestSubexpr visit(ASTNode n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnaryTestSubexpr visit(DashNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(BooleanNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(NumberNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(StringNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(NullNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(CTypeNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(NameDefNode n) {
        return new SimpleUnaryExpression(n);
    }

    @Override
    public UnaryTestSubexpr visit(NameRefNode n) {
        if (n.getText().equals("?")) {
            return new WildCardUnaryExpression(n);
        } else {
            return new SimpleUnaryExpression(n);
        }
    }

    @Override
    public UnaryTestSubexpr visit(QualifiedNameNode n) {
        // wildcard is allowed only as the first part
        UnaryTestSubexpr leading =
                n.getParts().get(0).accept(this);
        if (leading.isWildcard()) {
            return new WildCardUnaryExpression(n);
        } else {
            return new SimpleUnaryExpression(n);
        }
    }

    private UnaryTestSubexpr propagateWildcard(ASTNode n) {
        return Arrays.stream(n.getChildrenNode()).map(e -> e.accept(this)).anyMatch(UnaryTestSubexpr::isWildcard) ?
                new WildCardUnaryExpression((BaseNode) n) : new SimpleUnaryExpression((BaseNode) n);
    }

    public static abstract class UnaryTestSubexpr {

        private final BaseNode node;

        public UnaryTestSubexpr(BaseNode node) {
            this.node = node;
        }

        public BaseNode node() {
            return node;
        }

        public abstract boolean isWildcard();

        public boolean isUnaryTest() {
            return node instanceof UnaryTestNode || node instanceof DashNode;
        }
    }

    static class TopLevel extends UnaryTestSubexpr {

        public TopLevel(BaseNode node) {
            super(node);
        }

        @Override
        public boolean isWildcard() {
            return false;
        }
    }

    static class WildCardUnaryExpression extends UnaryTestSubexpr {

        public WildCardUnaryExpression(BaseNode node) {
            super(node);
        }

        @Override
        public boolean isWildcard() {
            return true;
        }
    }

    static class SimpleUnaryExpression extends UnaryTestSubexpr {

        public SimpleUnaryExpression(BaseNode node) {
            super(node);
        }

        @Override
        public boolean isWildcard() {
            return false;
        }
    }
}
