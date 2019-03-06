package org.drools.mvelcompiler.phase2;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.phase3.NameTypedExpression;
import org.drools.mvelcompiler.phase3.TypedExpression;

import static org.drools.modelcompiler.builder.generator.expressiontyper.FlattenScope.flattenScope;

public class FlattenExpressionPhase implements DrlGenericVisitor<TypedExpression, Void> {

    private final MvelCompilerContext mvelCompilerContext;

    public FlattenExpressionPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public FlattenExpressionResult invoke(Expression mvelExpression) {

        final List<Node> childrenNodes = flattenScope(mvelExpression);

        final Node firstChild = childrenNodes.get(0);
        List<Node> expressionNodesWithoutFirst = childrenNodes
                .subList(1, childrenNodes.size());

        return new FlattenExpressionResult(firstChild.accept(this, null), expressionNodesWithoutFirst);
    }

    @Override
    public TypedExpression visit(DrlNameExpr firstNode, Void arg) {
        String firstName = firstNode.getName().getIdentifier();
        Optional<Declaration> declarationById = mvelCompilerContext.findDeclarations(firstName);
        return new NameTypedExpression(firstNode, declarationById.get().getClazz());
    }
}

