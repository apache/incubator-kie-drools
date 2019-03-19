package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;
    private ModifyPreprocessPhase modifyPreprocessPhase = new ModifyPreprocessPhase();

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = DrlConstraintParser.parseBlock(mvelBlock);

        List<Statement> preProcessedStatements = new ArrayList<>();
        // TODO: This preprocessing will change the order of the modify statments
        // Write a test for that
        Map<String, Set<String>> modifiedProperties = new HashMap<>();
        for(Statement t : mvelExpression.getStatements()) {
            ModifyPreprocessPhase.ModifyPreprocessPhaseResult invoke = modifyPreprocessPhase.invoke(t);
            modifiedProperties.putAll(invoke.getModifyProperties());
            preProcessedStatements.addAll(invoke.getStatements());
        }

        List<Statement> statements = new ArrayList<>();
        for (Statement s : preProcessedStatements) {
            TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(s);
            TypedExpression lhs = new LHSPhase(mvelCompilerContext, rhs).invoke(s);
            Statement expression = (Statement) lhs.toJavaExpression();
            statements.add(expression);
        }

        return new ParsingResult(statements).setModifyProperties(modifiedProperties);
    }
}
