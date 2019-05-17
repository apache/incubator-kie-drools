package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.constraint.parser.ast.expr.ModifyStatement;
import org.drools.mvelcompiler.context.MvelCompilerContext;

// A special case of compiler in which only the modify statements are processed
public class ModifyCompiler {

    private final MvelCompilerContext mvelCompilerContext;
    private PreprocessPhase preprocessPhase = new PreprocessPhase();

    public ModifyCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = DrlConstraintParser.parseBlock(mvelBlock);

        // TODO: remove duplication in MvelCompiler and ModifyCompiler in removing empty stmt
        mvelExpression
                .findAll(EmptyStmt.class)
                .forEach(Node::remove);

        List<Statement> preProcessedStatements = new ArrayList<>();
        // TODO: This preprocessing will change the order of the modify statments Write a test for that

        // TODO: Preprocessor does not recurse see MapInitializationDrools3800Test.testPropertyReactivityHanging
        // or put a modify into a in if branch
        Map<String, Set<String>> modifiedProperties = new HashMap<>();

        mvelExpression.findAll(ModifyStatement.class)
                .forEach(s -> {
                    PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
                    modifiedProperties.putAll(invoke.getModifyProperties());
                    s.replace(new BlockStmt(NodeList.nodeList(invoke.getStatements())));
                });

        return new ParsingResult(preProcessedStatements).setModifyProperties(modifiedProperties);
    }
}
