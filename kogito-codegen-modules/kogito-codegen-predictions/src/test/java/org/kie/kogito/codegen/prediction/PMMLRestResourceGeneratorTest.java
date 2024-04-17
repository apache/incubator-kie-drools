/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.prediction;

import java.net.URLEncoder;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.di.impl.CDIDependencyInjectionAnnotator;
import org.drools.util.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.pmml.commons.model.KiePMMLModel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.CONTENT;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.QUARKUS_API_RESPONSE;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.QUARKUS_REQUEST_BODY;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.QUARKUS_SCHEMA;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.REF;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.SCHEMA;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.SPRING_API_RESPONSE;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.SPRING_REQUEST_BODY;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.SPRING_SCHEMA;
import static org.kie.kogito.pmml.CommonTestUtility.getKiePMMLModelInternal;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

class PMMLRestResourceGeneratorTest {

    private final static String APP_CANONICAL_NAME = "APP_CANONICAL_NAME";
    private final static KiePMMLModel KIE_PMML_MODEL = getKiePMMLModelInternal();
    private final static String INPUT_REF = "inputRef";
    private final static String RESULT_REF = "resultRef";
    private final static String OUTPUT_REF = "outputRef";
    private final static ClassOrInterfaceDeclaration TEMPLATE =
            getClassOrInterfaceDeclaration(QuarkusKogitoBuildContext.builder().build());
    private static PMMLRestResourceGenerator pmmlRestResourceGenerator;
    private static KogitoBuildContext context;

    private static String expectedUrl;

