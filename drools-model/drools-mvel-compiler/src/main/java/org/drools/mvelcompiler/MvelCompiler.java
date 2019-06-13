package org.drools.mvelcompiler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.ast.expr.WithStatement;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;
    private PreprocessPhase preprocessPhase = new PreprocessPhase();

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public ParsingResult compile(String mvelBlock) {

        BlockStmt mvelExpression = MvelParser.parseBlock(mvelBlock);

        preprocessPhase.removeEmptyStmt(mvelExpression);

        Set<String> allUsedBindings = new HashSet<>();
        List<String> modifyBindings = mvelExpression.findAll(ModifyStatement.class)
                .stream()
                .flatMap(this::transformStatementWithPreprocessing)
                .collect(toList());

        List<String> withBindings = mvelExpression.findAll(WithStatement.class)
                .stream()
                .flatMap(this::transformStatementWithPreprocessing)
                .collect(toList());

        allUsedBindings.addAll(modifyBindings);
        allUsedBindings.addAll(withBindings);

        List<Statement> statements = new ArrayList<>();
        Optional<Type> lastExpressionType = Optional.empty();
        for (Statement s : mvelExpression.getStatements()) {
            lastExpressionType = processWithMvelCompiler(statements, s);
        }

        return new ParsingResult(statements)
                .setLastExpressionType(lastExpressionType)
                .setUsedBindings(allUsedBindings);
    }

    private Stream<String> transformStatementWithPreprocessing(Statement s) {
        PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
        Optional<Node> parentNode = s.getParentNode();
        parentNode.ifPresent(p -> {
            BlockStmt p1 = (BlockStmt) p;
            p1.getStatements().addAll(0, invoke.getNewObjectStatements());
            p1.getStatements().addAll(invoke.getOtherStatements());
        });
        s.remove();
        return invoke.getUsedBindings().stream();
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
