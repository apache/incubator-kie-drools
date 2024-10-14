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

package org.kie.dmn.feel.util;

import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.CTypeNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.ContextTypeNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FormalParameterNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.FunctionTypeNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.ListTypeNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NamedParameterNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.PathExpressionNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.RangeTypeNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.Visitor;

public class MockVisitor implements Visitor<Object> {
    @Override
    public Object visit(ASTNode n) {
        return null;
    }

    @Override
    public Object visit(AtLiteralNode n) {
        return n;
    }

    @Override
    public Object visit(BetweenNode n) {
        return n;
    }

    @Override
    public Object visit(BooleanNode n) {
        return n;
    }

    @Override
    public Object visit(ContextNode n) {
        return n;
    }

    @Override
    public Object visit(ContextEntryNode n) {
        return n;
    }

    @Override
    public Object visit(ContextTypeNode n) {
        return n;
    }

    @Override
    public Object visit(CTypeNode n) {
        return n;
    }

    @Override
    public Object visit(DashNode n) {
        return n;
    }

    @Override
    public Object visit(FilterExpressionNode n) {
        return n;
    }

    @Override
    public Object visit(ForExpressionNode n) {
        return n;
    }

    @Override
    public Object visit(FormalParameterNode n) {
        return n;
    }

    @Override
    public Object visit(FunctionDefNode n) {
        return n;
    }

    @Override
    public Object visit(FunctionTypeNode n) {
        return n;
    }

    @Override
    public Object visit(FunctionInvocationNode n) {
        return n;
    }

    @Override
    public Object visit(IfExpressionNode n) {
        return n;
    }

    @Override
    public Object visit(InfixOpNode n) {
        return n;
    }

    @Override
    public Object visit(InNode n) {
        return n;
    }

    @Override
    public Object visit(InstanceOfNode n) {
        return n;
    }

    @Override
    public Object visit(IterationContextNode n) {
        return n;
    }

    @Override
    public Object visit(ListNode n) {
        return n;
    }

    @Override
    public Object visit(ListTypeNode n) {
        return n;
    }

    @Override
    public Object visit(NameDefNode n) {
        return n;
    }

    @Override
    public Object visit(NamedParameterNode n) {
        return n;
    }

    @Override
    public Object visit(NameRefNode n) {
        return n;
    }

    @Override
    public Object visit(NullNode n) {
        return n;
    }

    @Override
    public Object visit(NumberNode n) {
        return n;
    }

    @Override
    public Object visit(PathExpressionNode n) {
        return n;
    }

    @Override
    public Object visit(QualifiedNameNode n) {
        return n;
    }

    @Override
    public Object visit(QuantifiedExpressionNode n) {
        return n;
    }

    @Override
    public Object visit(RangeNode n) {
        return n;
    }

    @Override
    public Object visit(RangeTypeNode n) {
        return n;
    }

    @Override
    public Object visit(SignedUnaryNode n) {
        return n;
    }

    @Override
    public Object visit(StringNode n) {
        return n;
    }

    @Override
    public Object visit(UnaryTestListNode n) {
        return n;
    }

    @Override
    public Object visit(UnaryTestNode n) {
        return n;
    }
}
