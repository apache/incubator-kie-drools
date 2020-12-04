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

package org.kie.kogito.codegen.decision;

import java.net.URLEncoder;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.openapi.model.DMNModelIOSets;
import org.kie.dmn.openapi.model.DMNOASResult;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.CodegenUtils;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseStatement;

public class DecisionRestResourceGenerator {

    public static final String CDI_TEMPLATE = "/class-templates/DecisionRestResourceTemplate.java";
    public static final String SPRING_TEMPLATE = "/class-templates/spring/SpringDecisionRestResourceTemplate.java";
    private final DMNModel dmnModel;
    private final String decisionName;
    private final String nameURL;
    private final String packageName;
    private final String decisionId;
    private final String relativePath;
    private final String resourceClazzName;
    private final String appCanonicalName;
    private DependencyInjectionAnnotator annotator;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;
    private boolean isStronglyTyped = false;
    private DMNOASResult withOASResult;
    private boolean mpAnnPresent;
    private boolean swaggerAnnPresent;
    private final TemplatedGenerator generator;

    private static final Supplier<RuntimeException> TEMPLATE_WAS_MODIFIED = () -> new RuntimeException("Template was modified!");

    public DecisionRestResourceGenerator(DMNModel model, String appCanonicalName) {
        this.dmnModel = model;
        this.packageName = CodegenStringUtil.escapeIdentifier(model.getNamespace());
        this.decisionId = model.getDefinitions().getId();
        this.decisionName = CodegenStringUtil.escapeIdentifier(model.getName());
        this.nameURL = URLEncoder.encode(model.getName()).replace("+", " ");
        this.appCanonicalName = appCanonicalName;
        String classPrefix = StringUtils.ucFirst(decisionName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        generator = new TemplatedGenerator(packageName, "DecisionRestResource",CDI_TEMPLATE, SPRING_TEMPLATE, CDI_TEMPLATE);
    }

    public String generate() {
        CompilationUnit clazz = generator.compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(resourceClazzName, generator.templatePath(), "Cannot " +
                        "generate Decision REST Resource"));

        ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        template.setName(resourceClazzName);

        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        template.findAll(MethodDeclaration.class).forEach(this::interpolateMethods);

        interpolateInputType(template);
        interpolateInputData(template);
        interpolateExtractContextMethod(template);
        modifyDmnMethodForStronglyTyped(template);
        chooseMethodForStronglyTyped(template);

        if (useInjection()) {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(fd -> annotator.withInjection(fd));
        } else {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(this::initializeApplicationField);
        }

        MethodDeclaration dmnMethod = template.findAll(MethodDeclaration.class, x -> x.getName().toString().equals("dmn")).get(0);
        processOASAnn(dmnMethod, null);
        template.addMember(cloneForDMNResult(dmnMethod, "dmn_dmnresult", "dmnresult", "$dmnMethodUrl$"));
        for (DecisionService ds : dmnModel.getDefinitions().getDecisionService()) {
            if (ds.getAdditionalAttributes().keySet().stream().anyMatch(qn -> qn.getLocalPart().equals("dynamicDecisionService"))) {
                continue;
            }

            MethodDeclaration clonedMethod = dmnMethod.clone();
            processOASAnn(clonedMethod, ds);
            String name = CodegenStringUtil.escapeIdentifier("decisionService_" + ds.getName());
            clonedMethod.setName(name);
            MethodCallExpr evaluateCall = clonedMethod.findFirst(MethodCallExpr.class, x -> x.getNameAsString().equals("evaluateAll")).orElseThrow(TEMPLATE_WAS_MODIFIED);
            evaluateCall.setName(new SimpleName("evaluateDecisionService"));
            evaluateCall.addArgument(new StringLiteralExpr(ds.getName()));
            MethodCallExpr ctxCall = clonedMethod.findFirst(MethodCallExpr.class, x -> x.getNameAsString().equals("ctx")).orElseThrow(TEMPLATE_WAS_MODIFIED);
            ctxCall.addArgument(new StringLiteralExpr(ds.getName()));

            //insert request path
            final String path = ds.getName();
            interpolateRequestPath(path, "$dmnMethodUrl$", clonedMethod);

            ReturnStmt returnStmt = clonedMethod.findFirst(ReturnStmt.class).orElseThrow(TEMPLATE_WAS_MODIFIED);
            if (ds.getOutputDecision().size() == 1) {
                MethodCallExpr rewrittenReturnExpr = returnStmt.findFirst(MethodCallExpr.class,
                                                                          mce -> mce.getNameAsString().equals("extractContextIfSucceded") || mce.getNameAsString().equals("extractStronglyTypedContextIfSucceded"))
                                                               .orElseThrow(TEMPLATE_WAS_MODIFIED);
                rewrittenReturnExpr.setName("extractSingletonDSIfSucceded");
            }

            if (addonsConfig.useMonitoring()) {
                addMonitoringToMethod(clonedMethod, ds.getName());
            }

            template.addMember(clonedMethod);
            template.addMember(cloneForDMNResult(clonedMethod, name + "_dmnresult", ds.getName() + "/dmnresult", path));
        }

        //set the root path for the dmnMethod itself
        interpolateRequestPath("", "$dmnMethodUrl$", dmnMethod);

        interpolateOutputType(template);

        if (addonsConfig.useMonitoring()) {
            addMonitoringImports(clazz);
            ClassOrInterfaceDeclaration exceptionClazz = clazz.findFirst(ClassOrInterfaceDeclaration.class, x -> "DMNEvaluationErrorExceptionMapper".equals(x.getNameAsString()))
                    .orElseThrow(() -> new NoSuchElementException("Could not find DMNEvaluationErrorExceptionMapper, template has changed."));
            addExceptionMetricsLogging(exceptionClazz, nameURL);
            addMonitoringToMethod(dmnMethod, nameURL);
        }

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    private void processOASAnn(MethodDeclaration dmnMethod, DecisionService ds) {
        String inputRef = null;
        String outputRef = null;
        if (withOASResult!= null) {
            DMNModelIOSets ioSets = withOASResult.lookupIOSetsByModel(dmnModel);
            DMNType identifyInputSet = ds != null ? ioSets.lookupDSIOSetsByName(ds.getName()).getDSInputSet() : ioSets.getInputSet();
            DMNType identifyOutputSet = ds != null ? ioSets.lookupDSIOSetsByName(ds.getName()).getDSOutputSet() : ioSets.getOutputSet();
            inputRef = withOASResult.getNamingPolicy().getRef(identifyInputSet);
            outputRef = withOASResult.getNamingPolicy().getRef(identifyOutputSet);
        }
        final String DMN_DEFINITIONS_JSON = "dmnDefinitions.json";
        // MP / Quarkus
        processAnnForRef(dmnMethod,
                         "org.eclipse.microprofile.openapi.annotations.parameters.RequestBody",
                         "org.eclipse.microprofile.openapi.annotations.media.Schema",
                         DMN_DEFINITIONS_JSON + inputRef,
                         !mpAnnPresent);
        processAnnForRef(dmnMethod,
                         "org.eclipse.microprofile.openapi.annotations.responses.APIResponse",
                         "org.eclipse.microprofile.openapi.annotations.media.Schema",
                         DMN_DEFINITIONS_JSON + outputRef,
                         !mpAnnPresent);
        // io.swagger / SB
        processAnnForRef(dmnMethod,
                         "io.swagger.v3.oas.annotations.parameters.RequestBody",
                         "io.swagger.v3.oas.annotations.media.Schema",
                         DMN_DEFINITIONS_JSON + inputRef,
                         !swaggerAnnPresent);
        processAnnForRef(dmnMethod,
                         "io.swagger.v3.oas.annotations.responses.ApiResponse",
                         "io.swagger.v3.oas.annotations.media.Schema",
                         DMN_DEFINITIONS_JSON + outputRef,
                         !swaggerAnnPresent);
    }

    private void processAnnForRef(MethodDeclaration dmnMethod, String parentName, String innerName, String ref, boolean removeIt) {
        List<NormalAnnotationExpr> findAll = dmnMethod.findAll(NormalAnnotationExpr.class, x -> x.getName().toString().equals(parentName));
        if (findAll.isEmpty()) {
            if (removeIt) {
                return; // nothing to do
            } else {
                throw new IllegalStateException("Impossible to find annotation " + parentName + " on method " + dmnMethod.toString());
            }
        }
        NormalAnnotationExpr parentExpr = findAll.get(0);
        if (removeIt || ref == null) {
            parentExpr.remove();
        } else {
            NormalAnnotationExpr schemaAnn = parentExpr.findAll(NormalAnnotationExpr.class, x -> x.getName().toString().equals(innerName))
                                                    .get(0);
            schemaAnn.getPairs().removeIf(x -> true);
            schemaAnn.addPair("ref", new StringLiteralExpr(ref));
        }
    }

    private void removeAnnFromMethod(MethodDeclaration dmnMethod, String fqn) {
        for (NormalAnnotationExpr ann : dmnMethod.findAll(NormalAnnotationExpr.class, x -> x.getName().toString().equals(fqn))) {
            dmnMethod.remove(ann);
        }
    }

    private void chooseMethodForStronglyTyped(ClassOrInterfaceDeclaration template) {
        if (isStronglyTyped) {
            MethodDeclaration extractContextIfSucceded = template.findAll(MethodDeclaration.class, x -> x.getName().toString().equals("extractContextIfSucceded")).get(0);
            extractContextIfSucceded.remove();
        } else {
            MethodDeclaration extractContextIfSucceded = template.findAll(MethodDeclaration.class, x -> x.getName().toString().equals("extractStronglyTypedContextIfSucceded")).get(0);
            extractContextIfSucceded.remove();
        }
    }

    private void modifyDmnMethodForStronglyTyped(ClassOrInterfaceDeclaration template) {
        MethodDeclaration dmnMethod = template.findAll(MethodDeclaration.class, x -> x.getName().toString().equals("dmn")).get(0);
        if (!isStronglyTyped) {
            List<ExpressionStmt> convertStatement = dmnMethod.findAll(ExpressionStmt.class, stmt -> stmt.findFirst(MethodCallExpr.class, mce -> mce.getNameAsString().equals("convertToOutputSet")).isPresent());
            convertStatement.get(0).remove();
        }
    }

    private MethodDeclaration cloneForDMNResult(MethodDeclaration dmnMethod, String name, String pathName,
                                                String placeHolder) {
        MethodDeclaration clonedDmnMethod = dmnMethod.clone();
        // a DMNResult-returning method doesn't need the OAS annotations for the $ref of return type.
        removeAnnFromMethod(clonedDmnMethod, "org.eclipse.microprofile.openapi.annotations.responses.APIResponse");
        removeAnnFromMethod(clonedDmnMethod, "io.swagger.v3.oas.annotations.responses.ApiResponse");
        clonedDmnMethod.setName(name);

        interpolateRequestPath(pathName, placeHolder, clonedDmnMethod);

        ReturnStmt returnStmt = clonedDmnMethod.findFirst(ReturnStmt.class).orElseThrow(TEMPLATE_WAS_MODIFIED);
        returnStmt.setExpression(new NameExpr("result"));
        return clonedDmnMethod;
    }

    private void interpolateRequestPath(String pathName, String placeHolder, MethodDeclaration clonedDmnMethod) {
        clonedDmnMethod.getAnnotations().stream()
                .flatMap(a -> a.findAll(StringLiteralExpr.class).stream())
                .forEach(vv -> {
                    String s = vv.getValue();
                    String interpolated = s.replace(placeHolder, pathName);
                    vv.setString(interpolated);
                });
    }

    private void interpolateInputType(ClassOrInterfaceDeclaration template) {
        String inputType = isStronglyTyped ? "InputSet" : "java.util.Map<String, Object>";
        template.findAll(ClassOrInterfaceType.class, t -> t.asString().equals("$inputType$"))
                .forEach(type -> type.setName(inputType));
    }

    private void interpolateOutputType(ClassOrInterfaceDeclaration template) {
        String outputType = isStronglyTyped ? "OutputSet" : "Object";

        List<ClassOrInterfaceType> outputTypeOccurrences = template.findAll(ClassOrInterfaceType.class, t -> t.asString().equals("$outputType$"));
        
        // first, methods which return DMNResult shall just have DMNResult as the return type in signature (useful for GraalVM NI introspection)
        List<ClassOrInterfaceType> dmnResultOuputTypes = outputTypeOccurrences
              .stream()
              .filter(t -> t.getParentNode().isPresent() && t.getParentNode().get() instanceof MethodDeclaration)
              .filter(t -> {
                  MethodDeclaration parent = (MethodDeclaration) t.getParentNode().get();
                  return parent.getNameAsString().endsWith("dmnresult");
              })
              .collect(Collectors.toList());
        dmnResultOuputTypes.forEach(type -> type.setName("org.kie.kogito.dmn.rest.DMNResult"));
        outputTypeOccurrences.removeAll(dmnResultOuputTypes);

        // then, *remaining* methods which belong to Decision Service(s) shall simply be returning Object, since strongly output typing is not supported for DS use case yet.
        List<ClassOrInterfaceType> objectReturnTypes = outputTypeOccurrences
                .stream()
                .filter(t -> t.getParentNode().isPresent() && t.getParentNode().get() instanceof MethodDeclaration)
                .filter(t -> {
                    MethodDeclaration parent = (MethodDeclaration)t.getParentNode().get();
                    return parent.getNameAsString().startsWith("decisionService_");
                })
                .collect(Collectors.toList());

        objectReturnTypes.forEach(type -> type.setName("Object"));

        outputTypeOccurrences.removeAll(objectReturnTypes);
        outputTypeOccurrences.forEach(type -> type.setName(outputType));
    }

    private void interpolateInputData(ClassOrInterfaceDeclaration template) {
        String inputData = "variables"; // use "outputSet" if stronglyTyped when drools 7.44 is available
        template.findAll(NameExpr.class, expr -> expr.getNameAsString().equals("$inputData$"))
                .forEach(expr -> expr.setName(inputData));
    }

    private void interpolateExtractContextMethod(ClassOrInterfaceDeclaration template) {
        String extractContextMethod = isStronglyTyped ? "extractStronglyTypedContextIfSucceded" : "extractContextIfSucceded";
        template.findAll(MethodCallExpr.class, expr -> expr.getNameAsString().equals("$extractContextMethod$"))
                .forEach(expr -> expr.setName(extractContextMethod));
    }

    public String getNameURL() {
        return nameURL;
    }

    public DMNModel getDmnModel() {
        return this.dmnModel;
    }

    public DecisionRestResourceGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        this.generator.withDependencyInjection(annotator);
        return this;
    }

