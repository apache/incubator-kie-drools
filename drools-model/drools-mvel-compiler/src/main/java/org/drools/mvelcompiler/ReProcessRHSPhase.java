package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.BigDecimalExprT;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.LongLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;

/**
 * Used when you need to reprocess the RHS after having processed the LHS
 */
public class ReProcessRHSPhase implements DrlGenericVisitor<Optional<TypedExpression>, Void> {

    private TypedExpression lhs;

    ReProcessRHSPhase() {
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
    public Optional<TypedExpression> visit(IntegerLiteralExpr n, Void arg) {
        return convertWhenLHSISBigDecimal(() -> new IntegerLiteralExpressionT(n));
    }

    @Override
    public Optional<TypedExpression> visit(LongLiteralExpr n, Void arg) {
        return convertWhenLHSISBigDecimal(() -> new LongLiteralExpressionT(n));
    }

    private Optional<TypedExpression> convertWhenLHSISBigDecimal(Supplier<TypedExpression> conversionFunction) {
        return lhs.getType()
                .filter(BigDecimal.class::equals)
                .flatMap(t -> Optional.of(BigDecimalExprT.valueOf(conversionFunction.get())));
    }
}
