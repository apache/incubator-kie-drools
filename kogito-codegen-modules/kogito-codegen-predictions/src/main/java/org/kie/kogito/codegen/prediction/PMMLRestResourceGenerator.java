/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.prediction;

import java.net.URLEncoder;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;
import org.kie.kogito.codegen.core.CodegenUtils;
import org.kie.pmml.commons.model.KiePMMLModel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PMMLRestResourceGenerator {

    static final String QUARKUS_REQUEST_BODY = "org.eclipse.microprofile.openapi.annotations.parameters.RequestBody";
    static final String QUARKUS_API_RESPONSE = "org.eclipse.microprofile.openapi.annotations.responses.APIResponse";
    static final String QUARKUS_SCHEMA = "org.eclipse.microprofile.openapi.annotations.media.Schema";
    static final String SPRING_REQUEST_BODY = "io.swagger.v3.oas.annotations.parameters.RequestBody";
    static final String SPRING_API_RESPONSE = "io.swagger.v3.oas.annotations.responses.ApiResponse";
    static final String SPRING_SCHEMA = "io.swagger.v3.oas.annotations.media.Schema";
    static final String SCHEMA = "schema";
    static final String CONTENT = "content";
    static final String REF = "ref";

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
        String filePrefix = URLEncoder.encode(getSanitizedClassName(model.getFileName().replace(".pmml", "")));
        String classPrefix = URLEncoder.encode(getSanitizedClassName(model.getName()));
        String fullPath = String.format("/%s/%s", filePrefix, classPrefix);
        this.nameURL = fullPath.replaceAll("\\+", "%20");
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
        setPredictionFileName(template);
        setPredictionModelName(template);
        setOASAnnotations(template);
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

    void setPredictionFileName(ClassOrInterfaceDeclaration template) {
        template.getFieldByName("FILE_NAME")
                .orElseThrow(() -> new RuntimeException("Missing FILE_NAME field"))
                .getVariable(0)
                .setInitializer(new StringLiteralExpr(kiePMMLModel.getFileName()));
    }

    void setPredictionModelName(ClassOrInterfaceDeclaration template) {
        template.getFieldByName("MODEL_NAME")
                .orElseThrow(() -> new RuntimeException("Missing MODEL_NAME field"))
                .getVariable(0)
                .setInitializer(new StringLiteralExpr(kiePMMLModel.getName()));
    }

    void setOASAnnotations(ClassOrInterfaceDeclaration template) {
        String jsonFile = String.format("%s.json", getSanitizedClassName(kiePMMLModel.getName()));
        String inputRef = String.format("/%s#/definitions/InputSet", jsonFile);
        setResultOASAnnotations(template, jsonFile, inputRef);
        setDescriptiveOASAnnotations(template, jsonFile, inputRef);
    }

    void setResultOASAnnotations(ClassOrInterfaceDeclaration template, String jsonFile, String inputRef) {
        String outputRef = String.format("/%s#/definitions/ResultSet", jsonFile);
        NodeList<AnnotationExpr> annotations = template.getMethodsByName("result").get(0)
                .getAnnotations();
        switch (context.name()) {
            case "Quarkus":
                setQuarkusOASAnnotations(annotations, inputRef, outputRef);
                break;
            case "Spring":
                setSpringOASAnnotations(annotations, inputRef, outputRef);
                break;
            default:
                // noop
        }
    }

    void setDescriptiveOASAnnotations(ClassOrInterfaceDeclaration template, String jsonFile, String inputRef) {
        String outputRef = String.format("/%s#/definitions/OutputSet", jsonFile);
        NodeList<AnnotationExpr> annotations = template.getMethodsByName("descriptive").get(0)
                .getAnnotations();

        switch (context.name()) {
            case "Quarkus":
                setQuarkusOASAnnotations(annotations, inputRef, outputRef);
                break;
            case "Spring":
                setSpringOASAnnotations(annotations, inputRef, outputRef);
                break;
            default:
                // noop
        }
    }

    void setQuarkusOASAnnotations(NodeList<AnnotationExpr> annotations, String inputRef, String outputRef) {
        Optional<MemberValuePair> ref = getRefMemberValuePair(annotations, QUARKUS_REQUEST_BODY, QUARKUS_SCHEMA);
        ref.ifPresent(rf -> rf.setValue(new StringLiteralExpr(inputRef)));
        ref = getRefMemberValuePair(annotations, QUARKUS_API_RESPONSE, QUARKUS_SCHEMA);
        ref.ifPresent(rf -> rf.setValue(new StringLiteralExpr(outputRef)));
    }

    void setSpringOASAnnotations(NodeList<AnnotationExpr> annotations, String inputRef, String outputRef) {
        Optional<MemberValuePair> ref = getRefMemberValuePair(annotations, SPRING_REQUEST_BODY, SPRING_SCHEMA);
        ref.ifPresent(rf -> rf.setValue(new StringLiteralExpr(inputRef)));
        ref = getRefMemberValuePair(annotations, SPRING_API_RESPONSE, SPRING_SCHEMA);
        ref.ifPresent(rf -> rf.setValue(new StringLiteralExpr(outputRef)));
    }

    void initializeApplicationField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
    }

    Optional<MemberValuePair> getRefMemberValuePair(NodeList<AnnotationExpr> source, String annotationName, String schemaName) {
        return getAnnotationExpr(source, annotationName)
                .flatMap(annExpr -> getMemberValuePairFromAnnotation(annExpr, CONTENT))
                .flatMap(content -> getMemberValuePairFromMemberValuePair(content, SCHEMA))
                .flatMap(schema -> getNormalAnnotationExprFromMemberValuePair(schema, schemaName))
                .flatMap(schemaAnnotation -> getMemberValuePairFromAnnotation(schemaAnnotation, REF));
    }

    Optional<NormalAnnotationExpr> getNormalAnnotationExprFromMemberValuePair(MemberValuePair source, String searched) {
        return source.stream()
                .filter(node -> node instanceof NormalAnnotationExpr &&
                        searched.equals(((NormalAnnotationExpr) node).getName().asString()))
                .map(node -> (NormalAnnotationExpr) node)
                .findFirst();
    }

    Optional<MemberValuePair> getMemberValuePairFromMemberValuePair(MemberValuePair source, String searched) {
        return source.stream()
                .filter(node -> node instanceof MemberValuePair &&
                        searched.equals(((MemberValuePair) node).getName().asString()))
                .map(node -> (MemberValuePair) node)
                .findFirst();
    }

    Optional<MemberValuePair> getMemberValuePairFromAnnotation(AnnotationExpr source, String searched) {
        return source.getChildNodes().stream()
                .filter(node -> node instanceof MemberValuePair &&
                        searched.equals(((MemberValuePair) node).getName().asString()))
                .map(node -> (MemberValuePair) node)
                .findFirst();
    }

    Optional<AnnotationExpr> getAnnotationExpr(NodeList<AnnotationExpr> source, String searched) {
        return source.stream()
                .filter(annExpr -> searched.equals(annExpr.getNameAsString()))
                .findFirst();
    }

}
