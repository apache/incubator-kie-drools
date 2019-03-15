package org.drools.mvelcompiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.core.util.ClassUtils;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.ExpressionStmtT;
import org.drools.mvelcompiler.ast.FieldAccessTExpr;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.VariableDeclarationTExpr;
import org.drools.mvelcompiler.ast.VariableDeclaratorTExpr;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.Collections.singletonList;
import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;
import static org.drools.core.util.ClassUtils.getSetter;

public class LHSPhase implements DrlGenericVisitor<TypedExpression, LHSPhase.Context> {

    static class Context {
    }

    private final MvelCompilerContext mvelCompilerContext;
    private final TypedExpression rhs;

    public LHSPhase(MvelCompilerContext mvelCompilerContext, TypedExpression rhs) {
        this.mvelCompilerContext = mvelCompilerContext;
        this.rhs = rhs;
    }

    public TypedExpression invoke(Statement statement) {
        Context ctx = new Context();

        TypedExpression typedExpression = statement.accept(this, ctx);
        if (typedExpression == null) {
            throw new MvelCompilerException("Type check of " + printConstraint(statement) + " failed.");
        }
        return typedExpression;
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, Context arg) {
        Declaration typeFromDeclarations = mvelCompilerContext.getOrCreateDeclarations(printConstraint(n), getRHSType());
        Class<?> clazz = typeFromDeclarations.getClazz();
        return new SimpleNameTExpr(n, clazz);
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Context arg) {
        if(parentIsExpressionStmt(n)) {
            return rhs;
        }

        TypedExpression scope = n.getScope().accept(this, arg);
        TypedExpression name = n.getName().accept(this, arg);

        Class<?> setterArgumentType = getRHSType();

        return tryParseItAsSetter(n, scope, setterArgumentType)
                .orElse(new FieldAccessTExpr(n, scope, null)); // TODO public field access

    }

    private Optional<TypedExpression> tryParseItAsSetter(FieldAccessExpr n, TypedExpression scope, Class<?> setterArgumentType) {
        return scope.getType().map(scopeType -> {
            String setterName = printConstraint(n.getName());
            Method accessor = getSetter((Class<?>) scopeType, setterName, setterArgumentType);

            return new FieldToAccessorTExpr(n, scope, accessor, singletonList(rhs));
        });
    }

    @Override
    public TypedExpression visit(MethodCallExpr n, Context arg) {
        return rhs;
    }

    @Override
    public TypedExpression visit(VariableDeclarationExpr n, Context arg) {
        VariableDeclarationTExpr expr = new VariableDeclarationTExpr(n);
        for (Node e : n.getChildNodes()) {
            expr.addChildren(e.accept(this, arg));
        }
        return expr;
    }

    @Override
    public TypedExpression visit(VariableDeclarator n, Context arg) {
        Optional<TypedExpression> initExpression = Optional.of(rhs);
        return new VariableDeclaratorTExpr(n, n.getName(), initExpression);
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, Context arg) {
        TypedExpression expression = n.getExpression().accept(this, arg);
        return new ExpressionStmtT(n, expression);
    }

    @Override
    public TypedExpression visit(AssignExpr n, Context arg) {
        TypedExpression target = n.getTarget().accept(this, arg);
        if(target instanceof FieldToAccessorTExpr) {
            return target;
        } else {
            return new AssignExprT(n, target, rhs);
        }
    }

    /*
        This means there's not LHS
     */
    private boolean parentIsExpressionStmt(FieldAccessExpr n) {
        return n.getParentNode().filter(p -> p instanceof ExpressionStmt).isPresent();
    }

    private Class<?> getRHSType() {
        return (Class<?>) rhs.getType().orElseThrow(() -> new MvelCompilerException("RHS doesn't have a type"));
    }
}

