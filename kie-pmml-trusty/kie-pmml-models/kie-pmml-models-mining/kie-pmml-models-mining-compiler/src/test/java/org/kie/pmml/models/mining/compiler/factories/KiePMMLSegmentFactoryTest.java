/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.models.mining.compiler.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.models.mining.compiler.HasKnowledgeBuilderMock;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.kie.pmml.models.mining.compiler.dto.SegmentCompilationDTO;
import org.xml.sax.SAXException;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentFactory.KIE_PMML_SEGMENT_TEMPLATE;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentFactory.KIE_PMML_SEGMENT_TEMPLATE_JAVA;

public class KiePMMLSegmentFactoryTest extends AbstractKiePMMLFactoryTest {

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeClass
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
        COMPILATION_UNIT = getFromFileName(KIE_PMML_SEGMENT_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_SEGMENT_TEMPLATE).get();
    }

    @Test
    public void getSegmentsSourcesMap() {
        final List<Segment> segments = MINING_MODEL.getSegmentation().getSegments();
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentsSourcesMap(
                compilationDTO,
                nestedModels);
        assertNotNull(retrieved);
        commonEvaluateNestedModels(nestedModels);
        for (Segment segment : segments) {
            commonEvaluateMap(retrieved, segment);
        }
    }

    @Test
    public void getSegmentSourcesMap() {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final SegmentCompilationDTO segmentCompilationDTO =
                SegmentCompilationDTO.fromGeneratedPackageNameAndFields(compilationDTO, segment,
                                                                        compilationDTO.getFields());
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentSourcesMap(segmentCompilationDTO,
                                                                                         nestedModels);
        commonEvaluateNestedModels(nestedModels);
        commonEvaluateMap(retrieved, segment);
    }

    @Test
    public void getSegmentSourcesMapCompiled() throws Exception {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final List<KiePMMLModel> nestedModels = new ArrayList<>();

        final String expectedNestedModelGeneratedClass = getExpectedNestedModelClass(segment);

        final HasKnowledgeBuilderMock hasKnowledgeBuilderMock = new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER);
        try {
            hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedNestedModelGeneratedClass);
            fail("Expecting class not found: " + expectedNestedModelGeneratedClass);
        } catch (Exception e) {
            assertTrue(e instanceof ClassNotFoundException);
        }
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       hasKnowledgeBuilderMock);
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final SegmentCompilationDTO segmentCompilationDTO =
                SegmentCompilationDTO.fromGeneratedPackageNameAndFields(compilationDTO, segment,
                                                                        compilationDTO.getFields());
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentSourcesMapCompiled(segmentCompilationDTO,
                                                                                                 nestedModels);
        commonEvaluateNestedModels(nestedModels);
        commonEvaluateMap(retrieved, segment);
        hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedNestedModelGeneratedClass);
    }

    @Test
    public void getSegmentSourcesMapHasSourcesWithKiePMMLModelClass() {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final String regressionModelName = "CategoricalVariablesRegression";
        final String kiePMMLModelClass = PACKAGE_NAME + "." + regressionModelName;
        final Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(kiePMMLModelClass, String.format("public class %s {}", regressionModelName));
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final SegmentCompilationDTO segmentCompilationDTO =
                SegmentCompilationDTO.fromGeneratedPackageNameAndFields(compilationDTO, segment,
                                                                        compilationDTO.getFields());
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentSourcesMap(segmentCompilationDTO);
        commonEvaluateMap(retrieved, segment);
    }

    @Test
    public void setConstructor() {
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        String segmentName = "SEGMENTNAME";
        String generatedClassName = "GENERATEDCLASSNAME";
        String kiePMMLModelClass = "KIEPMMLMODELCLASS";
        double weight = 12.22;
        KiePMMLSegmentFactory.setConstructor(segmentName,
                                             generatedClassName,
                                             constructorDeclaration,
                                             kiePMMLModelClass,
                                             weight);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", segmentName)));
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(kiePMMLModelClass);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(classOrInterfaceType);
        superInvocationExpressionsMap.put(3, new NameExpr(objectCreationExpr.toString()));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("weight", new DoubleLiteralExpr(weight));
        assignExpressionMap.put("id", new StringLiteralExpr(segmentName));
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName,
                                             superInvocationExpressionsMap, assignExpressionMap));
    }

    private void commonEvaluateMap(final Map<String, String> toEvaluate, final Segment segment) {
        assertNotNull(toEvaluate);
    }

    private void commonEvaluateNestedModels(final List<KiePMMLModel> toEvaluate) {
        assertFalse(toEvaluate.isEmpty());
        toEvaluate.forEach(kiePMMLModel -> assertTrue(kiePMMLModel instanceof HasSourcesMap));
    }
}