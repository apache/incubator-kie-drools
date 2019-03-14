package org.drools.mvelcompiler;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Stack;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.ExpressionStmtT;
import org.drools.mvelcompiler.ast.MethodCallTExpr;
import org.drools.mvelcompiler.ast.NameTExpr;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.VariableDeclarationTExpr;
import org.drools.mvelcompiler.ast.VariableDeclaratorTExpr;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.Collections.singletonList;
import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;
import static org.drools.core.util.ClassUtils.getSetter;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class LHSPhase implements DrlGenericVisitor<TypedExpression, LHSPhase.Context> {

    static class Context {

        Stack<TypedExpression> lastTypedExpression = new Stack<>();
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
    public TypedExpression visit(SimpleName n, Context arg) {
        Declaration typeFromDeclarations = mvelCompilerContext.getDeclarations(n.asString());
        Class<?> clazz = typeFromDeclarations.getClazz();
        SimpleNameTExpr simpleNameTExpr = new SimpleNameTExpr(n, clazz);
        arg.lastTypedExpression.push(simpleNameTExpr);
        return simpleNameTExpr;
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, Context arg) {
        NameTExpr nameTExpr = new NameTExpr(n, null);
        for (Node children : n.getChildNodes()) {
            nameTExpr.addChildren(children.accept(this, arg));
        }
        return nameTExpr;
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Context arg) {
        TypedExpression scope = n.getScope().accept(this, arg);
        TypedExpression lastTypedExpression = arg.lastTypedExpression.peek();

        Class<?> setterArgumentType = (Class<?>) rhs.getType();
        Class<?> objectClass = toRawClass(lastTypedExpression.getType());
        String setterName = printConstraint(n.getName());
        Method accessor = getSetter(objectClass, setterName, setterArgumentType);

        return new MethodCallTExpr(n, scope, accessor, singletonList(rhs));
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
        ExpressionStmtT expressionStmtT = new ExpressionStmtT(n);
        TypedExpression expression = n.getExpression().accept(this, arg);
        expressionStmtT.addChildren(expression);
        return expressionStmtT;
    }

    @Override
    public TypedExpression visit(AssignExpr n, Context arg) {
        TypedExpression target = n.getTarget().accept(this, arg);
        if(target instanceof MethodCallTExpr) {
            return target;
        } else {
            return new AssignExprT(n, target, rhs);
        }
    }
}

