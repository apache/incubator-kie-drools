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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.Model;
import org.kie.kogito.process.impl.AbstractProcess;

public class ProcessGenerator {

    private final String packageName;
    private final WorkflowProcess process;
    private final ProcessExecutableModelGenerator legacyProcessGenerator;
    private final Map<String, WorkflowProcess> processMapping;
    private final String typeName;
    private final String modelTypeName;
    private final String generatedFilePath;
    private final String completePath;
    private final String canonicalName;
    private final String targetCanonicalName;
    private final String appCanonicalName;
    private String targetTypeName;
    private boolean hasCdi = false;   
    
    private List<CompilationUnit> additionalClasses = new ArrayList<>();

    public ProcessGenerator(
            WorkflowProcess process,
            ProcessExecutableModelGenerator legacyProcessGenerator,
            Map<String, WorkflowProcess> processMapping,
            String typeName,
            String modelTypeName,
            String appCanonicalName) {

        this.appCanonicalName = appCanonicalName;

        this.packageName = process.getPackageName();
        this.process = process;
        this.legacyProcessGenerator = legacyProcessGenerator;
        this.processMapping = processMapping;
        this.typeName = typeName;
        this.modelTypeName = modelTypeName;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "Process";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + generatedFilePath;
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String targetTypeName() {
        return targetTypeName;
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(completePath, generate().getBytes( StandardCharsets.UTF_8 ));
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.addImport("org.jbpm.process.core.datatype.impl.type.ObjectDataType");
        compilationUnit.addImport("org.jbpm.ruleflow.core.RuleFlowProcessFactory");
        compilationUnit.addImport("org.drools.core.util.KieFunctions");
        compilationUnit.getTypes().add(classDeclaration());
        return compilationUnit;
    }

    private MethodDeclaration createInstanceMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new ObjectCreationExpr()
                        .setType(processInstanceFQCN)
                        .setArguments(NodeList.nodeList(
                                new ThisExpr(),
                                new NameExpr("value"),
                                createProcessRuntime())));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(modelTypeName, "value")
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt()
                                 .addStatement(returnStmt));
        return methodDeclaration;
    }
    
    private MethodDeclaration createInstanceGenericMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new MethodCallExpr(new ThisExpr(), "createInstance").addArgument(new CastExpr(new ClassOrInterfaceType(null, modelTypeName), new NameExpr("value"))));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(Model.class.getCanonicalName(), "value")
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt()
                                 .addStatement(returnStmt));
        return methodDeclaration;
    }

    private MethodDeclaration legacyProcess(ProcessMetaData processMetaDate) {
        MethodDeclaration legacyProcess = processMetaDate.getGeneratedClassModel()
                .findFirst(MethodDeclaration.class).get()
                .setModifiers(Modifier.Keyword.PROTECTED)
                .setType(Process.class.getCanonicalName())
                .setName("legacyProcess");
        return legacyProcess;
    }

    private MethodCallExpr createProcessRuntime() {
        return new MethodCallExpr(
                new ThisExpr(),
                "createLegacyProcessRuntime");
    }
    
    private MethodDeclaration internalConfigure(ProcessMetaData processMetaData) {
        BlockStmt body = new BlockStmt();
        MethodDeclaration internalConfigure = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(targetTypeName)
                .setName("configure")
                .setBody(body);                

        if (!processMetaData.getGeneratedHandlers().isEmpty()) {
            
            processMetaData.getGeneratedHandlers().forEach((name, handler) -> {
                ClassOrInterfaceDeclaration clazz = handler.findFirst(ClassOrInterfaceDeclaration.class).get();
                if (hasCdi) {
                    
                    
                    clazz.addAnnotation("javax.enterprise.context.ApplicationScoped");
                    BlockStmt actionBody = new BlockStmt();
                    LambdaExpr forachBody = new LambdaExpr(new Parameter(new UnknownType(), "h"), actionBody);
                    MethodCallExpr forachHandler = new MethodCallExpr(new NameExpr("handlers"), "forEach");                    
                    forachHandler.addArgument(forachBody);
                    
                    MethodCallExpr workItemManager = new MethodCallExpr(new NameExpr("services"), "getWorkItemManager");            
                    MethodCallExpr registerHandler = new MethodCallExpr(workItemManager, "registerWorkItemHandler").addArgument(new MethodCallExpr(new NameExpr("h"), "getName")).addArgument(new NameExpr("h"));
                    
                    actionBody.addStatement(registerHandler);
                    
                    body.addStatement(forachHandler);
                } else {
                
                    String packageName = handler.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                    String clazzName = clazz.getName().toString();
                    
                    MethodCallExpr workItemManager = new MethodCallExpr(new NameExpr("services"), "getWorkItemManager");            
                    MethodCallExpr registerHandler = new MethodCallExpr(workItemManager, "registerWorkItemHandler").addArgument(new StringLiteralExpr(name)).addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, packageName + "." + clazzName), NodeList.nodeList()));
                    
                    body.addStatement(registerHandler);
                }
                // annotate for injection or add constructor for initialization
                handler.findAll(FieldDeclaration.class).forEach(fd -> {
                    if (hasCdi) {
                        fd.addAnnotation("javax.inject.Inject");
                    } else {
                        BlockStmt constructorBody = new BlockStmt();
                        AssignExpr assignExpr = new AssignExpr(
                                                               new FieldAccessExpr(new ThisExpr(), fd.getVariable(0).getNameAsString()),
                                                               new ObjectCreationExpr().setType(fd.getVariable(0).getType().toString()),
                                                               AssignExpr.Operator.ASSIGN);
                        
                        constructorBody.addStatement(assignExpr);
                        clazz.addConstructor(Keyword.PUBLIC).setBody(constructorBody);
                    }
                });
                
                
                additionalClasses.add(handler);
            });
        }
        if (!processMetaData.getGeneratedListeners().isEmpty()) {
            
            processMetaData.getGeneratedListeners().forEach(listener -> {
                
                ClassOrInterfaceDeclaration clazz = listener.findFirst(ClassOrInterfaceDeclaration.class).get();
                String packageName = listener.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                String clazzName = clazz.getName().toString();
                
                MethodCallExpr eventSupport = new MethodCallExpr(new NameExpr("services"), "getEventSupport");            
                MethodCallExpr registerListener = new MethodCallExpr(eventSupport, "addEventListener").addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, packageName + "." + clazzName), NodeList.nodeList()));
                
                body.addStatement(registerListener);
                
                additionalClasses.add(listener);
            });
        }
        body.addStatement(new ReturnStmt(new ThisExpr()));
        
        return internalConfigure;
    }

    public static ClassOrInterfaceType processType(String canonicalName) {
        return new ClassOrInterfaceType(null, canonicalName + "Process");
    }

    public static ClassOrInterfaceType abstractProcessType(String canonicalName) {
        return new ClassOrInterfaceType(null, AbstractProcess.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .setModifiers(Modifier.Keyword.PUBLIC);

        if (hasCdi) {
            cls.addAnnotation("javax.inject.Singleton")
            .addAnnotation(new SingleMemberAnnotationExpr(
                    new Name("javax.inject.Named"),
                    new StringLiteralExpr(process.getId())));
            FieldDeclaration handlersInjectFieldDeclaration = new FieldDeclaration()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName("javax.enterprise.inject.Instance"), NodeList.nodeList(new ClassOrInterfaceType(null, WorkItemHandler.class.getCanonicalName()))), "handlers"))
                    .addAnnotation("javax.inject.Inject");
            
            cls.addMember(handlersInjectFieldDeclaration);
        }

        String processInstanceFQCN = ProcessInstanceGenerator.qualifiedName(packageName, typeName);

        FieldDeclaration fieldDeclaration = new FieldDeclaration()
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, appCanonicalName), "app"));

        ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(appCanonicalName, "app")
                .setBody(new BlockStmt()
                                 // super(module.config().process())
                                 .addStatement(new MethodCallExpr(null, "super")
                                              .addArgument(
                                                      new MethodCallExpr(
                                                              new MethodCallExpr(new NameExpr("app"), "config"),
                                                              "process")))
                                 .addStatement(
                                         new AssignExpr(new FieldAccessExpr(new ThisExpr(), "app"), new NameExpr("app"), AssignExpr.Operator.ASSIGN)));
        ConstructorDeclaration emptyConstructorDeclaration = new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .setBody(new BlockStmt()
                                 .addStatement(
                                         new MethodCallExpr(null, "this").addArgument(new ObjectCreationExpr().setType(appCanonicalName))));

        
        MethodDeclaration createModelMethod = new MethodDeclaration()
                .addModifier(Keyword.PUBLIC)
                .setName("createModel")
                .setType(modelTypeName)
                .setBody(new BlockStmt()
                         .addStatement(new ReturnStmt(new ObjectCreationExpr(null, 
                                                                             new ClassOrInterfaceType(null, modelTypeName), 
                                                                             NodeList.nodeList()))));               
        
        ProcessMetaData processMetaData = legacyProcessGenerator.generate();
        
        MethodDeclaration methodDeclaration = createInstanceMethod(processInstanceFQCN);
        MethodDeclaration genericMethodDeclaration = createInstanceGenericMethod(processInstanceFQCN);
        cls.addExtendedType(abstractProcessType(modelTypeName))
                .addMember(fieldDeclaration)
                .addMember(emptyConstructorDeclaration)
                .addMember(constructorDeclaration)
                .addMember(methodDeclaration)
                .addMember(createModelMethod)
                .addMember(genericMethodDeclaration)
                .addMember(internalConfigure(processMetaData))
                .addMember(legacyProcess(processMetaData));
        
        if (hasCdi) {
            MethodDeclaration initMethod = new MethodDeclaration()
                    .addModifier(Keyword.PUBLIC)
                    .setName("init")
                    .setType(void.class)
                    .addAnnotation("javax.annotation.PostConstruct")
                    .setBody(new BlockStmt().addStatement(new MethodCallExpr(new ThisExpr(), "configure")));
            
            cls.addMember(initMethod);
        }
        
        
        if (!processMetaData.getSubProcesses().isEmpty()) {
            
            for (String subProcessId : processMetaData.getSubProcesses()) {
                FieldDeclaration subprocessFieldDeclaration = new FieldDeclaration();                    

                String fieldName = "process" + subProcessId;
                if (hasCdi) {
                    subprocessFieldDeclaration
                        .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(org.kie.kogito.process.Process.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, StringUtils.capitalize(subProcessId+"Model")))), fieldName))
                        .addAnnotation("javax.inject.Inject");
                } else {
                    MethodCallExpr initSubProcessField = new MethodCallExpr(new NameExpr("app"), "create" + StringUtils.capitalize(subProcessId) + "Process");
                    
                    subprocessFieldDeclaration
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(org.kie.kogito.process.Process.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, StringUtils.capitalize(subProcessId+"Model")))), fieldName));
                
                    constructorDeclaration.getBody().addStatement(new AssignExpr(new FieldAccessExpr(new ThisExpr(), fieldName), initSubProcessField, Operator.ASSIGN));
                    
                }
                
                cls.addMember(subprocessFieldDeclaration);
                                
            }
        }
        
        return cls;
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }

    public boolean isPublic() {
        return WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(process.getVisibility());
    }
    
    public String processId() {
        return process.getId();
    }
    
    public List<CompilationUnit> getAdditionalClasses() {
        return additionalClasses;
    }

    public ProcessGenerator withCdi(boolean dependencyInjection) {
        this.hasCdi = dependencyInjection;
        return this;
    }
}
