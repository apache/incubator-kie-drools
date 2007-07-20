package org.drools.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TypeResolver;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.util.ChainedProperties;

public class PackageBuilderConfigurationTest extends TestCase {

    protected void setUp() throws Exception {
        System.getProperties().remove( "drools.dialect.java.compiler" );
        System.getProperties().remove( "drools.dialect.default" );
    }

    protected void tearDown() throws Exception {
        System.getProperties().remove( "drools.dialect.java.compiler" );
        System.getProperties().remove( "drools.dialect.default" );
    }

    public void testIgnoreDetauls() {
        // check standard chained properties, that includes defaults
        ChainedProperties chainedProperties = new ChainedProperties( null,
                                                                     "packagebuilder.conf",
                                                                     true );
        System.out.println( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                           null ) );
        assertNotNull( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                      null ) );


        // now check that chained properties can ignore defaults
        chainedProperties = new ChainedProperties( null,
                                                   "packagebuilder.conf",
                                                   false );
        System.out.println( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                           null ) );
        assertNull( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                   null ) );


        // now check it can find defaults again.
        chainedProperties = new ChainedProperties( null,
                                                   "packagebuilder.conf",
                                                   true );
        System.out.println( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                           null ) );
        assertNotNull( chainedProperties.getProperty( "drools.dialect.java.compiler",
                                                      null ) );
    }

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

    public void testProgrammaticProperties() {
        PackageBuilderConfiguration cfg = new PackageBuilderConfiguration();
        assertTrue( cfg.getDefaultDialect() instanceof JavaDialect );

        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.default",
                                "mvel" );
        PackageBuilderConfiguration cfg1 = new PackageBuilderConfiguration( properties );
        assertTrue( cfg1.getDefaultDialect() instanceof MVELDialect );

        final PackageBuilderConfiguration cfg2 = new PackageBuilderConfiguration( properties );
        assertEquals( cfg1.getDefaultDialect().getClass(),
                      cfg2.getDefaultDialect().getClass() );
    }

    public void testMockDialect() {
        Package pkg = new Package( "org.pkg1" );

        PackageBuilderConfiguration cfg1 = new PackageBuilderConfiguration();
        MockDialectConfiguration mockConf = new MockDialectConfiguration();
        cfg1.getDialectRegistry().addDialectConfiguration( "mock",
                                                           mockConf );
        cfg1.setDefaultDialect( "mock" );

        PackageBuilder builder = new PackageBuilder( pkg,
                                                     cfg1 );

        MockDialect mockDialect2 = (MockDialect) builder.getPackageBuilderConfiguration().getDefaultDialect();
        assertSame( mockConf.getDialect(),
                    mockDialect2 );

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
        functionImportDescr.setTarget( "System.out.println" );
        pkgDescr.addFunctionImport( functionImportDescr );

        pkgDescr.addRule( ruleDescr );

        builder.addPackage( pkgDescr );

        assertSame( ruleDescr,
                    mockDialect2.getRuleDescr() );
        assertTrue( mockDialect2.getImport().contains( "java.util.HashMap" ) );
        assertTrue( mockDialect2.getStaticImport().contains( "System.out.println" ) );
        assertEquals( "eval was built",
                      evalDescr.getContent() );
        assertEquals( "consequence was built",
                      ruleDescr.getConsequence() );
        assertTrue( mockDialect2.isCompileAll() );

        assertNotNull( pkg.getRule( "test rule" ) );

    }

    public static class MockDialectConfiguration
        implements
        DialectConfiguration {
        private MockDialect                 dialect = new MockDialect();
        private PackageBuilderConfiguration conf;

        public Dialect getDialect() {
            return this.dialect;
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

        public void init(PackageBuilder builder) {
            this.builder = builder;

        }

        public void init(Package pkg) {
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

        public AnalysisResult analyzeBlock(RuleBuildContext context,
                                           BaseDescr descr,
                                           String text) {
            // TODO Auto-generated method stub
            return null;
        }

        public AnalysisResult analyzeExpression(RuleBuildContext context,
                                                BaseDescr descr,
                                                Object content) {
            // TODO Auto-generated method stub
            return null;
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
            // TODO Auto-generated method stub
            return null;
        }

        public ClassFieldExtractorCache getClassFieldExtractorCache() {
            // TODO Auto-generated method stub
            return null;
        }

        public ConsequenceBuilder getConsequenceBuilder() {
            return new MockConsequenceBuilder();
        }

        public RuleConditionBuilder getEvalBuilder() {
            return new MockEvalBuilder();
        }

        public String getExpressionDialectName() {
            // TODO Auto-generated method stub
            return null;
        }

        public FromBuilder getFromBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public PatternBuilder getPatternBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public PredicateBuilder getPredicateBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public QueryBuilder getQueryBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public List getResults() {
            // TODO Auto-generated method stub
            return null;
        }

        public ReturnValueBuilder getReturnValueBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public RuleClassBuilder getRuleClassBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public SalienceBuilder getSalienceBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public TypeResolver getTypeResolver() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getId() {
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

        public void build(RuleBuildContext context) {
            context.getRuleDescr().setConsequence( "consequence was built" );
        }

    }
}
