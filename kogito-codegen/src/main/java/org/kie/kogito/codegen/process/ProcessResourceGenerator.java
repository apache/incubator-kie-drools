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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.UserTaskModelMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.CodegenUtils;
import org.kie.kogito.codegen.GeneratorConfig;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;

/**
 * ProcessResourceGenerator
 */
public class ProcessResourceGenerator {

    private static final String REST_TEMPLATE_NAME = "RestResource";
    private static final String REACTIVE_REST_TEMPLATE_NAME = "ReactiveRestResource";
    private static final String REST_USER_TASK_TEMPLATE_NAME = "RestResourceUserTask";
    private static final String REST_SIGNAL_TEMPLATE_NAME = "RestResourceSignal";

    private final String relativePath;

    private final KogitoBuildContext context;
    private final String resourceClazzName;
    private final String processClazzName;
    private final String processName;
    private final String appCanonicalName;
    private WorkflowProcess process;
    private String processId;
    private String dataClazzName;
    private String modelfqcn;

    private boolean startable;
    private boolean dynamic;
    private List<UserTaskModelMetaData> userTasks;
    private Map<String, String> signals;

    public ProcessResourceGenerator(
            KogitoBuildContext context,
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName) {
        this.context = context;
        this.process = process;
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        this.appCanonicalName = appCanonicalName;
        String classPrefix = StringUtils.ucFirst(processName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = process.getPackageName().replace(".", "/") + "/" + resourceClazzName + ".java";
        this.modelfqcn = modelfqcn + "Output";
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
    }

    public ProcessResourceGenerator withUserTasks(List<UserTaskModelMetaData> userTasks) {
        this.userTasks = userTasks;
        return this;
    }

    public ProcessResourceGenerator withSignals(Map<String, String> signals) {
        this.signals = signals;
        return this;
    }

    public ProcessResourceGenerator withTriggers(boolean startable, boolean dynamic) {
        this.startable = startable;
        this.dynamic = dynamic;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    protected String getRestTemplateName() {
        boolean isReactiveGenerator = "reactive".equals(context.getApplicationProperty(GeneratorConfig.KOGITO_REST_RESOURCE_TYPE_PROP)
                .orElse(""));
        boolean isQuarkus = context.name().equals(QuarkusKogitoBuildContext.CONTEXT_NAME);

        return isQuarkus && isReactiveGenerator ? REACTIVE_REST_TEMPLATE_NAME : REST_TEMPLATE_NAME;
    }

    protected String getSignalResponseType(String outputType) {
        boolean isSpring = context.name().equals(SpringBootKogitoBuildContext.CONTEXT_NAME);

        return isSpring ? "ResponseEntity<" + outputType + ">" : outputType;
    }

    public String generate() {
        TemplatedGenerator.Builder templateBuilder = TemplatedGenerator.builder()
                .withFallbackContext(QuarkusKogitoBuildContext.CONTEXT_NAME);
        CompilationUnit clazz = templateBuilder.build(context, getRestTemplateName())
                .compilationUnitOrThrow();
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);

        ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        template.setName(resourceClazzName);
        AtomicInteger index = new AtomicInteger(0);
        //Generate signals endpoints
        Optional.ofNullable(signals)
                .ifPresent(signalsMap -> {
                    //using template class to the endpoints generation
                    CompilationUnit signalClazz = templateBuilder.build(context, REST_SIGNAL_TEMPLATE_NAME)
                            .compilationUnitOrThrow();

                    ClassOrInterfaceDeclaration signalTemplate = signalClazz
                            .findFirst(ClassOrInterfaceDeclaration.class)
                            .orElseThrow(() -> new NoSuchElementException("SignalResourceTemplate class not found!"));

                    signalsMap.entrySet()
                            .stream()
                            .filter(e -> Objects.nonNull(e.getKey()))
                            .forEach(entry -> {
                                String methodName = "signal_" + index.getAndIncrement();
                                String outputType = modelfqcn;
                                String signalName = entry.getKey();
                                String signalType = entry.getValue();

                                signalTemplate.findAll(MethodDeclaration.class)
                                        .forEach(md -> {
                                            MethodDeclaration cloned = md.clone();
                                            BlockStmt body = cloned.getBody().get();
                                            if (signalType == null) {
                                                body.findAll(NameExpr.class, nameExpr -> "data".equals(nameExpr.getNameAsString())).forEach(name -> name.replace(new NullLiteralExpr()));
                                            }
                                            template.addMethod(methodName, Keyword.PUBLIC)
                                                    .setType(getSignalResponseType(outputType))
                                                    // Remove data parameter ( payload ) if signalType is null 
                                                    .setParameters(signalType == null ? NodeList.nodeList(cloned.getParameter(0)) : cloned.getParameters())
                                                    .setBody(body)
                                                    .setAnnotations(cloned.getAnnotations());
                                        });

                                if (signalType != null) {
                                    template.findAll(ClassOrInterfaceType.class).forEach(name -> {
                                        String identifier = name.getNameAsString();
                                        name.setName(identifier.replace("$signalType$", signalType));
                                    });
                                }

                                template.findAll(StringLiteralExpr.class).forEach(vv -> {
                                    String s = vv.getValue();
                                    String interpolated = s.replace("$signalName$", signalName);
                                    interpolated = interpolated.replace("$signalPath$", sanitizeName(signalName));
                                    vv.setString(interpolated);
                                });
                            });
                });

        // security must be applied before user tasks are added to make sure that user task
        // endpoints are not security annotated as they should restrict access based on user assignments
        securityAnnotated(template);

        if (userTasks != null) {

            CompilationUnit userTaskClazz = templateBuilder.build(context, REST_USER_TASK_TEMPLATE_NAME)
                    .compilationUnitOrThrow();

            ClassOrInterfaceDeclaration userTaskTemplate = userTaskClazz
                    .findFirst(ClassOrInterfaceDeclaration.class)
                    .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
            for (UserTaskModelMetaData userTask : userTasks) {
                String methodSuffix = sanitizeName(userTask.getName()) + "_" + index.getAndIncrement();
                userTaskTemplate.findAll(MethodDeclaration.class).forEach(md -> {

                    MethodDeclaration cloned = md.clone();
                    template.addMethod(cloned.getName() + "_" + methodSuffix, Keyword.PUBLIC)
                            .setType(cloned.getType())
                            .setParameters(cloned.getParameters())
                            .setBody(cloned.getBody().get())
                            .setAnnotations(cloned.getAnnotations());
                });

                template.findAll(StringLiteralExpr.class).forEach(s -> interpolateUserTaskStrings(s, userTask));
                template.findAll(ClassOrInterfaceType.class).forEach(c -> interpolateUserTaskTypes(c, userTask.getInputModelClassSimpleName(), userTask.getOutputModelClassSimpleName()));
                template.findAll(NameExpr.class).forEach(c -> interpolateUserTaskNameExp(c, userTask));
                if (!userTask.isAdHoc()) {
                    template.findAll(MethodDeclaration.class)
                            .stream()
                            .filter(md -> md.getNameAsString().equals("signal_" + methodSuffix))
                            .collect(Collectors.toList()).forEach(template::remove);
                }
            }
        }

        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        Map<String, String> typeInterpolations = new HashMap<>();
        typeInterpolations.put("$Clazz$", resourceClazzName);
        typeInterpolations.put("$Type$", dataClazzName);
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, typeInterpolations));
        template.findAll(MethodDeclaration.class).forEach(this::interpolateMethods);