    public DecisionRestResourceGenerator withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    private void addExceptionMetricsLogging(ClassOrInterfaceDeclaration template, String nameURL) {
        MethodDeclaration method = template.findFirst(MethodDeclaration.class, x -> "toResponse".equals(x.getNameAsString()))
                .orElseThrow(() -> new NoSuchElementException("Method toResponse not found, template has changed."));

        BlockStmt body = method.getBody().orElseThrow(() -> new NoSuchElementException("This method should be invoked only with concrete classes and not with abstract methods or interfaces."));
        ReturnStmt returnStmt = body.findFirst(ReturnStmt.class).orElseThrow(() -> new NoSuchElementException("Check for null dmn result not found, can't add monitoring to endpoint."));
        NodeList<Statement> statements = body.getStatements();
        String methodArgumentName = method.getParameters().get(0).getNameAsString();
        statements.addBefore(parseStatement(String.format("SystemMetricsCollector.registerException(\"%s\", %s.getStackTrace()[0].toString());", nameURL, methodArgumentName)), returnStmt);
    }

    private void addMonitoringImports(CompilationUnit cu) {
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.core.system.metrics.SystemMetricsCollector"), false, false));
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.core.system.metrics.DMNResultMetricsBuilder"), false, false));
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.core.system.metrics.SystemMetricsCollector"), false, false));
    }

