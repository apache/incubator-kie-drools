package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
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

        preprocessPhase.removeEmptyStmt(mvelExpression);

        Set<String> modifiedProperties = new HashSet<>();
        mvelExpression.findAll(ModifyStatement.class)
                .forEach(s -> {
                    PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
                    modifiedProperties.addAll(invoke.getModifyProperties());
                    Optional<Node> parentNode = s.getParentNode();
                    parentNode.ifPresent(p -> {
                        BlockStmt p1 = (BlockStmt) p;
                        p1.getStatements().addAll(invoke.getNewObjectStatements());
                        p1.getStatements().addAll(invoke.getOtherStatements());
                        for (String modifiedProperty : invoke.getModifyProperties()) {
                            p1.addStatement(new MethodCallExpr(null, "update", nodeList(new NameExpr(modifiedProperty))));
                        }
                    });
                    s.remove();
                });

        return new ParsingResult(mvelExpression.getStatements()).setModifyProperties(modifiedProperties);
    }
}
