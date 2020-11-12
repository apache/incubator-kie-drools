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

import org.drools.compiler.compiler.JavaConfiguration;

/**
 * Creates JavaCompilers
 */
public enum JavaCompilerFactory {

    INSTANCE;

    private final Map classCache = new HashMap();
    
    public JavaCompiler createCompiler(JavaConfiguration.CompilerType compilerType) {
        return createCompiler(compilerType.getImplClassName());
    }

    public JavaCompiler createCompiler(String className) {
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
            return (JavaCompiler) clazz.getConstructor().newInstance();
        } catch (Throwable t) {
            return null;
        }
    }

    public JavaCompiler loadCompiler( JavaConfiguration configuration) {
        return loadCompiler( configuration.getCompiler(), configuration.getJavaLanguageLevel() );
    }

    public JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel ) {
        JavaCompiler compiler = createCompiler( compilerType );
        if (compiler == null) {
            throw new RuntimeException("Instance of " + compilerType + " compiler cannot be created!");
        } else {
            updateSettings( compiler.createDefaultSettings(), lngLevel );
        }
        return compiler;
    }

    private JavaCompilerSettings updateSettings( JavaCompilerSettings settings, String lngLevel ) {
        settings.setTargetVersion( lngLevel );
        settings.setSourceVersion( lngLevel );
        return settings;
    }
}
