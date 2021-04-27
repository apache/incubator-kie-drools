/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.drlxparse;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;

public class MultipleDrlxParseSuccess extends AbstractDrlxParseSuccess {

    private final BinaryExpr.Operator operator;
    private final DrlxParseSuccess[] results;

    public MultipleDrlxParseSuccess( BinaryExpr.Operator operator, DrlxParseSuccess... results ) {
        this.operator = operator;
        this.results = results;
    }

    public BinaryExpr.Operator getOperator() {
        return operator;
    }

    public DrlxParseSuccess[] getResults() {
        return results;
    }

    @Override
    public boolean isTemporal() {
        return Stream.of(results).anyMatch( DrlxParseSuccess::isTemporal );
    }

    @Override
    public Optional<Expression> getImplicitCastExpression() {
        return Optional.empty();
    }

    @Override
    public DrlxParseResult combineWith( DrlxParseResult other, BinaryExpr.Operator operator ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getExprId(DRLIdGenerator exprIdGenerator) {
        return Arrays.stream(results).map(s -> s.getExprId(exprIdGenerator)).collect(Collectors.joining());
    }

    @Override
    public DrlxParseResult setOriginalDrlConstraint(String originalDrlConstraint) {
        return this;
    }

    @Override
    public String getOriginalDrlConstraint() {
        return Arrays.stream(results).map(DrlxParseResult::getOriginalDrlConstraint).collect(Collectors.joining());
    }

    @Override
    public boolean isPredicate() {
        return Stream.of(results).allMatch( DrlxParseSuccess::isPredicate);
    }

    @Override
    public String getExprBinding() {
        return null;
    }

    @Override
    public Expression getExpr() {
        return new BinaryExpr( results[0].getExpr(), results[1].getExpr(), operator );
    }

    @Override
    public boolean isRequiresSplit() {
        return false;
    }
}
