/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.kie.kogito.UserTask;
import org.kie.kogito.UserTaskParam;
import org.kie.kogito.UserTaskParam.ParamType;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.nodeTypes.NodeWithVariables;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.core.util.StringUtils.ucFirst;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_AUTO_START;
import static org.jbpm.ruleflow.core.Metadata.DATA_OUTPUTS;

public class UserTaskModelMetaData {

    private static final String TASK_INTPUT_CLASS_SUFFIX = "TaskInput";
    private static final String TASK_OUTTPUT_CLASS_SUFFIX = "TaskOutput";
    private static final String TASK_MODEL_CLASS_SUFFIX = "TaskModel";
    private static final String TASK_NAME = "TaskName";
    private static final String WORK_ITEM = "workItem";
    private static final String PARAMS = "params";

    protected static final List<String> INTERNAL_FIELDS = Arrays.asList(TASK_NAME, "NodeName", "ActorId", "GroupId", "Priority", "Comment", "Skippable", "Content", "Locale");

    private final String packageName;

    private final VariableScope processVariableScope;
    private final VariableScope variableScope;
    private final HumanTaskNode humanTaskNode;
    private final String processId;

    private String inputModelClassName;
    private String inputModelClassSimpleName;

    private String outputModelClassName;
    private String outputModelClassSimpleName;

    private String taskModelClassName;
    private String taskModelClassSimpleName;

    public UserTaskModelMetaData(String packageName, VariableScope processVariableScope, VariableScope variableScope, HumanTaskNode humanTaskNode, String processId) {
        this.packageName = packageName;
        this.processVariableScope = processVariableScope;
        this.variableScope = variableScope;
        this.humanTaskNode = humanTaskNode;
        this.processId = processId;

        this.inputModelClassSimpleName = ucFirst(ProcessToExecModelGenerator.extractProcessId(processId) + "_" + humanTaskNode.getId() + "_" + TASK_INTPUT_CLASS_SUFFIX);
        this.inputModelClassName = packageName + '.' + inputModelClassSimpleName;

        this.outputModelClassSimpleName = ucFirst(ProcessToExecModelGenerator.extractProcessId(processId) + "_" + humanTaskNode.getId() + "_" + TASK_OUTTPUT_CLASS_SUFFIX);
        this.outputModelClassName = packageName + '.' + outputModelClassSimpleName;

        this.taskModelClassSimpleName = ucFirst(ProcessToExecModelGenerator.extractProcessId(processId) + "_" + humanTaskNode.getId() + "_" + TASK_MODEL_CLASS_SUFFIX);
        this.taskModelClassName = packageName + '.' + taskModelClassSimpleName;

    }

    public String generateInput() {
        CompilationUnit modelClass = compilationUnitInput();
        return modelClass.toString();
    }

    public String generateOutput() {
        CompilationUnit modelClass = compilationUnitOutput();
        return modelClass.toString();
    }

    public String generateModel() {
        CompilationUnit modelClass = compilationUnitModel();
        return modelClass.toString();
    }

    public String getInputModelClassName() {
        return inputModelClassName;
    }

    public String getOutputModelClassName() {
        return outputModelClassName;
    }

    public String getTaskModelClassName() {
        return taskModelClassName;
    }

    public String getName() {
        return (String) humanTaskNode.getWork().getParameters().getOrDefault(TASK_NAME, humanTaskNode.getName());
    }

    public String getNodeName() {
        return humanTaskNode.getName();
    }

    public long getId() {
        return humanTaskNode.getId();
    }

    private void addUserTaskAnnotation(ClassOrInterfaceDeclaration modelClass) {
        String taskName = (String) humanTaskNode.getWork().getParameter(TASK_NAME);
        if (taskName == null)
            taskName = humanTaskNode.getName();
        modelClass.addAndGetAnnotation(UserTask.class).addPair("taskName", new StringLiteralExpr(taskName)).addPair("processName", new StringLiteralExpr(processId));
    }

