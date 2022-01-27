/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.canonical;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.impl.AssignableChecker;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.rules.RuleUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parse;

/*
 *
 * Input/Output mapping with Rule Units:
 *
 * | Mapping | Process Variable | Rule Unit field   | Action
 * | IN      | scalar           | scalar            | Assignment
 * | IN      | scalar           | data source 	    | Add to (i.e. insert into) data source
 * | IN      | collection       | data source 	    | Add all contents from data source
 * | OUT     | scalar           | scalar 	        | Assignment
 * | OUT     | scalar           | data source 	    | get 1 value off the data source
 * | OUT     | collection       | data source 	    | Add all contents to the data source
 *
 */
public class RuleUnitHandler {

    public static final Logger logger = LoggerFactory.getLogger(ProcessToExecModelGenerator.class);

    private final RuleUnitDescription ruleUnit;
    private final ProcessContextMetaModel variableScope;
    private final RuleSetNode ruleSetNode;
    private final AssignableChecker assignableChecker;

    public RuleUnitHandler(RuleUnitDescription ruleUnit, ProcessContextMetaModel variableScope, RuleSetNode ruleSetNode, AssignableChecker assignableChecker) {
        this.ruleUnit = ruleUnit;
        this.variableScope = variableScope;
        this.ruleSetNode = ruleSetNode;
        this.assignableChecker = assignableChecker;
    }

    public Expression invoke() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/class-templates/RuleUnitFactoryTemplate.java");
        Expression ruleUnitFactory = parse(resourceAsStream).findFirst(Expression.class)
                .orElseThrow(() -> new IllegalArgumentException("Template does not contain an Expression"));

        String unitName = ruleUnit.getCanonicalName();

        ruleUnitFactory.findAll(ClassOrInterfaceType.class)
                .stream()
                .filter(t -> t.getNameAsString().equals("$Type$"))
                .forEach(t -> t.setName(unitName));

