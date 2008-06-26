package org.drools.rule.builder.dialect.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ModifyInterceptor;
import org.drools.base.TypeResolver;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.ImportError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.DialectRuntimeRegistry;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.LineMappings;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.CollectBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.ForallBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.rule.builder.ProcessClassBuilder;
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
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.compiler.AbstractParser;
import org.mvel.compiler.CompiledExpression;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.optimizers.OptimizerFactory;
import org.mvel.util.CompilerTools;
import org.mvel.util.ParseTools;

public class MVELDialect
    implements
    Dialect,
    Externalizable {

    private String                                        id                             = "mvel";

    private final static String                          EXPRESSION_DIALECT_NAME        = "MVEL";

    private static final PatternBuilder                  PATTERN_BUILDER                = new PatternBuilder();
    private static final QueryBuilder                    QUERY_BUILDER                  = new QueryBuilder();
    private static final MVELAccumulateBuilder           ACCUMULATE_BUILDER             = new MVELAccumulateBuilder();
    private static final SalienceBuilder                 SALIENCE_BUILDER               = new MVELSalienceBuilder();
    private static final MVELEvalBuilder                 EVAL_BUILDER                   = new MVELEvalBuilder();
    private static final MVELPredicateBuilder            PREDICATE_BUILDER              = new MVELPredicateBuilder();
    private static final MVELReturnValueBuilder          RETURN_VALUE_BUILDER           = new MVELReturnValueBuilder();
    private static final MVELConsequenceBuilder          CONSEQUENCE_BUILDER            = new MVELConsequenceBuilder();
    private static final MVELActionBuilder               ACTION_BUILDER                 = new MVELActionBuilder();
    private static final MVELReturnValueEvaluatorBuilder RETURN_VALUE_EVALUATOR_BUILDER = new MVELReturnValueEvaluatorBuilder();
    //private final JavaRuleClassBuilder            rule        = new JavaRuleClassBuilder();
    private static final MVELFromBuilder                 FROM_BUILDER                   = new MVELFromBuilder();
    private static final JavaFunctionBuilder             FUNCTION_BUILDER               = new JavaFunctionBuilder();
    private static final CollectBuilder                  COLLECT_BUILDER                = new CollectBuilder();

    private static final ForallBuilder                   FORALL_BUILDER                 = new ForallBuilder();
    private static final EntryPointBuilder               ENTRY_POINT_BUILDER            = new EntryPointBuilder();

    private static final GroupElementBuilder             GE_BUILDER                     = new GroupElementBuilder();

    // a map of registered builders
    private static Map                                   builders;

    static {
        initBuilder();
    }

    private static final MVELExprAnalyzer                analyzer                       = new MVELExprAnalyzer();

    private Map                                          interceptors;

    protected List                                       results;
    //private final JavaFunctionBuilder             function    = new JavaFunctionBuilder();

    protected MemoryResourceReader                       src;

    protected Package                                    pkg;
    private MVELDialectConfiguration                     configuration;

    private PackageRegistry                              packageRegistry;

    private ClassFieldAccessorCache                      classFieldExtractorCache;

    private Map                                          imports;
    private Map                                          packageImports;

    private boolean                                      strictMode;
    private int                                          languageLevel;
    public static final Object                           COMPILER_LOCK                  = new Object();
    
    public MVELDialect(PackageBuilder builder,
                       PackageRegistry pkgRegistry,
                       Package pkg) {
        this( builder, pkgRegistry, pkg, "mvel" );
    }

    public MVELDialect(PackageBuilder builder,
                       PackageRegistry pkgRegistry,
                       Package pkg,
                       String id) {
        this.id = id;
        this.pkg = pkg;
        this.packageRegistry = pkgRegistry;

        this.configuration = (MVELDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "mvel" );
        setLanguageLevel( this.configuration.getLangLevel() );
        this.classFieldExtractorCache = builder.getClassFieldExtractorCache();
        this.strictMode = this.configuration.isStrict();

        MVEL.setThreadSafe( true );

        this.imports = new HashMap();
        this.packageImports = new HashMap();

        this.interceptors = new HashMap( 1 );
        this.interceptors.put( "Modify",
                               new ModifyInterceptor() );

        this.results = new ArrayList();

        //        this.data = new MVELDialectRuntimeData( this.pkg.getDialectRuntimeRegistry() );
        //        
        //        this.pkg.getDialectRuntimeRegistry().setDialectData( ID,
        //                                                             this.data );

        MVELDialectRuntimeData data = null;
        // initialise the dialect runtime data if it doesn't already exist
        if ( pkg.getDialectRuntimeRegistry().getDialectData( getId() ) == null ) {
            data = new MVELDialectRuntimeData( this.pkg.getDialectRuntimeRegistry() );
            this.pkg.getDialectRuntimeRegistry().setDialectData( getId(),
                                                                 data );
        }

        this.results = new ArrayList();
        this.src = new MemoryResourceReader();
        if ( this.pkg != null ) {
            this.addImport( this.pkg.getName() + ".*" );
        }
    }
        

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        interceptors = (Map) in.readObject();
        results = (List) in.readObject();
        src = (MemoryResourceReader) in.readObject();
        pkg = (Package) in.readObject();
        packageRegistry = (PackageRegistry) in.readObject();
        configuration = (MVELDialectConfiguration) in.readObject();
        classFieldExtractorCache = (ClassFieldAccessorCache) in.readObject();
        imports = (Map) in.readObject();
        packageImports = (Map) in.readObject();
        strictMode = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( interceptors );
        out.writeObject( results );
        out.writeObject( src );
        out.writeObject( pkg );
        out.writeObject( packageRegistry );
        out.writeObject( configuration );
        out.writeObject( classFieldExtractorCache );
        out.writeObject( imports );
        out.writeObject( packageImports );
        out.writeBoolean( strictMode );
    }

    public void setLanguageLevel(int languageLevel) {
        this.languageLevel = languageLevel;
    }

    //    public static void setLanguageLevel(int level) {        
    //        synchronized ( lang ) {
    //            // this synchronisation is needed as setLanguageLevel is not thread safe
    //            // and we do not want to be calling this while something else is being parsed.
    //            // the flag ensures it is just called once and no more.
    //            if ( languageSet.booleanValue() == false ) {
    //                languageSet = new Boolean( true );
    //                AbstractParser.setLanguageLevel( level );
    //            }
    //        }
    //    }

    public static void initBuilder() {
        if ( builders != null ) {
            return;
        }

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        builders = new HashMap();

        builders.put( AndDescr.class,
                      GE_BUILDER );

        builders.put( OrDescr.class,
                      GE_BUILDER );

        builders.put( NotDescr.class,
                      GE_BUILDER );

        builders.put( ExistsDescr.class,
                      GE_BUILDER );

        builders.put( PatternDescr.class,
                      PATTERN_BUILDER );

        builders.put( FromDescr.class,
                      FROM_BUILDER );

        builders.put( QueryDescr.class,
                      QUERY_BUILDER );

        builders.put( AccumulateDescr.class,
                      ACCUMULATE_BUILDER );

        builders.put( EvalDescr.class,
                      EVAL_BUILDER );

        builders.put( CollectDescr.class,
                      COLLECT_BUILDER );

        builders.put( ForallDescr.class,
                      FORALL_BUILDER );

        builders.put( FunctionDescr.class,
                      FUNCTION_BUILDER );

        builders.put( EntryPointDescr.class,
                      ENTRY_POINT_BUILDER );
    }

    public void init(RuleDescr ruleDescr) {
        //MVEL:test null to Fix failing test on org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilderTest.testImperativeCodeError()

        // @todo: why is this here, MVEL doesn't compile anything? mdp
        String pkgName = this.pkg == null ? "" : this.pkg.getName();
        final String ruleClassName = JavaDialect.getUniqueLegalName( pkgName,
                                                                     ruleDescr.getName(),
                                                                     "mvel",
                                                                     "Rule",
                                                                     this.src );
        ruleDescr.setClassName( StringUtils.ucFirst( ruleClassName ) );
    }

    public void init(final ProcessDescr processDescr) {
        final String processDescrClassName = JavaDialect.getUniqueLegalName( this.pkg.getName(),
                                                                             processDescr.getName(),
                                                                             "mvel",
                                                                             "Process",
                                                                             this.src );
        processDescr.setClassName( StringUtils.ucFirst( processDescrClassName ) );
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

        context.getPkg().getDialectRuntimeRegistry().getLineMappings().put( name,
                                                                            mapping );

    }

    public void addProcess(final ProcessBuildContext context) {
        // @TODO setup line mappings
    }

    public void addFunction(FunctionDescr functionDescr,
                            TypeResolver typeResolver) {
        ExpressionCompiler compiler = new ExpressionCompiler( (String) functionDescr.getContent() );
        Serializable s1 = compiler.compile();
        Map<String, org.mvel.ast.Function> map = CompilerTools.extractAllDeclaredFunctions( (CompiledExpression) s1 );
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) this.packageRegistry.getDialectRuntimeRegistry().getDialectData( getId() );
        for ( org.mvel.ast.Function function : map.values() ) {
            data.addFunction( function );
        }
    }

    public void preCompileAddFunction(FunctionDescr functionDescr,
                                      TypeResolver typeResolver) {

    }

    public void postCompileAddFunction(FunctionDescr functionDescr,
                                       TypeResolver typeResolver) {

    }

    public void addImport(String importEntry) {
        if ( importEntry.endsWith( ".*" ) ) {
            importEntry = importEntry.substring( 0,
                                                 importEntry.length() - 2 );
            this.packageImports.put( importEntry,
                                     importEntry );
        } else {
            try {
                Class cls = this.packageRegistry.getTypeResolver().resolveType( importEntry );
                this.imports.put( ParseTools.getSimpleClassName( cls ),
                                  cls );
            } catch ( ClassNotFoundException e ) {
                this.results.add( new ImportError( importEntry,
                                                   1 ) );
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
            Class cls = this.pkg.getDialectRuntimeRegistry().getClassLoader().loadClass( className );
            Method[] methods = cls.getDeclaredMethods();
            for ( int i = 0; i < methods.length; i++ ) {
                if ( methods[i].getName().equals( methodName ) ) {
                    this.imports.put( methodName,
                                      methods[i] );
                    break;
                }
            }
        } catch ( ClassNotFoundException e ) {
            this.results.add( new ImportError( staticImportEntry,
                                               -1 ) );
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

    public Dialect.AnalysisResult analyzeExpression(PackageBuildContext context,
                                                    BaseDescr descr,
                                                    Object content,
                                                    final Set[] availableIdentifiers) {
        return analyzeExpression( context,
                                  descr,
                                  content,
                                  availableIdentifiers,
                                  null );
    }

    public Dialect.AnalysisResult analyzeExpression(PackageBuildContext context,
                                                    BaseDescr descr,
                                                    Object content,
                                                    final Set[] availableIdentifiers,
                                                    Map localTypes) {
        //new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()},

        Dialect.AnalysisResult result = null;
        try {
            result = this.analyzer.analyzeExpression( context,
                                                      (String) content,
                                                      availableIdentifiers,
                                                      localTypes );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Unable to determine the used declarations.\n" + e.getMessage() ) );
        }
        return result;
    }

    public Dialect.AnalysisResult analyzeBlock(PackageBuildContext context,
                                               BaseDescr descr,
                                               String text,
                                               final Set[] availableIdentifiers) {
        return analyzeBlock( context,
                             descr,
                             null,
                             text,
                             availableIdentifiers,
                             null );
    }

    public Dialect.AnalysisResult analyzeBlock(PackageBuildContext context,
                                               BaseDescr descr,
                                               Map interceptors,
                                               String text,
                                               final Set[] availableIdentifiers,
                                               Map localTypes) {
        //new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()}

        Dialect.AnalysisResult result = null;
        try {
            result = this.analyzer.analyzeExpression( context,
                                                      text,
                                                      availableIdentifiers,
                                                      localTypes );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          e,
                                                          "Unable to determine the used declarations.\n" + e.getMessage() ) );
        }
        return result;
    }

    public Serializable compile(final String text,
                                final Dialect.AnalysisResult analysis,
                                final Map interceptors,
                                final Map outerDeclarations,
                                final Map otherInputVariables,
                                final PackageBuildContext context) {
        final ParserContext parserContext = getParserContext( analysis,
                                                              outerDeclarations,
                                                              otherInputVariables,
                                                              context );

        ExpressionCompiler compiler = new ExpressionCompiler( text.trim() );

        if ( MVELDebugHandler.isDebugMode() ) {
            compiler.setDebugSymbols( true );
        }

        synchronized ( COMPILER_LOCK ) {
            AbstractParser.setLanguageLevel( languageLevel );
            return compiler.compile( parserContext );
        }
    }

    public ParserContext getParserContext(final Dialect.AnalysisResult analysis,
                                          final Map outerDeclarations,
                                          final Map otherInputVariables,
                                          final PackageBuildContext context) {
        // @todo proper source file name
        final ParserContext parserContext = new ParserContext( this.imports,
                                                               null,
                                                               "xxx" );//context.getPkg().getName()+"."+context.getRuleDescr().getClassName() );

        for ( Iterator it = this.packageImports.values().iterator(); it.hasNext(); ) {
            String packageImport = (String) it.next();
            parserContext.addPackageImport( packageImport );
        }

        parserContext.setStrictTypeEnforcement( strictMode );

        if ( interceptors != null ) {
            parserContext.setInterceptors( interceptors );
        }

        List list[] = analysis.getBoundIdentifiers();

        // @TODO yuck, we don't want conditions for configuration :(
        if ( context instanceof RuleBuildContext ) {
            //FIXME: analysis can be null, throws an NPE
            DeclarationScopeResolver resolver = ((RuleBuildContext) context).getDeclarationResolver();
            for ( Iterator it = list[0].iterator(); it.hasNext(); ) {
                String identifier = (String) it.next();
                Class cls = resolver.getDeclaration( identifier ).getExtractor().getExtractToClass();
                parserContext.addInput( identifier,
                                        cls );
            }
        }

        Map globalTypes = context.getPkg().getGlobals();
        for ( Iterator it = list[1].iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            parserContext.addInput( identifier,
                                    (Class) globalTypes.get( identifier ) );
        }

        if ( otherInputVariables != null ) {
            for ( Iterator it = otherInputVariables.entrySet().iterator(); it.hasNext(); ) {
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

    public ClassFieldAccessorCache getClassFieldExtractorCache() {
        return this.classFieldExtractorCache;
    }

    public PatternBuilder getPatternBuilder() {
        return this.PATTERN_BUILDER;
    }

    public QueryBuilder getQueryBuilder() {
        return this.QUERY_BUILDER;
    }

    public AccumulateBuilder getAccumulateBuilder() {
        return this.ACCUMULATE_BUILDER;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.CONSEQUENCE_BUILDER;
    }

    public ActionBuilder getActionBuilder() {
        return this.ACTION_BUILDER;
    }

    public MVELReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
        return this.RETURN_VALUE_EVALUATOR_BUILDER;
    }

    public RuleConditionBuilder getEvalBuilder() {
        return this.EVAL_BUILDER;
    }

    public FromBuilder getFromBuilder() {
        return this.FROM_BUILDER;
    }

    public EntryPointBuilder getEntryPointBuilder() {
        return this.ENTRY_POINT_BUILDER;
    }

    public PredicateBuilder getPredicateBuilder() {
        return this.PREDICATE_BUILDER;
    }

    public PredicateBuilder getExpressionPredicateBuilder() {
        return this.PREDICATE_BUILDER;
    }

    public SalienceBuilder getSalienceBuilder() {
        return this.SALIENCE_BUILDER;
    }

    public List getResults() {
        return results;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return this.RETURN_VALUE_BUILDER;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        throw new UnsupportedOperationException( "MVELDialect.getRuleClassBuilder is not supported" );
    }

    public ProcessClassBuilder getProcessClassBuilder() {
        throw new UnsupportedOperationException( "MVELDialect.getProcessClassBuilder is not supported" );
    }

    public TypeResolver getTypeResolver() {
        return this.packageRegistry.getTypeResolver();
    }

    public Map getInterceptors() {
        return this.interceptors;
    }

    public String getId() {
        return this.id;
    }

}