        if (context.hasDI()) {
            template.findAll(FieldDeclaration.class,
                    CodegenUtils::isProcessField).forEach(fd -> context.getDependencyInjectionAnnotator().withNamedInjection(fd, processId));

            template.findAll(FieldDeclaration.class,
                    CodegenUtils::isApplicationField).forEach(fd -> context.getDependencyInjectionAnnotator().withInjection(fd));
        } else {
            template.findAll(FieldDeclaration.class,
                    CodegenUtils::isProcessField).forEach(this::initializeProcessField);

            template.findAll(FieldDeclaration.class,
                    CodegenUtils::isApplicationField).forEach(this::initializeApplicationField);
        }

        // if triggers are not empty remove createResource method as there is another trigger to start process instances
        if ((!startable && !dynamic) || !isPublic()) {
            Optional<MethodDeclaration> createResourceMethod = template.findFirst(MethodDeclaration.class).filter(md -> md.getNameAsString().equals("createResource_" + processName));
            createResourceMethod.ifPresent(template::remove);
        }

        if (context.hasDI()) {
            context.getDependencyInjectionAnnotator().withApplicationComponent(template);
        }

        enableValidation(template);

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    private void securityAnnotated(ClassOrInterfaceDeclaration template) {
        if (context.hasDI() && process.getMetaData().containsKey("securityRoles")) {
            String[] roles = ((String) process.getMetaData().get("securityRoles")).split(",");
            template.findAll(MethodDeclaration.class).stream().filter(context.getDependencyInjectionAnnotator()::isRestAnnotated)
                    .forEach(md -> context.getDependencyInjectionAnnotator().withSecurityRoles(md, roles));
        }
    }

    private void enableValidation(ClassOrInterfaceDeclaration template) {
        Optional.ofNullable(context)
                .filter(KogitoBuildContext::isValidationSupported)
                .ifPresent(c -> template.findAll(Parameter.class)
                        .stream()
                        .filter(param -> param.getTypeAsString().equals(dataClazzName + "Input"))
                        .forEach(this::insertValidationAnnotations));
    }

    private void insertValidationAnnotations(Parameter param) {
        param.addAnnotation("javax.validation.Valid");
        param.addAnnotation("javax.validation.constraints.NotNull");
    }

    private void initializeProcessField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(processClazzName));
    }

    private void initializeApplicationField(FieldDeclaration fd) {
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
        String interpolated = s.replace("$taskName$", sanitizeName(userTask.getName()));
        interpolated = interpolated.replace("$taskNodeName$", userTask.getNodeName());
        vv.setString(interpolated);
    }

    private void interpolateUserTaskNameExp(NameExpr name, UserTaskModelMetaData userTask) {
        String identifier = name.getNameAsString();

        name.setName(identifier.replace("$TaskInput$", userTask.getInputModelClassSimpleName()));

        identifier = name.getNameAsString();
        name.setName(identifier.replace("$TaskOutput$", userTask.getOutputModelClassSimpleName()));
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

    private String sanitizeName(String name) {
        return name.replaceAll("\\s", "_");
    }

    public String generatedFilePath() {
        return relativePath;
    }

    protected boolean isPublic() {
        return WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(process.getVisibility());
    }
}