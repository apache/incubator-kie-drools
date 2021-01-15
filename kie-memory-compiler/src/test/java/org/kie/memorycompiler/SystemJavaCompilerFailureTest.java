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
package org.kie.memorycompiler;

import java.util.Map;

import javax.tools.ToolProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Collections.singletonMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ToolProvider.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class SystemJavaCompilerFailureTest {

    private final static String EXAMPLE_CLASS = "package org.kie.memorycompiler;\n" +
                                                "\n" +
                                                "public class ExampleClass {\n" +
                                                "\n" +
                                                "    public int sum(Integer a, Integer b){\n" +
                                                "        return a + b;\n" +
                                                "    }\n" +
                                                "}";

    @Test(expected = KieMemoryCompilerException.class)
    public void simulateJRE() throws Exception {
        PowerMockito.mockStatic(ToolProvider.class);
        PowerMockito.when(ToolProvider.getSystemJavaCompiler()).thenReturn(null);

        Map<String, String> source = singletonMap("org.kie.memorycompiler.ExampleClass", EXAMPLE_CLASS);
        KieMemoryCompiler.compile(source, this.getClass().getClassLoader());
    }
}
