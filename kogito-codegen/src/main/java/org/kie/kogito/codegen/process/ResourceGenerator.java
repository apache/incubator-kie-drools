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

package org.kie.kogito.codegen.process;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.process.CodegenUtils.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.compiler.canonical.UserTaskModelMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.drools.modelcompiler.builder.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
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
    private final String appCanonicalName;
    private DependencyInjectionAnnotator annotator;
    
    private boolean startable;
    private List<UserTaskModelMetaData> userTasks;
    private Map<String, String> signals;
    private List<TriggerMetaData> triggers;
    
    public ResourceGenerator(
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName) {
        this.process = process;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        this.appCanonicalName = appCanonicalName;
        String classPrefix = StringUtils.capitalize(processName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.modelfqcn = modelfqcn;
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
    }

    public ResourceGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }
    
    public ResourceGenerator withUserTasks(List<UserTaskModelMetaData> userTasks) {
        this.userTasks = userTasks;
        return this;
    }
    
    public ResourceGenerator withSignals(Map<String, String> signals) {
        this.signals = signals;
        return this;
    }
    
    public ResourceGenerator withTriggers(List<TriggerMetaData> triggers, boolean startable) {
        this.triggers = triggers;
        this.startable = startable;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    public String generate() {
        CompilationUnit clazz = parse(
                this.getClass().getResourceAsStream("/class-templates/RestResourceTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);

        ClassOrInterfaceDeclaration template =
                clazz.findFirst(ClassOrInterfaceDeclaration.class).get();

        template.setName(resourceClazzName);
        
        if (userTasks != null) {

            CompilationUnit userTaskClazz = parse(
                                                     this.getClass().getResourceAsStream("/class-templates/RestResourceUserTaskTemplate.java"));
            
            
            ClassOrInterfaceDeclaration userTaskTemplate =
                    userTaskClazz.findFirst(ClassOrInterfaceDeclaration.class).get();
            for (UserTaskModelMetaData userTask : userTasks) {
       
                userTaskTemplate.findAll(MethodDeclaration.class).forEach(md -> {                    
                    
                    MethodDeclaration cloned = md.clone();
                    template.addMethod(cloned.getName() + "_" + userTask.getId(), Keyword.PUBLIC)
                    .setType(cloned.getType())
                    .setParameters(cloned.getParameters())
                    .setBody(cloned.getBody().get())
                    .setAnnotations(cloned.getAnnotations());
                    
                });
                
                template.findAll(StringLiteralExpr.class).forEach(s -> interpolateUserTaskStrings(s, userTask));
                
                template.findAll(ClassOrInterfaceType.class).forEach(c -> interpolateUserTaskTypes(c, userTask.getInputMoodelClassSimpleName(), userTask.getOutputMoodelClassSimpleName()));
                template.findAll(NameExpr.class).forEach(c -> interpolateUserTaskNameExp(c, userTask));
                
            }
        }
        
        if (signals != null) {
            
            int index = 0;
            for (Entry<String, String> entry : signals.entrySet()) {
                MethodDeclaration signalMethod = new MethodDeclaration()
                        .setName("signal_" + index)
                        .setType(modelfqcn)
                        .setModifiers(Keyword.PUBLIC)
                        .addAnnotation("POST")
                        .addSingleMemberAnnotation("Path", new StringLiteralExpr("/{id}/" + entry.getKey()))
                        .addSingleMemberAnnotation("Produces", "MediaType.APPLICATION_JSON");
                
                signalMethod.addAndGetParameter("String", "id").addSingleMemberAnnotation("PathParam", new StringLiteralExpr("id"));
                
                if (entry.getValue() != null) {
                    signalMethod.addSingleMemberAnnotation("Consumes", "MediaType.APPLICATION_JSON");                    
                    signalMethod.addAndGetParameter(entry.getValue(), "data");
                }
                
                // method body to signal process instance                
                MethodCallExpr newSignal = new MethodCallExpr(new NameExpr(Sig.class.getCanonicalName()), "of")
                        .addArgument(new StringLiteralExpr(entry.getKey()))
                        .addArgument(entry.getValue() != null ? new NameExpr("data") : new NullLiteralExpr());
                MethodCallExpr instances = new MethodCallExpr(new NameExpr("process"), "instances");
                MethodCallExpr findById = new MethodCallExpr(instances, "findById").addArgument(new NameExpr("id"));
                MethodCallExpr getOptional = new MethodCallExpr(findById, "orElse").addArgument(new NullLiteralExpr());
                
                VariableDeclarator processInstance = new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(ProcessInstance.class.getCanonicalName()), 
                                                                                                     NodeList.nodeList(new ClassOrInterfaceType(null, modelfqcn))),
                                                                                                     "pi",
                                                                                                     getOptional);
                // local variable for process instance
                VariableDeclarationExpr processInstanceField = new VariableDeclarationExpr(processInstance);
                // signal only when there is non null process instance
                IfStmt processInstanceExists = new IfStmt(new BinaryExpr(new NameExpr("pi"), new NullLiteralExpr(), Operator.EQUALS), 
                                                new ReturnStmt(new NullLiteralExpr()), 
                                                 null);
                
                MethodCallExpr send = new MethodCallExpr(new NameExpr("pi"), "send").addArgument(newSignal);
                // return current state of variables after the signal
                MethodCallExpr variables = new MethodCallExpr(new NameExpr("pi"), "variables");
                signalMethod.createBody().addStatement(processInstanceField).addStatement(processInstanceExists).addStatement(send).addStatement(new ReturnStmt(variables));  
                
                
                template.addMember(signalMethod);
            }
        }
        
        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, dataClazzName));
        template.findAll(MethodDeclaration.class).forEach(this::interpolateMethods);

        if (useInjection()) {
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(fd -> annotator.withNamedInjection(fd, processId));
            
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> annotator.withInjection(fd));
        } else {
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(fd -> initializeProcessField(fd, template));
            
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> initializeApplicationField(fd, template));
        }
        
        // if triggers are not empty remove createResource method as there is another trigger to start process instances
        if (!startable || !isPublic()) {
            Optional<MethodDeclaration> createResourceMethod = template.findFirst(MethodDeclaration.class).filter(md -> md.getNameAsString().equals("createResource_" + processName));
            if (createResourceMethod.isPresent()) {
                template.remove(createResourceMethod.get());
            }
        }
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    
    private void initializeProcessField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(processClazzName));
    }
    
    private void initializeApplicationField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {        
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
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
    
    private void interpolateMethods(MethodDeclaration m) {
        SimpleName methodName = m.getName();
        String interpolated =
                methodName.asString().replace("$name$", processName);
        m.setName(interpolated);
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
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
    
    protected boolean isPublic() {
        return WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(process.getVisibility());
    }
}