        ruleUnitFactory.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("bind"))
                .ifPresent(m -> m.setBody(bind(variableScope, ruleSetNode, ruleUnit)));
        ruleUnitFactory.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unit"))
                .ifPresent(m -> m.setBody(unit(unitName)));
        ruleUnitFactory.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unbind"))
                .ifPresent(m -> m.setBody(unbind(variableScope, ruleSetNode, ruleUnit)));

        return ruleUnitFactory;
    }

    private BlockStmt unit(String unitName) {
        // app.get(org.kie.kogito.rules.RuleUnits.class).create(unitName)
        MethodCallExpr ruleUnit = new MethodCallExpr(
                new MethodCallExpr(new NameExpr("app"), "get")
                        .addArgument(new ClassExpr().setType(RuleUnits.class.getCanonicalName())),
                "create")
                        .addArgument(new ClassExpr().setType(unitName));
        return new BlockStmt().addStatement(new ReturnStmt(ruleUnit));
    }

    /*
     * bind data to the rule unit POJO
     */
    private BlockStmt bind(ProcessContextMetaModel variableScope, RuleSetNode node, RuleUnitDescription unitDescription) {
        RuleUnitMetaModel unit =
                new RuleUnitMetaModel(unitDescription, "unit", assignableChecker);

        BlockStmt actionBody = new BlockStmt();

        // create the RuleUnitData instance
        actionBody.addStatement(unit.newInstance());

        for (Map.Entry<String, String> e : getInputMappings(variableScope, node).entrySet()) {
            String procVar = e.getValue();
            String unitVar = e.getKey();

            if (!variableScope.hasVariable(procVar)) {
                continue;
            }

            boolean procVarIsCollection = variableScope.isCollectionType(procVar);
            boolean unitVarIsDataSource = unitDescription.hasDataSource(unitVar);

            // we assign procVars to unitVars, and subscribe unitVars for changes
            // subscription forward changes directly to the procVars
            if (procVarIsCollection && unitVarIsDataSource) {
                actionBody.addStatement(variableScope.assignVariable(procVar));
                actionBody.addStatement(
                        requireNonNull(procVar,
                                "The input collection variable of a data source cannot be null:" + procVar));
                actionBody.addStatement(
                        unit.injectCollection(unitVar, procVar));
            } else if (procVarIsCollection /* && !unitVarIsDataSource */) {
                Expression expression = variableScope.getVariable(procVar);
                actionBody.addStatement(unit.set(unitVar, expression));
            } else if (/* !procVarIsCollection && */ unitVarIsDataSource) {
                // set data source to variable
                Expression expression = variableScope.getVariable(procVar);
                actionBody.addStatement(
                        unit.injectScalar(unitVar, expression));
                // subscribe to updates to that data source
                actionBody.addStatement(
                        variableScope.assignVariable(procVar));
                actionBody.addStatement(
                        unit.extractIntoScalar(unitVar, procVar));
            } else {
                Expression expression = variableScope.getVariable(procVar);
                actionBody.addStatement(unit.set(unitVar, expression));
            }
        }

        actionBody.addStatement(new ReturnStmt(new NameExpr(unit.instanceVarName())));

        return actionBody;
    }

    private Map<String, String> getInputMappings(ProcessContextMetaModel variableScope, RuleSetNode node) {
        Map<String, String> entries = node.getIoSpecification().getInputMapping();
        if (entries.isEmpty()) {
            entries = new HashMap<>();
            for (String varName : variableScope.getVariableNames()) {
                entries.put(varName, varName);
            }
        }
        return entries;
    }

    private BlockStmt unbind(ProcessContextMetaModel variableScope, RuleSetNode node, RuleUnitDescription unitDescription) {
        RuleUnitMetaModel unit =
                new RuleUnitMetaModel(unitDescription, "unit", assignableChecker);

        BlockStmt actionBody = new BlockStmt();

        Map<String, String> mappings = getOutputMappings(variableScope, node);
        for (Map.Entry<String, String> e : mappings.entrySet()) {
            String unitVar = e.getKey();
            String procVar = e.getValue();
            boolean procVarIsCollection = variableScope.isCollectionType(procVar);
            boolean unitVarIsDataSource = unitDescription.hasDataSource(unitVar);
            if (procVarIsCollection && unitVarIsDataSource) {
                actionBody.addStatement(variableScope.assignVariable(procVar));
                actionBody.addStatement(
                        requireNonNull(procVar,
                                String.format(
                                        "Null collection variable used as an output variable: %s. " +
                                                "Initialize this variable to get the contents or the data source, " +
                                                "or use a non-collection data type to extract one value.",
                                        procVar)));
                actionBody.addStatement(unit.extractIntoCollection(unitVar, procVar));
            } else if (procVarIsCollection /* && !unitVarIsDataSource */) {
                actionBody.addStatement(variableScope.assignVariable(procVar));
                actionBody.addStatement(unit.extractIntoScalar(unitVar, procVar));
            } else if (/* !procVarIsCollection && */ unitVarIsDataSource) {
                actionBody.addStatement(variableScope.assignVariable(procVar));
                actionBody.addStatement(unit.extractIntoScalar(unitVar, procVar));
            } else /* !procVarIsCollection && !unitVarIsDataSource */ {
                MethodCallExpr setterCall = variableScope.setVariable(procVar);
                actionBody.addStatement(
                        setterCall.addArgument(unit.get(unitVar)));
            }
        }

        return actionBody;
    }

    private Map<String, String> getOutputMappings(ProcessContextMetaModel variableScope, RuleSetNode node) {
        Map<String, String> entries = node.getIoSpecification().getOutputMappingBySources();
        // if both are empty we use automatic binding, otherwise we do nothing
        if (node.getIoSpecification().getInputMapping().isEmpty() && entries.isEmpty()) {
            entries = new HashMap<>();
            for (String varName : variableScope.getVariableNames()) {
                entries.put(varName, varName);
            }
        }
        return entries;
    }

    public static MethodCallExpr requireNonNull(String targetProcessVar, String message) {
        return new MethodCallExpr().setScope(new NameExpr("java.util.Objects"))
                .setName("requireNonNull").addArgument(new NameExpr(targetProcessVar)).addArgument(new StringLiteralExpr(message));
    }
}
