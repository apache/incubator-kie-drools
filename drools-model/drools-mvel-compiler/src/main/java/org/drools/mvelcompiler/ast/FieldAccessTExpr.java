package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class FieldAccessTExpr extends TypedExpression {

    private final TypedExpression scope;
    private final TypedExpression fieldName;

    public FieldAccessTExpr(FieldAccessExpr originalExpression, TypedExpression scope, TypedExpression fieldName) {
        super(originalExpression);
        this.scope = scope;
        this.fieldName = fieldName;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new FieldAccessExpr((Expression) scope.toJavaExpression(), printConstraint(fieldName.toJavaExpression()));
    }
}
