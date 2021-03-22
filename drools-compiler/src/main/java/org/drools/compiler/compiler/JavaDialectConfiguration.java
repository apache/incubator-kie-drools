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

package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaDialectConfiguration extends JavaConfiguration implements DialectConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JavaDialectConfiguration.class);

    private static final JavaDialectConfiguration DEFAULT_JAVA_CONFIGURATION = new JavaDialectConfiguration(new KnowledgeBuilderConfigurationImpl(JavaConfiguration.class.getClassLoader()));
    private static final String DEFAULT_JAVA_VERSION = findJavaVersion(DEFAULT_JAVA_CONFIGURATION.conf.getChainedProperties().getProperty(JAVA_LANG_LEVEL_PROPERTY, System.getProperty("java.version")));

    private KnowledgeBuilderConfigurationImpl conf;

    public JavaDialectConfiguration() { }

    public JavaDialectConfiguration(KnowledgeBuilderConfigurationImpl conf) {
        init( conf );
    }

    public static JavaCompiler createDefaultCompiler() {
        return JavaCompilerFactory.loadCompiler(DEFAULT_JAVA_CONFIGURATION.getCompiler(), DEFAULT_JAVA_VERSION, "src/main/java/");
    }

    public static JavaCompiler createNativeCompiler() {
        return JavaCompilerFactory.loadCompiler(CompilerType.NATIVE, DEFAULT_JAVA_VERSION, "src/main/java/");
    }

    public static JavaCompiler createEclipseCompiler() {
        return JavaCompilerFactory.loadCompiler(CompilerType.ECLIPSE, DEFAULT_JAVA_VERSION, "src/main/java/");
    }

    @Override
    public void init(final KnowledgeBuilderConfigurationImpl conf) {
        this.conf = conf;

        setCompiler( getDefaultCompiler() );

        setJavaLanguageLevel( findJavaVersion(conf.getChainedProperties().getProperty(JAVA_LANG_LEVEL_PROPERTY, System.getProperty("java.version"))) );
    }

    @Override
    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration() {
        return this.conf;
    }

    @Override
    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new Dialect.DummyDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
    }

    public boolean hasEclipseCompiler() {
        try {
            Class.forName( CompilerType.ECLIPSE.getImplClassName(), true, this.conf.getClassLoader() );
            return true;
        } catch ( ClassNotFoundException e ) {
            return false;
        }
    }

    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    private CompilerType getDefaultCompiler() {
        try {
            final String prop = this.conf.getChainedProperties().getProperty( JAVA_COMPILER_PROPERTY, hasEclipseCompiler() ? "ECLIPSE" : "NATIVE" );
            if (logger.isDebugEnabled()) {
                logger.debug( "Selected compiler " + prop + " [drools.dialect.java.compiler:" +
                        this.conf.getChainedProperties().getProperty( JAVA_COMPILER_PROPERTY, null ) + ", hasEclipseCompiler:" + hasEclipseCompiler() + "]" );
            }

            if ( prop.equalsIgnoreCase( "NATIVE" ) ) {
                return CompilerType.NATIVE;
            } else if ( prop.equalsIgnoreCase( "ECLIPSE" ) ) {
                return CompilerType.ECLIPSE;
            } else {
                logger.error( "Drools config: unable to use the drools.compiler property. Using default. It was set to:" + prop );
                return CompilerType.ECLIPSE;
            }
        } catch ( final SecurityException e ) {
            logger.error( "Drools config: unable to read the drools.compiler property. Using default.", e);
            return CompilerType.ECLIPSE;
        }
    }
}
