package org.drools.rule.builder.dialect.mvel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TypeResolver;
import org.drools.compiler.ImportError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.Dialect;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.SalienceBuilder;
import org.mvel.integration.impl.ClassImportResolverFactory;
import org.mvel.integration.impl.StaticMethodImportResolverFactory;

public class MVELDialect
    implements
    Dialect {

    private final static String                     EXPRESSION_DIALECT_NAME = "MVEL";

    private final PatternBuilder                    pattern                 = new PatternBuilder();
    private final QueryBuilder                      query                   = new QueryBuilder();
    //private final JavaAccumulateBuilder           accumulate  = new JavaAccumulateBuilder();
    private final SalienceBuilder                   salience                = new MVELSalienceBuilder();
    private final MVELEvalBuilder                   eval                    = new MVELEvalBuilder();
    private final MVELPredicateBuilder              predicate               = new MVELPredicateBuilder();
    private final MVELReturnValueBuilder            returnValue             = new MVELReturnValueBuilder();
    private final MVELConsequenceBuilder            consequence             = new MVELConsequenceBuilder();
    //private final JavaRuleClassBuilder            rule        = new JavaRuleClassBuilder();
    private final MVELFromBuilder                   from                    = new MVELFromBuilder();

    private List                                    results;
    //private final JavaFunctionBuilder             function    = new JavaFunctionBuilder();

    private Package                                 pkg;
    private PackageBuilderConfiguration             configuration;
    private final TypeResolver                      typeResolver;
    private final ClassFieldExtractorCache          classFieldExtractorCache;
    private final MVELExprAnalyzer                  analyzer;

    private final StaticMethodImportResolverFactory staticImportFactory;
    private final ClassImportResolverFactory        importFactory;

    public void addFunction(FunctionDescr functionDescr,
                            TypeResolver typeResolver) {
        throw new UnsupportedOperationException( "MVEL does not support functions" );

    }

    // a map of registered builders
    private Map builders;

    public MVELDialect(final PackageBuilder builder) {
        this.pkg = builder.getPackage();
        this.configuration = builder.getPackageBuilderConfiguration();
        this.typeResolver = builder.getTypeResolver();
        this.classFieldExtractorCache = builder.getClassFieldExtractorCache();

        this.analyzer = new MVELExprAnalyzer();

        if ( pkg != null ) {
            init( pkg );
        }

        this.results = new ArrayList();

        initBuilder();

        this.importFactory = new ClassImportResolverFactory();
        this.staticImportFactory = new StaticMethodImportResolverFactory();
        this.importFactory.setNextFactory( this.staticImportFactory );
    }

    public void initBuilder() {
        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        this.builders = new HashMap();

        final GroupElementBuilder gebuilder = new GroupElementBuilder();

        this.builders.put( AndDescr.class,
                           gebuilder );

        this.builders.put( OrDescr.class,
                           gebuilder );

        this.builders.put( NotDescr.class,
                           gebuilder );

        this.builders.put( ExistsDescr.class,
                           gebuilder );

        this.builders.put( PatternDescr.class,
                           getPatternBuilder() );

        this.builders.put( FromDescr.class,
                           getFromBuilder() );

        this.builders.put( QueryDescr.class,
                           getQueryBuilder() );

        //        this.builders.put( AccumulateDescr.class,
        //                           getAccumulateBuilder() );

        this.builders.put( EvalDescr.class,
                           getEvalBuilder() );
    }

    public void init(Package pkg) {
        this.pkg = pkg;
        this.results = new ArrayList();

    }

    public void init(RuleDescr ruleDescr) {
    }

    public String getExpressionDialectName() {
        return EXPRESSION_DIALECT_NAME;
    }

    public void addRule(RuleBuildContext context) {

    }

    public void addImport(String importEntry) {
        if ( importEntry.endsWith( "*" ) ) {
            return;
        }
        try {
            Class cls = this.typeResolver.resolveType( importEntry );
            this.importFactory.addClass( cls );
        } catch ( ClassNotFoundException e ) {
            this.results.add( new ImportError( importEntry ) );
        }
    }

    public void addStaticImport(String staticImportEntry) {
        if ( staticImportEntry.endsWith( "*" ) ) {
            return;
        }

        int index = staticImportEntry.lastIndexOf( '.' );
        String className = staticImportEntry.substring( 0,
                                                        index );
        String methodName = staticImportEntry.substring( 0,
                                                         index + 1 );

        try {
            Class cls = configuration.getClassLoader().loadClass( className );
            Method[] methods = cls.getDeclaredMethods();
            for ( int i = 0; i < methods.length; i++ ) {
                if ( methods[i].equals( "methodName" ) ) {
                    this.staticImportFactory.createVariable( methodName,
                                                             methods[i] );
                    break;
                }
            }
        } catch ( ClassNotFoundException e ) {
            this.results.add( new ImportError( staticImportEntry ) );
        }
    }

    public StaticMethodImportResolverFactory getStaticMethodImportResolverFactory() {
        return this.staticImportFactory;
    }

    public ClassImportResolverFactory getClassImportResolverFactory() {
        return this.importFactory;
    }

    public void compileAll() {
    }

    public Dialect.AnalysisResult analyzeExpression(RuleBuildContext context,
                                           BaseDescr descr,
                                           Object content) {
        Dialect.AnalysisResult result = null;
        try {
            result = this.analyzer.analyzeExpression( (String) content,
                                                               new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    descr,
                                                    null,
                                                    "Unable to determine the used declarations" ) );
        }
        return result;
    }

    public Dialect.AnalysisResult analyzeBlock(RuleBuildContext context,
                                      BaseDescr descr,
                                      String text) {
        Dialect.AnalysisResult result = null;
        try {
            result = this.analyzer.analyzeExpression( text,
                                                               new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    descr,
                                                    null,
                                                    "Unable to determine the used declarations" ) );
        }
        return result;
    }

    public Object getBuilder(final Class clazz) {
        return this.builders.get( clazz );
    }

    public Map getBuilders() {
        return this.builders;
    }

    public ClassFieldExtractorCache getClassFieldExtractorCache() {
        return this.classFieldExtractorCache;
    }

    public PatternBuilder getPatternBuilder() {
        return this.pattern;
    }

    public QueryBuilder getQueryBuilder() {
        return this.query;
    }

    public AccumulateBuilder getAccumulateBuilder() {
        throw new UnsupportedOperationException( "MVEL does not yet support accumuate" );
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.consequence;
    }

    public ConditionalElementBuilder getEvalBuilder() {
        return this.eval;
    }

    public FromBuilder getFromBuilder() {
        return this.from;
    }

    public PredicateBuilder getPredicateBuilder() {
        return this.predicate;
    }

    public PredicateBuilder getExpressionPredicateBuilder() {
        return this.predicate;
    }

    public SalienceBuilder getSalienceBuilder() {
        return this.salience;
    }

    public List getResults() {
        return results;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return this.returnValue;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        return null;
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

}
