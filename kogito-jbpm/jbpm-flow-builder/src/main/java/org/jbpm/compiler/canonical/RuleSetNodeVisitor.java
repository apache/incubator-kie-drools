/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map.Entry;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.RuleSetNodeFactory;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.kie.api.definition.process.Node;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.units.GeneratedRuleUnitDescription;
import org.kie.kogito.rules.units.ReflectiveRuleUnitDescription;
import org.kie.kogito.rules.units.impl.RuleUnitComponentFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RuleSetNodeVisitor extends AbstractVisitor {

    public static final Logger logger = LoggerFactory.getLogger(ProcessToExecModelGenerator.class);

    private final ClassLoader contextClassLoader;

    public RuleSetNodeVisitor(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        String nodeName = ruleSetNode.getName();

        String callTargetName = "ruleSetNode" + node.getId();
        addFactoryMethodWithArgsWithAssignment(factoryField, body, RuleSetNodeFactory.class, callTargetName, "ruleSetNode", new LongLiteralExpr(ruleSetNode.getId()));
        addFactoryMethodWithArgs(body, callTargetName, "name", new StringLiteralExpr(getOrDefault(nodeName, "Rule")));

        RuleSetNode.RuleType ruleType = ruleSetNode.getRuleType();
        if (ruleType.getName().isEmpty()) {
            throw new IllegalArgumentException(
                    MessageFormat.format(
                            "Rule task \"{0}\" is invalid: you did not set a unit name, a rule flow group or a decision model.", nodeName));
        }

        NameExpr methodScope = new NameExpr(callTargetName);
        MethodCallExpr m;
        if (ruleType.isRuleFlowGroup()) {
            m = handleRuleFlowGroup(ruleType);
        } else if (ruleType.isRuleUnit()) {
            m = handleRuleUnit(variableScope, metadata, ruleSetNode, nodeName, ruleType);
        } else if (ruleType.isDecision()) {
            m = handleDecision((RuleSetNode.RuleType.Decision) ruleType);
        } else {
            throw new IllegalArgumentException("Rule task " + nodeName + "is invalid: unsupported rule language " + ruleSetNode.getLanguage());
        }
        m.setScope(methodScope);
        body.addStatement(m);

        for (Entry<String, String> entry : ruleSetNode.getInMappings().entrySet()) {
            addFactoryMethodWithArgs(body, callTargetName, "inMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }
        for (Entry<String, String> entry : ruleSetNode.getOutMappings().entrySet()) {
            addFactoryMethodWithArgs(body, callTargetName, "outMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }

        visitMetaData(ruleSetNode.getMetaData(), body, callTargetName);

        addFactoryMethodWithArgs(body, callTargetName, "done");

        if (ruleType.isRuleUnit()) {
            if (ruleSetNode.getInMappings().isEmpty()) {
                GeneratedRuleUnitDescription generatedRuleUnitDescription = new GeneratedRuleUnitDescription(ruleType.getName(), contextClassLoader);
                for (Variable v : variableScope.getVariables()) {
                    generatedRuleUnitDescription.putDatasourceVar(v.getName(), DataStore.class.getCanonicalName(), v.getType().getStringType());
                }
                RuleUnitComponentFactoryImpl impl = (RuleUnitComponentFactoryImpl) RuleUnitComponentFactory.get();
                impl.registerRuleUnitDescription(generatedRuleUnitDescription);
            }
        }
    }

    private MethodCallExpr handleDecision(RuleSetNode.RuleType.Decision ruleType) {

        StringLiteralExpr namespace = new StringLiteralExpr(ruleType.getNamespace());
        StringLiteralExpr model = new StringLiteralExpr(ruleType.getModel());
        Expression decision = ruleType.getDecision() == null ?
                new NullLiteralExpr() : new StringLiteralExpr(ruleType.getDecision());

        MethodCallExpr decisionModels =
                new MethodCallExpr(new NameExpr("app"), "decisionModels");
        MethodCallExpr decisionModel =
                new MethodCallExpr(decisionModels, "getDecisionModel")
                        .addArgument(namespace)
                        .addArgument(model);

        BlockStmt actionBody = new BlockStmt();
        LambdaExpr lambda = new LambdaExpr(new Parameter(new UnknownType(), "()"), actionBody);
        actionBody.addStatement(new ReturnStmt(decisionModel));

        return new MethodCallExpr("decision")
                        .addArgument(namespace)
                        .addArgument(model)
                        .addArgument(decision)
                        .addArgument(lambda);
    }

    private MethodCallExpr handleRuleUnit(VariableScope variableScope, ProcessMetaData metadata, RuleSetNode ruleSetNode, String nodeName, RuleSetNode.RuleType ruleType) {
        String unitName = ruleType.getName();
        ProcessContextMetaModel processContext = new ProcessContextMetaModel(variableScope, contextClassLoader);
        RuleUnitDescription description;

        try {
            Class<?> unitClass = loadUnitClass(nodeName, unitName, metadata.getPackageName());
            description = new ReflectiveRuleUnitDescription(null, (Class<? extends RuleUnitData>) unitClass);
        } catch (ClassNotFoundException e) {
            logger.warn("Rule task \"{}\": cannot load class {}. " +
                                "The unit data object will be generated.", nodeName, unitName);

            description = generateRuleUnitDescription(unitName, processContext);
        }

        RuleUnitHandler handler = new RuleUnitHandler(description, processContext, ruleSetNode);
        Expression ruleUnitFactory = handler.invoke();

        return new MethodCallExpr("ruleUnit")
                        .addArgument(new StringLiteralExpr(ruleType.getName()))
                        .addArgument(ruleUnitFactory);

    }

    private GeneratedRuleUnitDescription generateRuleUnitDescription(String unitName, ProcessContextMetaModel processContext) {
        GeneratedRuleUnitDescription d = new GeneratedRuleUnitDescription(unitName, contextClassLoader);
        for (Variable variable : processContext.getVariables()) {
            // fixme: if var is scalar, we should use a different type of data source: KOGITO-892
            d.putDatasourceVar(
                    variable.getName(),
                    DataStore.class.getCanonicalName(),
                    variable.getType().getStringType());
        }
        return d;
    }

    private MethodCallExpr handleRuleFlowGroup( RuleSetNode.RuleType ruleType) {
        // build supplier for rule runtime
        BlockStmt actionBody = new BlockStmt();
        LambdaExpr lambda = new LambdaExpr(new Parameter(new UnknownType(), "()"), actionBody);

        MethodCallExpr ruleRuntimeBuilder = new MethodCallExpr(
                new MethodCallExpr(new NameExpr("app"), "ruleUnits"), "ruleRuntimeBuilder");
        MethodCallExpr ruleRuntimeSupplier = new MethodCallExpr(
                ruleRuntimeBuilder, "newKieSession",
                NodeList.nodeList(new StringLiteralExpr("defaultStatelessKieSession"), new NameExpr("app.config().rule()")));
        actionBody.addStatement(new ReturnStmt(ruleRuntimeSupplier));

        return new MethodCallExpr("ruleFlowGroup")
                .addArgument(new StringLiteralExpr(ruleType.getName()))
                .addArgument(lambda);

    }

    private Class<?> loadUnitClass(String nodeName, String unitName, String packageName) throws ClassNotFoundException {
        ClassNotFoundException ex;
        try {
            return contextClassLoader.loadClass(unitName);
        } catch (ClassNotFoundException e) {
            ex = e;
        }
        // maybe the name is not qualified. Let's try with tacking the packageName at the front
        try {
            return contextClassLoader.loadClass(packageName + "." + unitName);
        } catch (ClassNotFoundException e) {
            // throw the original error
            throw ex;
        }
    }
}
