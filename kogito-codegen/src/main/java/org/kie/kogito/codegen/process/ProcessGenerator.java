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
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.lang.model.SourceVersion;

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
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.KieFunctions;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.impl.AbstractProcess;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

/**
 * Generates the Process&lt;T&gt; container
 * for a process, which encapsulates its "executable model".
 *
 * @see org.kie.kogito.process.Process
 */
public class ProcessGenerator {
    
    private static final String BUSINESS_KEY = "businessKey";
    private static final String CREATE_MODEL = "createModel";
    private static final String APPLICATION = "app";
    private static final String WPI = "wpi";
    private static final String FACTORY = "factory";

    private final String packageName;
    private final WorkflowProcess process;
    private final ProcessExecutableModelGenerator processExecutable;
    private final String typeName;
    private final String modelTypeName;
    private final String generatedFilePath;
    private final String completePath;
    private final String targetCanonicalName;
    private final String appCanonicalName;
    private String targetTypeName;
    private DependencyInjectionAnnotator annotator;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    private List<CompilationUnit> additionalClasses = new ArrayList<>();

    public ProcessGenerator(WorkflowProcess process,
                            ProcessExecutableModelGenerator processGenerator,
                            String typeName,
                            String modelTypeName,
                            String appCanonicalName) {

        this.appCanonicalName = appCanonicalName;

        this.packageName = process.getPackageName();
        this.process = process;
        this.processExecutable = processGenerator;
        this.typeName = typeName;
        this.modelTypeName = modelTypeName;        
        this.targetTypeName = typeName + "Process";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + generatedFilePath;

        if (!SourceVersion.isName(targetTypeName)) {
            throw new IllegalArgumentException("Process id '" + typeName + "' is not valid");   
        }

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
        compilationUnit.addImport(ObjectDataType.class);
        compilationUnit.addImport(RuleFlowProcessFactory.class);
        compilationUnit.addImport(KieFunctions.class);
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
                .addAnnotation(Override.class)
                .setBody(new BlockStmt()
                                 .addStatement(returnStmt));
        return methodDeclaration;
    }
    
