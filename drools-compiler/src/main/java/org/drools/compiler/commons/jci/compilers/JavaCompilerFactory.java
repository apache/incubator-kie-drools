/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.commons.jci.compilers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.compiler.compiler.JavaConfiguration;

/**
 * Creates JavaCompilers
 */
public enum JavaCompilerFactory {

    INSTANCE;

    private final Map classCache = new HashMap();
    
    public JavaCompiler loadCompiler( JavaConfiguration configuration) {
        return loadCompiler( configuration.getCompiler(), configuration.getJavaLanguageLevel() );
    }

    public JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel ) {
        return loadCompiler( compilerType, lngLevel, "" );
    }

    public JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel, String sourceFolder ) {
        JavaCompiler compiler = createCompiler( compilerType ).orElseThrow( () -> new RuntimeException("Instance of " + compilerType + " compiler cannot be created!") );
        compiler.setJavaCompilerSettings( createSettings( compiler, compilerType, lngLevel ) );
        compiler.setSourceFolder(sourceFolder);
        return compiler;
    }

    private JavaCompilerSettings createSettings( JavaCompiler compiler, JavaConfiguration.CompilerType compilerType, String lngLevel ) {
        JavaCompilerSettings settings = compiler.createDefaultSettings();
        settings.setTargetVersion( lngLevel );
        // FIXME: the native Java compiler doesn't work with JPMS
        if (compilerType == JavaConfiguration.CompilerType.ECLIPSE || lngLevel.startsWith( "1." )) {
            settings.setSourceVersion( lngLevel );
        }
        return settings;
    }

    private Optional<JavaCompiler> createCompiler( JavaConfiguration.CompilerType compilerType) {
        return createCompiler(compilerType.getImplClassName());
    }

    private Optional<JavaCompiler> createCompiler(String className) {
        Class clazz = (Class) classCache.get(className);

        if (clazz == null) {
            try {
                clazz = Class.forName(className);
                classCache.put(className, clazz);
            } catch (ClassNotFoundException ignored) {
                // Ignored
            }
        }

        if (clazz == null) {
            return null;
        }

        try {
            return Optional.of( (JavaCompiler) clazz.getConstructor().newInstance() );
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
