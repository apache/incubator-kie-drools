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
package org.drools.mvel.parser.ast.visitor;

import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.FullyQualifiedInlineCastExpr;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.HalfPointFreeExpr;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpressionElement;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpressionKeyValuePair;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;
import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.RuleBody;
import org.drools.mvel.parser.ast.expr.RuleConsequence;
import org.drools.mvel.parser.ast.expr.RuleDeclaration;
import org.drools.mvel.parser.ast.expr.RulePattern;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralInfiniteChunkExpr;
import org.drools.mvel.parser.ast.expr.WithStatement;

/**
 * Should be used instead of .clone() when cloning DRL AST
 * drlExpr.accept(new DrlCloneVisitor(), null);
 */
public class DrlCloneVisitor extends CloneVisitor implements DrlGenericVisitor<Visitable, Object> {

    @Override
    public Visitable visit(RuleDeclaration ruleDeclaration, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(RuleBody n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(RulePattern n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(DrlxExpression n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(OOPathExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(OOPathChunk n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(RuleConsequence n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(InlineCastExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(FullyQualifiedInlineCastExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(NullSafeFieldAccessExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(NullSafeMethodCallExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(PointFreeExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(TemporalLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(TemporalLiteralChunkExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(HalfBinaryExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(HalfPointFreeExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(BigDecimalLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(BigIntegerLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(TemporalLiteralInfiniteChunkExpr n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(DrlNameExpr n, Object arg) {
        return new DrlNameExpr(n.getName(), n.getBackReferencesCount());
    }

    @Override
    public Visitable visit(ModifyStatement n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(MapCreationLiteralExpression n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(ListCreationLiteralExpression n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(MapCreationLiteralExpressionKeyValuePair n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(ListCreationLiteralExpressionElement n, Object arg) {
        return null;
    }

    @Override
    public Visitable visit(WithStatement withStatement, Object arg) {
        return null;
    }
}
