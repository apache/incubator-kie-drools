package org.drools.mvelcompiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.core.util.ClassUtils;
import org.drools.mvelcompiler.ast.BinaryTExpr;
import org.drools.mvelcompiler.ast.FieldAccessTExpr;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.ListAccessExprT;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.ObjectCreationExpressionT;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.StringLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.util.OptionalUtils;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;
import static org.drools.core.util.ClassUtils.getAccessor;
import static org.drools.mvelcompiler.util.OptionalUtils.map2;

public class RHSPhase implements DrlGenericVisitor<TypedExpression, RHSPhase.Context> {

    static class Context {
        final Optional<TypedExpression> scope;

        Context(TypedExpression scope) {
            this.scope = Optional.ofNullable(scope);
        }

        Optional<Type> getScopeType() {
            return scope.flatMap(TypedExpression::getType);
        }
    }

    private final MvelCompilerContext mvelCompilerContext;

    public RHSPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public TypedExpression invoke(Statement statement) {
        Context ctx = new Context(null);

        TypedExpression typedExpression = statement.accept(this, ctx);
        if (typedExpression == null) {
            throw new MvelCompilerException("Type check of " + printConstraint(statement) + " failed.");
        }
        return typedExpression;
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, Context arg) {
        return n.getName().accept(this, arg);
    }

    @Override
    public TypedExpression visit(SimpleName n, Context arg) {
        if (!arg.scope.isPresent()) { // first node
            return simpleNameAsFirstNode(n);
        } else {
            return simpleNameAsField(n, arg);
        }
    }

    private TypedExpression simpleNameAsFirstNode(SimpleName n) {
        return asDeclaration(n)
                .orElseGet(() -> new UnalteredTypedExpression(n));
    }

    private TypedExpression simpleNameAsField(SimpleName n, Context arg) {
        return asPropertyAccessor(n, arg)
                .map(Optional::of)
                .orElseGet(() -> asFieldAccessTExpr(n, arg))
                .orElseGet(() -> new UnalteredTypedExpression(n));
    }

    private Optional<TypedExpression> asFieldAccessTExpr(SimpleName n, Context arg) {
        Optional<TypedExpression> lastTypedExpression = arg.scope;
        Optional<Type> typedExpression = arg.getScopeType();

        Optional<Field> fieldType = typedExpression.flatMap(te -> {
            Class parentClass = (Class) te;
            Field field = ClassUtils.getField(parentClass, n.asString());
            return Optional.ofNullable(field);
        });

        return OptionalUtils.map2(lastTypedExpression, fieldType, (te, ft) -> {
            Node parent = n.getParentNode().get(); // TODO fix this
            return new FieldAccessTExpr(te, ft);
        });
    }

    private Optional<TypedExpression> asDeclaration(SimpleName n) {
        Optional<Declaration> typeFromDeclarations = mvelCompilerContext.findDeclarations(n.asString());
        return typeFromDeclarations.map(d -> {
            Class<?> clazz = d.getClazz();
            return new SimpleNameTExpr(n.asString(), clazz);
        });
    }

    private Optional<TypedExpression> asPropertyAccessor(SimpleName n, Context arg) {
        Optional<TypedExpression> lastTypedExpression = arg.scope;
        Optional<Type> scopeType = arg.getScopeType();
        Optional<Method> optAccessor = scopeType.flatMap(t -> Optional.ofNullable(getAccessor((Class) t, n.asString())));

        return map2(lastTypedExpression, optAccessor, (lt, accessor) -> new FieldToAccessorTExpr(lt, accessor));
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Context arg) {
        TypedExpression scope = n.getScope().accept(this, arg);
        return n.getName().accept(this, new Context(scope));
    }

    @Override
    public TypedExpression visit(MethodCallExpr n, Context arg) {
        Optional<TypedExpression> scope = n.getScope().map(s -> s.accept(this, arg));
        TypedExpression name = n.getName().accept(this, new Context(scope.orElse(null)));
        final List<TypedExpression> arguments = new ArrayList<>(n.getArguments().size());
        for(Expression child : n.getArguments()) {
            TypedExpression a = child.accept(this, arg);
            arguments.add(a);
        }
        return new MethodCallExprT(n, n.getName().asString(), scope, arguments, name.getType());
    }

    @Override
    public TypedExpression visit(BinaryExpr n, Context arg) {
        TypedExpression left = n.getLeft().accept(this, arg);
        TypedExpression right = n.getRight().accept(this, arg);
        return new BinaryTExpr(left, right, n.getOperator());
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, Context arg) {
        return n.getExpression().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarationExpr n, Context arg) {
        return n.getVariables().iterator().next().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarator n, Context arg) {
        Optional<TypedExpression> initExpression = n.getInitializer().map(i -> i.accept(this, arg));
        return initExpression.orElse(null);
    }

    @Override
    public TypedExpression visit(AssignExpr n, Context arg) {
        return n.getValue().accept(this, arg);
    }

    @Override
    public TypedExpression visit(StringLiteralExpr n, Context arg) {
        return new StringLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(IntegerLiteralExpr n, Context arg) {
        return new IntegerLiteralExpressionT(n);
    }

    @Override
    public TypedExpression defaultMethod(Node n, Context context) {
        return new UnalteredTypedExpression(n);
    }

    @Override
    public TypedExpression visit(ObjectCreationExpr n, Context arg) {
        return new ObjectCreationExpressionT(n, mvelCompilerContext.resolveType(n.getType().asString()));
    }

    @Override
    public TypedExpression visit(ArrayAccessExpr n, Context arg) {
        TypedExpression name = n.getName().accept(this, arg);

        Optional<Type> type = name.getType();
        // TODO: Need a better type check here, check ExpressionTyper
        if(type.map(t -> t.getTypeName().endsWith("ArrayList")).isPresent()) {
            return new ListAccessExprT(name, n.getIndex(), type.get());
        }
        return new UnalteredTypedExpression(n, type.orElse(null));
    }
}

