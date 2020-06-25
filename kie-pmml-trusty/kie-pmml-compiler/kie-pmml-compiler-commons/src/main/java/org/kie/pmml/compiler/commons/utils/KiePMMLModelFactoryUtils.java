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
package org.kie.pmml.compiler.commons.utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.TransformationDictionary;

import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMapPopulation;
import static org.kie.pmml.compiler.commons.utils.DerivedFieldFunctionUtils.getDerivedFieldsMethodMap;

/**
 * Class to provide shared, helper methods to be invoked by model-specific
 * <b>factories</b> (e.g. KiePMMLTreeModelFactory, KiePMMLScorecardModelFactory, KiePMMLRegressionModelFactory)
 */
public class KiePMMLModelFactoryUtils {

    private KiePMMLModelFactoryUtils() {
        // Avoid instantiation
    }

    /**
     * Method to generate the code populating the <b>commonTransformationsMap</b> and <b>localTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>>s inside the constructor
     * @param constructorDeclaration
     * @param transformationDictionary
     * @param localTransformations
     */
    public static void populateTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final TransformationDictionary transformationDictionary, final LocalTransformations localTransformations) {
        final AtomicInteger arityCounter = new AtomicInteger(0);
        populateCommonTransformationsInConstructor(constructorDeclaration, transformationDictionary, arityCounter);
        populateLocalTransformationsInConstructor(constructorDeclaration, localTransformations, arityCounter);
    }

    /**
     * Method to generate the code populating the <b>commonTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>> inside the constructor
     * @param constructorDeclaration
     * @param transformationDictionary
     * @param arityCounter
     */
    static void populateCommonTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final TransformationDictionary transformationDictionary, AtomicInteger arityCounter) {
        if (transformationDictionary != null) {
            final Map<String, MethodDeclaration> derivedFieldsMethodMap = getDerivedFieldsMethodMap(transformationDictionary.getDerivedFields(), arityCounter);
            addMapPopulation(derivedFieldsMethodMap, constructorDeclaration.getBody(), "commonTransformationsMap");
        }
    }

    /**
     * Method to generate the code populating the <b>localTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>> inside the constructor
     * @param constructorDeclaration
     * @param localTransformations
     * @param arityCounter
     */
    static void populateLocalTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final LocalTransformations localTransformations, AtomicInteger arityCounter) {
        if (localTransformations != null) {
            final Map<String, MethodDeclaration> derivedFieldsMethodMap = getDerivedFieldsMethodMap(localTransformations.getDerivedFields(), arityCounter);
            addMapPopulation(derivedFieldsMethodMap, constructorDeclaration.getBody(), "localTransformationsMap");
        }
    }


}
