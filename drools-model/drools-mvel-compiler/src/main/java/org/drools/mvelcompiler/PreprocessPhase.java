package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.ast.expr.WithStatement;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

/**
 * This phase transforms modify and with statements in valid Java code
 *
 * It's used both in the MVEL Compiler and also to preprocess Drools' Java consequences that
 * use modify and with blocks.
 */
public class PreprocessPhase {

    private final boolean failOnEmptyRootScope;

    public PreprocessPhase() {
        this(false);
    }

    public PreprocessPhase(boolean failOnEmptyRootScope) {
        this.failOnEmptyRootScope = failOnEmptyRootScope;
    }

    interface PreprocessPhaseResult {

        Set<String> getUsedBindings();

        PreprocessPhaseResult addUsedBinding(String bindingName);

        List<Statement> getNewObjectStatements();

        List<Statement> getOtherStatements();

        PreprocessPhaseResult addOtherStatements(List<Statement> statements);

        PreprocessPhaseResult addNewObjectStatements(ExpressionStmt expressionStmt);
    }

    static class PreprocessedResult implements PreprocessPhaseResult {

        final List<Statement> newObjectStatements = new ArrayList<>();
        final List<Statement> otherStatements = new ArrayList<>();

        final Set<String> usedBindings = new HashSet<>();

        PreprocessedResult() {
        }

        public PreprocessPhaseResult addUsedBinding(String bindingName) {
            usedBindings.add(bindingName);
            return this;
        }

        public Set<String> getUsedBindings() {
            return usedBindings;
        }

        public List<Statement> getNewObjectStatements() {
            return newObjectStatements;
        }

        public List<Statement> getOtherStatements() {
            return otherStatements;
        }

        public PreprocessPhaseResult addOtherStatements(List<Statement> statements) {
            otherStatements.addAll(statements);
            return this;
        }

        public PreprocessPhaseResult addNewObjectStatements(ExpressionStmt expressionStmt) {
            newObjectStatements.add(expressionStmt);
            return this;
        }
    }

    static class StatementResult implements PreprocessPhaseResult {

        final List<Statement> newObjectStatements = new ArrayList<>();
        final List<Statement> otherStatements = new ArrayList<>();

        @Override
        public Set<String> getUsedBindings() {
            return new HashSet<>();
        }

        @Override
        public PreprocessPhaseResult addUsedBinding(String bindingName) {
            return this;
        }

        public List<Statement> getNewObjectStatements() {
            return newObjectStatements;
        }

        public List<Statement> getOtherStatements() {
            return otherStatements;
        }

        public PreprocessPhaseResult addOtherStatements(List<Statement> statements) {
            otherStatements.addAll(statements);
            return this;
        }

        public PreprocessPhaseResult addNewObjectStatements(ExpressionStmt expressionStmt) {
            newObjectStatements.add(expressionStmt);
            return this;
        }
    }

    public PreprocessPhaseResult invoke(Statement statement) {

        if (statement instanceof ModifyStatement) {
            return modifyPreprocessor((ModifyStatement) statement);
        } else if (statement instanceof WithStatement) {
            return withPreprocessor((WithStatement) statement);
        } else {
            return new StatementResult().addOtherStatements(Collections.singletonList(statement));
        }
    }

    private PreprocessPhaseResult modifyPreprocessor(ModifyStatement modifyStatement) {
        PreprocessPhaseResult result = new PreprocessedResult();

        final Expression scope = modifyStatement.getModifyObject();
        modifyStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> assignToFieldAccess(result, scope, assignExpr));

        // Do not use findAll as we should only process top level expressions
        modifyStatement
                .getExpressions()
                .replaceAll(e -> addScopeToMethodCallExpr(result, scope, e));

