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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.core.util.StringUtils;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.RuleSetNodeFactory;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.kie.api.definition.process.Node;
import org.kie.kogito.rules.DataObserver;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseStatement;

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

    private final ClassLoader contextClassLoader;

    public RuleSetNodeVisitor(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        String nodeName = ruleSetNode.getName();

        addFactoryMethodWithArgsWithAssignment(factoryField, body, RuleSetNodeFactory.class, "ruleSetNode" + node.getId(), "ruleSetNode", new LongLiteralExpr(ruleSetNode.getId()));
        addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(nodeName, "Rule")));
        // build supplier for either KieRuntime or DMNRuntime
        BlockStmt actionBody = new BlockStmt();
        LambdaExpr lambda = new LambdaExpr(new Parameter(new UnknownType(), "()"), actionBody);

        RuleSetNode.RuleType ruleType = ruleSetNode.getRuleType();
        if (ruleType.getName().isEmpty()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Rule task \"{0}\" is invalid: you did not set a unit name, a rule flow group or a decision model.", nodeName));
        }

        if (ruleType.isRuleFlowGroup()) {
            MethodCallExpr ruleRuntimeBuilder = new MethodCallExpr(
                    new MethodCallExpr(new NameExpr("app"), "ruleUnits"), "ruleRuntimeBuilder");
            MethodCallExpr ruleRuntimeSupplier = new MethodCallExpr(ruleRuntimeBuilder, "newKieSession", NodeList.nodeList(new StringLiteralExpr("defaultStatelessKieSession"), new NameExpr("app.config().rule()")));
            actionBody.addStatement(new ReturnStmt(ruleRuntimeSupplier));
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "ruleFlowGroup", new StringLiteralExpr(ruleType.getName()), lambda);
        } else if (ruleType.isRuleUnit()) {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/class-templates/RuleUnitFactoryTemplate.java");
            Optional<Expression> ruleUnitFactory = parse(resourceAsStream).findFirst(Expression.class);

            String unitName = ruleType.getName();
            Class<?> unitClass = loadUnitClass(nodeName, unitName, metadata.getPackageName());

            ruleUnitFactory.ifPresent(factory -> {
                factory.findAll(ClassOrInterfaceType.class)
                        .stream()
                        .filter(t -> t.getNameAsString().equals("$Type$"))
                        .forEach(t -> t.setName(unitName));

                factory.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("bind"))
                        .ifPresent(m -> m.setBody(bind(variableScope, ruleSetNode, unitClass)));
                factory.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unit"))
                        .ifPresent(m -> m.setBody(unit(unitName)));
                factory.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unbind"))
                        .ifPresent(m -> m.setBody(unbind(variableScope, ruleSetNode, unitClass)));
            });

            if (ruleUnitFactory.isPresent()) {
                addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "ruleUnit", new StringLiteralExpr(ruleType.getName()), ruleUnitFactory.get());
            } else {
                addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "ruleUnit", new StringLiteralExpr(ruleType.getName()));
            }
        } else if (ruleType.isDecision()) {
            RuleSetNode.RuleType.Decision decisionModel = (RuleSetNode.RuleType.Decision) ruleType;
            MethodCallExpr ruleRuntimeSupplier = new MethodCallExpr(new NameExpr("app"), "dmnRuntimeBuilder");
            actionBody.addStatement(new ReturnStmt(ruleRuntimeSupplier));
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "dmnGroup", new StringLiteralExpr(decisionModel.getNamespace()),
                                     new StringLiteralExpr(decisionModel.getModel()),
                                     decisionModel.getDecision() == null ? new NullLiteralExpr() : new StringLiteralExpr(decisionModel.getDecision()),
                                     lambda);
        } else {
            throw new IllegalArgumentException("Rule task " + nodeName + "is invalid: unsupported rule language " + ruleSetNode.getLanguage());
        }

        for (Entry<String, String> entry : ruleSetNode.getInMappings().entrySet()) {
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "inMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }
        for (Entry<String, String> entry : ruleSetNode.getOutMappings().entrySet()) {
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "outMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }

        visitMetaData(ruleSetNode.getMetaData(), body, "ruleSetNode" + node.getId());

        addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "done");

    }

    private Class<?> loadUnitClass(String nodeName, String unitName, String packageName) {
        IllegalArgumentException ex;
        try {
            return contextClassLoader.loadClass(unitName);
        } catch (ClassNotFoundException e) {
            ex = new IllegalArgumentException(
                    MessageFormat.format("Rule task \"{0}\" is invalid: cannot load unit {1}", nodeName, unitName), e);
        }
        // maybe the name is not qualified. Let's try with tacking the packageName at the front
        try {
            return contextClassLoader.loadClass(packageName + "." + unitName);
        } catch (ClassNotFoundException e) {
            // throw the original error
            throw ex;
        }
    }

    private BlockStmt bind(VariableScope variableScope, RuleSetNode node, Class<?> unitClass) {
        // we need an empty constructor for now
        AssignExpr assignExpr = new AssignExpr(
                new VariableDeclarationExpr(new ClassOrInterfaceType(null, unitClass.getCanonicalName()), "model"),
                new ObjectCreationExpr().setType(unitClass.getCanonicalName()),
                AssignExpr.Operator.ASSIGN);

        BlockStmt actionBody = new BlockStmt();
        actionBody.addStatement(assignExpr);

        // warning: load class here
        for (Map.Entry<String, String> e : node.getInMappings().entrySet()) {
            Variable v = variableScope.findVariable(extractVariableFromExpression(e.getValue()));
            if (v != null) {
                actionBody.addStatement(makeAssignment(v));
                actionBody.addStatement(callSetter(unitClass, "model", e.getKey(), e.getValue(), isCollectionType(v)));
            }
        }

        actionBody.addStatement(new ReturnStmt(new NameExpr("model")));
        return actionBody;
    }

    private boolean isCollectionType(Variable v) {
        String stringType = v.getType().getStringType();
        Class<?> type;
        try {
            type = contextClassLoader.loadClass(stringType);
            return Collection.class.isAssignableFrom(type);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private Statement callSetter(Class<?> variableDeclarations, String targetVar, String destField, String expression, boolean isCollection) {
        if (expression.startsWith("#{")) {
            expression = expression.substring(2, expression.length() -1);
        }

        return callSetter(variableDeclarations, targetVar, destField, new NameExpr(expression), isCollection);
    }

    private Statement callSetter(Class<?> unitClass, String targetVar, String destField, Expression expression, boolean isCollection) {
        String methodName = "get" + StringUtils.capitalize(destField);
        Method m;
        try {
            m = unitClass.getMethod(methodName);
            Expression fieldAccessor =
                    new MethodCallExpr(new NameExpr("model"), methodName);
            if ( DataStore.class.isAssignableFrom(m.getReturnType() ) ) {
                return injectData(isCollection, destField, fieldAccessor, "add", expression);
            } else if ( DataStream.class.isAssignableFrom(m.getReturnType() ) ) {
                return injectData(isCollection, destField, fieldAccessor, "append", expression);
            } // else fallback to the following
        } catch (NoSuchMethodException e) {
            // fallback to the following
        }

        String setter = "set" + StringUtils.capitalize(destField);
        try {
            m = unitClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Class<?> returnType = m.getReturnType();
        return new ExpressionStmt(new MethodCallExpr(new NameExpr(targetVar), setter).addArgument(
                new CastExpr(
                        new ClassOrInterfaceType(null, returnType.getCanonicalName()),
                        new EnclosedExpr(expression))));

    }

    private Statement injectData(boolean isCollection, String destField, Expression fieldAccessor, String acceptorMethodName, Expression expression) {
        if (isCollection) {
            return drainIntoDS(fieldAccessor, destField, acceptorMethodName, expression);
        } else {
            return invoke(fieldAccessor, acceptorMethodName, expression);
        }
    }

    private Statement drainIntoDS(Expression fieldAccessor, String destField, String acceptorMethodName, Expression expression) {
        VariableDeclarationExpr varDecl = declareErasedDataSource(fieldAccessor);

        MethodCallExpr methodCallExpr = new MethodCallExpr(new MethodCallExpr(expression, "stream"), "forEach").addArgument(
                new LambdaExpr().addParameter(new UnknownType(), "o")
                        .setBody(new BlockStmt()
                                         .addStatement(varDecl)
                                         .addStatement(new MethodCallExpr(new NameExpr("ds"), acceptorMethodName)
                                                               .addArgument((new NameExpr("o"))))));

        return new BlockStmt()
                .addStatement(parseStatement("java.util.Objects.requireNonNull("+expression+", \"The input collection variable of a data source cannot be null: "+expression+"\");"))
                .addStatement(methodCallExpr);
    }

    private ExpressionStmt invoke(Expression scope, String methodName, Expression arg) {
        return new ExpressionStmt(new MethodCallExpr(scope, methodName).addArgument(arg));
    }

    private BlockStmt unit(String unitName) {
        MethodCallExpr ruleUnit = new MethodCallExpr(
                new MethodCallExpr(new NameExpr("app"), "ruleUnits"), "create")
                .addArgument(new ClassExpr().setType(unitName));
        return new BlockStmt().addStatement(new ReturnStmt(ruleUnit));
    }

    private BlockStmt unbind(VariableScope variableScope, RuleSetNode node, Class<?> unitClass) {
        BlockStmt stmts = new BlockStmt();

        for (Map.Entry<String, String> e : node.getOutMappings().entrySet()) {
            stmts.addStatement(makeAssignmentFromModel(variableScope.findVariable(e.getValue()), e.getKey(), unitClass));
        }

        return stmts;
    }

    protected Statement makeAssignmentFromModel(Variable v, String name, Class<?> unitClass) {
        String vname = v.getName();
        ClassOrInterfaceType type = parseClassOrInterfaceType(v.getType().getStringType());

        String methodName = "get" + StringUtils.capitalize(name);
        Method m;
        try {
            m = unitClass.getMethod(methodName);
            if ( DataSource.class.isAssignableFrom( m.getReturnType() ) ) {
                Expression fieldAccessor =
                        new MethodCallExpr(new NameExpr("model"), methodName);

                if (isCollectionType(v)) {
                    VariableDeclarationExpr varDecl = declareErasedDataSource(fieldAccessor);

                    return new BlockStmt()
                            .addStatement(varDecl)
                            .addStatement(parseStatement("java.util.Collection c = (java.util.Collection) kcontext.getVariable(\"" + vname + "\");"))
                            .addStatement(parseStatement("java.util.Objects.requireNonNull(c, \"Null collection variable used as an output variable: "
                                                                 + vname + ". Initialize this variable to get the contents or the data source, " +
                                                                 "or use a non-collection data type to extract one value.\");"))
                            .addStatement(new ExpressionStmt(
                                    new MethodCallExpr(new NameExpr("ds"), "subscribe")
                                            .addArgument(new MethodCallExpr(
                                                    new NameExpr(DataObserver.class.getCanonicalName()), "of")
                                                                 .addArgument(parseExpression("c::add")))));

                } else {
                    return new ExpressionStmt(
                            new MethodCallExpr(fieldAccessor, "subscribe")
                                    .addArgument(new MethodCallExpr(
                                            new NameExpr(DataObserver.class.getCanonicalName()), "of")
                                                         .addArgument(parseExpression("o -> kcontext.setVariable(\"" + vname + "\", o)"))));
                }

            } // else fallback to the following
        } catch (NoSuchMethodException e) {
            // fallback to the following
        }




        // `type` `name` = (`type`) `model.get<Name>
        BlockStmt blockStmt = new BlockStmt();
        blockStmt.addStatement(new AssignExpr(
                new VariableDeclarationExpr(type, name),
                new CastExpr(
                        type,
                        new MethodCallExpr(
                                new NameExpr("model"),
                                "get" + StringUtils.capitalize(name))),
                AssignExpr.Operator.ASSIGN));
            blockStmt.addStatement(new MethodCallExpr()
                                       .setScope(new NameExpr("kcontext"))
                                       .setName("setVariable")
                                       .addArgument(new StringLiteralExpr(vname))
                                       .addArgument(name));

        return blockStmt;
    }

    private VariableDeclarationExpr declareErasedDataSource(Expression fieldAccessor) {
        return new VariableDeclarationExpr(new VariableDeclarator()
                                                   .setType(DataStream.class.getCanonicalName())
                                                   .setName("ds")
                                                   .setInitializer(fieldAccessor));
    }
}
