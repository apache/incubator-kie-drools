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
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.mining.MiningModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.mining.compiler.HasKnowledgeBuilderMock;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLMiningModelFactoryTest extends AbstractKiePMMLFactoryTest {

    private static final String TEMPLATE_SOURCE = "KiePMMLMiningModelTemplate.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "KiePMMLMiningModelTemplate";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeClass
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
        COMPILATION_UNIT = getFromFileName(TEMPLATE_SOURCE);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(TEMPLATE_CLASS_NAME).get();
    }

    @Test
    public void getKiePMMLMiningModel() {
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final KiePMMLMiningModel retrieved = KiePMMLMiningModelFactory.getKiePMMLMiningModel(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertEquals(MINING_MODEL.getAlgorithmName(), retrieved.getAlgorithmName());
        assertEquals(MINING_MODEL.isScorable(), retrieved.isScorable());
        assertEquals(targetFieldName, retrieved.getTargetField());
    }

    @Test
    public void getKiePMMLMiningModelSourcesMap() {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final Map<String, String> retrieved =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(compilationDTO, nestedModels);
        assertThat(retrieved).isNotNull();
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertEquals(expectedNestedModels, nestedModels.size());
    }

    @Test
    public void getKiePMMLMiningModelSourcesMapCompiled() {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final HasKnowledgeBuilderMock hasKnowledgeBuilderMock = new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER);
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       hasKnowledgeBuilderMock);
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final List<String> expectedGeneratedClasses =
                MINING_MODEL.getSegmentation().getSegments().stream().map(segment -> {

                    String modelName = segment.getModel().getModelName();
                    String sanitizedPackageName =
                            getSanitizedPackageName(compilationDTO.getSegmentationPackageName() + "."
                                                            + segment.getId());
                    String sanitizedClassName = getSanitizedClassName(modelName);
                    return String.format(PACKAGE_CLASS_TEMPLATE, sanitizedPackageName, sanitizedClassName);
                }).collect(Collectors.toList());
        expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
                fail("Expecting class not found: " + expectedGeneratedClass);
            } catch (Exception e) {
                assertTrue(e instanceof ClassNotFoundException);
            }
        });
        final Map<String, String> retrieved =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMapCompiled(compilationDTO, nestedModels);
        assertThat(retrieved).isNotNull();
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertEquals(expectedNestedModels, nestedModels.size());
        expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
            } catch (Exception e) {
                fail("Expecting class to be loaded, but got: " + e.getClass().getName() + " -> " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Test
    public void setConstructor() {
        PMML_MODEL pmmlModel = PMML_MODEL.byName(MINING_MODEL.getClass().getSimpleName());

        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(MINING_MODEL.getMiningFunction().value());
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasClassLoaderMock());
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        KiePMMLMiningModelFactory.setConstructor(compilationDTO, modelTemplate);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", MINING_MODEL.getModelName())));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetFieldName));
        assignExpressionMap.put("miningFunction",
                                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        ClassOrInterfaceType kiePMMLSegmentationClass =
                parseClassOrInterfaceType(compilationDTO.getSegmentationCanonicalClassName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentationClass);
        assignExpressionMap.put("segmentation", objectCreationExpr);
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        assertTrue(commonEvaluateConstructor(constructorDeclaration, getSanitizedClassName(MINING_MODEL.getModelName()),
                                             superInvocationExpressionsMap, assignExpressionMap));
    }
}