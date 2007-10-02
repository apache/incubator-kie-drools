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
import org.drools.base.ModifyInterceptor;
import org.drools.base.TypeResolver;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.Dialect;
import org.drools.compiler.ImportError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.LineMappings;
import org.drools.rule.Package;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.CollectBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.ForallBuilder;
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
import org.drools.rule.builder.dialect.java.JavaFunctionBuilder;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.KnowledgeHelper;
import org.drools.util.StringUtils;
import org.mvel.AbstractParser;
import org.mvel.ExpressionCompiler;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.optimizers.OptimizerFactory;
import org.mvel.util.ParseTools;

public class MVELDialect
    implements
    Dialect {

    public final static String                ID 					  = "mvel";

    private final static String               EXPRESSION_DIALECT_NAME = "MVEL";

    private final MVELRuleClassBuilder        rule                    = new MVELRuleClassBuilder();

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
    private final JavaFunctionBuilder         function                = new JavaFunctionBuilder();
    private final CollectBuilder              collect                 = new CollectBuilder();
    private final ForallBuilder               forall                  = new ForallBuilder();

    private Map                               interceptors;

    private List                              results;
    //private final JavaFunctionBuilder             function    = new JavaFunctionBuilder();

    private MemoryResourceReader                    src;

    private Package                           pkg;
    private MVELDialectConfiguration          configuration;
    private TypeResolver                      typeResolver;
    private ClassFieldExtractorCache          classFieldExtractorCache;
    private MVELExprAnalyzer                  analyzer;

    private Map                               imports;
    private Map                               packageImports;

    private boolean                           strictMode;
    
    private static Boolean                           languageSet = new Boolean( false );

    public void addFunction(FunctionDescr functionDescr,
                            TypeResolver typeResolver) {
        throw new UnsupportedOperationException( "MVEL does not support functions" );

    }

    // a map of registered builders
    private Map builders;

    public MVELDialect() {
    }
    
    public static void setLanguageLevel(int level) {
        synchronized ( languageSet ) {
            // this synchronisation is needed as setLanguageLevel is now thread safe
            // and we do not want ot be calling this while something else is being parsed.
            // the flag ensures it is just called once and no more.
            if ( languageSet.booleanValue() == false ) {
                languageSet = new Boolean( true );
                AbstractParser.setLanguageLevel( level );
            }
        }
    }

    public void init(PackageBuilder builder) {
        setLanguageLevel( 4 );
        this.pkg = builder.getPackage();
        this.configuration = (MVELDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "mvel" );
        this.typeResolver = builder.getTypeResolver();
        this.classFieldExtractorCache = builder.getClassFieldExtractorCache();
        this.strictMode = this.configuration.isStrict();

        // we currently default to reflective optimisation
        OptimizerFactory.setDefaultOptimizer("reflective");
        
        MVEL.setThreadSafe( true );

        this.analyzer = new MVELExprAnalyzer();
        this.imports = new HashMap();
        this.packageImports = new HashMap();

        this.interceptors = new HashMap( 1 );
        this.interceptors.put( "Modify",
                               new ModifyInterceptor() );

        this.results = new ArrayList();

        if ( pkg != null ) {
            init( pkg );
        }

        initBuilder();
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

        this.builders.put( CollectDescr.class,
                           this.collect );

        this.builders.put( ForallDescr.class,
                           this.forall );

        this.builders.put( FunctionDescr.class,
                           this.function );
   }

    public void init(Package pkg) {
        this.pkg = pkg;
        this.results = new ArrayList();
        this.src = new MemoryResourceReader();
        if( this.pkg != null ) {
            this.addImport( this.pkg.getName()+".*" );
        }
    }

    public void init(RuleDescr ruleDescr) {
        //MVEL:test null to Fix failing test on org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilderTest.testImperativeCodeError()

        // @todo: why is this here, MVEL doesn't compile anything? mdp
        String pkgName = this.pkg == null? "": this.pkg.getName();
        final String ruleClassName = JavaDialect.getUniqueLegalName( pkgName,
        															 ruleDescr.getName(),
        															 "mvel",
        															 this.src );
        ruleDescr.setClassName( StringUtils.ucFirst( ruleClassName ) );
        ruleDescr.setDialect( this );
    }

    public String getExpressionDialectName() {
        return EXPRESSION_DIALECT_NAME;
    }

    public void addRule(RuleBuildContext context) {
    	//MVEL: Compiler change
        final RuleDescr ruleDescr = context.getRuleDescr();

        // setup the line mappins for this rule
        final String name = this.pkg.getName() + "." + StringUtils.ucFirst( ruleDescr.getClassName() );
        final LineMappings mapping = new LineMappings( name );
        mapping.setStartLine( ruleDescr.getConsequenceLine() );
        mapping.setOffset( ruleDescr.getConsequenceOffset() );

        context.getPkg().getPackageCompilationData().getLineMappings().put( name, mapping );

    }

    public void addImport(String importEntry) {
        if ( importEntry.endsWith( ".*" ) ) {
            importEntry = importEntry.substring( 0, importEntry.length()-2  );
            this.packageImports.put( importEntry, importEntry );
        } else {
            try {
                Class cls = this.typeResolver.resolveType( importEntry );
                this.imports.put( ParseTools.getSimpleClassName( cls ), cls );
            } catch ( ClassNotFoundException e ) {
                this.results.add( new ImportError( importEntry, 1 ) );
            }
        }
    }

    public Map getImports() {
        return this.imports;
    }

    public Map getPackgeImports() {
        return this.packageImports;
    }

    public void addStaticImport(String staticImportEntry) {
        if ( staticImportEntry.endsWith( "*" ) ) {
            return;
        }

        int index = staticImportEntry.lastIndexOf( '.' );
        String className = staticImportEntry.substring( 0,
                                                        index );
        String methodName = staticImportEntry.substring( index + 1 );

        try {
            Class cls = this.pkg.getPackageCompilationData().getClassLoader().loadClass( className );
            Method[] methods = cls.getDeclaredMethods();
            for ( int i = 0; i < methods.length; i++ ) {
                if ( methods[i].getName().equals( methodName ) ) {
                    this.imports.put( methodName, methods[i] );
                    break;
                }
            }
        } catch ( ClassNotFoundException e ) {
            this.results.add( new ImportError( staticImportEntry, -1 ) );
        }
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
                                                    "Unable to determine the used declarations.\n" + e.getMessage()) );
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
                                                    e,
                                                    "Unable to determine the used declarations.\n" + e.getMessage()) );
        }
        return result;
    }

    public Serializable compile(final String text,
                                final Dialect.AnalysisResult analysis,
                                final Map interceptors,
                                final Map outerDeclarations,
                                final RuleBuildContext context) {
        final ParserContext parserContext = getParserContext(analysis, outerDeclarations, context );

        ExpressionCompiler compiler = new ExpressionCompiler( text.trim() );

        if (MVELDebugHandler.isDebugMode()) {
        	System.out.println("Source before MVEL Compilation:\n"+text.trim());
            compiler.setDebugSymbols( true );
        }

        Serializable expr = compiler.compile( parserContext );
        return expr;
    }

    public ParserContext getParserContext(final Dialect.AnalysisResult analysis, final Map outerDeclarations, final RuleBuildContext context) {
        final ParserContext parserContext = new ParserContext( this.imports,
                                                               null,
                                                               context.getPkg().getName()+"."+context.getRuleDescr().getClassName() );

        for ( Iterator it = this.packageImports.values().iterator(); it.hasNext(); ) {
            String packageImport = ( String ) it.next();
            parserContext.addPackageImport( packageImport );
        }

        parserContext.setStrictTypeEnforcement( strictMode );

        if ( interceptors != null ) {
            parserContext.setInterceptors( interceptors );
        }
        //FIXME: analysis can be null, throws an NPE
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

        return parserContext;
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
        return rule;
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public Map getInterceptors() {
        return this.interceptors;
    }

    public String getId() {
        return ID;
    }



}