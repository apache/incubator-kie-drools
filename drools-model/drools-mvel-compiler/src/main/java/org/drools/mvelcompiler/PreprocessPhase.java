/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvelcompiler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
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

import static com.github.javaparser.ast.NodeList.nodeList;
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
        } else if (statement instanceof WithStatement) {
            return withPreprocessor((WithStatement) statement);
        } else {
            return new StatementResult();
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

        NodeList<Statement> statements = wrapToExpressionStmt(modifyStatement.getExpressions());
        // delete modify statement and replace its own block of statements
        modifyStatement.replace(new BlockStmt(statements));

        return result;
    }

    private PreprocessPhaseResult withPreprocessor(WithStatement withStatement) {
        PreprocessPhaseResult result = new StatementResult();

        Deque<Statement> allNewStatements = new ArrayDeque<>();

        Optional<Expression> initScope = addTypeToInitialization(withStatement, allNewStatements);
        final Expression scope = initScope.orElse(withStatement.getWithObject());

        withStatement
                .findAll(AssignExpr.class)
                .replaceAll(assignExpr -> assignToFieldAccess(result, scope, assignExpr));

        // Do not use findAll as we should only process top level expressions
        withStatement
                .getExpressions()
                .replaceAll(e -> addScopeToMethodCallExpr(result, scope, e));

        NodeList<Statement> bodyStatements = wrapToExpressionStmt(withStatement.getExpressions());

        allNewStatements.addAll(bodyStatements);

        // delete modify statement and add the new statements to its children
        Node parentNode = withStatement.getParentNode()
                .orElseThrow(() -> new MvelCompilerException("A parent node is expected here"));

        // We need to replace the with statement with the statements without nesting the new statements inside
        // a BlockStmt otherwise other statements might reference the newly created instance
        // See RuleChainingTest.testRuleChainingWithLogicalInserts
        if(parentNode instanceof BlockStmt) {
            BlockStmt parentBlock = (BlockStmt) parentNode;

            Iterator<Statement> newStatementsReversed = allNewStatements.descendingIterator();
            while(newStatementsReversed.hasNext()) {
                parentBlock.getStatements().addAfter(newStatementsReversed.next(), withStatement);
            }

            withStatement.remove();
        } else {
            throw new MvelCompilerException("Expecting a BlockStmt as a parent");
        }


        return result;
    }

    private Optional<Expression> addTypeToInitialization(WithStatement withStatement, Deque<Statement> allNewStatements) {
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
                allNewStatements.add(new ExpressionStmt(withTypeAssignmentExpr));
                return of(new DrlNameExpr(targetVariableName));
            }
        }
        return empty();
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
