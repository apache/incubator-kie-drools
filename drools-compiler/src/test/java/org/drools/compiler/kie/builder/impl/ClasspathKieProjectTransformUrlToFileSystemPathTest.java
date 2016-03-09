/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ClasspathKieProjectTransformUrlToFileSystemPathTest {

    @Parameterized.Parameters(name = "URL={0}, expectedPath={1}")
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][] {
                { new URL("file:/some-path-to-the-module/target/test-classes"), "/some-path-to-the-module/target/test-classes" },
                { new URL("file:/some-path-to-the-module/target/test-classes/META-INF/kmodule.xml"), "/some-path-to-the-module/target/test-classes" },
                { new URL("jar:file:/C:/proj/parser/jar/parser.jar!/test.xml"), "/C:/proj/parser/jar/parser.jar" }
        });
    }
    @Parameterized.Parameter(0)
    public URL url;

    @Parameterized.Parameter(1)
    public String expectedPath;


    @Test
    public void testTransformUrl() {
        String actualPath = ClasspathKieProject.fixURLFromKProjectPath(url);
        Assertions.assertThat(actualPath).isEqualTo(expectedPath);
    }
}
