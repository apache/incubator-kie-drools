package org.drools.rule.builder.dialect.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

import org.drools.base.EvaluatorWrapper;
import org.drools.base.ModifyInterceptor;
import org.drools.base.TypeResolver;
import org.drools.base.ValueType;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.builder.KnowledgeBuilderResult;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.ImportError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.core.util.StringUtils;
import org.drools.definition.rule.Rule;
import org.drools.io.Resource;
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
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.WindowReferenceDescr;
import org.drools.rule.Declaration;
import org.drools.rule.LineMappings;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.CollectBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EnabledBuilder;
import org.drools.rule.builder.EngineElementBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.ForallBuilder;
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
import org.drools.rule.builder.WindowReferenceBuilder;
import org.drools.rule.builder.dialect.java.JavaFunctionBuilder;
import org.drools.runtime.rule.RuleContext;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.Evaluator;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.MVEL;

import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;
import static org.drools.rule.builder.dialect.DialectUtil.getUniqueLegalName;

public class MVELDialect
    implements
    Dialect,
    Externalizable {

    private String                                         id                             = "mvel";

    private final static String                            EXPRESSION_DIALECT_NAME        = "MVEL";

    protected static final PatternBuilder                  PATTERN_BUILDER                = new PatternBuilder();
    protected static final QueryBuilder                    QUERY_BUILDER                  = new QueryBuilder();
    protected static final MVELAccumulateBuilder           ACCUMULATE_BUILDER             = new MVELAccumulateBuilder();
    protected static final SalienceBuilder                 SALIENCE_BUILDER               = new MVELSalienceBuilder();
    protected static final EnabledBuilder                  ENABLED_BUILDER                = new MVELEnabledBuilder();
    protected static final MVELEvalBuilder                 EVAL_BUILDER                   = new MVELEvalBuilder();
    protected static final MVELPredicateBuilder            PREDICATE_BUILDER              = new MVELPredicateBuilder();
    protected static final MVELReturnValueBuilder          RETURN_VALUE_BUILDER           = new MVELReturnValueBuilder();
    protected static final MVELConsequenceBuilder          CONSEQUENCE_BUILDER            = new MVELConsequenceBuilder();
    // private final JavaRuleClassBuilder rule = new JavaRuleClassBuilder();
    protected static final MVELFromBuilder                 FROM_BUILDER                   = new MVELFromBuilder();
    protected static final JavaFunctionBuilder             FUNCTION_BUILDER               = new JavaFunctionBuilder();
    protected static final CollectBuilder                  COLLECT_BUILDER                = new CollectBuilder();

    protected static final ForallBuilder                   FORALL_BUILDER                 = new ForallBuilder();
    protected static final EntryPointBuilder               ENTRY_POINT_BUILDER            = new EntryPointBuilder();
    protected static final WindowReferenceBuilder          WINDOW_REFERENCE_BUILDER       = new WindowReferenceBuilder();

    protected static final GroupElementBuilder             GE_BUILDER                     = new GroupElementBuilder();

    // a map of registered builders
    private static Map<Class< ? >, EngineElementBuilder>   builders;

    static {
        initBuilder();
    }

    private static final MVELExprAnalyzer                  analyzer                       = new MVELExprAnalyzer();

    private final Map                                      interceptors                   = MVELCompilationUnit.INTERCEPTORS;

    protected List<KnowledgeBuilderResult>                 results;
    // private final JavaFunctionBuilder function = new JavaFunctionBuilder();

    protected MemoryResourceReader                         src;

    protected Package                                      pkg;
    private MVELDialectConfiguration                       configuration;

    private PackageBuilder                                 pkgBuilder;

    private PackageRegistry                                packageRegistry;

    private boolean                                        strictMode;
    private int                                            languageLevel;
    
    private MVELDialectRuntimeData                         data;

    public MVELDialect(PackageBuilder builder,
                       PackageRegistry pkgRegistry,
                       Package pkg) {
        this( builder,
              pkgRegistry,
              pkg,
              "mvel" );
    }

    public MVELDialect(PackageBuilder builder,
                       PackageRegistry pkgRegistry,
                       Package pkg,
                       String id) {
        this.id = id;
        this.pkg = pkg;
        this.pkgBuilder = builder;
        this.packageRegistry = pkgRegistry;
        this.configuration = (MVELDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "mvel" );
        setLanguageLevel( this.configuration.getLangLevel() );
        this.strictMode = this.configuration.isStrict();

        // setting MVEL option directly
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;

        this.results = new ArrayList<KnowledgeBuilderResult>();

        // this.data = new MVELDialectRuntimeData(
        // this.pkg.getDialectRuntimeRegistry() );
        //        
        // this.pkg.getDialectRuntimeRegistry().setDialectData( ID,
        // this.data );

        // initialise the dialect runtime data if it doesn't already exist
        if ( pkg.getDialectRuntimeRegistry().getDialectData( getId() ) == null ) {
            data = new MVELDialectRuntimeData();
            this.pkg.getDialectRuntimeRegistry().setDialectData( getId(),
                                                                 data );
            data.onAdd( this.pkg.getDialectRuntimeRegistry(),
                        this.pkgBuilder.getRootClassLoader() );
        } else {
            data = ( MVELDialectRuntimeData ) this.pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
        }

        this.results = new ArrayList<KnowledgeBuilderResult>();
        this.src = new MemoryResourceReader();
        if ( this.pkg != null ) {
            this.addImport( new ImportDescr( this.pkg.getName() + ".*" ) );
        }
        this.addImport( new ImportDescr( "java.lang.*" ) );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        results = (List<KnowledgeBuilderResult>) in.readObject();
        src = (MemoryResourceReader) in.readObject();
        pkg = (Package) in.readObject();
        packageRegistry = (PackageRegistry) in.readObject();
        configuration = (MVELDialectConfiguration) in.readObject();
        strictMode = in.readBoolean();
        data = ( MVELDialectRuntimeData ) this.pkg.getDialectRuntimeRegistry().getDialectData("mvel" );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( results );
        out.writeObject( src );
        out.writeObject( pkg );
        out.writeObject( packageRegistry );
        out.writeObject( configuration );
        out.writeBoolean( strictMode );
        out.writeObject( data );
    }

    public void setLanguageLevel(int languageLevel) {
        this.languageLevel = languageLevel;
    }

    // public static void setLanguageLevel(int level) {
    // synchronized ( lang ) {
    // // this synchronisation is needed as setLanguageLevel is not thread safe
    // // and we do not want to be calling this while something else is being
    // parsed.
    // // the flag ensures it is just called once and no more.
    // if ( languageSet.booleanValue() == false ) {
    // languageSet = new Boolean( true );
    // AbstractParser.setLanguageLevel( level );
    // }
    // }
    // }

    public static void initBuilder() {
        if ( builders != null ) {
            return;
        }

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        builders = new HashMap<Class< ? >, EngineElementBuilder>();

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

        builders.put( WindowReferenceDescr.class,
                      WINDOW_REFERENCE_BUILDER );
    }

    public void init(RuleDescr ruleDescr) {
        // MVEL:test null to Fix failing test on
        // org.drools.rule.builder.dialect.
        // mvel.MVELConsequenceBuilderTest.testImperativeCodeError()

        // @todo: why is this here, MVEL doesn't compile anything? mdp
        String pkgName = this.pkg == null ? "" : this.pkg.getName();
        final String ruleClassName = getUniqueLegalName(pkgName,
                                                        ruleDescr.getName(),
                                                        "mvel",
                                                        "Rule",
                                                        this.src);
        ruleDescr.setClassName( StringUtils.ucFirst( ruleClassName ) );
    }

    public void init(final ProcessDescr processDescr) {
        final String processDescrClassName = getUniqueLegalName(this.pkg.getName(),
                                                                processDescr.getName(),
                                                                "mvel",
                                                                "Process",
                                                                this.src);
        processDescr.setClassName( StringUtils.ucFirst( processDescrClassName ) );
    }

    public String getExpressionDialectName() {
        return EXPRESSION_DIALECT_NAME;
    }

    public void addRule(RuleBuildContext context) {
        // MVEL: Compiler change
        final RuleDescr ruleDescr = context.getRuleDescr();

        // setup the line mappins for this rule
        final String name = this.pkg.getName() + "." + StringUtils.ucFirst( ruleDescr.getClassName() );
        final LineMappings mapping = new LineMappings( name );
        mapping.setStartLine( ruleDescr.getConsequenceLine() );
        mapping.setOffset( ruleDescr.getConsequenceOffset() );

        context.getPkg().getDialectRuntimeRegistry().getLineMappings().put( name,
                                                                            mapping );

    }

    public void addFunction(FunctionDescr functionDescr,
                            TypeResolver typeResolver,
                            Resource resource) {
//        Serializable s1 = compile( (String) functionDescr.getText(),
//                                   null,
//                                   null,
//                                   null,
//                                   null,
//                                   null );
//        
//        final ParserContext parserContext = getParserContext( analysis,
//                                                              outerDeclarations,
//                                                              otherInputVariables,
//                                                              context );
//        return MVELCompilationUnit.compile( text, pkgBuilder.getRootClassLoader(), parserContext, languageLevel );
//        
        
//        Map<String, org.mvel2.ast.Function> map = org.mvel2.util.CompilerTools.extractAllDeclaredFunctions( (org.mvel2.compiler.CompiledExpression) s1 );
//        MVELDialectRuntimeData data = (MVELDialectRuntimeData) this.packageRegistry.getDialectRuntimeRegistry().getDialectData( getId() );
//        for ( org.mvel2.ast.Function function : map.values() ) {
//            data.addFunction( function );
//        }
    }

    public void preCompileAddFunction(FunctionDescr functionDescr,
                                      TypeResolver typeResolver) {

    }

    public void postCompileAddFunction(FunctionDescr functionDescr,
                                       TypeResolver typeResolver) {

    }

    public void addImport(ImportDescr importDescr) {
        String importEntry = importDescr.getTarget();
        if ( importEntry.endsWith( ".*" ) ) {
            importEntry = importEntry.substring( 0,
                                                 importEntry.length() - 2 );
            data.addPackageImport( importEntry );
        } else {
            try {
                Class cls = this.packageRegistry.getTypeResolver().resolveType( importEntry );
                data.addImport( cls.getSimpleName(), cls );
            } catch ( ClassNotFoundException e ) {
                this.results.add( new ImportError( importDescr, 1 ) );
            }
        }
    }

    public void addStaticImport(ImportDescr importDescr) {
        String staticImportEntry = importDescr.getTarget();
        if ( staticImportEntry.endsWith( "*" ) ) {
            addStaticPackageImport(importDescr);
            return;
        }

        int index = staticImportEntry.lastIndexOf( '.' );
        String className = staticImportEntry.substring( 0, index );
        String methodName = staticImportEntry.substring( index + 1 );

        try {
            Class cls = this.pkgBuilder.getRootClassLoader().loadClass( className );
            if ( cls != null ) {

                // First try and find a matching method
                for ( Method method : cls.getDeclaredMethods() ) {
                    if ( method.getName().equals( methodName ) ) {
                        this.data.addImport( methodName, method );
                        return;
                    }
                }

                //no matching method, so now try and find a matching public property
                for ( Field field : cls.getFields() ) {
                    if ( field.isAccessible() && field.getName().equals( methodName ) ) {
                        this.data.addImport( methodName, field  );
                        return;
                    }
                }
            }
        } catch ( ClassNotFoundException e ) {
        }
        // we never managed to make the import, so log an error
        this.results.add( new ImportError( importDescr, -1 ) );
    }

    public void addStaticPackageImport(ImportDescr importDescr) {
        String staticImportEntry = importDescr.getTarget();
        int index = staticImportEntry.lastIndexOf( '.' );
        String className = staticImportEntry.substring(0, index);
        Class cls = null;
        try {
            cls = pkgBuilder.getRootClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) { }
        if (cls == null) results.add( new ImportError( importDescr, -1 ) );

        for (Method method : cls.getDeclaredMethods()) {
            if ((method.getModifiers() | Modifier.STATIC) > 0) {
                this.data.addImport(method.getName(), method);
            }
        }

        for (Field field : cls.getFields()) {
            if (field.isAccessible() && (field.getModifiers() | Modifier.STATIC) > 0) {
                this.data.addImport(field.getName(), field);
                return;
            }
        }
    }

    //    private Map staticFieldImports = new HashMap();
    //    private Map staticMethodImports = new HashMap();

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public void compileAll() {
    }

    public AnalysisResult analyzeExpression(final PackageBuildContext context,
                                                    final BaseDescr descr,
                                                    final Object content,
                                                    final BoundIdentifiers availableIdentifiers) {
        return analyzeExpression(context,
                descr,
                content,
                availableIdentifiers,
                null);
    }

    public AnalysisResult analyzeExpression(final PackageBuildContext context,
                                                    final BaseDescr descr,
                                                    final Object content,
                                                    final BoundIdentifiers availableIdentifiers,
                                                    final Map<String, Class<?>> localTypes) {

        AnalysisResult result = null;
        // the following is required for proper error handling
        BaseDescr temp = context.getParentDescr();
        context.setParentDescr( descr );
        try {
            result = analyzer.analyzeExpression( context,
                                                 (String) content,
                                                 availableIdentifiers,
                                                 localTypes,
                                                 "drools",
                                                 KnowledgeHelper.class );
        } catch ( final Exception e ) {
            copyErrorLocation(e, descr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Unable to determine the used declarations.\n" + e.getMessage() ) );
        } finally {
            // setting it back to original parent descr
            context.setParentDescr( temp );
        }
        return result;
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                               final BaseDescr descr,
                                               final String text,
                                               final BoundIdentifiers availableIdentifiers) {
        return analyzeBlock( context,
                             descr,
                             null,
                             text,
                             availableIdentifiers,
                             null,
                             "drools",
                             KnowledgeHelper.class);
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                               final BaseDescr descr,
                                               final Map interceptors,
                                               final String text,
                                               final BoundIdentifiers availableIdentifiers,
                                               final Map<String, Class<?>> localTypes,
                                               String contextIndeifier,
                                               Class kcontextClass) {

        return analyzer.analyzeExpression( context,
                                           text,
                                           availableIdentifiers,
                                           localTypes,
                                           contextIndeifier,
                                           kcontextClass);
    }

    public MVELCompilationUnit getMVELCompilationUnit(final String expression,
                                                      final AnalysisResult analysis,
                                                      Declaration[] previousDeclarations,
                                                      Declaration[] localDeclarations,
                                                      final Map<String, Class<?>> otherInputVariables,
                                                      final PackageBuildContext context,
                                                      String contextIndeifier,
                                                      Class kcontextClass) {
        Map<String, Class> resolvedInputs = new LinkedHashMap<String, Class>();
        List<String> ids = new ArrayList<String>();
        
        if ( analysis.getBoundIdentifiers().getThisClass() != null || ( localDeclarations  != null && localDeclarations.length > 0 ) ) {
            Class cls = analysis.getBoundIdentifiers().getThisClass();
            ids.add( "this" );
            resolvedInputs.put( "this",
                                 (cls != null) ? cls : Object.class ); // the only time cls is null is in accumumulate's acc/reverse
        }
        ids.add(  contextIndeifier );
        resolvedInputs.put( contextIndeifier, 
                            kcontextClass );
        ids.add(  "kcontext" );
        resolvedInputs.put( "kcontext", 
                            kcontextClass );
        ids.add(  "rule" );
        resolvedInputs.put( "rule", 
                            Rule.class );
        
        List<String> strList = new ArrayList<String>();
        for( Entry<String, Class<?>> e : analysis.getBoundIdentifiers().getGlobals().entrySet() ) {
            strList.add(  e.getKey() );
            ids.add(  e.getKey() );            
            resolvedInputs.put( e.getKey(), e.getValue() );
        }
        String[] globalIdentifiers = strList.toArray( new String[strList.size()] );
        
        strList.clear();
        for( String op : analysis.getBoundIdentifiers().getOperators().keySet() ) {
            strList.add( op );
            ids.add( op );            
            resolvedInputs.put( op, EvaluatorWrapper.class );
        }
        EvaluatorWrapper[] operators = new EvaluatorWrapper[strList.size()];
        for( int i = 0; i < operators.length; i++ ) {
            operators[i] = analysis.getBoundIdentifiers().getOperators().get( strList.get( i ) );
        }
        
        if ( previousDeclarations != null ) {
            for (Declaration decl : previousDeclarations ) {
                if ( analysis.getBoundIdentifiers().getDeclrClasses().containsKey( decl.getIdentifier() ) ) {
                    ids.add( decl.getIdentifier() );
                    resolvedInputs.put( decl.getIdentifier(),
                                        decl.getExtractor().getExtractToClass() );
                }
            }
        }
        
        if ( localDeclarations != null ) {
            for (Declaration decl : localDeclarations ) {
                if ( analysis.getBoundIdentifiers().getDeclrClasses().containsKey( decl.getIdentifier() ) ) {
                    ids.add( decl.getIdentifier() );
                    resolvedInputs.put( decl.getIdentifier(),
                                        decl.getExtractor().getExtractToClass() );
                }
            }
        }
        
        // "not bound" identifiers could be drools, kcontext and rule
        // but in the case of accumulate it could be vars from the "init" section.        
        //String[] otherIdentifiers = otherInputVariables == null ? new String[]{} : new String[otherInputVariables.size()];
        strList = new ArrayList<String>();
        if ( otherInputVariables != null ) {
            MVELAnalysisResult mvelAnalysis = ( MVELAnalysisResult ) analysis;
            for (Entry<String, Class<?>> stringClassEntry : otherInputVariables.entrySet()) {
                if ((!analysis.getNotBoundedIdentifiers().contains(stringClassEntry.getKey()) && !mvelAnalysis.getMvelVariables().keySet().contains(stringClassEntry.getKey())) || "rule".equals(stringClassEntry.getKey())) {
                    // no point including them if they aren't used
                    // and rule was already included
                    continue;
                }
                ids.add(stringClassEntry.getKey());
                strList.add(stringClassEntry.getKey());
                resolvedInputs.put(stringClassEntry.getKey(), stringClassEntry.getValue());
            }
        }
        String[] otherIdentifiers =  strList.toArray( new String[strList.size()]);
        
        String[] inputIdentifiers = new String[resolvedInputs.size()];
        String[] inputTypes = new String[resolvedInputs.size()];
        int i = 0;
        for ( String id : ids ) {
            inputIdentifiers[i] = id;
            inputTypes[i++] = resolvedInputs.get( id ).getName();
        }

        String name;
        if ( context != null && context.getPkg() != null && context.getPkg().getName() != null ) {
            if ( context instanceof RuleBuildContext ) {
                name = context.getPkg().getName() + "." + ((RuleBuildContext) context).getRuleDescr().getClassName();
            } else {
                name = context.getPkg().getName() + ".Unknown";
            }
        } else {
            name = "Unknown";
        }
        return new MVELCompilationUnit( name,
                                        expression,
                                        globalIdentifiers,
                                        operators,
                                        previousDeclarations,
                                        localDeclarations,
                                        otherIdentifiers,
                                        inputIdentifiers,
                                        inputTypes,
                                        languageLevel,
                                        ((MVELAnalysisResult)analysis).isTypesafe()  );
    }

    public EngineElementBuilder getBuilder(final Class clazz) {
        return builders.get( clazz );
    }

    public Map<Class< ? >, EngineElementBuilder> getBuilders() {
        return builders;
    }

    public PatternBuilder getPatternBuilder() {
        return PATTERN_BUILDER;
    }

    public QueryBuilder getQueryBuilder() {
        return QUERY_BUILDER;
    }

    public AccumulateBuilder getAccumulateBuilder() {
        return ACCUMULATE_BUILDER;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return CONSEQUENCE_BUILDER;
    }

    public RuleConditionBuilder getEvalBuilder() {
        return EVAL_BUILDER;
    }

    public FromBuilder getFromBuilder() {
        return FROM_BUILDER;
    }

    public EntryPointBuilder getEntryPointBuilder() {
        return ENTRY_POINT_BUILDER;
    }

    public PredicateBuilder getPredicateBuilder() {
        return PREDICATE_BUILDER;
    }

    public PredicateBuilder getExpressionPredicateBuilder() {
        return PREDICATE_BUILDER;
    }

    public SalienceBuilder getSalienceBuilder() {
        return SALIENCE_BUILDER;
    }

    public EnabledBuilder getEnabledBuilder() {
        return ENABLED_BUILDER;
    }

    public List<KnowledgeBuilderResult> getResults() {
        return results;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return RETURN_VALUE_BUILDER;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        throw new UnsupportedOperationException( "MVELDialect.getRuleClassBuilder is not supported" );
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

    public PackageRegistry getPackageRegistry() {
        return this.packageRegistry;
    }
}
