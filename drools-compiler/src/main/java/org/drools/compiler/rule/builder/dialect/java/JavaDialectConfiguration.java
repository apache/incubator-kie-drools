/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.java;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.builder.dialect.asm.ClassLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.mvel2.asm.Opcodes.V1_5;
import static org.mvel2.asm.Opcodes.V1_6;
import static org.mvel2.asm.Opcodes.V1_7;
import static org.mvel2.asm.Opcodes.V1_8;

/**
 * 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * 
 * You can also use the system property "drools.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "JANINO" only. 
 * 
 * drools.dialect.java.compiler = <ECLIPSE|JANINO>
 * drools.dialect.java.compiler.lnglevel = <1.5|1.6>
 * 
 * The default compiler is Eclipse and the default lngLevel is 1.5.
 * The lngLevel will attempt to autodiscover your system using the 
 * system property "java.version"
 * 
 * The JavaDialectConfiguration will attempt to validate that the specified compiler
 * is in the classpath, using ClassLoader.loasClass(String). If you intented to
 * just Janino sa the compiler you must either overload the compiler property before 
 * instantiating this class or the PackageBuilder, or make sure Eclipse is in the 
 * classpath, as Eclipse is the default.
 */
public class JavaDialectConfiguration
    implements
    DialectConfiguration {

    protected static final transient Logger logger = LoggerFactory.getLogger(JavaDialectConfiguration.class);
    
    public static final String          JAVA_COMPILER_PROPERTY = "drools.dialect.java.compiler";

    public static final int             ECLIPSE         = 0;
    public static final int             JANINO          = 1;
    public static final int             NATIVE          = 2;

    public static final String[]        LANGUAGE_LEVELS = new String[]{"1.5", "1.6", "1.7", "1.8"};

    private String                      languageLevel;

    private KnowledgeBuilderConfigurationImpl conf;

    private int                         compiler;

    public JavaDialectConfiguration() {
    }

    public void init(final KnowledgeBuilderConfigurationImpl conf) {
        this.conf = conf;

        setCompiler( getDefaultCompiler() );
        
        setJavaLanguageLevel( getDefaultLanguageLevel() );
    }

    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration() {
        return this.conf;
    }

    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new JavaDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
    }

    public String getJavaLanguageLevel() {
        return this.languageLevel;
    }

    /**
     * You cannot set language level below 1.5, as we need static imports, 1.5 is now the default.
     * @param languageLevel
     */
    public void setJavaLanguageLevel(final String languageLevel) {
        if ( Arrays.binarySearch( LANGUAGE_LEVELS,
                                  languageLevel ) < 0 ) {
            throw new RuntimeException( "value '" + languageLevel + "' is not a valid language level" );
        }
        this.languageLevel = languageLevel;
    }

    /** 
     * Set the compiler to be used when building the rules semantic code blocks.
     * This overrides the default, and even what was set as a system property. 
     */
    public void setCompiler(final int compiler) {
        // check that the jar for the specified compiler are present
        if ( compiler == ECLIPSE ) {
            try {
                Class.forName( "org.eclipse.jdt.internal.compiler.Compiler", true, this.conf.getClassLoader() );
            } catch ( ClassNotFoundException e ) {
                throw new RuntimeException( "The Eclipse JDT Core jar is not in the classpath" );
            }
        } else if ( compiler == JANINO ){
            try {
                Class.forName( "org.codehaus.janino.Parser", true, this.conf.getClassLoader() );
            } catch ( ClassNotFoundException e ) {
                throw new RuntimeException( "The Janino jar is not in the classpath" );
            }
        }
        
        switch ( compiler ) {
            case ECLIPSE :
                this.compiler = ECLIPSE;
                break;
            case JANINO :
                this.compiler = JANINO;
                break;
            case NATIVE :
                this.compiler = NATIVE;
                break;
            default :
                throw new RuntimeException( "value '" + compiler + "' is not a valid compiler" );
        }
    }

    public int getCompiler() {
        return this.compiler;
    }

    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    private int getDefaultCompiler() {
        try {
            final String prop = this.conf.getChainedProperties().getProperty( JAVA_COMPILER_PROPERTY,
                                                                              "ECLIPSE" );
            if ( prop.equals( "NATIVE" ) ) {
                return NATIVE;
            } else if ( prop.equals( "ECLIPSE" ) ) {
                return ECLIPSE;
            } else if ( prop.equals( "JANINO" ) ) {
                return JANINO;
            } else {
                logger.error( "Drools config: unable to use the drools.compiler property. Using default. It was set to:" + prop );
                return ECLIPSE;
            }
        } catch ( final SecurityException e ) {
            logger.error( "Drools config: unable to read the drools.compiler property. Using default.", e);
            return ECLIPSE;
        }
    }

    private String getDefaultLanguageLevel() {
        switch (ClassLevel.findJavaVersion(this.conf.getChainedProperties())) {
            case V1_5:
                return "1.5";
            case V1_6:
                return "1.6";
            case V1_7:
                return "1.7";
            case V1_8:
                return "1.8";
            default:
                return "1.7";
        }
    }
}
