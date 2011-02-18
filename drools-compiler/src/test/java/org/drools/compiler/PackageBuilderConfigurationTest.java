package org.drools.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.base.TypeResolver;
import org.drools.io.Resource;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EnabledBuilder;
import org.drools.rule.builder.EngineElementBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.util.ChainedProperties;

public class PackageBuilderConfigurationTest {

    @Before
    public void setUp() throws Exception {
        System.getProperties().remove( "drools.dialect.java.compiler" );
        System.getProperties().remove( "drools.dialect.default" );
    }

    @After
    public void tearDown() throws Exception {
        System.getProperties().remove( "drools.dialect.java.compiler" );
        System.getProperties().remove( "drools.dialect.default" );
    }

    @Test
    public void testIgnoreDefaults() {
        // check standard chained properties, that includes defaults
        ChainedProperties chainedProperties = new ChainedProperties( "packagebuilder.conf",
                                                                     getClass().getClassLoader(),
                                                                     true );
        //System.out.println( chainedProperties.getProperty( "drools.dialect.java.compiler",
        //                                                   null ) );
        assertNotNull( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                      null ) );


        // now check that chained properties can ignore defaults
        chainedProperties = new ChainedProperties( "packagebuilder.conf",
                                                   getClass().getClassLoader(),
                                                   false );
        //System.out.println( chainedProperties.getProperty( "drools.dialect.java.compiler",
        //                                                   null ) );
        assertNull( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                   null ) );


        // now check it can find defaults again.
        chainedProperties = new ChainedProperties( "packagebuilder.conf",
                                                   getClass().getClassLoader(),
                                                   true );
        //System.out.println( chainedProperties.getProperty( "drools.dialect.java.compiler",
        //                                                   null ) );
        assertNotNull( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                      null ) );
    }

    @Test
    public void testSystemProperties() {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        assertEquals( JavaDialectConfiguration.ECLIPSE,
                      javaConf.getCompiler() );

        System.setProperty( "drools.dialect.java.compiler",
                            "JANINO" );
        cfg = new PackageBuilderConfiguration();
        javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        assertEquals( JavaDialectConfiguration.JANINO,
                      javaConf.getCompiler() );

        PackageBuilderConfiguration cfg2 = new PackageBuilderConfiguration();
        JavaDialectConfiguration javaConf2 = (JavaDialectConfiguration) cfg2.getDialectConfiguration( "java" );
        assertEquals( javaConf.getCompiler(),
                      javaConf2.getCompiler() );

        System.setProperty( "drools.dialect.java.compiler",
                            "ECLIPSE" );
        cfg = new PackageBuilderConfiguration();
        javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        assertEquals( JavaDialectConfiguration.ECLIPSE,
                      javaConf.getCompiler() );

        javaConf2.setCompiler( JavaDialectConfiguration.ECLIPSE );
        assertEquals( JavaDialectConfiguration.ECLIPSE,
                      javaConf2.getCompiler() );

        javaConf2.setCompiler( JavaDialectConfiguration.JANINO );
        assertEquals( JavaDialectConfiguration.JANINO,
                      javaConf2.getCompiler() );

        final PackageBuilderConfiguration cfg3 = new PackageBuilderConfiguration();
        JavaDialectConfiguration javaConf3 = (JavaDialectConfiguration) cfg3.getDialectConfiguration( "java" );
        assertEquals( javaConf.getCompiler(),
                      javaConf3.getCompiler() );
    }

    @Test
    public void testProgrammaticProperties() {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        assertTrue( cfg.getDefaultDialect().equals( "java") );

        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.default",
                                "mvel" );
        PackageBuilderConfiguration cfg1 = new PackageBuilderConfiguration( properties );
        assertEquals("mvel", cfg1.getDefaultDialect() );

        final PackageBuilderConfiguration cfg2 = new PackageBuilderConfiguration( properties );
        assertEquals( cfg1.getDefaultDialect().getClass(),
                      cfg2.getDefaultDialect().getClass() );
    }

    @Test
    public void testProgramaticProperties2() {
        JavaDialectConfiguration javaConf =  new JavaDialectConfiguration( );
        javaConf.init(new PackageBuilderConfiguration());
        javaConf.setCompiler( JavaDialectConfiguration.ECLIPSE );
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        cfg.setDialectConfiguration( "java", javaConf );
        JavaDialectConfiguration javaConf2 = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        assertSame( javaConf,
                    javaConf2 );
        assertEquals( JavaDialectConfiguration.ECLIPSE,
                      javaConf2.getCompiler() );

        javaConf =  new JavaDialectConfiguration();
        javaConf.init(new PackageBuilderConfiguration());
        javaConf.setCompiler( JavaDialectConfiguration.JANINO );
        cfg = new PackageBuilderConfiguration();
        cfg.setDialectConfiguration( "java", javaConf );
        javaConf2 = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
        assertSame( javaConf,
                    javaConf2 );
        assertEquals( JavaDialectConfiguration.JANINO,
                      javaConf2.getCompiler() );
    }

    @Test
    public void testMockDialect() {
        Package pkg = new Package( "org.pkg1" );

        PackageBuilderConfiguration cfg1 = new PackageBuilderConfiguration();
        MockDialectConfiguration mockConf = new MockDialectConfiguration();
//        cfg1.buildDialectRegistry().addDialect( "mock",
//                                                mockConf.getDialect() );
        
        cfg1.addDialect("mock", mockConf );
        cfg1.setDefaultDialect( "mock" );

        PackageBuilder builder = new PackageBuilder( pkg,
                                                     cfg1 );

        PackageRegistry pkgRegistry = builder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry dialectRegistry = pkgRegistry.getDialectCompiletimeRegistry();
        MockDialect mockDialect2 = (MockDialect) dialectRegistry.getDialect( cfg1.getDefaultDialect() );

        assertSame( builder,
                    mockDialect2.getPackageBuilder() );
        assertSame( pkg,
                    mockDialect2.getPkg() );
        assertNull( mockDialect2.getRuleDescr() );

        RuleDescr ruleDescr = new RuleDescr( "test rule" );
        ruleDescr.addAttribute( new AttributeDescr( "dialect",
                                                    "mock" ) );
        ruleDescr.setLhs( new AndDescr() );
        EvalDescr evalDescr = new EvalDescr();
        ruleDescr.getLhs().addDescr( evalDescr );

        PackageDescr pkgDescr = new PackageDescr( "org.pkg1" );
        pkgDescr.addImport( new ImportDescr( "java.util.HashMap" ) );
        FunctionImportDescr functionImportDescr = new FunctionImportDescr();
        functionImportDescr.setTarget( "java.lang.System.currentTimeMillis" );
        pkgDescr.addFunctionImport( functionImportDescr );

        pkgDescr.addRule( ruleDescr );

        builder.addPackage( pkgDescr );

        assertSame( ruleDescr,
                    mockDialect2.getRuleDescr() );
        assertTrue( mockDialect2.getImport().contains( "java.util.HashMap" ) );
        assertTrue( mockDialect2.getStaticImport().contains( "java.lang.System.currentTimeMillis" ) );
        assertEquals( "eval was built",
                      evalDescr.getContent() );
        assertEquals( "consequence was built",
                      ruleDescr.getConsequence() );
        assertTrue( mockDialect2.isCompileAll() );

        assertNotNull( pkg.getRule( "test rule" ) );

        // make sure there were no other general errors.
        assertFalse( builder.hasErrors() );
    }

    public static class MockDialectConfiguration
        implements
        DialectConfiguration {
        private PackageBuilderConfiguration conf;

        public Dialect newDialect(PackageBuilder pkgBuilder, PackageRegistry pkgRegistry, Package pkg) {
            return new MockDialect( pkgBuilder, pkgRegistry, pkg);
        }

        public PackageBuilderConfiguration getPackageBuilderConfiguration() {
            return this.conf;
        }

        public void init(PackageBuilderConfiguration configuration) {
            this.conf = configuration;
        }

    }

    public static class MockDialect
        implements
        Dialect {
        private PackageBuilder builder;
        private Package        pkg;
        private RuleDescr      ruleDescr;
        private Rule           rule;

        private List           imports       = new ArrayList();
        private List           staticImports = new ArrayList();

        private boolean        compileAll    = false;
        
        public MockDialect(PackageBuilder builder, PackageRegistry pkgRegistry, Package pkg) {
            this.builder = builder;
            this.pkg = pkg;
        }

        public void init(RuleDescr ruleDescr) {
            this.ruleDescr = ruleDescr;
        }

        public PackageBuilder getPackageBuilder() {
            return builder;
        }

        public Package getPkg() {
            return pkg;
        }

        public RuleDescr getRuleDescr() {
            return ruleDescr;
        }

        public void addFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver) {
            // TODO Auto-generated method stub

        }

        public void addImport(String importEntry) {
            this.imports.add( importEntry );
        }

        public List getImport() {
            return this.imports;
        }

        public void addStaticImport(String importEntry) {
            this.imports.add( importEntry );
        }

        public List getStaticImport() {
            return this.imports;
        }

        public void addRule(RuleBuildContext context) {
            this.rule = context.getRule();
        }
        
        public Rule getRule() {
            return this.rule;
        }

        public void compileAll() {
            this.compileAll = true;
        }

        public boolean isCompileAll() {
            return this.compileAll;
        }

        public AccumulateBuilder getAccumulateBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public RuleConditionBuilder getBuilder(Class clazz) {
            if ( clazz == EvalDescr.class ) {
                return getEvalBuilder();
            } else if ( clazz == AndDescr.class ) {
                return new GroupElementBuilder();
            } else {
                throw new RuntimeException( "clazz " + clazz + " is not yet configured " );
            }
        }

        public Map getBuilders() {
            return null;
        }
        public ConsequenceBuilder getConsequenceBuilder() {
            return new MockConsequenceBuilder();
        }

        public RuleConditionBuilder getEvalBuilder() {
            return new MockEvalBuilder();
        }

        public String getExpressionDialectName() {
            return null;
        }

        public FromBuilder getFromBuilder() {
            return null;
        }

        public PatternBuilder getPatternBuilder() {
            return null;
        }

        public PredicateBuilder getPredicateBuilder() {
            return null;
        }

        public QueryBuilder getQueryBuilder() {
            return null;
        }

        public List getResults() {
            return null;
        }

        public ReturnValueBuilder getReturnValueBuilder() {
            return null;
        }

        public RuleClassBuilder getRuleClassBuilder() {
            return null;
        }
        
        public SalienceBuilder getSalienceBuilder() {
            return null;
        }

        public TypeResolver getTypeResolver() {
            return null;
        }
        
        public String getId() {
            return "mock";
        }

        public void init(ProcessDescr processDescr) {
            // TODO Auto-generated method stub
            
        }

        public EntryPointBuilder getEntryPointBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public void postCompileAddFunction(FunctionDescr functionDescr,
        		TypeResolver typeResolver) {
        	// TODO Auto-generated method stub

        }

        public void preCompileAddFunction(FunctionDescr functionDescr,
                                          TypeResolver typeResolver) {
            // TODO Auto-generated method stub
            
        }

        public EnabledBuilder getEnabledBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public void addFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver,
                                Resource resource) {
            // TODO Auto-generated method stub
            
        }

        public AnalysisResult analyzeExpression(PackageBuildContext context,
                                                BaseDescr descr,
                                                Object content,
                                                BoundIdentifiers availableIdentifiers) {
            // TODO Auto-generated method stub
            return null;
        }

        public AnalysisResult analyzeBlock(PackageBuildContext context,
                                           BaseDescr descr,
                                           String text,
                                           BoundIdentifiers availableIdentifiers) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class MockEvalBuilder
        implements
        RuleConditionBuilder {

        public RuleConditionElement build(RuleBuildContext context,
                                          BaseDescr descr) {
            EvalDescr evalDescr = (EvalDescr) descr;
            evalDescr.setContent( "eval was built" );
            return null;
        }

        public RuleConditionElement build(RuleBuildContext context,
                                          BaseDescr descr,
                                          Pattern prefixPattern) {
            return null;
        }

    }

    public static class MockConsequenceBuilder
        implements
        ConsequenceBuilder {

        public void build(RuleBuildContext context, String name) {
            context.getRuleDescr().setConsequence( "consequence was built" );
        }

    }
}