        List<Statement> statements = wrapToExpressionStmt(modifyStatement.getExpressions());
        return result.addOtherStatements(statements);
    }

    private PreprocessPhaseResult withPreprocessor(WithStatement withStatement) {
        PreprocessPhaseResult result = new StatementResult();

        Optional<Expression> initScope = addTypeToInitialization(withStatement, result);
        final Expression scope = initScope.orElse(withStatement.getWithObject());

        withStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> assignToFieldAccess(result, scope, assignExpr));

        // Do not use findAll as we should only process top level expressions
        withStatement
                .getExpressions()
                .replaceAll(e -> addScopeToMethodCallExpr(result, scope, e));

        List<Statement> statements = wrapToExpressionStmt(withStatement.getExpressions());

        return result.addOtherStatements(statements);
    }

    private Optional<Expression> addTypeToInitialization(WithStatement withStatement, PreprocessPhaseResult result) {
        if (withStatement.getWithObject().isAssignExpr()) {
            AssignExpr assignExpr = withStatement.getWithObject().asAssignExpr();
            Expression assignExprValue = assignExpr.getValue();
            Expression assignExprTarget = assignExpr.getTarget();

            if (assignExprValue.isObjectCreationExpr() && assignExprTarget instanceof DrlNameExpr) {
                ObjectCreationExpr constructor = assignExprValue.asObjectCreationExpr();
                ClassOrInterfaceType ctorType = constructor.getType();

                String targetVariableName = ((DrlNameExpr) assignExprTarget).getNameAsString();
                VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(ctorType, targetVariableName);
                AssignExpr withTypeAssignmentExpr = new AssignExpr(variableDeclarationExpr, assignExprValue, assignExpr.getOperator());
                ExpressionStmt expressionStmt = new ExpressionStmt(withTypeAssignmentExpr);
                result.addNewObjectStatements(expressionStmt);
                return of(new DrlNameExpr(targetVariableName));
            }
        }
        return empty();
    }

    private List<Statement> wrapToExpressionStmt(NodeList<Statement> expressions) {
        return expressions
                .stream()
                .filter(Objects::nonNull)
                .filter(Statement::isExpressionStmt)
                .map(s -> s.asExpressionStmt().getExpression())
                .map(ExpressionStmt::new)
                .collect(Collectors.toList());
    }

    private Statement addScopeToMethodCallExpr(PreprocessPhaseResult result, Expression scope, Statement e) {
        if (e != null && e.isExpressionStmt()) {
            Expression expression = e.asExpressionStmt().getExpression();
            if (expression.isMethodCallExpr()) {
                MethodCallExpr mcExpr = expression.asMethodCallExpr();

                MethodCallExpr rootMcExpr = findRootScope(mcExpr);
                if (rootMcExpr == null) {
                    return e;
                }

                Expression enclosed = new EnclosedExpr(scope);
                rootMcExpr.setScope(enclosed);

                if (scope.isNameExpr() || scope instanceof DrlNameExpr) { // some classes such "AtomicInteger" have a setter called "set"
                    result.addUsedBinding(printConstraint(scope));
                }

                return new ExpressionStmt(mcExpr);
            }
        }
        return e;
    }

    private MethodCallExpr findRootScope(MethodCallExpr mcExpr) {
        Optional<Expression> opt = mcExpr.getScope();
        if (!opt.isPresent()) {
            return mcExpr;
        } else {
            Expression scope = opt.get();
            if (scope.isMethodCallExpr()) {
                return findRootScope(scope.asMethodCallExpr());
            }
        }
        if (failOnEmptyRootScope) {
            throw new MvelCompilerException( "Invalid modify statement: " + mcExpr );
        }
        return null;
    }

    private AssignExpr assignToFieldAccess(PreprocessPhaseResult result, Expression scope, AssignExpr assignExpr) {
        DrlNameExpr originalFieldAccess = (DrlNameExpr) assignExpr.getTarget();
        String propertyName = originalFieldAccess.getName().asString();
        result.addUsedBinding(printConstraint(scope));

        FieldAccessExpr fieldAccessWithScope = new FieldAccessExpr(scope, propertyName);
        assignExpr.setTarget(fieldAccessWithScope);

        return assignExpr;
    }

    public void removeEmptyStmt(BlockStmt blockStmt) {
        blockStmt
                .findAll(EmptyStmt.class)
                .forEach(Node::remove);
    }
}
