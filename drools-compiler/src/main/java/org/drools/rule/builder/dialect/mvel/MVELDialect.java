package org.drools.rule.builder.dialect.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.base.ModifyInterceptor;
import org.drools.base.TypeResolver;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELDebugHandler;
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
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
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
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.rule.builder.dialect.java.JavaFunctionBuilder;
import org.drools.runtime.rule.RuleContext;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.AbstractParser;
import org.mvel2.compiler.ExpressionCompiler;

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

    protected static final GroupElementBuilder             GE_BUILDER                     = new GroupElementBuilder();

    // a map of registered builders
    private static Map<Class< ? >, EngineElementBuilder>   builders;

    static {
        initBuilder();
    }

    private static final MVELExprAnalyzer                  analyzer                       = new MVELExprAnalyzer();

    private Map                                            interceptors;

    protected List                                         results;
    // private final JavaFunctionBuilder function = new JavaFunctionBuilder();

    protected MemoryResourceReader                         src;

    protected Package                                      pkg;
    private MVELDialectConfiguration                       configuration;

    private PackageBuilder                                 pkgBuilder;

    private PackageRegistry                                packageRegistry;

    private Map<String, Object>                             imports;
    private Set<String>                                    packageImports;

    private boolean                                        strictMode;
    private int                                            languageLevel;

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

        this.imports = new HashMap();
        this.packageImports = new HashSet();

        // setting MVEL option directly
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;

        this.interceptors = new HashMap( 1 );
        this.interceptors.put( "Modify",
                               new ModifyInterceptor() );

        this.results = new ArrayList();

        // this.data = new MVELDialectRuntimeData(
        // this.pkg.getDialectRuntimeRegistry() );
        //        
        // this.pkg.getDialectRuntimeRegistry().setDialectData( ID,
        // this.data );

        MVELDialectRuntimeData data = null;
        // initialise the dialect runtime data if it doesn't already exist
        if ( pkg.getDialectRuntimeRegistry().getDialectData( getId() ) == null ) {
            data = new MVELDialectRuntimeData();
            this.pkg.getDialectRuntimeRegistry().setDialectData( getId(),
                                                                 data );
            data.onAdd( this.pkg.getDialectRuntimeRegistry(),
                        this.pkgBuilder.getRootClassLoader() );
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
        imports = (Map) in.readObject();
        packageImports = (Set) in.readObject();
        strictMode = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( interceptors );
        out.writeObject( results );
        out.writeObject( src );
        out.writeObject( pkg );
        out.writeObject( packageRegistry );
        out.writeObject( configuration );
        out.writeObject( imports );
        out.writeObject( packageImports );
        out.writeBoolean( strictMode );
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
    }

    public void init(RuleDescr ruleDescr) {
        // MVEL:test null to Fix failing test on
        // org.drools.rule.builder.dialect.
        // mvel.MVELConsequenceBuilderTest.testImperativeCodeError()

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

    public void addImport(String importEntry) {
        if ( importEntry.endsWith( ".*" ) ) {
            importEntry = importEntry.substring( 0,
                                                 importEntry.length() - 2 );
            this.packageImports.add( importEntry );
        } else {
            try {
                Class cls = this.packageRegistry.getTypeResolver().resolveType( importEntry );
                this.imports.put( cls.getSimpleName(),
                                  cls );
            } catch ( ClassNotFoundException e ) {
                this.results.add( new ImportError( importEntry,
                                                   1 ) );
            }
        }
    }

    public Map<String, Object> getImports() {
        return this.imports;
    }

    public Set<String> getPackgeImports() {
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
            Class cls = this.pkgBuilder.getRootClassLoader().loadClass( className );
            if ( cls != null ) {

                // First try and find a matching method
                for ( Method method : cls.getDeclaredMethods() ) {
                    if ( method.getName().equals( methodName ) ) {
                        this.imports.put( methodName,
                                          method );
                        return;
                    }
                }

                //no matching method, so now try and find a matching public property
                for ( Field field : cls.getFields() ) {
                    if ( field.isAccessible() && field.getName().equals( methodName ) ) {
                        this.imports.put( methodName,
                                          field );
                        return;
                    }
                }
            }
        } catch ( ClassNotFoundException e ) {
        }
        // we never managed to make the import, so log an error
        this.results.add( new ImportError( staticImportEntry,
                                           -1 ) );
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
        return analyzeExpression( context,
                                  descr,
                                  content,
                                  availableIdentifiers,
                                  null );
    }

    public AnalysisResult analyzeExpression(final PackageBuildContext context,
                                                    final BaseDescr descr,
                                                    final Object content,
                                                    final BoundIdentifiers availableIdentifiers,
                                                    final Map<String, Class<?>> localTypes) {

        AnalysisResult result = null;
        try {
            result = analyzer.analyzeExpression( context,
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

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                               final BaseDescr descr,
                                               final String text,
                                               final BoundIdentifiers availableIdentifiers) {
        return analyzeBlock( context,
                             descr,
                             null,
                             text,
                             availableIdentifiers,
                             null );
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                               final BaseDescr descr,
                                               final Map interceptors,
                                               final String text,
                                               final BoundIdentifiers availableIdentifiers,
                                               final Map<String, Class<?>> localTypes) {

        AnalysisResult result = null;
        result = analyzer.analyzeExpression( context,
                                             text,
                                             availableIdentifiers,
                                             localTypes );
        return result;
    }

    public MVELCompilationUnit getMVELCompilationUnit(final String expression,
                                                      final AnalysisResult analysis,
                                                      Declaration[] previousDeclarations,
                                                      Declaration[] localDeclarations,
                                                      final Map<String, Class<?>> otherInputVariables,
                                                      final PackageBuildContext context) {
        String[] pkgImports  = this.packageImports.toArray( new String[this.packageImports.size()] );

        //String[] imports = new String[this.imports.size()];
        List<String> importClasses = new ArrayList<String>();
        List<String> importMethods = new ArrayList<String>();
        List<String> importFields = new ArrayList<String>();
        for ( Iterator it = this.imports.values().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof Class ) {
                importClasses.add( ((Class) object).getName() );
            } else if ( object instanceof Method ) {
                Method method = (Method) object;
                importMethods.add( method.getDeclaringClass().getName() + "." + method.getName() );
            } else {
                Field field = (Field) object;
                importFields.add( field.getDeclaringClass().getName() + "." + field.getName() );
            }
        }

        Map<String, Class> resolvedInputs = new LinkedHashMap<String, Class>();
        List<String> ids = new ArrayList<String>();
        
        if ( analysis.getBoundIdentifiers().getThisClass()  != null ) {
            ids.add( "this" );
            resolvedInputs.put( "this",
                                analysis.getBoundIdentifiers().getThisClass() );
        }
        ids.add(  "drools" );
        resolvedInputs.put( "drools", 
                            KnowledgeHelper.class );
        ids.add(  "kcontext" );
        resolvedInputs.put( "kcontext", 
                            KnowledgeHelper.class );
        ids.add(  "rule" );
        resolvedInputs.put( "rule", 
                            Rule.class );
        
        List<String> strList = new ArrayList();
        int i = 0;
        for( Entry<String, Class<?>> e : analysis.getBoundIdentifiers().getGlobals().entrySet() ) {
            strList.add(  e.getKey() );
            ids.add(  e.getKey() );            
            resolvedInputs.put( e.getKey(), e.getValue() );
        }
        String[] globalIdentifiers = strList.toArray( new String[strList.size()] );
        
        if ( previousDeclarations != null ) {
            for (Declaration decl : previousDeclarations ) {
                if ( analysis.getBoundIdentifiers().getDeclarations().containsKey( decl.getIdentifier() ) ) {
                    ids.add( decl.getIdentifier() );
                    resolvedInputs.put( decl.getIdentifier(),
                                        decl.getExtractor().getExtractToClass() );
                }
            }
        }
        
        if ( localDeclarations != null ) {
            for (Declaration decl : localDeclarations ) {
                if ( analysis.getBoundIdentifiers().getDeclarations().containsKey( decl.getIdentifier() ) ) {
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
            i = 0;
            for ( Iterator it = otherInputVariables.entrySet().iterator(); it.hasNext(); ) {
                Entry entry = (Entry) it.next();
                if ( !analysis.getNotBoundedIdentifiers().contains( entry.getKey() ) || "rule".equals( entry.getKey() ) ) {
                    // no point including them if they aren't used
                    // and rule was already included
                    continue;
                }
                ids.add( (String) entry.getKey() );
                strList.add( (String) entry.getKey() );
                resolvedInputs.put( (String) entry.getKey(),
                                    (Class) entry.getValue() );
            }
        }
        String[] otherIdentifiers =  strList.toArray( new String[strList.size()]);
        
        String[] inputIdentifiers = new String[resolvedInputs.size()];
        String[] inputTypes = new String[resolvedInputs.size()];
        i = 0;
        for ( String id : ids ) {
            inputIdentifiers[i] = id;
            inputTypes[i++] = resolvedInputs.get( id ).getName();
        }

        String name;
        if ( context != null && context.getPkg() != null & context.getPkg().getName() != null ) {
            if ( context instanceof RuleBuildContext ) {
                name = context.getPkg().getName() + "." + ((RuleBuildContext) context).getRuleDescr().getClassName();
            } else {
                name = context.getPkg().getName() + ".Unknown";
            }
        } else {
            name = "Unknown";
        }
        MVELCompilationUnit compilationUnit = new MVELCompilationUnit( name,
                                                                       expression,
                                                                       pkgImports,
                                                                       importClasses.toArray( new String[importClasses.size()] ),
                                                                       importMethods.toArray( new String[importMethods.size()] ),
                                                                       importFields.toArray( new String[importFields.size()] ),
                                                                       globalIdentifiers,
                                                                       previousDeclarations,
                                                                       localDeclarations,
                                                                       otherIdentifiers,
                                                                       inputIdentifiers,
                                                                       inputTypes,
                                                                       languageLevel,
                                                                       context.isTypesafe()  );

        return compilationUnit;
    }

    public EngineElementBuilder getBuilder(final Class clazz) {
        return this.builders.get( clazz );
    }

    public Map<Class< ? >, EngineElementBuilder> getBuilders() {
        return this.builders;
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

    public EnabledBuilder getEnabledBuilder() {
        return this.ENABLED_BUILDER;
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
