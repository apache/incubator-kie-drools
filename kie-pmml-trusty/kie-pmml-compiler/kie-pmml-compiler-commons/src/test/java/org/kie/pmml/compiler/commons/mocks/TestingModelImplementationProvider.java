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
package org.kie.pmml.compiler.commons.mocks;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.mocks.TestModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.testingutility.KiePMMLTestingModel.PMML_MODEL_TYPE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setKiePMMLConstructorSuperNameInvocation;
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
    public Class<KiePMMLTestingModel> getKiePMMLModelClass() {
        return KiePMMLTestingModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(CompilationDTO<TestModel> compilationDTO) {
        return getKiePMMLTestModelSourcesMap(compilationDTO);
    }

    private Map<String, String> getKiePMMLTestModelSourcesMap(final CompilationDTO<TestModel> compilationDTO) {

        String className = compilationDTO.getSimpleClassName();
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_TEST_MODEL_TEMPLATE_JAVA,
                                                                                 KIE_PMML_TEST_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String modelName = compilationDTO.getModelName();
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        setConstructor(className, constructorDeclaration, compilationDTO.getFileName(), modelName);
        Map<String, String> toReturn = new HashMap<>();
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    private void setConstructor(final String generatedClassName,
                                final ConstructorDeclaration constructorDeclaration,
                                final String fileName,
                                final String modelName) {
        setKiePMMLConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, fileName, modelName);
    }
}
