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

/**
 * This class configures the package compiler. 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * You may also use this class to override the class loader defaults that are otherwise used.
 * Normally you will not need to look at this class, unless you want to override the defaults.
 * 
 * You can also use the system property "drools.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "JANINO" only. 
 */
public class PackageBuilderConfiguration {
    public static final int ECLIPSE  = 0;
    public static final int JANINO   = 1;
    
    /** This will be only setup once. It tries to look for a system property */
    private static final int CONFIGURED_COMPILER   = getDefaultCompiler();
    
    private int             compiler = CONFIGURED_COMPILER;

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

    /** 
     * Set the compiler to be used when building the rules semantic code blocks.
     * This overrides the default, and even what was set as a system property. 
     */
    public void setCompiler(int compiler) {
        switch ( compiler ) {
            case PackageBuilderConfiguration.ECLIPSE :
                this.compiler = PackageBuilderConfiguration.ECLIPSE;
                break;
            case PackageBuilderConfiguration.JANINO :
                this.compiler = PackageBuilderConfiguration.JANINO;
                break;
            default :
                throw new RuntimeDroolsException( "value '" + compiler + "' is not a valid compiler" );
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /** Use this to override the classloader that will be used for the rules. */
    public void setClassLoader(ClassLoader classLoader) {
        if ( classLoader != null ) {
            this.classLoader = classLoader;
        }
    }
    
    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    static int getDefaultCompiler() {
        try {
            String prop = System.getProperty( "drools.compiler", "ECLIPSE" );
            if (prop.equals( "ECLIPSE".intern() )) {
                return ECLIPSE;
            } else if (prop.equals( "JANINO" )) {
                return JANINO;
            } else {
                System.err.println("Drools config: unable to use the drools.compiler property. Using default. It was set to:" + prop);
                return ECLIPSE;
            }
        } catch (SecurityException e) {
            System.err.println("Drools config: unable to read the drools.compiler property. Using default.");
            return ECLIPSE;
        }
    }
}