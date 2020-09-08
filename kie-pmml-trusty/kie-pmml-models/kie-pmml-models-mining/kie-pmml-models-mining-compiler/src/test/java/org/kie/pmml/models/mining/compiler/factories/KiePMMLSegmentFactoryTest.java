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
import java.util.function.Consumer;

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
import org.dmg.pmml.mining.Segment;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.xml.sax.SAXException;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    public void getSegments() {
        final List<Segment> segments = MINING_MODEL.getSegmentation().getSegments();
        final List<KiePMMLSegment> retrieved = KiePMMLSegmentFactory.getSegments(DATA_DICTIONARY,
                                                                                 TRANSFORMATION_DICTIONARY,
                                                                                 segments,
                                                                                 KNOWLEDGE_BUILDER);
        assertNotNull(retrieved);
        assertEquals(segments.size(), retrieved.size());
        for (int i = 0; i < segments.size(); i++) {
            commonEvaluateSegment(retrieved.get(i), segments.get(i));
        }
    }

    @Test
    public void getSegment() {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final KiePMMLSegment retrieved = KiePMMLSegmentFactory.getSegment(DATA_DICTIONARY,
                                                                          TRANSFORMATION_DICTIONARY,
                                                                          segment,
                                                                          KNOWLEDGE_BUILDER);
        commonEvaluateSegment(retrieved, segment);
    }

    @Test
    public void getSegmentsSourcesMap() {
        final List<Segment> segments = MINING_MODEL.getSegmentation().getSegments();
        final String packageName = "packagename";
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentsSourcesMap(
                packageName,
                DATA_DICTIONARY,
                TRANSFORMATION_DICTIONARY,
                segments,
                KNOWLEDGE_BUILDER,
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
        final String packageName = "packagename";
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentSourcesMap(packageName,
                                                                                         DATA_DICTIONARY,
                                                                                         TRANSFORMATION_DICTIONARY,
                                                                                         segment,
                                                                                         KNOWLEDGE_BUILDER,
                                                                                         nestedModels);
        commonEvaluateNestedModels(nestedModels);
        commonEvaluateMap(retrieved, segment);
    }

    @Test
    public void getSegmentSourcesMapHasSourcesWithKiePMMLModelClass() {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final String packageName = "packagename";
        final String regressionModelName = "CategoricalVariablesRegression";
        final String kiePMMLModelClass = packageName + "." + regressionModelName;
        final Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(kiePMMLModelClass, String.format("public class %s {}", regressionModelName));
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentSourcesMap(packageName,
                                                                                         DATA_DICTIONARY,
                                                                                         segment);
        commonEvaluateMap(retrieved, segment);
    }

    @Test
    public void setConstructor() {
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        String segmentName = "SEGMENTNAME";
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateClassName = "PREDICATECLASSNAME";
        String kiePMMLModelClass = "KIEPMMLMODELCLASS";
        double weight = 12.22;
        KiePMMLSegmentFactory.setConstructor(segmentName,
                                             generatedClassName,
                                             constructorDeclaration,
                                             predicateClassName,
                                             kiePMMLModelClass,
                                             weight);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", segmentName)));
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(predicateClassName);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(classOrInterfaceType);
        superInvocationExpressionsMap.put(2, new NameExpr(objectCreationExpr.toString()));
        classOrInterfaceType = parseClassOrInterfaceType(kiePMMLModelClass);
        objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(classOrInterfaceType);
        superInvocationExpressionsMap.put(3, new NameExpr(objectCreationExpr.toString()));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("weight", new DoubleLiteralExpr(weight));
        assignExpressionMap.put("id", new StringLiteralExpr(segmentName));
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName,
                                             superInvocationExpressionsMap, assignExpressionMap));
    }

    private void commonEvaluateSegment(final KiePMMLSegment toEvaluate, final Segment segment) {
        assertNotNull(toEvaluate);
        assertEquals(segment.getId(), toEvaluate.getName());
        assertEquals(segment.getPredicate().getClass().getSimpleName(), toEvaluate.getKiePMMLPredicate().getName());
        assertNotNull(toEvaluate.getModel());
    }

    private void commonEvaluateMap(final Map<String, String> toEvaluate, final Segment segment) {
        assertNotNull(toEvaluate);
    }

    private void commonEvaluateNestedModels(final List<KiePMMLModel> toEvaluate ) {
        assertFalse(toEvaluate.isEmpty());
        toEvaluate.forEach(kiePMMLModel -> assertTrue(kiePMMLModel instanceof HasSourcesMap));
    }
}