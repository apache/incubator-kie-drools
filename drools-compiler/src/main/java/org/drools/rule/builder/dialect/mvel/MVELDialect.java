package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TypeResolver;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.DroolsMVELKnowledgeHelper;
import org.drools.compiler.Dialect;
import org.drools.compiler.ImportError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AccumulateDescr;
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
import org.drools.rule.Declaration;
import org.drools.rule.Package;
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
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.KnowledgeHelper;
import org.mvel.ASTNode;
import org.mvel.AbstractParser;
import org.mvel.ExpressionCompiler;
import org.mvel.ParserContext;
import org.mvel.ast.WithNode;
import org.mvel.integration.Interceptor;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.integration.impl.ClassImportResolverFactory;
import org.mvel.integration.impl.StaticMethodImportResolverFactory;

public class MVELDialect
    implements
    Dialect {

    private final static String               EXPRESSION_DIALECT_NAME = "MVEL";

    private final PatternBuilder              pattern                 = new PatternBuilder();
    private final QueryBuilder                query                   = new QueryBuilder();
    private final MVELAccumulateBuilder       accumulate              = new MVELAccumulateBuilder();
    private final SalienceBuilder             salience                = new MVELSalienceBuilder();
    private final MVELEvalBuilder             eval                    = new MVELEvalBuilder();
    private final MVELPredicateBuilder        predicate               = new MVELPredicateBuilder();
    private final MVELReturnValueBuilder      returnValue             = new MVELReturnValueBuilder();
    private final MVELConsequenceBuilder      consequence             = new MVELConsequenceBuilder();
    //private final JavaRuleClassBuilder            rule        = new JavaRuleClassBuilder();
    private final MVELFromBuilder             from                    = new MVELFromBuilder();

    private Map                               interceptors;

    private List                              results;
    //private final JavaFunctionBuilder             function    = new JavaFunctionBuilder();

    private Package                           pkg;
    private MVELDialectConfiguration          configuration;
    private TypeResolver                      typeResolver;
    private ClassFieldExtractorCache          classFieldExtractorCache;
    private MVELExprAnalyzer                  analyzer;

    private StaticMethodImportResolverFactory staticImportFactory;
    private ClassImportResolverFactory        importFactory;

    private boolean                           strictMode;

    public void addFunction(FunctionDescr functionDescr,
                            TypeResolver typeResolver) {
        throw new UnsupportedOperationException( "MVEL does not support functions" );

    }

    // a map of registered builders
    private Map builders;

    public MVELDialect() {
    }

    public void init(PackageBuilder builder) {
        AbstractParser.setLanguageLevel( 4 );

        this.pkg = builder.getPackage();
        this.configuration = (MVELDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "mvel" );
        this.typeResolver = builder.getTypeResolver();
        this.classFieldExtractorCache = builder.getClassFieldExtractorCache();
        this.strictMode = this.configuration.isStrict();

        this.analyzer = new MVELExprAnalyzer();

        if ( pkg != null ) {
            init( pkg );
        }

        this.results = new ArrayList();

        initBuilder();

        this.interceptors = new HashMap( 1 );
        this.interceptors.put( "Modify",
                               new ModifyInterceptor() );

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

        this.builders.put( AccumulateDescr.class,
                           getAccumulateBuilder() );

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
            Class cls = this.configuration.getPackageBuilderConfiguration().getClassLoader().loadClass( className );
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

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public void compileAll() {
    }

    public Dialect.AnalysisResult analyzeExpression(RuleBuildContext context,
                                                    BaseDescr descr,
                                                    Object content) {
        return analyzeExpression( context,
                                  descr,
                                  content,
                                  null );
    }

    public Dialect.AnalysisResult analyzeExpression(RuleBuildContext context,
                                                    BaseDescr descr,
                                                    Object content,
                                                    Map localTypes) {
        Dialect.AnalysisResult result = null;
        try {
            result = this.analyzer.analyzeExpression( context,
                                                      (String) content,
                                                      new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()},
                                                      localTypes );
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
        return analyzeBlock( context,
                             descr,
                             null,
                             text,
                             null );
    }

    public Dialect.AnalysisResult analyzeBlock(RuleBuildContext context,
                                               BaseDescr descr,
                                               Map interceptors,
                                               String text,
                                               Map localTypes) {
        Dialect.AnalysisResult result = null;
        try {
            result = this.analyzer.analyzeExpression( context,
                                                      text,
                                                      new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()},
                                                      localTypes );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    descr,
                                                    null,
                                                    "Unable to determine the used declarations" ) );
        }
        return result;
    }

    public Serializable compile(final String text,
                                final Dialect.AnalysisResult analysis,
                                final Map interceptors,
                                final Map outerDeclarations,
                                final RuleBuildContext context) {
        Map imports = getClassImportResolverFactory().getImportedClasses();
        imports.putAll( getStaticMethodImportResolverFactory().getImportedMethods() );

        final ParserContext parserContext = new ParserContext( imports,
                                                               null,
                                                               null );

        //this.configuration.get

        parserContext.setStrictTypeEnforcement( strictMode );
        if ( interceptors != null ) {
            parserContext.setInterceptors( interceptors );
        }

        List list[] = analysis.getBoundIdentifiers();
        DeclarationScopeResolver resolver = context.getDeclarationResolver();
        for ( Iterator it = list[0].iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            Class cls = resolver.getDeclaration( identifier ).getExtractor().getExtractToClass();
            parserContext.addInput( identifier,
                                    cls );
        }

        Map globalTypes = context.getPkg().getGlobals();
        for ( Iterator it = list[1].iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            parserContext.addInput( identifier,
                                    (Class) globalTypes.get( identifier ) );
        }

        Map mvelVars = ((MVELAnalysisResult) analysis).getMvelVariables();
        if ( mvelVars != null ) {
            for ( Iterator it = mvelVars.entrySet().iterator(); it.hasNext(); ) {
                Entry entry = (Entry) it.next();
                parserContext.addInput( (String) entry.getKey(),
                                        (Class) entry.getValue() );
            }
        }

        if ( outerDeclarations != null ) {
            for ( Iterator it = outerDeclarations.entrySet().iterator(); it.hasNext(); ) {
                Entry entry = (Entry) it.next();
                parserContext.addInput( (String) entry.getKey(),
                                        ((Declaration) entry.getValue()).getExtractor().getExtractToClass() );
            }
        }

        parserContext.addInput( "drools",
                                KnowledgeHelper.class );

        ExpressionCompiler compiler = new ExpressionCompiler( text );
        Serializable expr = compiler.compile( parserContext );
        return expr;
    }

    public RuleConditionBuilder getBuilder(final Class clazz) {
        return (RuleConditionBuilder) this.builders.get( clazz );
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
        return this.accumulate;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.consequence;
    }

    public RuleConditionBuilder getEvalBuilder() {
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

    public Map getInterceptors() {
        return this.interceptors;
    }

    public static class AssertInterceptor
        implements
        Interceptor {
        public int doBefore(ASTNode node,
                            VariableResolverFactory factory) {
            return 0;
        }

        public int doAfter(Object value,
                           ASTNode node,
                           VariableResolverFactory factory) {
            ((DroolsMVELFactory) factory).getWorkingMemory().insert( value );
            return 0;
        }
    }

    public static class ModifyInterceptor
        implements
        Interceptor {
        public int doBefore(ASTNode node,
                            VariableResolverFactory factory) {
            Object object = ((WithNode) node).getNestedStatement().getValue( null,
                                                                             factory );

            DroolsMVELKnowledgeHelper resolver = (DroolsMVELKnowledgeHelper) factory.getVariableResolver( "drools" );
            KnowledgeHelper helper = (KnowledgeHelper) resolver.getValue();
            helper.modifyRetract( object );
            return 0;
        }

        public int doAfter(Object value,
                           ASTNode node,
                           VariableResolverFactory factory) {
            DroolsMVELKnowledgeHelper resolver = (DroolsMVELKnowledgeHelper) factory.getVariableResolver( "drools" );
            KnowledgeHelper helper = (KnowledgeHelper) resolver.getValue();
            helper.modifyInsert( value );
            return 0;
        }
    }
}
