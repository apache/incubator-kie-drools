package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.expr.ModifyStatement;

import static org.drools.core.util.StringUtils.lcFirst;

public class ModifyPreprocessPhase {

    static class ModifyPreprocessPhaseResult {

        final List<Statement> statements = new ArrayList<>();
        // TODO: Refactor this
        final Map<String, Set<String>> modifyProperties = new HashMap<>();

        ModifyPreprocessPhaseResult() {
        }

        List<Statement> getStatements() {
            return statements;
        }

        public Map<String, Set<String>> getModifyProperties() {
            return modifyProperties;
        }

        ModifyPreprocessPhaseResult addModifyProperties(String name, String p) {
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

        public ModifyPreprocessPhaseResult addStatements(List<Statement> statements) {
            this.statements.addAll(statements);
            return this;
        }
    }

    public ModifyPreprocessPhaseResult invoke(Statement statement) {
        ModifyPreprocessPhaseResult result = new ModifyPreprocessPhaseResult();

        if (!(statement instanceof ModifyStatement)) {
            return result.addStatements(Collections.singletonList(statement));
        }

        ModifyStatement modifyStatement = (ModifyStatement) statement;

        modifyStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> {

                    DrlNameExpr originalFieldAccess = (DrlNameExpr) assignExpr.getTarget();
                    SimpleName scope = modifyStatement.getModifyObject();
                    DrlNameExpr newScope = new DrlNameExpr(scope);
                    String propertyName = originalFieldAccess.getName().asString();
                    result.addModifyProperties(scope.asString(), propertyName);

                    FieldAccessExpr fieldAccessWithScope = new FieldAccessExpr(newScope, propertyName);
                    assignExpr.setTarget(fieldAccessWithScope);

                    return assignExpr;
                });


        // Do not use findAll as we should only process top level expressions
        modifyStatement
                .getExpressions()
                .replaceAll(e -> {
                    if(e != null && e.isExpressionStmt()) {
                        Expression expression = e.asExpressionStmt().getExpression();
                        if(expression.isMethodCallExpr()) {
                            MethodCallExpr mcExpr = expression.asMethodCallExpr();
                            SimpleName scope = modifyStatement.getModifyObject();
                            DrlNameExpr newScope = new DrlNameExpr(scope);
                            mcExpr.setScope(newScope);

                            final String methodName = mcExpr.getName().asString();
                            String set = methodName.replace("set", "");
                            if(!"".equals(set)) { // some classes such "AtomicInteger" have a setter called "set"
                                result.addModifyProperties(scope.asString(), lcFirst(set));
                            }

                            return new ExpressionStmt(mcExpr);
                        }
                    }
                    return e;
                });

        List<Statement> statements = modifyStatement.getExpressions()
                .stream()
                .filter(Objects::nonNull)
                .filter(Statement::isExpressionStmt)
                .map(s -> s.asExpressionStmt().getExpression())
                .map(ExpressionStmt::new)
                .collect(Collectors.toList());
        return result.addStatements(statements);
    }
}
