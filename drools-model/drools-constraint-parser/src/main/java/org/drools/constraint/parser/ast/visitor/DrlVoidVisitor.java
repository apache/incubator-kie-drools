/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.constraint.parser.ast.visitor;

import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.constraint.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.constraint.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.constraint.parser.ast.expr.CommaSeparatedMethodCallExpr;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.expr.DrlxExpression;
import org.drools.constraint.parser.ast.expr.HalfBinaryExpr;
import org.drools.constraint.parser.ast.expr.HalfPointFreeExpr;
import org.drools.constraint.parser.ast.expr.InlineCastExpr;
import org.drools.constraint.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.constraint.parser.ast.expr.MapCreationLiteralExpressionKeyValuePair;
import org.drools.constraint.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.constraint.parser.ast.expr.NullSafeMethodCallExpr;
import org.drools.constraint.parser.ast.expr.OOPathChunk;
import org.drools.constraint.parser.ast.expr.OOPathExpr;
import org.drools.constraint.parser.ast.expr.PointFreeExpr;
import org.drools.constraint.parser.ast.expr.RuleBody;
import org.drools.constraint.parser.ast.expr.RuleConsequence;
import org.drools.constraint.parser.ast.expr.RuleDeclaration;
import org.drools.constraint.parser.ast.expr.RulePattern;
import org.drools.constraint.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.constraint.parser.ast.expr.TemporalLiteralExpr;
import org.drools.constraint.parser.ast.expr.TemporalLiteralInfiniteChunkExpr;

public interface DrlVoidVisitor<A> extends VoidVisitor<A> {

    default void visit(RuleDeclaration ruleDeclaration, A arg) { }

    default void visit(RuleBody ruleBody, A arg) { }

    default void visit(RulePattern rulePattern, A arg) { }

    default void visit(DrlxExpression expr, A arg) { }

    default void visit(OOPathExpr expr, A arg) { }

    default void visit(OOPathChunk chunk, A arg) { }

    default void visit(RuleConsequence ruleConsequence, A arg) { }

    default void visit(InlineCastExpr inlineCastExpr, A arg) { }

    default void visit(NullSafeFieldAccessExpr nullSafeFieldAccessExpr, A arg) { }

    default void visit(NullSafeMethodCallExpr nullSafeMethodCallExpr, A arg) { }

    default void visit(PointFreeExpr pointFreeExpr, A arg) { }

    default void visit(TemporalLiteralExpr temporalLiteralExpr, A arg) { }

    default void visit(TemporalLiteralChunkExpr temporalLiteralChunkExpr, A arg) {}

    default void visit(HalfBinaryExpr n, A arg) {}

    default void visit(HalfPointFreeExpr n, A arg) {}

    default void visit(BigDecimalLiteralExpr bigDecimalLiteralExpr, A arg) {}

    default void visit(BigIntegerLiteralExpr bigIntegerLiteralExpr, A arg) {}

    default void visit(TemporalLiteralInfiniteChunkExpr temporalLiteralInfiniteChunkExpr, A arg) { }

    default void visit(CommaSeparatedMethodCallExpr commaSeparatedMethodCallExpr, A arg) {  }

    default void visit(DrlNameExpr drlNameExpr, A arg) { };

    default void visit(MapCreationLiteralExpression n, A arg) { };

    default void visit(MapCreationLiteralExpressionKeyValuePair n, A arg) { };
}
