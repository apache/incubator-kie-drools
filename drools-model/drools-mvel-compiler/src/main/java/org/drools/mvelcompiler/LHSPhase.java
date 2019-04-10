package org.drools.mvelcompiler;

import java.lang.reflect.Method;
import java.util.Optional;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.ExpressionStmtT;
import org.drools.mvelcompiler.ast.FieldAccessTExpr;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.VariableDeclaratorTExpr;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;
import static org.drools.core.util.ClassUtils.getSetter;

public class LHSPhase implements DrlGenericVisitor<TypedExpression, LHSPhase.Context> {

    Logger logger = LoggerFactory.getLogger(LHSPhase.class);

    static class Context {

    }

    private final MvelCompilerContext mvelCompilerContext;
    private final TypedExpression rhs;

    public LHSPhase(MvelCompilerContext mvelCompilerContext, TypedExpression rhs) {
        this.mvelCompilerContext = mvelCompilerContext;
        this.rhs = rhs;
    }

    public TypedExpression invoke(Statement statement) {
        logger.debug("RHS phase on:\t\t" + printConstraint(statement));
        Context ctx = new Context();

        TypedExpression typedExpression = statement.accept(this, ctx);
        if (typedExpression == null) {
            throw new MvelCompilerException("Type check of " + printConstraint(statement) + " failed.");
        }
        logger.debug("RHS phase completed");
        return typedExpression;
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, Context arg) {
        logger.debug("DrlNameExpr:\t\t" + printConstraint(n));

        String variableName = printConstraint(n);
        Optional<Declaration> declaration = mvelCompilerContext.findDeclarations(variableName);

        return declaration.<TypedExpression>map(d -> new SimpleNameTExpr(n.getNameAsString(), d.getClazz()))
                .orElseGet(() -> {
                    mvelCompilerContext.addDeclaration(variableName, getRHSType());
                    return new VariableDeclaratorTExpr(n, variableName, getRHSType(), Optional.of(rhs));
                });
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Context arg) {
        logger.debug("FieldAccessExpr:\t\t" + printConstraint(n));

        if (parentIsExpressionStmt(n)) {
            return rhs;
        }

        TypedExpression scope = n.getScope().accept(this, arg);
        TypedExpression name = n.getName().accept(this, arg);

        Class<?> setterArgumentType = getRHSType();

        return tryParseItAsSetter(n, scope, setterArgumentType)
                .orElse(new FieldAccessTExpr(scope, null)); // TODO public field access
    }

    private Optional<TypedExpression> tryParseItAsSetter(FieldAccessExpr n, TypedExpression scope, Class<?> setterArgumentType) {
        return scope.getType().map(scopeType -> {
            String setterName = printConstraint(n.getName());
            Method accessor = getSetter((Class<?>) scopeType, setterName, setterArgumentType);

            return new FieldToAccessorTExpr(scope, accessor, singletonList(rhs));
        });
    }

    @Override
    public TypedExpression visit(MethodCallExpr n, Context arg) {
        logger.debug("MethodCallExpr:\t\t" + printConstraint(n));

        return rhs;
    }

    @Override
    public TypedExpression visit(VariableDeclarationExpr n, Context arg) {
        logger.debug("VariableDeclarationExpr:\t\t" + printConstraint(n));

        // assuming there's always one declaration for variable
        return n.getVariables().iterator().next().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarator n, Context arg) {
        logger.debug("VariableDeclarator:\t\t" + printConstraint(n));

        Optional<TypedExpression> initExpression = Optional.of(rhs);

        String variableName = n.getName().asString();
        Class<?> type = getRHSorLHSType(n);

        mvelCompilerContext.addDeclaration(variableName, type);

        return new VariableDeclaratorTExpr(n, variableName, type, initExpression);
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, Context arg) {
        logger.debug("ExpressionStmt:\t\t" + printConstraint(n));

        TypedExpression expression = n.getExpression().accept(this, arg);
        return new ExpressionStmtT(expression);
    }

    @Override
    public TypedExpression visit(AssignExpr n, Context arg) {
        logger.debug("AssignExpr:\t\t" + printConstraint(n));

        TypedExpression target = n.getTarget().accept(this, arg);
        if (target instanceof FieldToAccessorTExpr) {
            return target;
        } else if (target instanceof VariableDeclaratorTExpr) {
            return target;
        } else {
            return new AssignExprT(n.getOperator(), target, rhs);
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

    private Class<?> getRHSorLHSType(VariableDeclarator n) {
        return (Class<?>) rhs.getType()
                .orElseGet(() -> mvelCompilerContext.resolveType(((NodeWithType) n).getType().asString()));
    }
}

