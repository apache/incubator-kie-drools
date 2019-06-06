package org.drools.mvelcompiler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.constraint.parser.ast.expr.ModifyStatement;
import org.drools.constraint.parser.ast.expr.WithStatement;
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

        // TODO: This preprocessing will change the order of the modify statments
        // Write a test for that

        Map<String, Set<String>> modifiedProperties = new HashMap<>();
        Consumer<Statement> preprocessStatement = preprocessStatementCurried(modifiedProperties);
        mvelExpression.findAll(ModifyStatement.class).forEach(preprocessStatement);
        mvelExpression.findAll(WithStatement.class).forEach(preprocessStatement);

        List<Statement> statements = new ArrayList<>();
        Optional<Type> lastExpressionType = Optional.empty();
        for (Statement s : mvelExpression.getStatements()) {
            lastExpressionType = processWithMvelCompiler(statements, s);
        }

        return new ParsingResult(statements)
                .setLastExpressionType(lastExpressionType)
                .setModifyProperties(modifiedProperties);
    }

    private Consumer<Statement> preprocessStatementCurried(Map<String, Set<String>> modifiedProperties) {
        return s -> {
            PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
            modifiedProperties.putAll(invoke.getModifyProperties());
            Optional<Node> parentNode = s.getParentNode();
            parentNode.ifPresent(p -> {
                BlockStmt p1 = (BlockStmt) p;
                p1.getStatements().addAll(0, invoke.getStatements());
            });
            s.remove();
        };
    }

    private Optional<Type> processWithMvelCompiler(List<Statement> statements, Statement s) {
        if (s.isBlockStmt()) {
            BlockStmt body = s.asBlockStmt();
            for (Statement children : body.getStatements()) {
                processWithMvelCompiler(statements, children);
            }
        } else if (s instanceof IfStmt) {
            IfStmt ifStmt = s.asIfStmt();
            NodeList<Statement> thenStmts = NodeList.nodeList();
            processWithMvelCompiler(thenStmts, ifStmt.getThenStmt());

            NodeList<Statement> elseStmts = NodeList.nodeList();
            ifStmt.getElseStmt().ifPresent(elseStmt -> processWithMvelCompiler(elseStmts, elseStmt));

            statements.add(new IfStmt(ifStmt.getCondition(), new BlockStmt(thenStmts), new BlockStmt(elseStmts)));

        } else {
            Optional<Type> lastExpressionType;
            TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(s);
            TypedExpression lhs = new LHSPhase(mvelCompilerContext, ofNullable(rhs)).invoke(s);
            Statement expression = (Statement) lhs.toJavaExpression();
            statements.add(expression);
            lastExpressionType = ofNullable(rhs).flatMap(TypedExpression::getType);
            return lastExpressionType;
        }
        return Optional.empty();
    }
}