    @BeforeAll
    public static void setup() {
        context = QuarkusKogitoBuildContext.builder().build();
        pmmlRestResourceGenerator = new PMMLRestResourceGenerator(context, KIE_PMML_MODEL, APP_CANONICAL_NAME);
        assertNotNull(pmmlRestResourceGenerator);
        String filePrefix = URLEncoder.encode(getSanitizedClassName(KIE_PMML_MODEL.getFileName().replace(".pmml", "")));
        String classPrefix = URLEncoder.encode(getSanitizedClassName(KIE_PMML_MODEL.getName()));
        expectedUrl = String.format("/%s/%s", filePrefix, classPrefix);
        System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, String.format("%s/test-classes", AppPaths.TARGET_DIR));
    }

    @AfterAll
    public static void cleanup() {
        System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
    }

    private static ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration(KogitoBuildContext context) {
        CompilationUnit clazz = TemplatedGenerator.builder()
                .build(context, "PMMLRestResource")
                .compilationUnitOrThrow();

        clazz.setPackageDeclaration(CodegenStringUtil.escapeIdentifier("IDENTIFIER"));
        return clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface " +
                        "declaration!"));
    }

    @Test
    void constructor() {
        assertTrue(pmmlRestResourceGenerator.restPackageName.startsWith("org.kie.kogito"));
        assertEquals(APP_CANONICAL_NAME, pmmlRestResourceGenerator.appCanonicalName);
    }

    @Test
    void generateWithDependencyInjection() {
        context.setDependencyInjectionAnnotator(new CDIDependencyInjectionAnnotator());
        String retrieved = pmmlRestResourceGenerator.generate();
        commonEvaluateGenerate(retrieved);
        String expected = "Application application;";
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void generateWithoutDependencyInjection() {
        context.setDependencyInjectionAnnotator(null);
        String retrieved = pmmlRestResourceGenerator.generate();
        commonEvaluateGenerate(retrieved);
        String expected = String.format("Application application = new %s();", APP_CANONICAL_NAME);
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void getNameURL() {
        assertEquals(expectedUrl, pmmlRestResourceGenerator.getNameURL());
    }

    @Test
    void getKiePMMLModel() {
        assertEquals(KIE_PMML_MODEL, pmmlRestResourceGenerator.getKiePMMLModel());
    }

    @Test
    void className() {
        String classPrefix = getSanitizedClassName(KIE_PMML_MODEL.getName());
        String expected = StringUtils.ucFirst(classPrefix) + "Resource";
        assertEquals(expected, pmmlRestResourceGenerator.className());
    }

    @Test
    void generatedFilePath() {
        String retrieved = pmmlRestResourceGenerator.generatedFilePath();
        assertTrue(retrieved.startsWith("org/kie/kogito"));
        String classPrefix = getSanitizedClassName(KIE_PMML_MODEL.getName());
        String expected = StringUtils.ucFirst(classPrefix) + "Resource.java";
        assertTrue(retrieved.endsWith(expected));
    }

    @Test
    void setPathValue() {
        final Optional<SingleMemberAnnotationExpr> retrievedOpt = TEMPLATE.findFirst(SingleMemberAnnotationExpr.class);
        assertTrue(retrievedOpt.isPresent());
        SingleMemberAnnotationExpr retrieved = retrievedOpt.get();
        assertEquals("Path", retrieved.getName().asString());
        pmmlRestResourceGenerator.setPathValue(TEMPLATE);
        assertEquals(expectedUrl, retrieved.getMemberValue().asStringLiteralExpr().asString());
    }

    @Test
    void setPredictionFileName() {
        assertTrue(TEMPLATE.getFieldByName("FILE_NAME").isPresent());
        final FieldDeclaration modelName = TEMPLATE.getFieldByName("FILE_NAME").get();
        assertFalse(modelName.getVariable(0).getInitializer().isPresent());
        pmmlRestResourceGenerator.setPredictionFileName(TEMPLATE);
        assertTrue(modelName.getVariable(0).getInitializer().isPresent());
        assertEquals(KIE_PMML_MODEL.getFileName(),
                modelName.getVariable(0).getInitializer().get().asStringLiteralExpr().asString());
    }

    @Test
    void setPredictionModelName() {
        assertTrue(TEMPLATE.getFieldByName("MODEL_NAME").isPresent());
        final FieldDeclaration modelName = TEMPLATE.getFieldByName("MODEL_NAME").get();
        assertFalse(modelName.getVariable(0).getInitializer().isPresent());
        pmmlRestResourceGenerator.setPredictionModelName(TEMPLATE);
        assertTrue(modelName.getVariable(0).getInitializer().isPresent());
        assertEquals(KIE_PMML_MODEL.getName(),
                modelName.getVariable(0).getInitializer().get().asStringLiteralExpr().asString());
    }

    @Test
    void setQuarkusResultAOSAnnotations() {
        ClassOrInterfaceDeclaration quarkusTemplate =
                getClassOrInterfaceDeclaration(QuarkusKogitoBuildContext.builder().build());
        KogitoBuildContext quarkusContext = QuarkusKogitoBuildContext.builder().build();
        PMMLRestResourceGenerator quarkusPMMLRestResourceGenerator = new PMMLRestResourceGenerator(quarkusContext,
                KIE_PMML_MODEL,
                APP_CANONICAL_NAME);
        NodeList<AnnotationExpr> annotations = quarkusTemplate.getMethodsByName("result").get(0)
                .getAnnotations();
        quarkusPMMLRestResourceGenerator.setQuarkusOASAnnotations(annotations, INPUT_REF, RESULT_REF);
        String retrieved = quarkusTemplate.toString();
        String expected = String.format("@%1$s(%2$s = @org.eclipse.microprofile.openapi.annotations.media.Content" +
                "(mediaType = \"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML input\")",
                QUARKUS_REQUEST_BODY,
                CONTENT,
                SCHEMA,
                QUARKUS_SCHEMA,
                REF,
                INPUT_REF);
        assertTrue(retrieved.contains(expected));
        expected = String.format("@%1$s(%2$s = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType " +
                "= \"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML result\")",
                QUARKUS_API_RESPONSE,
                CONTENT,
                SCHEMA,
                QUARKUS_SCHEMA,
                REF,
                RESULT_REF);
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void setQuarkusDescriptiveOASAnnotations() {
        ClassOrInterfaceDeclaration quarkusTemplate =
                getClassOrInterfaceDeclaration(QuarkusKogitoBuildContext.builder().build());
        KogitoBuildContext quarkusContext = QuarkusKogitoBuildContext.builder().build();
        PMMLRestResourceGenerator quarkusPMMLRestResourceGenerator = new PMMLRestResourceGenerator(quarkusContext,
                KIE_PMML_MODEL,
                APP_CANONICAL_NAME);
        NodeList<AnnotationExpr> annotations = quarkusTemplate.getMethodsByName("descriptive").get(0)
                .getAnnotations();
        quarkusPMMLRestResourceGenerator.setQuarkusOASAnnotations(annotations, INPUT_REF, OUTPUT_REF);
        String retrieved = quarkusTemplate.toString();
        String expected = String.format("@%1$s(%2$s = @org.eclipse.microprofile.openapi.annotations.media.Content" +
                "(mediaType = \"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML input\")",
                QUARKUS_REQUEST_BODY,
                CONTENT,
                SCHEMA,
                QUARKUS_SCHEMA,
                REF,
                INPUT_REF);
        assertTrue(retrieved.contains(expected));
        expected = String.format("@%1$s(%2$s = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType " +
                "= \"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML full output\")",
                QUARKUS_API_RESPONSE,
                CONTENT,
                SCHEMA,
                QUARKUS_SCHEMA,
                REF,
                OUTPUT_REF);
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void setSpringResultOASAnnotations() {
        ClassOrInterfaceDeclaration springTemplate =
                getClassOrInterfaceDeclaration(SpringBootKogitoBuildContext.builder().build());
        KogitoBuildContext springContext = SpringBootKogitoBuildContext.builder().build();
        PMMLRestResourceGenerator springPMMLRestResourceGenerator = new PMMLRestResourceGenerator(springContext,
                KIE_PMML_MODEL,
                APP_CANONICAL_NAME);
        NodeList<AnnotationExpr> annotations = springTemplate.getMethodsByName("result").get(0)
                .getAnnotations();
        springPMMLRestResourceGenerator.setSpringOASAnnotations(annotations, INPUT_REF, RESULT_REF);
        String retrieved = springTemplate.toString();
        String expected = String.format("@%1$s(%2$s = @io.swagger.v3.oas.annotations.media.Content(mediaType = " +
                "\"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML input\")",
                SPRING_REQUEST_BODY,
                CONTENT,
                SCHEMA,
                SPRING_SCHEMA,
                REF,
                INPUT_REF);
        assertTrue(retrieved.contains(expected));
        expected = String.format("@%1$s(%2$s = @io.swagger.v3.oas.annotations.media.Content(mediaType = " +
                "\"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML result\")",
                SPRING_API_RESPONSE,
                CONTENT,
                SCHEMA,
                SPRING_SCHEMA,
                REF,
                RESULT_REF);
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void setSpringDescriptiveOASAnnotations() {
        ClassOrInterfaceDeclaration springTemplate =
                getClassOrInterfaceDeclaration(SpringBootKogitoBuildContext.builder().build());
        KogitoBuildContext springContext = SpringBootKogitoBuildContext.builder().build();
        PMMLRestResourceGenerator springPMMLRestResourceGenerator = new PMMLRestResourceGenerator(springContext,
                KIE_PMML_MODEL,
                APP_CANONICAL_NAME);
        NodeList<AnnotationExpr> annotations = springTemplate.getMethodsByName("descriptive").get(0)
                .getAnnotations();
        springPMMLRestResourceGenerator.setSpringOASAnnotations(annotations, INPUT_REF, OUTPUT_REF);
        String retrieved = springTemplate.toString();
        String expected = String.format("@%1$s(%2$s = @io.swagger.v3.oas.annotations.media.Content(mediaType = " +
                "\"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML input\")",
                SPRING_REQUEST_BODY,
                CONTENT,
                SCHEMA,
                SPRING_SCHEMA,
                REF,
                INPUT_REF);
        assertTrue(retrieved.contains(expected));
        expected = String.format("@%1$s(%2$s = @io.swagger.v3.oas.annotations.media.Content(mediaType = " +
                "\"application/json\", " +
                "%3$s = @%4$s(%5$s = \"%6$s\")), description = \"PMML full output\")",
                SPRING_API_RESPONSE,
                CONTENT,
                SCHEMA,
                SPRING_SCHEMA,
                REF,
                OUTPUT_REF);
        assertTrue(retrieved.contains(expected));
    }

    private void commonEvaluateGenerate(String retrieved) {
        assertNotNull(retrieved);
        String classPrefix = getSanitizedClassName(KIE_PMML_MODEL.getName());
        String expected = String.format("@Path(\"%s\")", expectedUrl);
        assertTrue(retrieved.contains(expected));
        expected = StringUtils.ucFirst(classPrefix) + "Resource";
        expected = String.format("public class %s extends org.kie.kogito.pmml.AbstractPMMLRestResource {", expected);
        assertTrue(retrieved.contains(expected));
    }
}
