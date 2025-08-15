/*
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
package org.kie.kogito.codegen.process;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.kie.api.definition.process.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import static java.lang.String.format;

/**
 * Java rule validator.
 */
class JavaRuleFlowProcessValidator extends RuleFlowProcessValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaRuleFlowProcessValidator.class);
    private static final String KCONTEXT = "kcontext";

    private static final JavaRuleFlowProcessValidator INSTANCE = new JavaRuleFlowProcessValidator();

    public static JavaRuleFlowProcessValidator getInstance() {
        return INSTANCE;
    }

    private JavaRuleFlowProcessValidator() {
    }

    @Override
    protected void validateNodes(Node[] nodes, List<ProcessValidationError> errors, RuleFlowProcess process) {
        super.validateNodes(nodes, errors, process);
        for (Node node : nodes) {
            if (node instanceof ActionNode) {
                final ActionNode actionNode = (ActionNode) node;
                if (actionNode.getAction() instanceof DroolsConsequenceAction) {
                    validateJava(actionNode, errors, process);
                }
            }
        }
    }

    private void validateJava(ActionNode actionNode, List<ProcessValidationError> errors, RuleFlowProcess process) {
        DroolsConsequenceAction droolsAction = (DroolsConsequenceAction) actionNode.getAction();
        ParseResult<CompilationUnit> parse = new JavaParser(new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver())))
                .parse("import org.kie.kogito.internal.process.runtime.KogitoProcessContext;\n" +
                        "import org.jbpm.process.instance.impl.Action;\n" +
                        " class Test {\n" +
                        "    Action action = kcontext -> {" + droolsAction.getConsequence() + "\n};\n" +
                        "}");
        if (parse.isSuccessful()) {
            CompilationUnit unit = parse.getResult().orElseThrow();
            try {
                Set<String> knownVariables = getKnownVariables(actionNode, process);
                // add local variables
                unit.findAll(VariableDeclarationExpr.class).stream().flatMap(v -> v.getVariables().stream()).map(VariableDeclarator::getNameAsString).forEach(knownVariables::add);
                //add Support for lambda expressions
                unit.findAll(LambdaExpr.class).forEach(le -> le.getParameters().forEach(p -> knownVariables.add(p.getNameAsString())));
                resolveVariablesType(unit, knownVariables);
            } catch (UnsolvedSymbolException ex) {
                if (LOGGER.isErrorEnabled()) {
                    VoidVisitor<Void> printer = new DefaultPrettyPrinterVisitor(new DefaultPrinterConfiguration());
                    unit.findFirst(BlockStmt.class).ifPresent(block -> {
                        block.accept(printer, null);
                        LOGGER.error(printer.toString());
                    });
                }
                //Small hack to extract the variable name causing the issue
                //Name comes as "Solving x" where x is the variable name
                final String[] solving = ex.getName().split(" ");
                addErrorMessage(process,
                        actionNode,
                        errors,
                        format("uses unknown variable in the script: %s", solving.length == 2 ? solving[1] : solving[0]));
            }
        } else {
            addErrorMessage(process,
                    actionNode,
                    errors,
                    format("unable to parse Java content: %s", parse.getProblems()));
        }
    }

    private static Set<String> getKnownVariables(ActionNode actionNode, RuleFlowProcess process) {
        Set<String> knownVariables = new HashSet<>();
        knownVariables.add(KCONTEXT);
        knownVariables.addAll(Arrays.asList(process.getVariableScope().getVariableNames()));
        knownVariables.addAll(Arrays.asList(process.getGlobalNames()));
        if (actionNode.getParentContainer() instanceof ContextContainer) {
            VariableScope variableScope = (VariableScope) ((ContextContainer) actionNode.getParentContainer()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
            if (variableScope != null) {
                knownVariables.addAll(Arrays.asList(variableScope.getVariableNames()));
            }
        }
        return knownVariables;
    }

    private static void resolveVariablesType(CompilationUnit unit, Set<String> knownVariables) {
        filterAndResolve(unit.findAll(MethodCallExpr.class).stream()
                .map(MethodCallExpr::getScope)
                .flatMap(Optional::stream), knownVariables);
        filterAndResolve(unit.findAll(AssignExpr.class).stream().map(AssignExpr::getTarget), knownVariables);
        resolveVariablesTypes(unit, knownVariables);
    }

    private static void resolveVariablesTypes(com.github.javaparser.ast.Node node, Set<String> knownVariables) {
        node.findAll(MethodCallExpr.class).stream()
                .flatMap(m -> m.getArguments().stream())
                .forEach(arg -> {
                    if (arg.isMethodCallExpr() || arg.isBinaryExpr()) {
                        resolveVariablesTypes(arg, knownVariables);
                    } else {
                        arg.findAll(NameExpr.class).stream().filter(ex -> !knownVariables.contains(ex.getNameAsString())).forEach(ex -> ex.calculateResolvedType());
                    }
                });
        node.findAll(BinaryExpr.class).stream()
                .map(BinaryExpr::asBinaryExpr)
                .forEach(bex -> {
                    processExpr(bex.getLeft(), knownVariables);
                    processExpr(bex.getRight(), knownVariables);
                });
    }

    private static void filterAndResolve(Stream<Expression> expressions, Set<String> knownVariables) {
        expressions.filter(expression -> expression.isNameExpr() && !knownVariables.contains(expression.asNameExpr().getNameAsString())).forEach(Expression::calculateResolvedType);
    }

    private static void processExpr(Expression expression, Set<String> knownVariables) {
        if (expression.isNameExpr()) {
            if (!knownVariables.contains(expression.asNameExpr().getNameAsString())) {
                expression.calculateResolvedType();
            }
        } else {
            resolveVariablesTypes(expression, knownVariables);
        }
    }
}
