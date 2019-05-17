package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.expr.ModifyStatement;
import org.drools.constraint.parser.ast.expr.WithStatement;

import static com.github.javaparser.ast.NodeList.nodeList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;
import static org.drools.core.util.StringUtils.lcFirst;

public class PreprocessPhase {

    interface PreprocessPhaseResult {
        List<Statement> getStatements();
        Map<String, Set<String>> getModifyProperties();
        PreprocessPhaseResult addModifyProperties(String name, String p);
        PreprocessPhaseResult addStatements(List<Statement> statements);
    }

    static class ModifyResult implements PreprocessPhaseResult {

        final List<Statement> statements = new ArrayList<>();
        // TODO: Refactor this
        final Map<String, Set<String>> modifyProperties = new HashMap<>();

        ModifyResult() {
        }

        public List<Statement> getStatements() {
            return statements;
        }

        public Map<String, Set<String>> getModifyProperties() {
            return modifyProperties;
        }

        public PreprocessPhaseResult addModifyProperties(String name, String p) {
            Set<String> modifiedPropertiesSet = modifyProperties.get(name);
            if (modifiedPropertiesSet == null) {
                HashSet<String> value = new HashSet<>();
                value.add(p);
                modifyProperties.put(name, value);
            } else {
                modifiedPropertiesSet.add(p);
            }
            return this;
        }

        public PreprocessPhaseResult addStatements(List<Statement> statements) {
            this.statements.addAll(statements);
            return this;
        }
    }

    static class StatementResult implements  PreprocessPhaseResult {

        final List<Statement> statements = new ArrayList<>();

        @Override
        public List<Statement> getStatements() {
            return statements;
        }

        @Override
        public Map<String, Set<String>> getModifyProperties() {
            return new HashMap<>();
        }

        @Override
        public PreprocessPhaseResult addModifyProperties(String name, String p) {
            return this;
        }

        @Override
        public PreprocessPhaseResult addStatements(List<Statement> statements) {
            this.statements.addAll(statements);
            return this;
        }
    }

    public PreprocessPhaseResult invoke(Statement statement) {

        if (statement instanceof ModifyStatement) {
            return modifyPreprocessor((ModifyStatement) statement);
        } else if (statement instanceof WithStatement) {
            return withPreprocessor((WithStatement)statement);
        } else {
            return new StatementResult().addStatements(Collections.singletonList(statement));
        }
    }

    private PreprocessPhaseResult modifyPreprocessor(ModifyStatement modifyStatement) {
        PreprocessPhaseResult result = new ModifyResult();

        final Expression scope = modifyStatement.getModifyObject();
        modifyStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> assignToFieldAccess(result, scope, assignExpr));

        // Do not use findAll as we should only process top level expressions
        modifyStatement
                .getExpressions()
                .replaceAll(e -> addScopeToMethodCallExpr(result, scope, e));

        List<Statement> statements = wrapToExpressionStmt(modifyStatement.getExpressions());
        return result.addStatements(statements);
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

        return result.addStatements(statements);
    }

    private Optional<Expression> addTypeToInitialization(WithStatement withStatement, PreprocessPhaseResult result) {
        if (withStatement.getWithObject().isAssignExpr()) {
            AssignExpr assignExpr = withStatement.getWithObject().asAssignExpr();
            Expression assignExprValue = assignExpr.getValue();
            Expression assignExprTarget = assignExpr.getTarget();

            if (assignExprValue.isObjectCreationExpr() && assignExprTarget instanceof DrlNameExpr) {
                ObjectCreationExpr constructor = assignExprValue.asObjectCreationExpr();
                ClassOrInterfaceType ctorType = constructor.getType();

                String targetVariableName = ((DrlNameExpr)assignExprTarget).getNameAsString();
                VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(ctorType, targetVariableName);
                AssignExpr withTypeAssignmentExpr = new AssignExpr(variableDeclarationExpr, assignExprValue, assignExpr.getOperator());
                ExpressionStmt expressionStmt = new ExpressionStmt(withTypeAssignmentExpr);
                result.addStatements(nodeList(expressionStmt));
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
        if(e != null && e.isExpressionStmt()) {
            Expression expression = e.asExpressionStmt().getExpression();
            if(expression.isMethodCallExpr()) {
                MethodCallExpr mcExpr = expression.asMethodCallExpr();
                Expression enclosed = new EnclosedExpr(scope);
                mcExpr.setScope(enclosed);

                final String methodName = mcExpr.getName().asString();
                String set = methodName.replace("set", "");
                if(scope.isNameExpr() || scope instanceof DrlNameExpr) { // some classes such "AtomicInteger" have a setter called "set"
                    result.addModifyProperties(printConstraint(scope), "".equals(set) ? "" : lcFirst(set));
                }

                return new ExpressionStmt(mcExpr);
            }
        }
        return e;
    }

    private AssignExpr assignToFieldAccess(PreprocessPhaseResult result, Expression scope, AssignExpr assignExpr) {
        DrlNameExpr originalFieldAccess = (DrlNameExpr) assignExpr.getTarget();
        String propertyName = originalFieldAccess.getName().asString();
        result.addModifyProperties(printConstraint(scope), propertyName);

        FieldAccessExpr fieldAccessWithScope = new FieldAccessExpr(scope, propertyName);
        assignExpr.setTarget(fieldAccessWithScope);

        return assignExpr;
    }
}
