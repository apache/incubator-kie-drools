package org.drools.mvelcompiler;

import java.lang.reflect.Method;
import java.util.Stack;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.SimpleName;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.FieldAccessTExpr;
import org.drools.mvelcompiler.ast.MethodCallTExpr;
import org.drools.mvelcompiler.ast.NameTExpr;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getAccessor;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class TypedExpressionPhase implements DrlGenericVisitor<TypedExpression, TypedExpressionPhase.Context> {

    static class Context {

        Stack<TypedExpression> lastTypedExpression = new Stack<>();
    }

    private final MvelCompilerContext mvelCompilerContext;

    public TypedExpressionPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public TypedExpression invoke(Expression expression) {
        Context ctx = new Context();

        TypedExpression typedExpression = expression.accept(this, ctx);
        if(typedExpression == null) {
            throw new MvelCompilerException("Type check of " + printConstraint(expression) + " failed.");
        }
        return typedExpression;
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Context arg) {
        TypedExpression expression = new FieldAccessTExpr(n);
        for (Node children : n.getChildNodes()) {
            expression.addChildren(children.accept(this, arg));
        }
        return expression;
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
    public TypedExpression visit(SimpleName n, Context arg) {
        if (arg.lastTypedExpression.isEmpty()) { // first node
            return simpleNameAsFirstNode(n, arg);
        } else {
            return simpleNameAsField(n, arg);
        }
    }

    private SimpleNameTExpr simpleNameAsFirstNode(SimpleName n, Context arg) {
        Declaration typeFromDeclarations = mvelCompilerContext.getDeclarations(n.asString());
        Class<?> clazz = typeFromDeclarations.getClazz();
        SimpleNameTExpr simpleNameTExpr = new SimpleNameTExpr(n, clazz);
        arg.lastTypedExpression.push(simpleNameTExpr);
        return simpleNameTExpr;
    }

    private TypedExpression simpleNameAsField(SimpleName n, Context arg) {
        TypedExpression lastTypedExpression = arg.lastTypedExpression.peek();
        Method accessor = getAccessor(toRawClass(lastTypedExpression.getType()), n.asString());
        MethodCallTExpr methodCallTExpr = new MethodCallTExpr(n, lastTypedExpression, accessor);
        arg.lastTypedExpression.push(methodCallTExpr);
        return methodCallTExpr;
    }
}

