/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.java.compiler;


import org.drools.java.compiler.resources.ResourceReader;
import org.drools.java.compiler.resources.ResourceStore;

/**
 * Base class for compiler implementations. Provides just a few
 * convenience methods.
 */
public abstract class AbstractJavaCompiler implements JavaCompiler {

    protected CompilationProblemHandler problemHandler;

    private JavaCompilerSettings javaCompilerSettings;

    public void setCompilationProblemHandler( final CompilationProblemHandler pHandler ) {
        problemHandler = pHandler;
    }

    public CompilationResult compile( final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore ) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        return compile(pClazzNames, pReader, pStore, classLoader, createDefaultSettings());
    }

    public CompilationResult compile( final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader ) {
        return compile(pClazzNames, pReader, pStore, pClassLoader, javaCompilerSettings != null ? javaCompilerSettings : createDefaultSettings());
    }

    public void setJavaCompilerSettings( JavaCompilerSettings javaCompilerSettings ) {
        this.javaCompilerSettings = javaCompilerSettings;
    }
}
