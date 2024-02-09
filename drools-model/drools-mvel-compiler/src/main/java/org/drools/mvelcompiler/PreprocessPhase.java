/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvelcompiler;

import java.util.ArrayList;
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
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.printer.PrintUtil;

import static com.github.javaparser.ast.NodeList.nodeList;
import static com.github.javaparser.utils.StringEscapeUtils.escapeJava;
import static org.drools.mvel.parser.printer.PrintUtil.printNode;

/**
 * This phase transforms modify statements in valid Java code
 *
 * It's used both in the MVEL Compiler and also to preprocess Drools' Java consequences that
 * use modify blocks.
 */
public class PreprocessPhase {

    interface PreprocessPhaseResult {

        Set<String> getUsedBindings();

        PreprocessPhaseResult addUsedBinding(String bindingName);
    }

    static class PreprocessedResult implements PreprocessPhaseResult {

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
    }

    static class StatementResult implements PreprocessPhaseResult {

        final List<Statement> otherStatements = new ArrayList<>();

        @Override
        public Set<String> getUsedBindings() {
            return new HashSet<>();
        }

        @Override
        public PreprocessPhaseResult addUsedBinding(String bindingName) {
            return this;
        }
    }

    public PreprocessPhaseResult invoke(Statement statement) {

        if (statement instanceof ModifyStatement) {
            return modifyPreprocessor((ModifyStatement) statement);
        } else {
            return new StatementResult();
        }
    }

    private PreprocessPhaseResult modifyPreprocessor(ModifyStatement modifyStatement) {
        PreprocessPhaseResult result = new PreprocessedResult();

        final Expression scope = modifyStatement.getModifyObject();
        modifyStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> assignToFieldAccess(scope, assignExpr));

        // Do not use findAll as we should only process top level expressions
        modifyStatement
                .getExpressions()
                .replaceAll(e -> addScopeToMethodCallExpr(scope, e));

        NodeList<Statement> statements = wrapToExpressionStmt(modifyStatement.getExpressions());
        // delete modify statement and replace its own block of statements
        modifyStatement.replace(new BlockStmt(statements));

        // even if no property is modified inside modify block, need to call update because its properties may be modified in RHS
        if (scope.isNameExpr() || scope instanceof DrlNameExpr) {
            result.addUsedBinding(printNode(scope));
        }

        return result;
    }

    private NodeList<Statement> wrapToExpressionStmt(NodeList<Statement> expressions) {
        return nodeList(expressions
                .stream()
                .filter(Objects::nonNull)
                .filter(Statement::isExpressionStmt)
                .map(s -> s.asExpressionStmt().getExpression())
                .map(ExpressionStmt::new)
                .collect(Collectors.toList()));
    }

    private Statement addScopeToMethodCallExpr(Expression scope, Statement e) {
        if (e != null && e.isExpressionStmt()) {
            Expression expression = e.asExpressionStmt().getExpression();
            if (expression.isMethodCallExpr()) {
                MethodCallExpr mcExpr = expression.asMethodCallExpr();

                Expression rootExpr = findRootScope(mcExpr);
                if (rootExpr == null) {
                    return e;
                } else if (rootExpr.isMethodCallExpr()) {
                    MethodCallExpr rootMcExpr = rootExpr.asMethodCallExpr();
                    Expression enclosed = new EnclosedExpr(scope);
                    rootMcExpr.setScope(enclosed);
                    return new ExpressionStmt(mcExpr);
                } else if (rootExpr instanceof DrlNameExpr) {
                    throwExceptionIfSameDrlName(rootExpr, scope);
                    // Unknown name. Assume a property of the fact
                    replaceRootExprWithFieldAccess(scope, (DrlNameExpr) rootExpr);
                }
            }
        }
        return e;
    }

    private void throwExceptionIfSameDrlName(Expression target, Expression scope) {
        if (isSameDrlName(target, scope)) {
            throw new MvelCompilerException("Invalid modify statement: " + PrintUtil.printNode(target));
        }
    }

    private boolean isSameDrlName(Expression target, Expression scope) {
        return target instanceof DrlNameExpr && scope instanceof DrlNameExpr &&
               ((DrlNameExpr) target).getName().equals(((DrlNameExpr) scope).getName());
    }

    private Expression findRootScope(Expression expr) {
        Expression scope;
        if (expr.isMethodCallExpr()) {
            MethodCallExpr mcExpr = expr.asMethodCallExpr();
            Optional<Expression> opt = mcExpr.getScope();
            if (opt.isEmpty()) {
                return mcExpr; // return MethodCallExpr if no scope
            } else {
                scope = opt.get();
                return findRootScope(scope);
            }
        } else if (expr.isFieldAccessExpr()) {
            FieldAccessExpr faExpr = expr.asFieldAccessExpr();
            scope = faExpr.getScope(); // FieldAccessExpr must have a scope
            return findRootScope(scope);
        } else {
            scope = expr;
        }

        return scope;
    }

    private AssignExpr assignToFieldAccess(Expression scope, AssignExpr assignExpr) {
        Expression target = assignExpr.getTarget();

        if (target instanceof DrlNameExpr) { // e.g. age = 10
            return propertyNameToFieldAccess(scope, assignExpr, (DrlNameExpr) target);
        } else if (target.isFieldAccessExpr()) { // e.g. address.city = 10
            Expression rootScope = findRootScope(target);
            throwExceptionIfSameDrlName(rootScope, scope);
            replaceRootExprWithFieldAccess(scope, (DrlNameExpr) rootScope);
            return assignExpr;
        } else {
            throw new MvelCompilerException("Unexpected target: " + target.getClass() + ", assignExpr: " + PrintUtil.printNode(assignExpr));
        }
    }

    private void replaceRootExprWithFieldAccess(Expression scope, DrlNameExpr rootExpr) {
        String propertyName = rootExpr.getName().asString();
        FieldAccessExpr fieldAccessWithScope = new FieldAccessExpr(scope, propertyName);
        Optional<Node> optRootParent = rootExpr.getParentNode();
        if (optRootParent.isPresent()) {
            Node rootParent = optRootParent.get();
            if (rootParent instanceof FieldAccessExpr) {
                ((FieldAccessExpr)rootParent).setScope(fieldAccessWithScope);
            } else if (rootParent instanceof MethodCallExpr) {
                ((MethodCallExpr)rootParent).setScope(fieldAccessWithScope);
            } else {
                throw new MvelCompilerException(String.format("Unexpected rootParent: %s, rootExpr: %s", rootParent.getClass(), PrintUtil.printNode(rootExpr)));
            }
        } else {
            throw new MvelCompilerException(String.format("rootExpr doesn't have a parent: %s, rootExpr: %s", rootExpr.getClass(), PrintUtil.printNode(rootExpr)));
        }
    }

    private AssignExpr propertyNameToFieldAccess(Expression scope, AssignExpr assignExpr, DrlNameExpr originalFieldAccess) {
        String propertyName = originalFieldAccess.getName().asString();
        FieldAccessExpr fieldAccessWithScope = new FieldAccessExpr(scope, propertyName);
        assignExpr.setTarget(fieldAccessWithScope);
        return assignExpr;
    }

    public void removeEmptyStmt(BlockStmt blockStmt) {
        blockStmt
                .findAll(EmptyStmt.class)
                .forEach(Node::remove);
    }

    public StringLiteralExpr replaceTextBlockWithConcatenatedStrings(TextBlockLiteralExpr textBlockLiteralExpr) {
        // In TextBlocks the `"` character is allowed so it needs to be escaped
        return new StringLiteralExpr(escapeJava(textBlockLiteralExpr.stripIndent()));
    }
}
