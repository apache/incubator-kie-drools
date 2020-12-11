package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ast.expr.ModifyStatement;

import static com.github.javaparser.ast.NodeList.nodeList;

// A special case of compiler in which only the modify statements are processed
public class ModifyCompiler {

    private static final PreprocessPhase preprocessPhase = new PreprocessPhase(true);

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = MvelParser.parseBlock(mvelBlock);

        preprocessPhase.removeEmptyStmt(mvelExpression);

        Set<String> usedBindings = new HashSet<>();
        mvelExpression.findAll(ModifyStatement.class)
                .forEach(s -> {
                    Optional<Node> parentNode = s.getParentNode();
                    PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
                    usedBindings.addAll(invoke.getUsedBindings());
                    parentNode.ifPresent(p -> {
                        BlockStmt parentBlock = (BlockStmt) p;
                        for (String modifiedFact : invoke.getUsedBindings()) {
                            parentBlock.addStatement(new MethodCallExpr(null, "update", nodeList(new NameExpr(modifiedFact))));
                        }
                    });
                    s.remove();
                });

        return new ParsingResult(mvelExpression.getStatements()).setUsedBindings(usedBindings);
    }
}
