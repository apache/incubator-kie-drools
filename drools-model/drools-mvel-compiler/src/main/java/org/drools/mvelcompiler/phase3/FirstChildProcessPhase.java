package org.drools.mvelcompiler.phase3;

import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.phase2.FlattenedExpressionResult;
import org.drools.mvelcompiler.phase4.NameTypedExpression;
import org.drools.mvelcompiler.phase4.TypedExpression;

public class FirstChildProcessPhase implements DrlGenericVisitor<TypedExpression, Void> {

    private final MvelCompilerContext mvelCompilerContext;

    public FirstChildProcessPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public FirstChildProcessResult invoke(FlattenedExpressionResult flattenedExpressionResult) {

        Expression expression = flattenedExpressionResult.getExpression();
        TypedExpression accept1 = expression.accept(this, null);

        return new FirstChildProcessResult(accept1, flattenedExpressionResult.getOtherNodes());
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Void arg) {
        for(Node children : n.getChildNodes()) {
            return children.accept(this, null);
        }
        return null;
    }


    @Override
    public TypedExpression visit(DrlNameExpr firstNode, Void arg) {
        String firstName = firstNode.getName().getIdentifier();
        Optional<Declaration> declarationById = mvelCompilerContext.findDeclarations(firstName);
        return new NameTypedExpression(firstNode, declarationById.get().getClazz());
    }
}

