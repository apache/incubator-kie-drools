package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
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
