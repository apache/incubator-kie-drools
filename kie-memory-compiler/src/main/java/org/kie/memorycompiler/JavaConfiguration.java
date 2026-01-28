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
package org.kie.memorycompiler;

import java.util.Arrays;

import org.kie.memorycompiler.jdknative.NativeJavaCompiler;

/**
 * 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * 
 * You can also use the system property "drools.dialect.java.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "NATIVE" only.
 * 
 * drools.dialect.java.compiler = <ECLIPSE|NATIVE>
 * drools.dialect.java.compiler.lnglevel = <1.5|...|21>
 * 
 * The default compiler is Eclipse and the default lngLevel is 17.
 * The lngLevel will attempt to autodiscover your system using the 
 * system property "java.version"
 */
public class JavaConfiguration {

    // This should be in alphabetic order to search with BinarySearch
    protected static final String[]  LANGUAGE_LEVELS = new String[]{"1.5", "1.6", "1.7", "1.8", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "9"};

    public static final String JAVA_COMPILER_PROPERTY = "drools.dialect.java.compiler";
    public static final String JAVA_LANG_LEVEL_PROPERTY = "drools.dialect.java.compiler.lnglevel";

    public enum CompilerType {
        ECLIPSE("org.drools.ecj.EclipseJavaCompiler"),
        NATIVE(NativeJavaCompiler.class);

        private final String implClassName;
        private Class<?> implClass;

        CompilerType( String className ) {
            this.implClassName = className;
        }

        CompilerType( Class<?> implClass ) {
            this.implClassName = implClass.getCanonicalName();
            this.implClass = implClass;
        }

        public Class<?> getImplClass() {
            if (implClass == null) {
                try {
                    implClass = Class.forName( implClassName );
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException( e );
                }
            }
            return implClass;
        }

        public String getImplClassName() {
            return implClassName;
        }
    }

    private String                      languageLevel;

    private CompilerType                compiler;

    public static String findJavaVersion() {
        return findJavaVersion( System.getProperty( JAVA_LANG_LEVEL_PROPERTY, System.getProperty("java.version") ) );
    }

    public static String findJavaVersion(String level) {
        String normalized = normalizeVersion(level);
        if (Arrays.binarySearch(LANGUAGE_LEVELS, normalized) >= 0) {
            return normalized;
        }
        return "17"; // default
    }

    private static String normalizeVersion(String version) {
        String[] parts = version.split("\\.");
        if ("1".equals(parts[0]) && parts.length > 1) {
            return "1." + parts[1]; // Legacy format: 1.8.0_292 -> 1.8
        }
        return parts[0]; // Modern format: 21.0.2 -> 21
    }

    public String getJavaLanguageLevel() {
        return this.languageLevel;
    }

    /**
     * You cannot set language level below 1.5, as we need static imports. 17 is now the default.
     * @param languageLevel
     */
    public void setJavaLanguageLevel(final String languageLevel) {
        if ( Arrays.binarySearch( LANGUAGE_LEVELS, languageLevel ) < 0 ) {
            throw new RuntimeException( "value '" + languageLevel + "' is not a valid language level" );
        }
        this.languageLevel = languageLevel;
    }

    /** 
     * Set the compiler to be used when building the rules semantic code blocks.
     * This overrides the default, and even what was set as a system property. 
     */
    public void setCompiler(final CompilerType compiler) {
        switch ( compiler ) {
            case ECLIPSE :
                this.compiler = CompilerType.ECLIPSE;
                break;
            case NATIVE :
                this.compiler = CompilerType.NATIVE;
                break;
            default :
                throw new RuntimeException( "value '" + compiler + "' is not a valid compiler" );
        }
    }

    public CompilerType getCompiler() {
        return this.compiler;
    }
}
