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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.drools.core.util.StringUtils;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.core.util.StringUtils.ucFirst;

public class ProcessToExecModelGenerator {

    public static final ProcessToExecModelGenerator INSTANCE = new ProcessToExecModelGenerator(
            ProcessToExecModelGenerator.class.getClassLoader());

    private static final String PROCESS_CLASS_SUFFIX = "Process";
    private static final String MODEL_CLASS_SUFFIX = "Model";
    private static final String PROCESS_TEMPLATE_FILE = "/class-templates/ProcessTemplate.java";

    private final ProcessVisitor processVisitor;

    public ProcessToExecModelGenerator(ClassLoader contextClassLoader) {
        this.processVisitor = new ProcessVisitor(contextClassLoader);
    }

    public ProcessMetaData generate(WorkflowProcess process) {
        CompilationUnit parsedClazzFile = parse(this.getClass().getResourceAsStream(PROCESS_TEMPLATE_FILE));
        parsedClazzFile.setPackageDeclaration(process.getPackageName());
        Optional<ClassOrInterfaceDeclaration> processClazzOptional = parsedClazzFile.findFirst(
                ClassOrInterfaceDeclaration.class,
                sl -> true);

        String extractedProcessId = extractProcessId(process.getId());

        if (!processClazzOptional.isPresent()) {
            throw new NoSuchElementException("Cannot find class declaration in the template");
        }
        ClassOrInterfaceDeclaration processClazz = processClazzOptional.get();
        processClazz.setName(ucFirst(extractedProcessId + PROCESS_CLASS_SUFFIX));
        String packageName = parsedClazzFile.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(null);
        ProcessMetaData metadata =
                new ProcessMetaData(process.getId(), extractedProcessId, process.getName(), process.getVersion(),
                        packageName, processClazz.getNameAsString());

        Optional<MethodDeclaration> processMethod = parsedClazzFile.findFirst(MethodDeclaration.class, sl -> sl
                .getName()
                .asString()
                .equals("process"));

        processVisitor.visitProcess(process, processMethod.get(), metadata);

        metadata.setGeneratedClassModel(parsedClazzFile);
        return metadata;
    }

    public ModelMetaData generateModel(WorkflowProcess process) {
        String packageName = process.getPackageName();
        String name = extractModelClassName(process.getId());
        VariableScope variableScope = getVariableScope(process);
        String toModelClassName = extractModelClassName(process.getId()) + "Output";
        return new ModelMetaData(process.getId(),
                packageName,
                name,
                ((KogitoWorkflowProcess) process).getVisibility(),
                VariableDeclarations.of(variableScope),
                false,
                "/class-templates/ModelTemplate.java",
                new AddMethodConsumer("toModel", toModelClassName,
                        VariableDeclarations.ofOutput(variableScope), true));
    }

    public ModelMetaData generateInputModel(WorkflowProcess process) {
        String packageName = process.getPackageName();
        String modelName = extractModelClassName(process.getId());
        String name = modelName + "Input";
        VariableDeclarations inputVars = VariableDeclarations.ofInput(getVariableScope(process));
        return new ModelMetaData(process.getId(),
                packageName, name,
                ((KogitoWorkflowProcess) process).getVisibility(),
                inputVars,
                true,
                "/class-templates/ModelNoIDTemplate.java",
                new AddMethodConsumer("toModel", modelName, inputVars, false));
    }

    public ModelMetaData generateOutputModel(WorkflowProcess process) {
        String packageName = process.getPackageName();
        String modelName = extractModelClassName(process.getId());
        String name = modelName + "Output";
        VariableScope variableScope = getVariableScope(process);
        return new ModelMetaData(process.getId(),
                packageName,
                name,
                ((KogitoWorkflowProcess) process).getVisibility(),
                VariableDeclarations.ofOutput(variableScope),
                true,
                "/class-templates/ModelTemplate.java",
                new AddMethodConsumer("toModel", modelName,
                        VariableDeclarations.ofOutput(variableScope), true));
    }

    private static VariableScope getVariableScope(WorkflowProcess process) {
        return (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
    }

    private static class AddMethodConsumer implements Consumer<CompilationUnit> {

        private String methodName;
        private String returnClassName;
        private VariableDeclarations vars;
        private boolean includeId;

        public AddMethodConsumer(String methodName, String returnClassName, VariableDeclarations vars,
                boolean includeId) {
            this.methodName = methodName;
            this.returnClassName = returnClassName;
            this.vars = vars;
            this.includeId = includeId;
        }

        @Override
        public void accept(CompilationUnit cu) {
            Optional<ClassOrInterfaceDeclaration> clazz = cu.findFirst(ClassOrInterfaceDeclaration.class);
            if (!clazz.isPresent()) {
                throw new NoSuchElementException("Cannot find class declaration in the template");
            }
            ClassOrInterfaceType type = parseClassOrInterfaceType(returnClassName);
            final String resultVarName = "result";

            //Setting the Class type in the interface implementation
            clazz.get().findAll(ClassOrInterfaceType.class)
                    .stream()
                    .filter(t -> t.getNameAsString().equals("$modelClass$"))
                    .forEach(t -> t.setName(returnClassName));

            //Adding the Method itself
            MethodDeclaration method = clazz.get()
                    .addMethod(methodName, Modifier.Keyword.PUBLIC)
                    .setType(type)
                    .addAnnotation(Override.class);
            BlockStmt body = new BlockStmt();
            VariableDeclarationExpr returnVar = new VariableDeclarationExpr(type, resultVarName);
            body.addStatement(new AssignExpr(returnVar, new ObjectCreationExpr(null, type, NodeList.nodeList()),
                    AssignExpr.Operator.ASSIGN));
            NameExpr returnName = new NameExpr(resultVarName);
            // fill id
            if (includeId) {
                body.addStatement(new MethodCallExpr(returnName, "setId").addArgument(new MethodCallExpr(null,
                        "getId")));
            }
            for (Variable var : vars.getTypes().values()) {
                final String fieldName = StringUtils.ucFirst(var.getSanitizedName());
                body.addStatement(new MethodCallExpr(returnName, "set" + fieldName).addArgument(new MethodCallExpr(null,
                        "get" +
                                fieldName)));
            }
            body.addStatement(new ReturnStmt(returnName));
            method.setBody(body);
        }
    }

    public static String extractModelClassName(String processId) {
        return ucFirst(extractProcessId(processId) + MODEL_CLASS_SUFFIX);
    }

    public List<UserTaskModelMetaData> generateUserTaskModel(WorkflowProcess process) {
        String packageName = process.getPackageName();
        List<UserTaskModelMetaData> userTaskModels = new ArrayList<>();

        VariableScope variableScope = (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(
                VariableScope.VARIABLE_SCOPE);

        for (Node node : ((WorkflowProcessImpl) process).getNodesRecursively()) {
            if (node instanceof HumanTaskNode) {
                HumanTaskNode humanTaskNode = (HumanTaskNode) node;
                VariableScope nodeVariableScope = (VariableScope) ((ContextContainer) humanTaskNode
                        .getParentContainer()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
                if (nodeVariableScope == null) {
                    nodeVariableScope = variableScope;
                }
                userTaskModels.add(new UserTaskModelMetaData(packageName, variableScope, nodeVariableScope,
                        humanTaskNode, process.getId()));
            }
        }

        return userTaskModels;
    }

    public static String extractProcessId(String processId) {
        if (processId.contains(".")) {
            return processId.substring(processId.lastIndexOf('.') + 1);
        }

        return processId;
    }
}
