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

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentationSourcesMap;

public class KiePMMLMiningModelFactory {

    static final String KIE_PMML_MINING_MODEL_TEMPLATE_JAVA = "KiePMMLMiningModelTemplate.tmpl";
    static final String KIE_PMML_MINING_MODEL_TEMPLATE = "KiePMMLMiningModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLMiningModelFactory.class.getName());

    private KiePMMLMiningModelFactory() {
        // Avoid instantiation
    }

    public static Map<String, String> getKiePMMLMiningModelSourcesMap(final MiningModelCompilationDTO compilationDTO,
                                                                      final List<KiePMMLModel> nestedModels) {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", compilationDTO.getFields(),
                     compilationDTO.getModel(), compilationDTO.getPackageName());
        final Map<String, String> toReturn = getSegmentationSourcesMap(compilationDTO,
                                                                       nestedModels);
        nestedModels.forEach(nestedModel -> {
            if (!(nestedModel instanceof KiePMMLModelWithSources)) {
                throw new KiePMMLException("Expecting only KiePMMLModelWithSources at this phase; retrieved " + nestedModel.getClass());
            }
            toReturn.putAll(((KiePMMLModelWithSources) nestedModel).getSourcesMap());
        });
        return getKiePMMLMiningModelSourcesMapCommon(compilationDTO,
                                                     toReturn);
    }

    static Map<String, String> getKiePMMLMiningModelSourcesMapCommon(final MiningModelCompilationDTO compilationDTO,
                                                                     final Map<String, String> toReturn) {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", compilationDTO.getFields(),
                     compilationDTO.getModel(), compilationDTO.getPackageName());
        if (!toReturn.containsKey(compilationDTO.getSegmentationCanonicalClassName())) {
            throw new KiePMMLException("Expected generated class " + compilationDTO.getSegmentationCanonicalClassName() + " not " +
                                               "found");
        }
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(compilationDTO.getSimpleClassName(),
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_MINING_MODEL_TEMPLATE_JAVA,
                                                                                 KIE_PMML_MINING_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(compilationDTO.getSimpleClassName())
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + compilationDTO.getSimpleClassName()));
        setConstructor(compilationDTO, modelTemplate);
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final MiningModelCompilationDTO compilationDTO,
                               final ClassOrInterfaceDeclaration modelTemplate) {
        KiePMMLModelFactoryUtils.init(compilationDTO, modelTemplate);
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        ClassOrInterfaceType kiePMMLSegmentationClass =
                parseClassOrInterfaceType(compilationDTO.getSegmentationCanonicalClassName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentationClass);
        CommonCodegenUtils.setAssignExpressionValue(body, "segmentation", objectCreationExpr);
    }
}
