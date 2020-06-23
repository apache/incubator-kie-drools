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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.dmg.pmml.PMML;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelFactoryUtilsTest {

    private static final String SOURCE = "TransformationsSample.pmml";
    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";
    private static PMML pmmlModel;
    private ConstructorDeclaration constructorDeclaration;

    @BeforeClass
    public static void setup() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(SOURCE));
        assertNotNull(pmmlModel);

    }

    @Before
    public void init() {
        CompilationUnit compilationUnit = getFromFileName(TEMPLATE_SOURCE);
        constructorDeclaration = compilationUnit.getClassByName(TEMPLATE_CLASS_NAME)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve ClassOrInterfaceDeclaration " + TEMPLATE_CLASS_NAME + "  from " + TEMPLATE_SOURCE))
                .getDefaultConstructor()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve default constructor from " + TEMPLATE_SOURCE));
        assertNotNull(constructorDeclaration);
    }

    @Test
    public void populateTransformationsInConstructor() {
        assertNotNull(constructorDeclaration);
    }

    @Test
    public void populateCommonTransformationsInConstructor() {
        assertNotNull(constructorDeclaration);
    }

    @Test
    public void populateLocalTransformationsInConstructor() {
        KiePMMLModelFactoryUtils.populateLocalTransformationsInConstructor(constructorDeclaration, pmmlModel.getModels().get(0).getLocalTransformations());
    }
}