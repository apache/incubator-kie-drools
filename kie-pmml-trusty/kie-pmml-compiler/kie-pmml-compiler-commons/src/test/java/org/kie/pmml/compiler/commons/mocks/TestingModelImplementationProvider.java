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
package org.kie.pmml.compiler.commons.mocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModelWithSources;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.testingutility.KiePMMLTestingModel.PMML_MODEL_TYPE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

/**
 * <b>Fake</b> <code>ModelImplementationProvider</code> used for testing. It is mapped to <code>TestModel</code> (mock)
 */
public class TestingModelImplementationProvider implements ModelImplementationProvider<TestModel, KiePMMLTestingModel> {

    public static final String KIE_PMML_TEST_MODEL_TEMPLATE_JAVA =
            "KiePMMLTestModelTemplate.tmpl";
    public static final String KIE_PMML_TEST_MODEL_TEMPLATE =
            "KiePMMLTestModelTemplate";

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLTestingModel getKiePMMLModel(final String packageName,
                                            final DataDictionary dataDictionary,
                                            final TransformationDictionary transformationDictionary,
                                            final TestModel model,
                                            final HasClassLoader hasClassLoader) {
        return KiePMMLTestingModel.builder("TEST_MODEL",
                                    Collections.emptyList(),
                                    MINING_FUNCTION.REGRESSION)
                .build();
    }

    @Override
    public KiePMMLTestingModel getKiePMMLModelWithSources(final String packageName,
                                                       final DataDictionary dataDictionary,
                                                       final TransformationDictionary transformationDictionary,
                                                       final TestModel model,
                                                       final HasClassLoader hasClassLoader) {
        final Map<String, String> sourcesMap = getKiePMMLTestModelSourcesMap(dataDictionary, transformationDictionary
                , model, packageName);
        return new KiePMMLTestingModelWithSources(model.getModelName(), packageName, sourcesMap);
    }

    private Map<String, String> getKiePMMLTestModelSourcesMap(final DataDictionary dataDictionary,
                                                              final TransformationDictionary transformationDictionary,
                                                              final TestModel model,
                                                              final String packageName) {

        String className = getSanitizedClassName(model.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_TEST_MODEL_TEMPLATE_JAVA,
                                                                                 KIE_PMML_TEST_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String modelName = model.getModelName();
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        setConstructor(className, constructorDeclaration, modelName);
        Map<String, String> toReturn = new HashMap<>();
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    private void setConstructor(final String generatedClassName,
                                final ConstructorDeclaration constructorDeclaration,
                                final String modelName) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, modelName);
    }

}
