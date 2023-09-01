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
package org.kie.memorycompiler;

import java.util.Optional;

/**
 * Creates JavaCompilers
 */
public class JavaCompilerFactory {

    public static JavaCompiler loadCompiler( JavaConfiguration configuration) {
        return loadCompiler( configuration.getCompiler(), configuration.getJavaLanguageLevel() );
    }

    public static JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel ) {
        return loadCompiler( compilerType, lngLevel, "" );
    }

    public static JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel, String sourceFolder ) {
        JavaCompiler compiler = createCompiler( compilerType ).orElseThrow( () -> new RuntimeException("Instance of " + compilerType + " compiler cannot be created!") );
        compiler.setJavaCompilerSettings( createSettings( compiler, lngLevel ) );
        compiler.setSourceFolder(sourceFolder);
        return compiler;
    }

    private static JavaCompilerSettings createSettings( JavaCompiler compiler, String lngLevel ) {
        JavaCompilerSettings settings = compiler.createDefaultSettings();
        settings.setTargetVersion( lngLevel );
        settings.setSourceVersion( lngLevel );
        return settings;
    }

    private static Optional<JavaCompiler> createCompiler( JavaConfiguration.CompilerType compilerType) {
        return createCompiler(compilerType.getImplClass());
    }

    private static Optional<JavaCompiler> createCompiler(Class compilerClass) {
        try {
            return Optional.of( (JavaCompiler) compilerClass.getConstructor().newInstance() );
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
