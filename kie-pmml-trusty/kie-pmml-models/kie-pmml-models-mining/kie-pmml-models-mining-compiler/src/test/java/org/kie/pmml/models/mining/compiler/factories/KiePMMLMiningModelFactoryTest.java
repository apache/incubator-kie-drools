/**
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
package org.kie.pmml.models.mining.compiler.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.mining.MiningModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.xml.sax.SAXException;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLMiningModelFactoryTest extends AbstractKiePMMLFactoryTest {

    private static final String TEMPLATE_SOURCE = "KiePMMLMiningModelTemplate.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "KiePMMLMiningModelTemplate";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeAll
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
        COMPILATION_UNIT = getFromFileName(TEMPLATE_SOURCE);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(TEMPLATE_CLASS_NAME).get();
    }


    @Test
    void getKiePMMLMiningModelSourcesMap() {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILE_NAME");
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final Map<String, String> retrieved =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(compilationDTO, nestedModels);
        assertThat(retrieved).isNotNull();
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertThat(nestedModels).hasSize(expectedNestedModels);
    }

    @Test
    void setConstructor() {
        PMML_MODEL pmmlModel = PMML_MODEL.byName(MINING_MODEL.getClass().getSimpleName());

        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(MINING_MODEL.getMiningFunction().value());
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        KiePMMLMiningModelFactory.setConstructor(compilationDTO, modelTemplate);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", "FILENAME")));
        superInvocationExpressionsMap.put(1, new NameExpr(String.format("\"%s\"", MINING_MODEL.getModelName())));
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
        assertThat(commonEvaluateConstructor(constructorDeclaration, getSanitizedClassName(MINING_MODEL.getModelName()),
                superInvocationExpressionsMap, assignExpressionMap)).isTrue();
    }
}