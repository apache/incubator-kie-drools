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
package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;

import static com.github.javaparser.StaticJavaParser.parseResource;
import static org.drools.model.codegen.execmodel.util.lambdareplace.MaterializedLambdaTestUtils.verifyCreatedClass;

public class ExecModelLambdaPostProcessorTest {

    @Before
    public void configJP() {
        StaticJavaParser.getConfiguration().setCharacterEncoding(Charset.defaultCharset());
    }

    @Test
    public void convertPatternLambda() throws Exception {

        CompilationUnit inputCU = parseResource("org/drools/model/codegen/execmodel/util/lambdareplace/PatternTestHarness.java");
        CompilationUnit clone = inputCU.clone();

        new ExecModelLambdaPostProcessor("mypackage", "rulename", new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), clone, true).convertLambdas();

        String PATTERN_HARNESS = "PatternTestHarness";
        MethodDeclaration expectedResult = getMethodChangingName(inputCU, PATTERN_HARNESS, "expectedOutput");
        MethodDeclaration actual = getMethodChangingName(clone, PATTERN_HARNESS, "inputMethod");

        verifyCreatedClass(expectedResult, actual);
    }

    private MethodDeclaration getMethodChangingName(CompilationUnit inputCU, String className, String methodName) {
        return inputCU.getClassByName(className)
                .map(c -> c.getMethodsByName(methodName))
                .flatMap(methods -> methods.stream().findFirst())
                .map(m -> m.setName("testMethod"))
                .orElseThrow(RuntimeException::new);
    }
}