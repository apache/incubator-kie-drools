/*
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
package org.kie.dmn.feel.lang.ast.visitor;

import org.kie.dmn.feel.lang.ast.*;

public abstract class DefaultedVisitor<T> implements Visitor<T> {

    public abstract T defaultVisit(ASTNode n);

    @Override
    public T visit(ASTNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(DashNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(DescendantExpressionNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(BooleanNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(NumberNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(StringNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(NullNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(CTypeNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(NameDefNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(NameRefNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(QualifiedNameNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(InfixOpNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(InstanceOfNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(IfExpressionNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(ForExpressionNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(BetweenNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(ContextNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(ContextEntryNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(FilterExpressionNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(FunctionDefNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(FunctionInvocationNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(NamedParameterNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(InNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(IterationContextNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(ListNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(PathExpressionNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(QuantifiedExpressionNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(RangeNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(RangeTypeNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(SignedUnaryNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(UnaryTestNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(UnaryTestListNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(FormalParameterNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(AtLiteralNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(ListTypeNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(ContextTypeNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(FunctionTypeNode n) {
        return defaultVisit(n);
    }

    @Override
    public T visit(UndefinedValueNode n) {
        return defaultVisit(n);
    }

}