    private MethodDeclaration createInstanceWithBusinessKeyMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new ObjectCreationExpr()
                        .setType(processInstanceFQCN)
                        .setArguments(NodeList.nodeList(
                                new ThisExpr(),
                                new NameExpr("value"),
                                new NameExpr(BUSINESS_KEY),
                                createProcessRuntime())));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(String.class.getCanonicalName(), BUSINESS_KEY)
                .addParameter(modelTypeName, "value")
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt().addStatement(returnStmt));
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
                .setBody(new BlockStmt().addStatement(returnStmt));
        return methodDeclaration;
    }
    
    private MethodDeclaration createInstanceGenericWithBusinessKeyMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new MethodCallExpr(new ThisExpr(), "createInstance")
                .addArgument(new NameExpr(BUSINESS_KEY))
                .addArgument(new CastExpr(new ClassOrInterfaceType(null, modelTypeName), new NameExpr("value"))));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(String.class.getCanonicalName(), BUSINESS_KEY)
                .addParameter(Model.class.getCanonicalName(), "value")
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt().addStatement(returnStmt));
        return methodDeclaration;
    }

    private MethodDeclaration createInstanceGenericWithWorkflowInstanceMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new ObjectCreationExpr()
                        .setType(processInstanceFQCN)
                        .setArguments(NodeList.nodeList(
                                new ThisExpr(),
                                new MethodCallExpr(new ThisExpr(), CREATE_MODEL),
                                createProcessRuntime(),
                                new NameExpr(WPI))));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(WorkflowProcessInstance.class.getCanonicalName(), WPI)
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt().addStatement(returnStmt));
        return methodDeclaration;
    }

    private MethodDeclaration createReadOnlyInstanceGenericWithWorkflowInstanceMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new ObjectCreationExpr()
                        .setType(processInstanceFQCN)
                        .setArguments(NodeList.nodeList(
                                new ThisExpr(),
                                new MethodCallExpr(new ThisExpr(), CREATE_MODEL),
                                new NameExpr(WPI))));

        methodDeclaration.setName("createReadOnlyInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(WorkflowProcessInstance.class.getCanonicalName(), WPI)
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt().addStatement(returnStmt));
        return methodDeclaration;
    }

    private MethodDeclaration process(ProcessMetaData processMetaData) {
        return processMetaData.getGeneratedClassModel()
                .findFirst(MethodDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a method declaration!"))
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(Process.class.getCanonicalName())
                .setName("process");
    }

    private MethodCallExpr createProcessRuntime() {
        return new MethodCallExpr(
                new ThisExpr(),
                "createProcessRuntime");
    }

    private Optional<MethodDeclaration> internalConfigure(ProcessMetaData processMetaData) {
        if (!processMetaData.getGeneratedListeners().isEmpty()) {
            BlockStmt body = new BlockStmt();
            MethodDeclaration internalConfigure = new MethodDeclaration()
                    .setModifiers(Modifier.Keyword.PUBLIC)
                    .setType(targetTypeName)
                    .setName("configure")
                    .setBody(body);

            // always call super.configure
            body.addStatement(new MethodCallExpr(new SuperExpr(), "configure"));
            processMetaData.getGeneratedListeners().forEach(listener -> {
                ClassOrInterfaceDeclaration clazz = listener.findFirst(ClassOrInterfaceDeclaration.class).get();
                MethodCallExpr eventSupport = new MethodCallExpr(new NameExpr("services"), "getEventSupport");
                MethodCallExpr registerListener = new MethodCallExpr(eventSupport, "addEventListener")
                    .addArgument(
                        new ObjectCreationExpr(
                            null,
                            new ClassOrInterfaceType(
                                null,
                                listener.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("") + "." +clazz.getName()),
                            NodeList.nodeList()));

                body.addStatement(registerListener);

                additionalClasses.add(listener);
            });
            body.addStatement(new ReturnStmt(new ThisExpr()));
            return Optional.of(internalConfigure);
        }
        return Optional.empty();
    }
    
    private Optional<MethodDeclaration> internalRegisterListeners(ProcessMetaData processMetaData) {
        if (!processMetaData.getSubProcesses().isEmpty()) {
            BlockStmt body = new BlockStmt();
            MethodDeclaration internalRegisterListeners = new MethodDeclaration()
                    .setModifiers(Modifier.Keyword.PROTECTED)
                    .setType(void.class)
                    .setName("registerListeners")
                    .setBody(body);   
            
            for (Entry<String, String> subProcess : processMetaData.getSubProcesses().entrySet()) {
                MethodCallExpr signalManager = new MethodCallExpr(new NameExpr("services"), "getSignalManager");
                MethodCallExpr registerListener = new MethodCallExpr(signalManager, "addEventListener").addArgument(new StringLiteralExpr(subProcess.getValue())).addArgument(new NameExpr("completionEventListener"));
                body.addStatement(registerListener);
            }
            return Optional.of(internalRegisterListeners);
        }
        return Optional.empty();
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
        ProcessMetaData processMetaData = processExecutable.generate();
        ConstructorDeclaration constructor = getConstructorDeclaration().addParameter(appCanonicalName, APPLICATION);

        MethodCallExpr handlersCollection = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList");
        MethodCallExpr superMethod = new MethodCallExpr(null, "super")
                .addArgument(new NameExpr(APPLICATION))
                .addArgument(handlersCollection);

        if (addonsConfig.usePersistence()) {
            constructor.addParameter(ProcessInstancesFactory.class.getCanonicalName(), FACTORY);
            superMethod.addArgument(new NameExpr(FACTORY));
        }
        
        constructor.setBody(new BlockStmt()
                                    .addStatement(superMethod)
                                    .addStatement(new MethodCallExpr("activate")));
        
        if (useInjection()) {
            annotator.withNamedApplicationComponent(cls, process.getId());
            annotator.withEagerStartup(cls);
            annotator.withInjection(constructor);
        }

        Map<String, CompilationUnit> handlers = processMetaData.getGeneratedHandlers();
        if (!handlers.isEmpty()) {
            MethodCallExpr initMethodCall = new MethodCallExpr(null, "this").addArgument(new NameExpr(APPLICATION));
            ConstructorDeclaration decl = getConstructorDeclaration()
                    .addParameter(appCanonicalName, APPLICATION)
                    .setBody(new BlockStmt().addStatement(initMethodCall));
            if (addonsConfig.usePersistence()) {
                initMethodCall.addArgument(new NameExpr(FACTORY));
                decl.addParameter(ProcessInstancesFactory.class.getCanonicalName(), FACTORY);
            }
            cls.addMember(decl);

            for (Entry<String, CompilationUnit> handler : handlers.entrySet()) {
                String varName = handler.getKey().substring(handler.getKey().lastIndexOf('.') + 1);
                varName = Character.toLowerCase(varName.charAt(0)) + varName.substring(1);
                ClassOrInterfaceDeclaration handlerClazz =
                        handler
                            .getValue()
                            .findFirst(ClassOrInterfaceDeclaration.class)
                            .orElseThrow(
                                         () -> new NoSuchElementException(
                                             "Compilation unit doesn't contain a method declaration!"));
                String clazzName =
                        handler
                            .getValue()
                            .getPackageDeclaration()
                            .map(pd -> pd.getName().toString() + '.' + handlerClazz.getName())
                            .orElse(handlerClazz.getName().asString());

                ClassOrInterfaceType clazzNameType = parseClassOrInterfaceType(clazzName);
                Parameter parameter = new Parameter(clazzNameType, varName);
                if (useInjection()) {
                    annotator.withApplicationComponent(handlerClazz);
                    annotator
                        .withInjection(
                                       handlerClazz
                                           .getConstructors()
                                           .stream()
                                           .filter(c -> !c.getParameters().isEmpty())
                                           .findFirst()
                                           .orElseThrow(
                                                        () -> new IllegalStateException(
                                                            "Cannot find a non empty constructor to annotate in handler class " +
                                                                                        handlerClazz)),true);
                }
             
                initMethodCall
                    .addArgument(
                                 new ObjectCreationExpr(
                                     null,
                                     clazzNameType,
                                     NodeList.nodeList()));

                constructor.addParameter(parameter);
                handlersCollection.addArgument(new NameExpr(varName));
                additionalClasses.add(handler.getValue());
            }
        }
        String processInstanceFQCN = ProcessInstanceGenerator.qualifiedName(packageName, typeName);
        cls.addExtendedType(abstractProcessType(modelTypeName))
                .addMember(constructor)
                .addMember(getConstructorDeclaration())
                .addMember(createInstanceMethod(processInstanceFQCN))
                .addMember(createInstanceWithBusinessKeyMethod(processInstanceFQCN))
                .addMember(new MethodDeclaration()
                                   .addModifier(Keyword.PUBLIC)
                                   .setName(CREATE_MODEL)
                                   .setType(modelTypeName)
                                   .addAnnotation(Override.class)
                                   .setBody(new BlockStmt()
                                                    .addStatement(new ReturnStmt(new ObjectCreationExpr(null,
                                                                                                        new ClassOrInterfaceType(null, modelTypeName),
                                                                                                        NodeList.nodeList())))))
                .addMember(createInstanceGenericMethod(processInstanceFQCN))
                .addMember(createInstanceGenericWithBusinessKeyMethod(processInstanceFQCN))
                .addMember(createInstanceGenericWithWorkflowInstanceMethod(processInstanceFQCN))
                .addMember(createReadOnlyInstanceGenericWithWorkflowInstanceMethod(processInstanceFQCN))
                .addMember(process(processMetaData));


        internalConfigure(processMetaData).ifPresent(cls::addMember);
        internalRegisterListeners(processMetaData).ifPresent(cls::addMember);

        if (!processMetaData.getSubProcesses().isEmpty()) {
            
            for (Entry<String, String> subProcess : processMetaData.getSubProcesses().entrySet()) {
                FieldDeclaration subprocessFieldDeclaration = new FieldDeclaration();                    

                String fieldName = "process" + subProcess.getKey();
                ClassOrInterfaceType modelType = new ClassOrInterfaceType(null, new SimpleName(org.kie.kogito.process.Process.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, StringUtils.ucFirst(subProcess.getKey() + "Model"))));
                if (useInjection()) {
                    subprocessFieldDeclaration
                        .addVariable(new VariableDeclarator(modelType, fieldName));
                    annotator.withInjection(subprocessFieldDeclaration);
                } else {
                    // app.processes().processById()
                    MethodCallExpr initSubProcessField = new MethodCallExpr(
                            new MethodCallExpr(new NameExpr(APPLICATION), "processes"),
                            "processById").addArgument(new StringLiteralExpr(subProcess.getKey()));
                    
                    subprocessFieldDeclaration.addVariable(new VariableDeclarator(modelType, fieldName));
                    constructor.getBody().addStatement(new AssignExpr(new FieldAccessExpr(new ThisExpr(), fieldName), new CastExpr(modelType, initSubProcessField), Operator.ASSIGN));
                    
                }
                
                cls.addMember(subprocessFieldDeclaration);
            }
        }

        if (!processMetaData.getTriggers().isEmpty()) {
            
            for (TriggerMetaData trigger : processMetaData.getTriggers()) {
                // add message produces as field
                if (trigger.getType().equals(TriggerMetaData.TriggerType.ProduceMessage)) {
                    String producerFieldType = packageName + "." + typeName + "MessageProducer_" + trigger.getOwnerId();
                    String producerFielName = "producer_" + trigger.getOwnerId();
                    
                    FieldDeclaration producerFieldieldDeclaration = new FieldDeclaration()
                            .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, producerFieldType), producerFielName));
                    cls.addMember(producerFieldieldDeclaration);
                    
                    if (useInjection()) {
                        annotator.withInjection(producerFieldieldDeclaration);
                    } else {
                        
                        AssignExpr assignExpr = new AssignExpr(
                                                               new FieldAccessExpr(new ThisExpr(), producerFielName),
                                                               new ObjectCreationExpr().setType(producerFieldType),
                                                               AssignExpr.Operator.ASSIGN);
                        
                        
                        cls.getConstructors().forEach(c -> c.getBody().addStatement(assignExpr));
                        
                    }
                }
            }
        }
        cls.getMembers().sort(new BodyDeclarationComparator());
        return cls;
    }

    private ConstructorDeclaration getConstructorDeclaration() {
        return new ConstructorDeclaration()
            .setName(targetTypeName)
            .addModifier(Modifier.Keyword.PUBLIC);
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

    public ProcessGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }
    
    public ProcessGenerator withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
}
