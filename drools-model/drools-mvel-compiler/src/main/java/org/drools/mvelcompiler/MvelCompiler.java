package org.drools.mvelcompiler;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;

    private TypedExpressionPhase typedExpressionPhase;

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
        typedExpressionPhase = new TypedExpressionPhase(mvelCompilerContext);
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = DrlConstraintParser.parseBlock(mvelBlock);

        for (Statement t : mvelExpression.getStatements()) {
            TypedExpression expressionsWithType = typedExpressionPhase.invoke(t.asExpressionStmt().getExpression());

            Node expression = expressionsWithType.toJavaExpression();
            return new ParsingResult(mvelCompilerContext, expression.toString());
        }

        return null;
    }
}
