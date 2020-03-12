/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.kie.dmn.feel.lang.ast;

public interface Visitor<T> {
    T visit(ASTNode n);
    T visit(DashNode n);
    T visit(BooleanNode n);
    T visit(NumberNode n);
    T visit(StringNode n);
    T visit(NullNode n);
    T visit(CTypeNode n);
    T visit(NameDefNode n);
    T visit(NameRefNode n);
    T visit(QualifiedNameNode n);
    T visit(InfixOpNode n);
    T visit(InstanceOfNode n);
    T visit(IfExpressionNode n);
    T visit(ForExpressionNode n);
    T visit(BetweenNode n);
    T visit(ContextNode n);
    T visit(ContextEntryNode n);
    T visit(FilterExpressionNode n);
    T visit(FunctionDefNode n);
    T visit(FunctionInvocationNode n);
    T visit(NamedParameterNode n);
    T visit(InNode n);
    T visit(IterationContextNode n);
    T visit(ListNode n);
    T visit(PathExpressionNode n);
    T visit(QuantifiedExpressionNode n);
    T visit(RangeNode n);
    T visit(SignedUnaryNode n);
    T visit(UnaryTestNode n);
    T visit(UnaryTestListNode n);
    T visit(FormalParameterNode n);
    T visit(AtLiteralNode n);
    T visit(ListTypeNode n);
    T visit(ContextTypeNode n);
    T visit(FunctionTypeNode n);
}