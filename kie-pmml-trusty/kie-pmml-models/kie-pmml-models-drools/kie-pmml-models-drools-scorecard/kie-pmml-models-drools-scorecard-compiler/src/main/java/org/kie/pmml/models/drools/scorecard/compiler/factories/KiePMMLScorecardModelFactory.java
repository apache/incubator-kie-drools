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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.dmg.pmml.Field;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.drools.utils.KiePMMLDroolsModelFactoryUtils.getKiePMMLModelCompilationUnit;

/**
 * Class used to generate <code>KiePMMLScorecard</code> out of a <code>DataDictionary</code> and a
 * <code>ScorecardModel</code>
 */
public class KiePMMLScorecardModelFactory {

    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA = "KiePMMLScorecardModelTemplate.tmpl";
    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE = "KiePMMLScorecardModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static Map<String, String> getKiePMMLScorecardModelSourcesMap(final DroolsCompilationDTO<Scorecard> compilationDTO) {
        logger.trace("getKiePMMLScorecardModelSourcesMap {} {} {}", compilationDTO.getFields(),
                     compilationDTO.getModel(), compilationDTO.getPackageName());
        CompilationUnit cloneCU = getKiePMMLModelCompilationUnit(compilationDTO,
                                                                 KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA,
                                                                 KIE_PMML_SCORECARD_MODEL_TEMPLATE);
        String className = compilationDTO.getSimpleClassName();
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        setConstructor(compilationDTO,
                       modelTemplate);
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = compilationDTO.getPackageCanonicalClassName();
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    /**
     * This method returns a <code>KiePMMLDroolsAST</code> out of the given <code>DataDictionary</code> and
     * <code>Scorecard</code>.
     * <b>It also populate the given <code>Map</code> that has to be used for final
     * <code>KiePMMLScorecardModel</code></b>
     *
     * @param fields
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                       final Scorecard model,
                                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                       final List<KiePMMLDroolsType> types) {
        logger.trace("getKiePMMLDroolsAST {}", model);
        return KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST(fields, model, fieldTypeMap, types);
    }

    static void setConstructor(final DroolsCompilationDTO<Scorecard> compilationDTO,
                               final ClassOrInterfaceDeclaration modelTemplate) {
        KiePMMLModelFactoryUtils.init(compilationDTO,
                                      modelTemplate);
    }
}