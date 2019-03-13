package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

public class MvelCompiler {

    private TypedExpressionPhase typedExpressionPhase;

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        typedExpressionPhase = new TypedExpressionPhase(mvelCompilerContext);
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = DrlConstraintParser.parseBlock(mvelBlock);

        List<Statement> statements = new ArrayList<>();
        for (Statement t : mvelExpression.getStatements()) {
            TypedExpression expressionsWithType = typedExpressionPhase.invoke(t);
            Statement expression = (Statement) expressionsWithType.toJavaExpression();
            statements.add(expression);
        }

        return new ParsingResult(statements);
    }
}
