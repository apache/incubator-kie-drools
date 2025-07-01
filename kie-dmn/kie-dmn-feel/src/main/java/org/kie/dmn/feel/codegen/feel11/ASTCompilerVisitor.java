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
package org.kie.dmn.feel.codegen.feel11;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.dmn.feel.lang.ast.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASTCompilerVisitor implements Visitor<BlockStmt> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASTCompilerVisitor.class);
    private final ASTCompilerHelper compilerHelper;

    public ASTCompilerVisitor() {
        compilerHelper = new ASTCompilerHelper(this);
    }

    @Override
    public BlockStmt visit(ASTNode n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public BlockStmt visit(AtLiteralNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(BetweenNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(BooleanNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(ContextEntryNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(ContextNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(ContextTypeNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(CTypeNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(DashNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(DescendantExpressionNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(ForExpressionNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(FilterExpressionNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(FormalParameterNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(FunctionDefNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(FunctionInvocationNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(FunctionTypeNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(IfExpressionNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(InfixOpNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(InNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(InstanceOfNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(IterationContextNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(ListNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(ListTypeNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(NameDefNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(NamedParameterNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(NameRefNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(NullNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(NumberNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(PathExpressionNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(QualifiedNameNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(QuantifiedExpressionNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(RangeNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(RangeTypeNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(SignedUnaryNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(StringNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(TemporalConstantNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(UnaryTestListNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(UnaryTestNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    @Override
    public BlockStmt visit(UndefinedValueNode n) {
        LOGGER.trace("visit {}", n);
        return compilerHelper.add(n);
    }

    public String getLastVariableName() {
        LOGGER.trace("getLastVariableName");
        return compilerHelper.getLastVariableName();
    }

    public BlockStmt returnError(String errorMessage) {
        LOGGER.trace("returnError {}", errorMessage);
        return compilerHelper.returnError(errorMessage);
    }

}
