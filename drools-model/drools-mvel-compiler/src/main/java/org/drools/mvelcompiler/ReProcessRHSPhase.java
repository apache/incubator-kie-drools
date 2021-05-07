/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.BigDecimalConvertedExprT;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.LongLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

/**
 * Used when you need to reprocess the RHS after having processed the LHS
 */
public class ReProcessRHSPhase implements DrlGenericVisitor<Optional<TypedExpression>, Void> {

    private TypedExpression lhs;
    private MvelCompilerContext mvelCompilerContext;

    ReProcessRHSPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public Optional<TypedExpression> invoke(TypedExpression rhs, TypedExpression lhs) {
        this.lhs = lhs;
        return Optional.ofNullable(rhs).flatMap(r -> r.toJavaExpression().accept(this, null));
    }

    @Override
    public Optional<TypedExpression> defaultMethod(Node n, Void context) {
        return Optional.empty();
    }

    @Override
    public Optional<TypedExpression> visit(BinaryExpr n, Void arg) {
        return convertWhenLHSISBigDecimal(() -> new UnalteredTypedExpression(n));
    }

    @Override
    public Optional<TypedExpression> visit(IntegerLiteralExpr n, Void arg) {
        return convertWhenLHSISBigDecimal(() -> new IntegerLiteralExpressionT(n));
    }

    @Override
    public Optional<TypedExpression> visit(LongLiteralExpr n, Void arg) {
        return convertWhenLHSISBigDecimal(() -> new LongLiteralExpressionT(n));
    }

    @Override
    public Optional<TypedExpression> visit(NameExpr n, Void arg) {
        if(mvelCompilerContext
                .findDeclarations(n.toString())
                .filter(d -> d.getClazz() != BigDecimal.class)
                .isPresent()) { // avoid wrapping BigDecimal declarations
            return convertWhenLHSISBigDecimal(() -> new UnalteredTypedExpression(n));
        } else {
            return Optional.empty();
        }
    }

    private Optional<TypedExpression> convertWhenLHSISBigDecimal(Supplier<TypedExpression> conversionFunction) {
        return lhs.getType()
                .filter(BigDecimal.class::equals)
                .flatMap(t -> Optional.of(new BigDecimalConvertedExprT(conversionFunction.get())));
    }
}
