/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.submarine.codegen.process;

import java.util.List;

import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.UserTaskModelMetaData;
import org.kie.api.definition.process.WorkflowProcess;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class ResourceGenerator {

    private final String relativePath;

    private WorkflowProcess process;
    private final String packageName;
    private final String resourceClazzName;
    private final String processClazzName;
    private String processId;
    private String dataClazzName;
    private String modelfqcn;
    private final String processName;
    private boolean hasCdi;
    
    private List<UserTaskModelMetaData> userTasks;

    public ResourceGenerator(
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn) {
        this.process = process;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.capitalize(processName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.modelfqcn = modelfqcn;
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
    }

    public ResourceGenerator withCdi(boolean hasCdi) {
        this.hasCdi = hasCdi;
        return this;
    }
    
    public ResourceGenerator withUserTasks(List<UserTaskModelMetaData> userTasks) {
        this.userTasks = userTasks;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    public String generate() {
        CompilationUnit clazz = JavaParser.parse(
                this.getClass().getResourceAsStream("/class-templates/RestResourceTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);

        ClassOrInterfaceDeclaration template =
                clazz.findFirst(ClassOrInterfaceDeclaration.class).get();

        template.setName(resourceClazzName);
        
        if (userTasks != null) {

            CompilationUnit userTaskClazz = JavaParser.parse(
                                                     this.getClass().getResourceAsStream("/class-templates/RestResourceUserTaskTemplate.java"));
            
            
            ClassOrInterfaceDeclaration userTaskTemplate =
                    userTaskClazz.findFirst(ClassOrInterfaceDeclaration.class).get();
            for (UserTaskModelMetaData userTask : userTasks) {
       
                userTaskTemplate.findAll(MethodDeclaration.class).forEach(md -> {                    
                    
                    template.addMethod(md.getName() + "_" + userTask.getId(), Keyword.PUBLIC)
                    .setType(md.getType())
                    .setParameters(md.getParameters())
                    .setBody(md.getBody().get())
                    .setAnnotations(md.getAnnotations());
                    
                });
                
                template.findAll(StringLiteralExpr.class).forEach(s -> interpolateUserTaskStrings(s, userTask));
                
                template.findAll(ClassOrInterfaceType.class).forEach(c -> interpolateUserTaskTypes(c, userTask.getInputMoodelClassSimpleName(), userTask.getOutputMoodelClassSimpleName()));
                template.findAll(NameExpr.class).forEach(c -> interpolateUserTaskNameExp(c, userTask));
                
            }
        }
        
        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        template.findAll(ClassOrInterfaceType.class).forEach(this::interpolateTypes);

        if (hasCdi) {
            template.findAll(FieldDeclaration.class,
                             this::isProcessField).forEach(this::annotateFields);
        } else {
            template.findAll(FieldDeclaration.class,
                             this::isProcessField).forEach(fd -> initializeField(fd, template));
        }

        return clazz.toString();
    }

    private boolean isProcessField(FieldDeclaration fd) {
        return fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("Process");
    }

    private void annotateFields(FieldDeclaration fd) {
        fd.addAnnotation("javax.inject.Inject");
        fd.addSingleMemberAnnotation("javax.inject.Named", new StringLiteralExpr(processId));
    }
    
    private void initializeField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {
        BlockStmt body = new BlockStmt();
        AssignExpr assignExpr = new AssignExpr(
                                               new FieldAccessExpr(new ThisExpr(), "process"),
                                               new ObjectCreationExpr().setType(processClazzName),
                                               AssignExpr.Operator.ASSIGN);
        
        body.addStatement(assignExpr);
        template.addConstructor(Keyword.PUBLIC).setBody(body);
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String s = vv.getValue();
        String documentation =
                process.getMetaData()
                        .getOrDefault("Documentation", processName).toString();
        String interpolated =
                s.replace("$name$", processName)
                        .replace("$id$", processId)
                        .replace("$documentation$", documentation);
        vv.setString(interpolated);
    }
    
    private void interpolateUserTaskStrings(StringLiteralExpr vv, UserTaskModelMetaData userTask) {
        String s = vv.getValue();
   
        String interpolated =
                s.replace("$taskname$", userTask.getName().replaceAll("\\s", "_"));
        vv.setString(interpolated);
    }
    
    private void interpolateUserTaskNameExp(NameExpr name, UserTaskModelMetaData userTask) {        
        String identifier = name.getNameAsString();
        
        name.setName(identifier.replace("$TaskInput$", userTask.getInputMoodelClassSimpleName()));
        
        identifier = name.getNameAsString();
        name.setName(identifier.replace("$TaskOutput$", userTask.getOutputMoodelClassSimpleName()));
    }

    private void interpolateTypes(ClassOrInterfaceType t) {
        SimpleName returnType = t.asClassOrInterfaceType().getName();
        interpolateTypes(returnType);
        t.getTypeArguments().ifPresent(this::interpolateTypeArguments);
    }

    private void interpolateTypes(SimpleName returnType) {
        String identifier = returnType.getIdentifier();
        returnType.setIdentifier(identifier.replace("$Type$", dataClazzName));
    }

    private void interpolateTypeArguments(NodeList<Type> ta) {
        ta.stream().map(Type::asClassOrInterfaceType)
                .forEach(this::interpolateTypes);
    }
    
    
    private void interpolateUserTaskTypes(ClassOrInterfaceType t, String inputClazzName, String outputClazzName) {
        SimpleName returnType = t.asClassOrInterfaceType().getName();
        interpolateUserTaskTypes(returnType, inputClazzName, outputClazzName);
        t.getTypeArguments().ifPresent(o -> interpolateUserTaskTypeArguments(o, inputClazzName, outputClazzName));
    }

    private void interpolateUserTaskTypes(SimpleName returnType, String inputClazzName, String outputClazzName) {
        String identifier = returnType.getIdentifier();
              
        returnType.setIdentifier(identifier.replace("$TaskInput$", inputClazzName));
        
        identifier = returnType.getIdentifier();
        returnType.setIdentifier(identifier.replace("$TaskOutput$", outputClazzName));
    }

    private void interpolateUserTaskTypeArguments(NodeList<Type> ta, String inputClazzName, String outputClazzName) {
        ta.stream().map(Type::asClassOrInterfaceType)
                .forEach(t -> interpolateUserTaskTypes(t, inputClazzName, outputClazzName));
    }

    public String generatedFilePath() {
        return relativePath;
    }
}