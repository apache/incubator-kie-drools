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
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.DecisionService;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.CodegenUtils;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseStatement;

public class DecisionRestResourceGenerator {

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

    private static final Supplier<RuntimeException> TEMPLATE_WAS_MODIFIED = () -> new RuntimeException("Template was modified!");

    public DecisionRestResourceGenerator(DMNModel model, String appCanonicalName) {
        this.dmnModel = model;
        this.packageName = CodegenStringUtil.escapeIdentifier(model.getNamespace());
        this.decisionId = model.getDefinitions().getId();
        this.decisionName = CodegenStringUtil.escapeIdentifier(model.getName());
        this.nameURL = URLEncoder.encode(model.getName()).replace("+", "%20");
        this.appCanonicalName = appCanonicalName;
        String classPrefix = StringUtils.ucFirst(decisionName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
    }

    public String generate() {
        CompilationUnit clazz = parse(this.getClass().getResourceAsStream("/class-templates/DecisionRestResourceTemplate.java"));
        clazz.setPackageDeclaration(this.packageName);

        ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        template.setName(resourceClazzName);

        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        template.findAll(MethodDeclaration.class).forEach(this::interpolateMethods);

        interpolateInputType(template);

        if (useInjection()) {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(fd -> annotator.withInjection(fd));
        } else {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(this::initializeApplicationField);
        }

        MethodDeclaration dmnMethod = template.findAll(MethodDeclaration.class, x -> x.getName().toString().equals("dmn")).get(0);
        template.addMember(cloneForDMNResult(dmnMethod, "dmn_dmnresult", "dmnresult"));
        for (DecisionService ds : dmnModel.getDefinitions().getDecisionService()) {
            if (ds.getAdditionalAttributes().keySet().stream().anyMatch(qn -> qn.getLocalPart().equals("dynamicDecisionService"))) {
                continue;
            }

            MethodDeclaration clonedMethod = dmnMethod.clone();
            String name = CodegenStringUtil.escapeIdentifier("decisionService_" + ds.getName());
            clonedMethod.setName(name);
            MethodCallExpr evaluateCall = clonedMethod.findFirst(MethodCallExpr.class, x -> x.getNameAsString().equals("evaluateAll")).orElseThrow(TEMPLATE_WAS_MODIFIED);
            evaluateCall.setName(new SimpleName("evaluateDecisionService"));
            evaluateCall.addArgument(new StringLiteralExpr(ds.getName()));
            clonedMethod.addAnnotation(new SingleMemberAnnotationExpr(new Name("javax.ws.rs.Path"), new StringLiteralExpr(ds.getName())));
            ReturnStmt returnStmt = clonedMethod.findFirst(ReturnStmt.class).orElseThrow(TEMPLATE_WAS_MODIFIED);
            if (ds.getOutputDecision().size() == 1) {
                MethodCallExpr rewrittenReturnExpr = returnStmt.findFirst(MethodCallExpr.class,
                                                                          mce -> mce.getNameAsString().equals("extractContextIfSucceded"))
                                                               .orElseThrow(TEMPLATE_WAS_MODIFIED);
                rewrittenReturnExpr.setName("extractSingletonDSIfSucceded");
            }

            if (addonsConfig.useMonitoring()) {
                addMonitoringToMethod(clonedMethod, ds.getName());
            }

            template.addMember(clonedMethod);
            template.addMember(cloneForDMNResult(clonedMethod, name + "_dmnresult", ds.getName() + "/dmnresult"));
        }

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

    private MethodDeclaration cloneForDMNResult(MethodDeclaration dmnMethod, String name, String pathName) {
        MethodDeclaration clonedDmnMethod = dmnMethod.clone();
        clonedDmnMethod.setName(name);
        final Name jaxrsPathAnnName = new Name("javax.ws.rs.Path");
        clonedDmnMethod.getAnnotations().removeIf(ae -> ae.getName().equals(jaxrsPathAnnName));
        clonedDmnMethod.addAnnotation(new SingleMemberAnnotationExpr(jaxrsPathAnnName, new StringLiteralExpr(pathName)));
        ReturnStmt returnStmt = clonedDmnMethod.findFirst(ReturnStmt.class).orElseThrow(TEMPLATE_WAS_MODIFIED);
        returnStmt.setExpression(new NameExpr("result"));
        return clonedDmnMethod;
    }

    private void interpolateInputType(ClassOrInterfaceDeclaration template) {
        String inputType = isStronglyTyped ? "InputSet" : "java.util.Map<String, Object>";
        template.findAll(ClassOrInterfaceType.class, t -> t.asString().equals("$inputType$"))
                .forEach(type -> type.setName(inputType));
    }

    public String getNameURL() {
        return nameURL;
    }

    public DMNModel getDmnModel() {
        return this.dmnModel;
    }

    public DecisionRestResourceGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
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
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.system.metrics.SystemMetricsCollector"), false, false));
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.system.metrics.DMNResultMetricsBuilder"), false, false));
        cu.addImport(new ImportDeclaration(new Name("org.kie.kogito.monitoring.system.metrics.SystemMetricsCollector"), false, false));
    }

    private void addMonitoringToMethod(MethodDeclaration method, String nameURL) {
        BlockStmt body = method.getBody().orElseThrow(() -> new NoSuchElementException("This method should be invoked only with concrete classes and not with abstract methods or interfaces."));
        NodeList<Statement> statements = body.getStatements();
        ReturnStmt returnStmt = body.findFirst(ReturnStmt.class).orElseThrow(() -> new NoSuchElementException("Return statement not found: can't add monitoring to endpoint. Template was modified."));
        statements.addFirst(parseStatement("double startTime = System.nanoTime();"));
        statements.addBefore(parseStatement("double endTime = System.nanoTime();"), returnStmt);
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
}