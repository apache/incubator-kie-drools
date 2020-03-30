package org.kie.memorycompiler;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class KieMemoryCompilerTest {

    private final static String EXAMPLE_CLASS = "package org.kie.memorycompiler;\n" +
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
}