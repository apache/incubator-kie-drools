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
package org.drools.mvel.compiler.kie.builder.impl;

import java.net.URL;
import java.util.stream.Stream;

import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ClasspathKieProjectTransformUrlToFileSystemPathTest {

    public static Stream<Arguments> parameters() throws Exception {
    	
    	return Stream.of(arguments(new URL("file:/some-path-to-the-module/target/test-classes"), "/some-path-to-the-module/target/test-classes"),
    			arguments(new URL("file:/some-path-to-the-module/target/test-classes/META-INF/kmodule.xml"), "/some-path-to-the-module/target/test-classes"),
    			arguments(new URL("jar:file:/C:/proj/parser/jar/parser.jar!/test.xml"), "/C:/proj/parser/jar/parser.jar"));
    }


    @ParameterizedTest(name = "URL={0}, expectedPath={1}")
    @MethodSource("parameters")
    public void testTransformUrl(URL url, String expectedPath) {
        String actualPath = ClasspathKieProject.fixURLFromKProjectPath(url);
        assertThat(actualPath).isEqualTo(expectedPath);
    }
}
