package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.ast.expr.WithStatement;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;
    private final PreprocessPhase preprocessPhase = new PreprocessPhase();

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public CompiledExpressionResult compileExpression(String mvelExpressionString) {
        Expression parsedExpression = MvelParser.parseExpression(mvelExpressionString);
        Node compiled = compileExpression(parsedExpression);

        return new CompiledExpressionResult((Expression) compiled);
    }

    public CompiledBlockResult compileStatement(String mvelBlock) {

        BlockStmt mvelExpression = MvelParser.parseBlock(mvelBlock);

        preprocessPhase.removeEmptyStmt(mvelExpression);

        Set<String> allUsedBindings = new HashSet<>();

        List<String> modifyUsedBindings = mvelExpression.findAll(ModifyStatement.class)
                .stream()
                .flatMap(this::transformStatementWithPreprocessing)
                .collect(toList());

        List<String> withUsedBindings = mvelExpression.findAll(WithStatement.class)
                .stream()
                .flatMap(this::transformStatementWithPreprocessing)
                .collect(toList());

        allUsedBindings.addAll(modifyUsedBindings);
        allUsedBindings.addAll(withUsedBindings);

        List<Statement> statements = new ArrayList<>();
        for (Statement s : mvelExpression.getStatements()) {
            processWithMvelCompiler(statements, s);
        }

        return new CompiledBlockResult(statements)
                .setUsedBindings(allUsedBindings);
    }

    private Stream<String> transformStatementWithPreprocessing(Statement s) {
        PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
        s.remove();
        return invoke.getUsedBindings().stream();
    }

    private void processWithMvelCompiler(List<Statement> statements, Statement s) {
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
            statements.add((Statement) compileStatement(s));
        }
    }

    private Node compileStatement(Node n) {
        TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(n);
        TypedExpression lhs = new LHSPhase(mvelCompilerContext, ofNullable(rhs)).invoke(n);

        Optional<TypedExpression> postProcessedRHS = new ReProcessRHSPhase(mvelCompilerContext).invoke(rhs, lhs);
        TypedExpression postProcessedLHS = postProcessedRHS.map(ppr -> new LHSPhase(mvelCompilerContext, of(ppr)).invoke(n)).orElse(lhs);

        return postProcessedLHS.toJavaExpression();
    }

    // Avoid processing the LHS as it's not present while compiling an expression
    private Node compileExpression(Node n) {
        TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(n);
        return rhs.toJavaExpression();
    }
}
