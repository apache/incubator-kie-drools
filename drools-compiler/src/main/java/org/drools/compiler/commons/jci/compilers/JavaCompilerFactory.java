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
import org.drools.core.util.ClassUtils;

/**
 * Creates JavaCompilers
 */
public enum JavaCompilerFactory {

    INSTANCE;

    private final Map classCache = new HashMap();
    
    /**
     * Tries to guess the class name by convention. So for compilers
     * following the naming convention
     * 
     *   org.apache.commons.jci.compilers.SomeJavaCompiler
     *   
     * you can use the short-hands "some"/"Some"/"SOME". Otherwise
     * you have to provide the full class name. The compiler is
     * getting instanciated via (cached) reflection.
     * 
     * @param pHint
     * @return JavaCompiler or null
     */
    public JavaCompiler createCompiler(final String pHint) {
        
        final String className;
        if (pHint.indexOf('.') < 0) {
            className = "org.drools.compiler.commons.jci.compilers." + ClassUtils.toJavaCasing(pHint) + "JavaCompiler";
        } else {
            className = pHint;
        }
        
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
            return (JavaCompiler) clazz.newInstance();
        } catch (Throwable t) {
            return null;
        }
    }

    public JavaCompiler loadCompiler( JavaConfiguration configuration) {
        return loadCompiler( configuration.getCompiler(), configuration.getJavaLanguageLevel() );
    }

    public JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel ) {
        JavaCompiler compiler;
        switch ( compilerType ) {
            case JANINO : {
                compiler = createCompiler( "janino" );
                break;
            }
            case NATIVE : {
                compiler = createCompiler( "native" );
                if (compiler == null) {
                    throw new RuntimeException("Instance of native compiler cannot be created!");
                } else {
                    updateSettings( compiler.createDefaultSettings(), lngLevel );
                }
                break;
            }
            case ECLIPSE :
            default : {
                compiler = createCompiler( "eclipse" );
                if (compiler == null) {
                    throw new RuntimeException("Instance of eclipse compiler cannot be created!");
                } else {
                    updateSettings( compiler.createDefaultSettings(), lngLevel );
                }
                break;
            }
        }
        return compiler;
    }

    private JavaCompilerSettings updateSettings( JavaCompilerSettings settings, String lngLevel ) {
        settings.setTargetVersion( lngLevel );
        settings.setSourceVersion( lngLevel );
        return settings;
    }
}
