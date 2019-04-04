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

import com.github.javaparser.ast.visitor.GenericVisitor;
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

public interface DrlGenericVisitor<R, A> extends GenericVisitor<R,A> {
    default R visit(RuleDeclaration ruleDeclaration, A arg) { return null; }

    default R visit(RuleBody ruleBody, A arg) { return null; }

    default R visit(RulePattern rulePattern, A arg) { return null; }

    default R visit(DrlxExpression expr, A arg) { return null; }

    default R visit(OOPathExpr expr, A arg) { return null; }

    default R visit(OOPathChunk chunk, A arg) { return null; }

    default R visit(RuleConsequence ruleConsequence, A arg) { return null; }

    default R visit(InlineCastExpr inlineCastExpr, A arg) { return null; }

    default R visit(NullSafeFieldAccessExpr nullSafeFieldAccessExpr, A arg) { return null; }

    default R visit(NullSafeMethodCallExpr nullSafeMethodCallExpr, A arg) { return null; }

    default R visit(PointFreeExpr pointFreeExpr, A arg) { return null; }

    default R visit(TemporalLiteralExpr temporalLiteralExpr, A arg) { return null; }

    default R visit(TemporalLiteralChunkExpr temporalLiteralChunkExpr, A arg) { return null; }

    default R visit(HalfBinaryExpr n, A arg) { return null; }

    default R visit(HalfPointFreeExpr n, A arg) { return null; }

    default R visit(BigDecimalLiteralExpr bigDecimalLiteralExpr, A arg) { return null; }

    default R visit(BigIntegerLiteralExpr bigIntegerLiteralExpr, A arg) { return null; }

    default R visit(TemporalLiteralInfiniteChunkExpr temporalLiteralInfiniteChunkExpr, A arg) { return null; }

    default R visit(CommaSeparatedMethodCallExpr commaSeparatedMethodCallExpr, A arg) { throw new UnsupportedOperationException(); }

    default R visit(DrlNameExpr drlNameExpr, A arg) { return null; };

    default R visit(MapCreationLiteralExpression n, A arg) { return null; };

    default R visit(MapCreationLiteralExpressionKeyValuePair n, A arg) { return null; };
}
