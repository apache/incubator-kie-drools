package org.drools.compiler;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.drools.RuntimeDroolsException;

public class PackageBuilderConfiguration {
    public static final int ECLIPSE  = 0;
    public static final int JANINO   = 1;

    private int             compiler = JavaCompilerFactory.ECLIPSE;

    private ClassLoader     classLoader;

    public PackageBuilderConfiguration() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ( classLoader == null ) {
            classLoader = this.getClass().getClassLoader();
        }
        this.classLoader = classLoader;
    }

    public int getCompiler() {
        return this.compiler;
    }

    public void setCompiler(int compiler) {
        switch ( compiler ) {
            case PackageBuilderConfiguration.ECLIPSE :
                this.compiler = JavaCompilerFactory.ECLIPSE;
                break;
            case PackageBuilderConfiguration.JANINO :
                this.compiler = JavaCompilerFactory.JANINO;
                break;
            default :
                throw new RuntimeDroolsException( "value '" + compiler + "' is not a valid compiler" );
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        if ( classLoader != null ) {
            this.classLoader = classLoader;
        }
    }
}