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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaParserUtilsTest {

    private static final String TEMPLATE_FILE = "Template.tmpl";
    private static final String TEMPLATE_CLASS = "Template";
    private static final String NOT_PARSABLE_FILE = "Unparsable.tmpl";


    @Test
    public void getFromFileName() {
        CompilationUnit retrieved = JavaParserUtils.getFromFileName(TEMPLATE_FILE);
        assertNotNull(retrieved);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getFromFileNameNotParsable() {
        JavaParserUtils.getFromFileName(NOT_PARSABLE_FILE);
    }

    @Test(expected = AssertionError.class)
    public void getFromFileNameNotExisting() {
        JavaParserUtils.getFromFileName("not_existing");
    }

    @Test
    public void getKiePMMLModelCompilationUnitWithPackage() {
        String className = "ClassName";
        String packageName = "apackage";
        CompilationUnit retrieved = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,  TEMPLATE_FILE, TEMPLATE_CLASS);
        assertNotNull(retrieved);
        assertTrue(retrieved.getPackageDeclaration().isPresent());
        assertEquals(packageName, retrieved.getPackageDeclaration().get().getName().asString());
        assertFalse(retrieved.getClassByName(TEMPLATE_CLASS).isPresent());
        assertTrue(retrieved.getClassByName(className).isPresent());
    }

    @Test
    public void getKiePMMLModelCompilationUnitWithoutPackage() {
        String className = "ClassName";
        CompilationUnit retrieved = JavaParserUtils.getKiePMMLModelCompilationUnit(className, null,  TEMPLATE_FILE, TEMPLATE_CLASS);
        assertNotNull(retrieved);
        assertFalse(retrieved.getPackageDeclaration().isPresent());
        assertFalse(retrieved.getClassByName(TEMPLATE_CLASS).isPresent());
        assertTrue(retrieved.getClassByName(className).isPresent());
    }

    @Test
    public void getFullClassName() {
        String className = "ClassName";
        String packageName = "apackage";
        PackageDeclaration packageDeclaration = new PackageDeclaration();
        packageDeclaration.setName(new Name(packageName));
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration.setName(className);
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(packageDeclaration);
        compilationUnit.setTypes(NodeList.nodeList(classOrInterfaceDeclaration));
        String retrieved = JavaParserUtils.getFullClassName(compilationUnit);
        String expected = packageName + "." + className;
        assertEquals(expected, retrieved);

    }
}