    private void addUserTaskParamAnnotation(FieldDeclaration fd, UserTaskParam.ParamType paramType) {
        fd.tryAddImportToParentCompilationUnit(ParamType.class);
        fd.addAndGetAnnotation(UserTaskParam.class).addPair("value", ParamType.class.getSimpleName() + '.' + paramType);
    }

    private RuntimeException cannotFindClass() {
        return new IllegalStateException("Cannot find class declaration in the template");
    }

    private CompilationUnit compilationUnitInput() {
        // task input handling
        CompilationUnit compilationUnit = parse(this.getClass().getResourceAsStream("/class-templates/TaskInputTemplate.java"));
        compilationUnit.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelClass = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class,
                sl1 -> true).orElseThrow(this::cannotFindClass);
        addComment(compilationUnit, "Task input");
        addUserTaskAnnotation(modelClass);

        modelClass.setName(inputModelClassSimpleName);

        // setup of static from method body
        ClassOrInterfaceType modelType = new ClassOrInterfaceType(null, modelClass.getNameAsString());
        BlockStmt staticFromMap = new BlockStmt();
        VariableDeclarationExpr itemField = new VariableDeclarationExpr(modelType, "item");
        staticFromMap.addStatement(new AssignExpr(itemField, new ObjectCreationExpr(null, modelType, NodeList.nodeList()), AssignExpr.Operator.ASSIGN));
        NameExpr item = new NameExpr("item");

        for (Entry<String, String> entry : humanTaskNode.getInMappings().entrySet()) {

            Variable variable = Optional.ofNullable(variableScope.findVariable(entry.getValue()))
                    .orElse(processVariableScope.findVariable(entry.getValue()));

            if (variable == null) {
                throw new IllegalStateException("Task " + humanTaskNode.getName() + " (input) " + entry.getKey() + " reference not existing variable " + entry.getValue());
            }

            FieldDeclaration fd = new FieldDeclaration().addVariable(
                    new VariableDeclarator()
                            .setType(variable.getType().getStringType())
                            .setName(entry.getKey()))
                    .addModifier(Modifier.Keyword.PRIVATE);

            modelClass.addMember(fd);

            addUserTaskParamAnnotation(fd, UserTaskParam.ParamType.INPUT);

            fd.createGetter();
            fd.createSetter();

            // from static method body
            FieldAccessExpr field = new FieldAccessExpr(item, entry.getKey());

            ClassOrInterfaceType type = parseClassOrInterfaceType(variable.getType().getStringType());
            staticFromMap.addStatement(new AssignExpr(field, new CastExpr(
                    type,
                    new MethodCallExpr(
                            new NameExpr(PARAMS),
                            "get")
                                    .addArgument(new StringLiteralExpr(entry.getKey()))),
                    AssignExpr.Operator.ASSIGN));
        }

