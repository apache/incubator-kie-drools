/*
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
package org.drools.ecj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.kie.memorycompiler.resources.MemoryResourceStore;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("don't test EclipseJavaCompiler now")
@EnabledForJreRange(min = JRE.JAVA_21)
class Jdk21SyntaxTest {

    private static final String JDK21_TYPE_SWITCH_CLASS = """
            package org.kie.memorycompiler;
            
            public class ExampleClass {
            
                public String typeSwitch(Object obj) {
                    return switch (obj) {
                      case Integer i -> "int: " + i;
                      case String s  -> "string: " + s;
                      default        -> "other";
                    };
                }
            }
            """;

    @Test
    void jdk21typeSwitch() throws Exception {
        String fileStr = "org/kie/memorycompiler/ExampleClass.java";
        List<String> classes = new ArrayList<>();
        classes.add(fileStr);

        MemoryResourceReader reader = new MemoryResourceReader();
        MemoryResourceStore store = new MemoryResourceStore();

        reader.add(fileStr, JDK21_TYPE_SWITCH_CLASS.getBytes());

        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion("21");
        settings.setTargetVersion("21");
        EclipseJavaCompiler compiler = new EclipseJavaCompiler(settings, "");
        CompilationResult res = compiler.compile(classes.toArray(new String[classes.size()]), reader, store);
        assertThat(res.getErrors()).isEmpty();

        byte[] byteCode = store.getResources().values().iterator().next();
        KieMemoryCompiler.MemoryCompilerClassLoader kieMemoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(this.getClass().getClassLoader());
        String className = "org.kie.memorycompiler.ExampleClass";
        kieMemoryCompilerClassLoader.addCode(className, byteCode);
        Class<?> exampleClazz = kieMemoryCompilerClassLoader.loadClass(className);
        Object instance = exampleClazz.getDeclaredConstructors()[0].newInstance();
        Method typeSwitchMethod = exampleClazz.getMethod("typeSwitch", Object.class);
        Object result = typeSwitchMethod.invoke(instance, 51);
        assertThat(result).isEqualTo("int: 51");
    }
}
