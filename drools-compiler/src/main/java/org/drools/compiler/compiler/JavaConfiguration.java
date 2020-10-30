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

package org.drools.compiler.compiler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.EngineElementBuilder;
import org.drools.compiler.rule.builder.EntryPointBuilder;
import org.drools.compiler.rule.builder.FromBuilder;
import org.drools.compiler.rule.builder.GroupElementBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.PredicateBuilder;
import org.drools.compiler.rule.builder.QueryBuilder;
import org.drools.compiler.rule.builder.ReturnValueBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleClassBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.addon.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.utils.ChainedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

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
 * is in the classpath, using ClassLoader.loadClass(String). If you intented to
 * just Janino sa the compiler you must either overload the compiler property before 
 * instantiating this class or the PackageBuilder, or make sure Eclipse is in the 
 * classpath, as Eclipse is the default.
 */
public class JavaConfiguration
    implements
    DialectConfiguration {

    protected static final transient Logger logger = LoggerFactory.getLogger( JavaConfiguration.class);

    public static final String JAVA_COMPILER_PROPERTY = "drools.dialect.java.compiler";
    public static final String JAVA_LANG_LEVEL_PROPERTY = "drools.dialect.java.compiler.lnglevel";

    public enum CompilerType {
        ECLIPSE, JANINO, NATIVE
    }

    // This should be in alphabetic order to search with BinarySearch
    protected static final String[]  LANGUAGE_LEVELS = new String[]{"1.5", "1.6", "1.7", "1.8", "10", "11", "12", "9"};

    private String                      languageLevel;

    private KnowledgeBuilderConfigurationImpl conf;

    private CompilerType                compiler;

    public JavaConfiguration() {
    }

    public void init(final KnowledgeBuilderConfigurationImpl conf) {
        this.conf = conf;

        setCompiler( getDefaultCompiler() );
        
        setJavaLanguageLevel( findJavaVersion(conf.getChainedProperties()) );
    }

    public static String findJavaVersion( ChainedProperties chainedProperties) {
        String level = chainedProperties.getProperty(JAVA_LANG_LEVEL_PROPERTY,
                System.getProperty("java.version"));

        if ( level.startsWith( "1.5" ) ) {
            return "1.5";
        } else if ( level.startsWith( "1.6" ) ) {
            return "1.6";
        } else if ( level.startsWith( "1.7" ) ) {
            return "1.7";
        } else if ( level.startsWith( "1.8" ) ) {
            return "1.8";
        } else if ( level.startsWith( "9" ) ) {
            return "9";
        } else if ( level.startsWith( "10" ) ) {
            return "10";
        } else if ( level.startsWith( "11" ) ) {
            return "11";
        } else if ( level.startsWith( "12" ) ) {
            return "11";
        } else {
            return "1.8";
        }
    }

    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration() {
        return this.conf;
    }

    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new DummyDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
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
    public void setCompiler(final CompilerType compiler) {
        // check that the jar for the specified compiler are present
        if ( compiler == CompilerType.ECLIPSE ) {
            try {
                Class.forName( "org.eclipse.jdt.internal.compiler.Compiler", true, this.conf.getClassLoader() );
            } catch ( ClassNotFoundException e ) {
                throw new RuntimeException( "The Eclipse JDT Core jar is not in the classpath" );
            }
        } else if ( compiler == CompilerType.JANINO ){
            try {
                Class.forName( "org.codehaus.janino.Parser", true, this.conf.getClassLoader() );
            } catch ( ClassNotFoundException e ) {
                throw new RuntimeException( "The Janino jar is not in the classpath" );
            }
        }
        
        switch ( compiler ) {
            case ECLIPSE :
                this.compiler = CompilerType.ECLIPSE;
                break;
            case JANINO :
                this.compiler = CompilerType.JANINO;
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

    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    private CompilerType getDefaultCompiler() {
        try {
            final String prop = this.conf.getChainedProperties().getProperty( JAVA_COMPILER_PROPERTY,
                                                                              "ECLIPSE" );
            if ( prop.equals( "NATIVE" ) ) {
                return CompilerType.NATIVE;
            } else if ( prop.equals( "ECLIPSE" ) ) {
                return CompilerType.ECLIPSE;
            } else if ( prop.equals( "JANINO" ) ) {
                return CompilerType.JANINO;
            } else {
                logger.error( "Drools config: unable to use the drools.compiler property. Using default. It was set to:" + prop );
                return CompilerType.ECLIPSE;
            }
        } catch ( final SecurityException e ) {
            logger.error( "Drools config: unable to read the drools.compiler property. Using default.", e);
            return CompilerType.ECLIPSE;
        }
    }

    public static class DummyDialect implements Dialect {

        public static final String ID = "java";

        private final InternalKnowledgePackage pkg;
        private final ClassLoader rootClassLoader;
        private final KnowledgeBuilderConfigurationImpl pkgConf;
        private final PackageRegistry packageRegistry;

        DummyDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
            this.rootClassLoader = rootClassLoader;
            this.pkgConf = pkgConf;
            this.pkg = pkg;
            this.packageRegistry = pkgRegistry;

            JavaDialectRuntimeData data = (JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData(ID);

            // initialise the dialect runtime data if it doesn't already exist
            if (data == null) {
                data = new JavaDialectRuntimeData();
                this.pkg.getDialectRuntimeRegistry().setDialectData(ID, data);
                data.onAdd(this.pkg.getDialectRuntimeRegistry(), rootClassLoader);
            } else {
                data = (JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData(ID);
            }
        }

        @Override
        public String getId() {
            return ID;
        }

        @Override
        public PackageRegistry getPackageRegistry() {
            return packageRegistry;
        }

        @Override
        public void addImport(ImportDescr importDescr) {
            // we don't need to do anything here
        }

        @Override
        public void addStaticImport(ImportDescr importDescr) {
            // we don't need to do anything here
        }

        @Override
        public void init( RuleDescr ruleDescr ) {
            // we don't need to do anything here
        }

        @Override
        public void init( ProcessDescr processDescr ) {
            // we don't need to do anything here
        }

        @Override
        public EngineElementBuilder getBuilder( Class clazz ) {
            if (clazz == PatternDescr.class) {
                return getPatternBuilder();
            }
            if (clazz == EntryPointDescr.class) {
                return getEntryPointBuilder();
            }
            if (clazz == AndDescr.class || clazz == OrDescr.class || clazz == NotDescr.class || clazz == ExistsDescr.class) {
                return new GroupElementBuilder();
            }
            return throwExceptionForMissingMvel();
        }

        @Override
        public PatternBuilder getPatternBuilder() {
            return new PatternBuilder();
        }

        @Override
        public EntryPointBuilder getEntryPointBuilder() {
            return new EntryPointBuilder();
        }

        @Override
        public TypeResolver getTypeResolver() {
            return this.packageRegistry.getTypeResolver();
        }

        @Override
        public String getExpressionDialectName() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public Map<Class<?>, EngineElementBuilder> getBuilders() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public SalienceBuilder getSalienceBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public EnabledBuilder getEnabledBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public QueryBuilder getQueryBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public RuleConditionBuilder getEvalBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public AccumulateBuilder getAccumulateBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public PredicateBuilder getPredicateBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public ReturnValueBuilder getReturnValueBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public ConsequenceBuilder getConsequenceBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public RuleClassBuilder getRuleClassBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public FromBuilder getFromBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public AnalysisResult analyzeExpression( PackageBuildContext context, BaseDescr descr, Object content, BoundIdentifiers availableIdentifiers ) {
            return throwExceptionForMissingMvel();
        }

        @Override
        public AnalysisResult analyzeBlock( PackageBuildContext context, BaseDescr descr, String text, BoundIdentifiers availableIdentifiers ) {
            return throwExceptionForMissingMvel();
        }

        @Override
        public void compileAll() {
            throwExceptionForMissingMvel();
        }

        @Override
        public void addRule( RuleBuildContext context ) {
            throwExceptionForMissingMvel();
        }

        @Override
        public void addFunction( FunctionDescr functionDescr, TypeResolver typeResolver, Resource resource ) {
            throwExceptionForMissingMvel();
        }

        @Override
        public List<KnowledgeBuilderResult> getResults() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public void clearResults() {
            throwExceptionForMissingMvel();
        }

        @Override
        public void postCompileAddFunction( FunctionDescr functionDescr, TypeResolver typeResolver ) {
            throwExceptionForMissingMvel();
        }

        @Override
        public void preCompileAddFunction( FunctionDescr functionDescr, TypeResolver typeResolver ) {
            throwExceptionForMissingMvel();
        }
    }
}
