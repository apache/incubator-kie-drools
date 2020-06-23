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

import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.TransformationDictionary;

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
        populateCommonTransformationsInConstructor(constructorDeclaration, transformationDictionary);
        populateLocalTransformationsInConstructor(constructorDeclaration, localTransformations);
    }

    /**
     * Method to generate the code populating the <b>commonTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>> inside the constructor
     * @param constructorDeclaration
     * @param transformationDictionary
     */
    static void populateCommonTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final TransformationDictionary transformationDictionary) {

    }

    /**
     * Method to generate the code populating the <b>localTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>> inside the constructor
     * @param constructorDeclaration
     * @param localTransformations
     */
    static void populateLocalTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final LocalTransformations localTransformations) {
        for (DerivedField derivedField : localTransformations.getDerivedFields()) {

        }
    }
}
