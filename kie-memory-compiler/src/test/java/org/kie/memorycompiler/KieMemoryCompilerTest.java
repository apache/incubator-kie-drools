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

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KieMemoryCompilerTest {

    private final static String EXAMPLE_CLASS =
            "package org.kie.memorycompiler;\n" +
            "\n" +
            "public class ExampleClass {\n" +
            "\n" +
            "    public int sum(Integer a, Integer b){\n" +
            "        return a + b;\n" +
            "    }\n" +
            "}";

    @Test
    public void compileAndLoadClass() throws Exception {
        Map<String, String> source = singletonMap("org.kie.memorycompiler.ExampleClass", EXAMPLE_CLASS);
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(source, this.getClass().getClassLoader());

        Class<?> exampleClazz = compiled.get("org.kie.memorycompiler.ExampleClass");
        assertThat(exampleClazz, is(notNullValue()));

        Object instance = exampleClazz.getDeclaredConstructors()[0].newInstance();
        Method sumMethod = exampleClazz.getMethod("sum", Integer.class, Integer.class);
        Object result = sumMethod.invoke(instance, 2, 3);
        assertThat(result, is(5));
    }

    @Test(expected = KieMemoryCompilerException.class)
    public void invalidClass() {
        Map<String, String> source = singletonMap("org.kie.memorycompiler.InvalidJavaClass", "Invalid Java Code");
        KieMemoryCompiler.compile(source, this.getClass().getClassLoader());
    }

    private final static String WARNING_CLASS =
            "package org.kie.memorycompiler;\n" +
            "\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class WarningClass {\n" +
            "\n" +
            "    private List<String> warningField;\n" +
            "\n" +
            "    public void setWarningField(Object warningField) {\n" +
            "        this.warningField = (List<String>) warningField;\n" +
            "    }\n" +
            "\n" +
            "    public int minus(Integer a, Integer b) {\n" +
            "        return a - b;\n" +
            "    }\n" +
            "\n" +
            "};\n";

    @Test
    public void doNotFailOnWarning() throws Exception {
        Map<String, String> source = singletonMap("org.kie.memorycompiler.WarningClass", WARNING_CLASS);
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(source, this.getClass().getClassLoader());

        Class<?> exampleClazz = compiled.get("org.kie.memorycompiler.WarningClass");
        assertThat(exampleClazz, is(notNullValue()));

        Object instance = exampleClazz.getDeclaredConstructors()[0].newInstance();
        Method minusMethod = exampleClazz.getMethod("minus", Integer.class, Integer.class);
        Object result = minusMethod.invoke(instance, 8, 4);
        assertThat(result, is(4));
    }

    private final static String EXAMPLE_INNER_CLASS =
            "package org.kie.memorycompiler;\n" +
            "\n" +
            "public class ExampleClass {\n" +
            "\n" +
            "    public int sum(Integer a, Integer b){\n" +
            "        return a + b;\n" +
            "    }\n" +
            "\n" +
            "    public static class InnerClass { }\n" +
            "}";

    @Test
    public void compileInnerClass() throws Exception {
        Map<String, String> source = singletonMap("org.kie.memorycompiler.ExampleClass", EXAMPLE_INNER_CLASS);
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(source, this.getClass().getClassLoader());

        assertEquals(2, compiled.size());

        assertNotNull(compiled.get("org.kie.memorycompiler.ExampleClass"));
        assertNotNull(compiled.get("org.kie.memorycompiler.ExampleClass$InnerClass"));
    }
}