    private void addMonitoringToMethod(MethodDeclaration method, String nameURL) {
        BlockStmt body = method.getBody().orElseThrow(() -> new NoSuchElementException("This method should be invoked only with concrete classes and not with abstract methods or interfaces."));
        NodeList<Statement> statements = body.getStatements();
        ReturnStmt returnStmt = body.findFirst(ReturnStmt.class).orElseThrow(() -> new NoSuchElementException("Return statement not found: can't add monitoring to endpoint. Template was modified."));
        statements.addFirst(parseStatement("long startTime = System.nanoTime();"));
        statements.addBefore(parseStatement("long endTime = System.nanoTime();"), returnStmt);
        statements.addBefore(parseStatement("SystemMetricsCollector.registerElapsedTimeSampleMetrics(\"" + nameURL + "\", endTime - startTime);"), returnStmt);
        statements.addBefore(parseStatement(String.format("DMNResultMetricsBuilder.generateMetrics(result, \"%s\");", nameURL)), returnStmt);
    }

    private void initializeApplicationField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String s = vv.getValue();
        String documentation = "";
        String interpolated = s.replace("$name$", decisionName)
                .replace("$nameURL$", nameURL)
                .replace("$id$", decisionId)
                .replace("$modelName$", dmnModel.getName())
                .replace("$modelNamespace$", dmnModel.getNamespace())
                .replace("$documentation$", documentation);
        vv.setString(interpolated);
    }

    private void interpolateMethods(MethodDeclaration m) {
        SimpleName methodName = m.getName();
        String interpolated = methodName.asString().replace("$name$", decisionName);
        m.setName(interpolated);
    }

    public String generatedFilePath() {
        return relativePath;
    }

    protected boolean useInjection() {
        return this.annotator != null;
    }

    public DecisionRestResourceGenerator withStronglyTyped(boolean stronglyTyped) {
        this.isStronglyTyped = stronglyTyped;
        return this;
    }

    public DecisionRestResourceGenerator withOASResult(DMNOASResult oasResult, boolean mpAnnPresent, boolean swaggerAnnPresent) {
        this.withOASResult = oasResult;
        this.mpAnnPresent = mpAnnPresent;
        this.swaggerAnnPresent = swaggerAnnPresent;
        return this;
    }
}
