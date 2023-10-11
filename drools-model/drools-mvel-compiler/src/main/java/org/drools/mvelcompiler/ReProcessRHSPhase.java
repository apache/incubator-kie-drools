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
package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.BigDecimalConvertedExprT;
import org.drools.mvelcompiler.ast.DoubleLiteralExpressionT;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.LongLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

/**
 * Used when you need to reprocess the RHS after having processed the LHS
 */
public class ReProcessRHSPhase implements DrlGenericVisitor<Optional<TypedExpression>, ReProcessRHSPhase.Context> {

    private TypedExpression lhs;
    private MvelCompilerContext mvelCompilerContext;

    static class Context {
        private UnaryExpr unaryExpr;

        Context withUnaryExpr(UnaryExpr unaryExpr) {
            this.unaryExpr = unaryExpr;
            return this;
        }

        Optional<UnaryExpr> getUnaryExpr() {
            return Optional.ofNullable(unaryExpr);
        }
    }

    ReProcessRHSPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public Optional<TypedExpression> invoke(TypedExpression rhs, TypedExpression lhs) {
        this.lhs = lhs;
        return Optional.ofNullable(rhs).flatMap(r -> r.toJavaExpression().accept(this, new Context()));
    }

    @Override
    public Optional<TypedExpression> defaultMethod(Node n, ReProcessRHSPhase.Context context) {
        return Optional.empty();
    }

    @Override
    public Optional<TypedExpression> visit(UnaryExpr n, ReProcessRHSPhase.Context context) {
        return n.getExpression().accept(this, context.withUnaryExpr(n));
    }

    @Override
    public Optional<TypedExpression> visit(BinaryExpr n, ReProcessRHSPhase.Context context) {
        return convertWhenLHSISBigDecimal(() -> new UnalteredTypedExpression(n), context);
    }

    @Override
    public Optional<TypedExpression> visit(IntegerLiteralExpr n, ReProcessRHSPhase.Context context) {
        return convertWhenLHSISBigDecimal(() -> new IntegerLiteralExpressionT(n), context);
    }

    @Override
    public Optional<TypedExpression> visit(DoubleLiteralExpr n, ReProcessRHSPhase.Context context) {
        return convertWhenLHSISBigDecimal(() -> new DoubleLiteralExpressionT(n), context);
    }

    @Override
    public Optional<TypedExpression> visit(LongLiteralExpr n, ReProcessRHSPhase.Context context) {
        return convertWhenLHSISBigDecimal(() -> new LongLiteralExpressionT(n), context);
    }

    @Override
    public Optional<TypedExpression> visit(NameExpr n, ReProcessRHSPhase.Context context) {
        if(mvelCompilerContext
                .findDeclarations(n.toString())
                .filter(d -> d.getClazz() != BigDecimal.class)
                .isPresent()) { // avoid wrapping BigDecimal declarations
            return convertWhenLHSISBigDecimal(() -> new UnalteredTypedExpression(n), context);
        } else {
            return Optional.empty();
        }
    }

    private Optional<TypedExpression> convertWhenLHSISBigDecimal(Supplier<TypedExpression> conversionFunction, ReProcessRHSPhase.Context context) {
        return lhs.getType()
                .filter(BigDecimal.class::equals)
                .flatMap(t -> Optional.of(new BigDecimalConvertedExprT(conversionFunction.get(), context.getUnaryExpr())));
    }
}
