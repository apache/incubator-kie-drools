package org.drools.mvelcompiler.phase2;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static org.drools.modelcompiler.builder.generator.expressiontyper.FlattenScope.flattenScope;

public class FlattenExpressionPhase implements DrlGenericVisitor<FlattenedExpressionResult, Void> {

    private final MvelCompilerContext mvelCompilerContext;

    public FlattenExpressionPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public FlattenedExpressionResult invoke(Expression mvelExpression) {
        return mvelExpression.accept(this, null);
    }

    @Override
    public FlattenedExpressionResult visit(FieldAccessExpr n, Void arg) {
        return defaultExpression(n);
    }

    private FlattenedExpressionResult defaultExpression(Expression mvelExpression) {
        // is Flatten avoidable?
        final List<Node> childrenNodes = flattenScope(mvelExpression);

        final Node firstChild = childrenNodes.get(0);


        List<Node> expressionNodesWithoutFirst = childrenNodes
                .subList(1, childrenNodes.size());

        return new FlattenedExpressionResult(firstChild, expressionNodesWithoutFirst, mvelExpression);
    }
}

