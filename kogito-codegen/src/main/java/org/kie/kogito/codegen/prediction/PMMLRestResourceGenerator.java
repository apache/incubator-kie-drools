/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.prediction;

import java.net.URLEncoder;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.CodegenUtils;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.pmml.commons.model.KiePMMLModel;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PMMLRestResourceGenerator {

    public static final String CDI_TEMPLATE = "/class-templates/PMMLRestResourceTemplate.java";
    private static final String SPRING_TEMPLATE = "/class-templates/spring/SpringPMMLRestResourceTemplate.java";

    private final String nameURL;
    final String packageName;
    final String appCanonicalName;
    DependencyInjectionAnnotator annotator;
    private final String resourceClazzName;
    private final String relativePath;
    private final KiePMMLModel kiePMMLModel;
    private final TemplatedGenerator generator;

    public PMMLRestResourceGenerator(KiePMMLModel model, String appCanonicalName) {
        this.kiePMMLModel = model;
        this.packageName = "org.kie.kogito." + CodegenStringUtil.escapeIdentifier(model.getClass().getPackage().getName());
        String classPrefix = getSanitizedClassName(model.getName());
        this.nameURL = URLEncoder.encode(classPrefix).replaceAll("\\+", "%20");
        this.appCanonicalName = appCanonicalName;
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.generator = new TemplatedGenerator(packageName, "DecisionRestResource",CDI_TEMPLATE, SPRING_TEMPLATE, CDI_TEMPLATE);
    }

    public String generate() {
        CompilationUnit clazz = generator.compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(resourceClazzName, generator.templatePath(), "Cannot " +
                        "generate Prediction REST Resource"));

        ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface " +
                                                                      "declaration!"));

        template.setName(resourceClazzName);

        setPathValue(template);
        setPredictionModelName(template);

        if (useInjection()) {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(fd -> annotator.withInjection(fd));
        } else {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(this::initializeApplicationField);
        }

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    public String getNameURL() {
        return nameURL;
    }

    public KiePMMLModel getKiePMMLModel() {
        return this.kiePMMLModel;
    }

    public PMMLRestResourceGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        this.generator.withDependencyInjection(annotator);
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    public String generatedFilePath() {
        return relativePath;
    }

    protected boolean useInjection() {
        return this.annotator != null;
    }

    void setPathValue(ClassOrInterfaceDeclaration template) {
        template.findFirst(SingleMemberAnnotationExpr.class).orElseThrow(() -> new RuntimeException("")).setMemberValue(new StringLiteralExpr(nameURL));
    }

    void setPredictionModelName(ClassOrInterfaceDeclaration template) {
        template.getMethodsByName("pmml").get(0)
                .getBody().orElseThrow(() -> new RuntimeException(""))
                .getStatement(0).asExpressionStmt().getExpression()
                .asVariableDeclarationExpr()
                .getVariable(0)
                .getInitializer().orElseThrow(() -> new RuntimeException(""))
                .asMethodCallExpr()
                .getArgument(0).asStringLiteralExpr().setString(kiePMMLModel.getName());
    }

    private void initializeApplicationField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
    }
}