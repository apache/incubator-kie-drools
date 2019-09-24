package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
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
        return lhs.getType().flatMap(t -> {
            if (BigDecimal.class.equals(t)) {
                return asBigDecimalValueOf(n);
            }
            return Optional.empty();
        });
    }

    private Optional<TypedExpression> asBigDecimalValueOf(IntegerLiteralExpr n) {
        Optional<TypedExpression> bigDecimal = Optional.of(new SimpleNameTExpr(BigDecimal.class.getCanonicalName(), null));
        List<TypedExpression> arguments = Collections.singletonList(new IntegerLiteralExpressionT(new IntegerLiteralExpr(n.asInt())));
        MethodCallExprT valueOf = new MethodCallExprT("valueOf", bigDecimal, arguments, Optional.of(BigDecimal.class));
        return Optional.of(valueOf);
    }
}
