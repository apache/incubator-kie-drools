package org.drools.mvelcompiler;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
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
