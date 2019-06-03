package org.drools.mvelcompiler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.Optional.ofNullable;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;
    private PreprocessPhase preprocessPhase = new PreprocessPhase();

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = DrlConstraintParser.parseBlock(mvelBlock);

        preprocessPhase.removeEmptyStmt(mvelExpression);

        // TODO: Preprocessor does not recurse see MapInitializationDrools3800Test.testPropertyReactivityHanging
        // or put a modify into a in if branch

        List<Statement> preProcessedModifyStatements = new ArrayList<>();
        // TODO: This preprocessing will change the order of the modify statments
        // Write a test for that
        Map<String, Set<String>> modifiedProperties = new HashMap<>();
        for(Statement t : mvelExpression.getStatements()) {
            PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(t);
            modifiedProperties.putAll(invoke.getModifyProperties());
            preProcessedModifyStatements.addAll(invoke.getStatements());
        }

        List<Statement> statements = new ArrayList<>();
        Optional<Type> lastExpressionType = Optional.empty();
        for (Statement s : preProcessedModifyStatements) {
            TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(s);
            TypedExpression lhs = new LHSPhase(mvelCompilerContext, ofNullable(rhs)).invoke(s);
            Statement expression = (Statement) lhs.toJavaExpression();
            statements.add(expression);
            lastExpressionType = ofNullable(rhs).flatMap(TypedExpression::getType);
        }

        return new ParsingResult(statements)
                .setLastExpressionType(lastExpressionType)
                .setModifyProperties(modifiedProperties);
    }
}
