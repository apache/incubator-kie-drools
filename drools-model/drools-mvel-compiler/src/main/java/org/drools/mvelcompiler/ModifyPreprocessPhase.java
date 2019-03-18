package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.constraint.parser.ast.expr.DrlNameExpr;
import org.drools.constraint.parser.ast.expr.ModifyStatement;

import static java.util.Collections.singletonList;
import static org.drools.core.util.StringUtils.lcFirst;

public class ModifyPreprocessPhase {

    static class ModifyPreprocessPhaseResult {

        final List<Statement> statements;
        final List<String> modifyProperties;

        ModifyPreprocessPhaseResult(List<Statement> statements, List<String> modifyProperties) {
            this.statements = statements;
            this.modifyProperties = modifyProperties;
        }

        public List<Statement> getStatements() {
            return statements;
        }

        public List<String> getModifyProperties() {
            return modifyProperties;
        }
    }

    public ModifyPreprocessPhaseResult invoke(Statement statement) {
        List<String> modifyProperties = new ArrayList<>();
        if (!(statement instanceof ModifyStatement)) {
            return new ModifyPreprocessPhaseResult(singletonList(statement), modifyProperties);
        }

        ModifyStatement modifyStatement = (ModifyStatement) statement;

        modifyStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> {

                    DrlNameExpr originalFieldAccess = (DrlNameExpr) assignExpr.getTarget();
                    SimpleName scope = modifyStatement.getModifyObject();
                    DrlNameExpr newScope = new DrlNameExpr(scope);
                    String propertyName = originalFieldAccess.getName().asString();
                    modifyProperties.add(propertyName);

                    FieldAccessExpr fieldAccessWithScope = new FieldAccessExpr(newScope, propertyName);
                    assignExpr.setTarget(fieldAccessWithScope);

                    return assignExpr;
                });

        modifyStatement
                .findAll(MethodCallExpr.class)
                .replaceAll(mcExpr -> {

                    SimpleName scope = modifyStatement.getModifyObject();
                    DrlNameExpr newScope = new DrlNameExpr(scope);
                    mcExpr.setScope(newScope);

                    final String methodName = mcExpr.getName().asString();
                    modifyProperties.add(lcFirst(methodName.replace("set", "")));

                    return mcExpr;
                });

        List<Statement> statements = modifyStatement.getExpressions()
                .stream()
                .map(ExpressionStmt::new).collect(Collectors.toList());
        return new ModifyPreprocessPhaseResult(statements, modifyProperties);
    }
}
