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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;

import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMapPopulation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.populateMethodDeclarations;
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
     * Add <b>common</b> and <b>local</b> local transformations management inside the given <code>ClassOrInterfaceDeclaration</code>
     * @param toPopulate
     * @param transformationDictionary
     * @param localTransformations
     */
    public static void addTransformationsInClassOrInterfaceDeclaration(final ClassOrInterfaceDeclaration toPopulate, final TransformationDictionary transformationDictionary, final LocalTransformations localTransformations) {
        final AtomicInteger arityCounter = new AtomicInteger(0);
        final Map<String, MethodDeclaration> commonDerivedFieldsMethodMap = (transformationDictionary != null && transformationDictionary.getDerivedFields() != null) ? getDerivedFieldsMethodMap(transformationDictionary.getDerivedFields(), arityCounter) : Collections.emptyMap();
        final Map<String, MethodDeclaration> localDerivedFieldsMethodMap = (localTransformations != null && localTransformations.getDerivedFields() != null) ? getDerivedFieldsMethodMap(localTransformations.getDerivedFields(), arityCounter) : Collections.emptyMap();
        populateMethodDeclarations(toPopulate, commonDerivedFieldsMethodMap.values());
        populateMethodDeclarations(toPopulate, localDerivedFieldsMethodMap.values());
        final ConstructorDeclaration constructorDeclaration = toPopulate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", toPopulate.getName())));
        populateTransformationsInConstructor(constructorDeclaration, commonDerivedFieldsMethodMap, localDerivedFieldsMethodMap);
    }

    /**
     * Populating the <b>commonTransformationsMap</b> and <b>localTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>>s inside the constructor
     * @param constructorDeclaration
     * @param commonDerivedFieldsMethodMap
     * @param localDerivedFieldsMethodMap
     */
    static void populateTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final Map<String, MethodDeclaration> commonDerivedFieldsMethodMap, final Map<String, MethodDeclaration> localDerivedFieldsMethodMap) {
        addMapPopulation(commonDerivedFieldsMethodMap, constructorDeclaration.getBody(), "commonTransformationsMap");
        addMapPopulation(localDerivedFieldsMethodMap, constructorDeclaration.getBody(), "localTransformationsMap");
    }
}
