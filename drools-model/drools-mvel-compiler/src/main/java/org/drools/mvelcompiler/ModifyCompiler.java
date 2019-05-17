package org.drools.mvelcompiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.constraint.parser.ast.expr.ModifyStatement;

import static com.github.javaparser.ast.NodeList.nodeList;

// A special case of compiler in which only the modify statements are processed
public class ModifyCompiler {

    private PreprocessPhase preprocessPhase = new PreprocessPhase();

    public ModifyCompiler() {
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = DrlConstraintParser.parseBlock(mvelBlock);

        // TODO: remove duplication in MvelCompiler and ModifyCompiler in removing empty stmt
        mvelExpression
                .findAll(EmptyStmt.class)
                .forEach(Node::remove);

        // TODO: This preprocessing will change the order of the modify statments Write a test for that

        // TODO: Preprocessor does not recurse see MapInitializationDrools3800Test.testPropertyReactivityHanging
        // or put a modify into a in if branch
        Map<String, Set<String>> modifiedProperties = new HashMap<>();

        mvelExpression.findAll(ModifyStatement.class)
                .forEach(s -> {
                    PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
                    modifiedProperties.putAll(invoke.getModifyProperties());
                    Optional<Node> parentNode = s.getParentNode();
                    parentNode.ifPresent(p -> {
                        BlockStmt p1 = (BlockStmt) p;
                        p1.getStatements().addAll(invoke.getStatements());
                        for (String modifiedProperty : invoke.getModifyProperties().keySet()) {
                            p1.addStatement(new MethodCallExpr(null, "update", nodeList(new NameExpr(modifiedProperty))));
                        }
                    });
                    s.remove();
                });

        return new ParsingResult(mvelExpression.getStatements()).setModifyProperties(modifiedProperties);
    }
}
