/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;

/* The diff produced while using equalToIgnoringWhiteSpace is abysmal but is correct, while the one
 * produced by JavaParser's equals is better but it fails also on identical ASTs.
 * By using this method to verify produced classes we've got the best of two worlds
 */
public class MaterializedLambdaTestUtils {


    public static void verifyCreatedClass(CreatedClass aClass, String expectedResult) {
        try {
            MatcherAssert.assertThat(aClass.getCompilationUnitAsString(), equalToIgnoringWhiteSpace(expectedResult));
        } catch (AssertionError e) {
            assertEquals(StaticJavaParser.parse(expectedResult), aClass.getCompilationUnit());
        }
    }

    public static void verifyCreatedClass(MethodDeclaration expected, MethodDeclaration actual) {
        try {
            MatcherAssert.assertThat(actual.toString(), equalToIgnoringWhiteSpace(expected.toString()));
        } catch (AssertionError e) {
            MatcherAssert.assertThat(actual, equalTo(expected));
        }
    }
}
