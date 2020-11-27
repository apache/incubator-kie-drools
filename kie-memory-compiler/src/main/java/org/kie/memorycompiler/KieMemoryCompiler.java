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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.kie.memorycompiler.jdknative.NativeJavaCompiler;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.kie.memorycompiler.resources.MemoryResourceStore;

public class KieMemoryCompiler {

    private KieMemoryCompiler() { }

    /**
     * Compile the given sources and add compiled classes to the given <code>ClassLoader</code>
     * <b>classNameSourceMap</b>' key must be the <b>FQDN</b> of the class to compile
     *
     * @param classNameSourceMap
     * @param classLoader
     * @return
     */
    public static Map<String, Class<?>> compile(Map<String, String> classNameSourceMap, ClassLoader classLoader) {
        MemoryResourceReader reader = new MemoryResourceReader();
        MemoryResourceStore store = new MemoryResourceStore();
        String[] classNames = new String[classNameSourceMap.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : classNameSourceMap.entrySet()) {
            classNames[i] = toJavaSource( entry.getKey() );
            reader.add( classNames[i], entry.getValue().getBytes());
            i++;
        }

        NativeJavaCompiler compiler = new NativeJavaCompiler();
        CompilationResult res = compiler.compile( classNames, reader, store, classLoader );

        if (res.getErrors().length > 0) {
            throw new KieMemoryCompilerException(Arrays.toString( res.getErrors() ));
        }

        MemoryCompilerClassLoader kieMemoryCompilerClassLoader = new MemoryCompilerClassLoader(classLoader);

        Map<String, Class<?>> toReturn = new HashMap<>();
        for (String className : classNameSourceMap.keySet()) {
            byte[] bytes = store.read( toClassSource( className ) );
            kieMemoryCompilerClassLoader.addCode( className, bytes );
            try {
                toReturn.put(className, kieMemoryCompilerClassLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
        return toReturn;
    }

    private static String toJavaSource( String s ) {
        return s.replace( '.', '/' ) + ".java";
    }

    private static String toClassSource( String s ) {
        return s.replace( '.', '/' ) + ".class";
    }

    public static class MemoryCompilerClassLoader extends ClassLoader {

        private Map<String, byte[]> customCompiledCode = new HashMap<>();

        public MemoryCompilerClassLoader(ClassLoader parent) {
            super(parent);
        }

        public void addCode(String name, byte[] bytes) {
            customCompiledCode.put(name, bytes);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] byteCode = customCompiledCode.get(name);
            if (byteCode == null) {
                return super.findClass(name);
            }
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