        Set<String> metaParameters = humanTaskNode.getWork().getMetaParameters();
        for (Entry<String, Object> entry : humanTaskNode.getWork().getParameters().entrySet()) {

            if (entry.getValue() == null || INTERNAL_FIELDS.contains(entry.getKey()) || metaParameters.contains(entry
                    .getKey())) {
                continue;
            }

            FieldDeclaration fd = new FieldDeclaration().addVariable(
                    new VariableDeclarator()
                            .setType(entry.getValue().getClass().getCanonicalName())
                            .setName(entry.getKey()))
                    .addModifier(Modifier.Keyword.PRIVATE);
            modelClass.addMember(fd);
            addUserTaskParamAnnotation(fd, UserTaskParam.ParamType.INPUT);

            fd.createGetter();
            fd.createSetter();

            // from static method body
            FieldAccessExpr field = new FieldAccessExpr(item, entry.getKey());

            ClassOrInterfaceType type = parseClassOrInterfaceType(entry.getValue().getClass().getCanonicalName());
            staticFromMap.addStatement(new AssignExpr(field, new CastExpr(
                    type,
                    new MethodCallExpr(
                            new NameExpr(PARAMS),
                            "get")
                                    .addArgument(new StringLiteralExpr(entry.getKey()))),
                    AssignExpr.Operator.ASSIGN));
        }
        Optional<MethodDeclaration> staticFromMethod = modelClass.findFirst(
                MethodDeclaration.class, sl -> sl.getName().asString().equals("fromMap") && sl.isStatic());
        if (staticFromMethod.isPresent()) {
            MethodDeclaration from = staticFromMethod.get();
            from.setType(modelClass.getNameAsString());
            staticFromMap.addStatement(new ReturnStmt(new NameExpr("item")));
            from.setBody(staticFromMap);
        }
        return compilationUnit;
    }

    @SuppressWarnings({ "unchecked" })
    private CompilationUnit compilationUnitOutput() {
        CompilationUnit compilationUnit = parse(this.getClass().getResourceAsStream("/class-templates/TaskOutputTemplate.java"));
        compilationUnit.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelClass = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class, sl1 -> true).orElseThrow(this::cannotFindClass);
        addComment(compilationUnit, "Task output");
        addUserTaskAnnotation(modelClass);
        modelClass.setName(outputModelClassSimpleName);

        // setup of the toMap method body
        BlockStmt toMapBody = new BlockStmt();
        BlockStmt fromMapBody = new BlockStmt();
        NameExpr params = new NameExpr(PARAMS);
        final String fromMapReturnName = "result";
        NameExpr fromMapReturn = new NameExpr(fromMapReturnName);
        fromMapBody.addStatement(new AssignExpr(new VariableDeclarationExpr(parseClassOrInterfaceType(
                outputModelClassName), fromMapReturnName),
                new ObjectCreationExpr().setType(outputModelClassSimpleName), Operator.ASSIGN));

        ClassOrInterfaceType toMap = new ClassOrInterfaceType(null, new SimpleName(Map.class.getSimpleName()), NodeList
                .nodeList(new ClassOrInterfaceType(null, String.class.getSimpleName()), new ClassOrInterfaceType(null,
                        Object.class.getSimpleName())));

        VariableDeclarationExpr paramsField = new VariableDeclarationExpr(toMap, PARAMS);
        toMapBody.addStatement(new AssignExpr(paramsField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null,
                HashMap.class.getSimpleName() + "<>"), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));

        for (Entry<String, String> entry : humanTaskNode.getOutMappings().entrySet()) {
            if (entry.getValue() == null || INTERNAL_FIELDS.contains(entry.getKey())) {
                continue;
            }

            Variable variable = Optional.ofNullable(variableScope.findVariable(entry.getValue()))
                    .orElse(processVariableScope.findVariable(entry.getValue()));

            if (variable == null) {
                // check if given mapping is an expression
                Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(entry.getValue());
                if (matcher.find()) {
                    Map<String, String> dataOutputs = (Map<String, String>) humanTaskNode.getMetaData(DATA_OUTPUTS);
                    variable = new Variable();
                    variable.setName(entry.getKey());
                    variable.setType(new ObjectDataType(dataOutputs.get(entry.getKey())));
                } else {
                    throw new IllegalStateException("Task " + humanTaskNode.getName() + " (output) " + entry.getKey() + " reference not existing variable " + entry.getValue());
                }
            }

            FieldDeclaration fd = new FieldDeclaration().addVariable(
                    new VariableDeclarator()
                            .setType(variable.getType().getStringType())
                            .setName(entry.getKey()))
                    .addModifier(Modifier.Keyword.PRIVATE);
            modelClass.addMember(fd);
            addUserTaskParamAnnotation(fd, UserTaskParam.ParamType.OUTPUT);

            fd.createGetter();
            fd.createSetter();

            // toMap method body
            MethodCallExpr putVariable = new MethodCallExpr(params, "put");
            putVariable.addArgument(new StringLiteralExpr(entry.getKey()));
            putVariable.addArgument(new FieldAccessExpr(new ThisExpr(), entry.getKey()));
            toMapBody.addStatement(putVariable);

            // fromMap method body
            fromMapBody.addStatement(new AssignExpr(new FieldAccessExpr(fromMapReturn, entry.getKey()),
                    new CastExpr(new ClassOrInterfaceType(null, variable.getType().getStringType()),
                            new MethodCallExpr(params, "get").addArgument(new StringLiteralExpr(entry.getKey()))),
                    Operator.ASSIGN));

        }

        toMapBody.addStatement(new ReturnStmt(params));
        fromMapBody.addStatement(new ReturnStmt(fromMapReturn));
        modelClass.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("toMap")).ifPresent(
                methodDeclaration -> methodDeclaration.setBody(toMapBody));
        modelClass.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("fromMap")).ifPresent(
                methodDeclaration -> {
                    methodDeclaration.setBody(fromMapBody);
                    methodDeclaration.setType(outputModelClassSimpleName);
                });

        return compilationUnit;
    }

    private CompilationUnit compilationUnitModel() {
        CompilationUnit compilationUnit = parse(this.getClass().getResourceAsStream(
                "/class-templates/TaskModelTemplate.java"));
        compilationUnit.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelClass = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class, sl1 -> true).orElseThrow(this::cannotFindClass);
        addComment(compilationUnit, "Task model");
        modelClass.setName(taskModelClassSimpleName);
        modelClass.getImplementedTypes().forEach(t -> t
                .setTypeArguments(
                        NodeList.nodeList(parseClassOrInterfaceType(inputModelClassName), parseClassOrInterfaceType(
                                outputModelClassName))));
        modelClass.findAll(NameExpr.class).forEach(this::templateReplacement);
        modelClass.findAll(VariableDeclarationExpr.class).forEach(this::templateReplacement);
        modelClass.findAll(FieldDeclaration.class).forEach(this::templateReplacement);
        modelClass.findAll(ObjectCreationExpr.class).forEach(this::templateReplacement);
        modelClass.findAll(MethodDeclaration.class).forEach(this::templateReplacement);
        modelClass.findAll(Parameter.class).forEach(this::templateReplacement);
        return compilationUnit;
    }

    private void addComment(CompilationUnit unit, String prefix) {
        unit.addOrphanComment(new LineComment(prefix + " for user task '" + humanTaskNode.getName() + "' in process '" + processId + "'"));
    }

    private void templateReplacement(NameExpr name) {
        name.setName(templateReplacement(name.getNameAsString()));
    }

    private <T extends Node, R extends Type> void templateReplacement(NodeWithType<T, R> expr) {
        expr.setType(templateReplacement(expr.getTypeAsString()));
    }

    private <T extends Node> void templateReplacement(NodeWithVariables<T> expr) {
        for (VariableDeclarator variable : expr.getVariables()) {
            variable.setType(templateReplacement(variable.getTypeAsString()));
        }
    }

    public String templateReplacement(String template) {
        template = template.replace("$TaskInput$", inputModelClassName);
        template = template.replace("$TaskOutput$", outputModelClassName);
        template = template.replace("$TaskModel$", taskModelClassName);
        return template;
    }

    public boolean isAdHoc() {
        return !Boolean.parseBoolean((String) humanTaskNode.getMetaData(CUSTOM_AUTO_START))
                && (humanTaskNode.getIncomingConnections() == null || humanTaskNode.getIncomingConnections().isEmpty());
    }

    public SwitchEntry getModelSwitchEntry() {
        SwitchEntry entry = new SwitchEntry();
        entry.setLabels(NodeList.nodeList(new StringLiteralExpr(Long.toString(humanTaskNode.getId()))));
        entry.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(
                taskModelClassSimpleName), new SimpleName("from")).addArgument(WORK_ITEM)));
        return entry;
    }
}
