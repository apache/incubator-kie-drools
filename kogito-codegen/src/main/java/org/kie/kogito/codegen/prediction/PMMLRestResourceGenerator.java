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
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.pmml.commons.model.KiePMMLModel;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PMMLRestResourceGenerator {

    private final String nameURL;
    final String restPackageName;
    final String appCanonicalName;
    private final String resourceClazzName;
    private final String relativePath;
    private final KogitoBuildContext context;
    private final KiePMMLModel kiePMMLModel;
    private final TemplatedGenerator generator;

    public PMMLRestResourceGenerator(KogitoBuildContext context, KiePMMLModel model, String appCanonicalName) {
        this.context = context;
        this.kiePMMLModel = model;
        this.restPackageName = "org.kie.kogito." + CodegenStringUtil.escapeIdentifier(model.getClass().getPackage().getName());
        String classPrefix = getSanitizedClassName(model.getName());
        this.nameURL = URLEncoder.encode(classPrefix).replaceAll("\\+", "%20");
        this.appCanonicalName = appCanonicalName;
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = restPackageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.generator = TemplatedGenerator.builder()
                .withPackageName(restPackageName)
                .withFallbackContext(QuarkusKogitoBuildContext.CONTEXT_NAME)
                .build(context, "PMMLRestResource");
    }

    public String generate() {
        CompilationUnit clazz = generator.compilationUnitOrThrow("Cannot generate Prediction REST Resource");

        ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface " +
                                                                      "declaration!"));

        template.setName(resourceClazzName);

        setPathValue(template);
        setPredictionModelName(template);

        if (context.hasDI()) {
            template.findAll(FieldDeclaration.class,
                             CodegenUtils::isApplicationField).forEach(fd -> context.getDependencyInjectionAnnotator().withInjection(fd));
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

    public String className() {
        return resourceClazzName;
    }

    public String generatedFilePath() {
        return relativePath;
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