package org.drools.rule.builder.dialect.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.drools.base.TypeResolver;
import org.drools.builder.KnowledgeBuilderResult;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.JavaCompiler;
import org.drools.commons.jci.compilers.JavaCompilerFactory;
import org.drools.commons.jci.compilers.JavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.compiler.PackageBuilder.ErrorHandler;
import org.drools.compiler.PackageBuilder.FunctionErrorHandler;
import org.drools.compiler.PackageBuilder.RuleErrorHandler;
import org.drools.compiler.PackageBuilder.RuleInvokerErrorHandler;
import org.drools.compiler.PackageBuilder.SrcErrorHandler;
import org.drools.core.util.StringUtils;
import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;
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
import org.drools.rule.Function;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.LineMappings;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.CollectBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EnabledBuilder;
import org.drools.rule.builder.EngineElementBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.ForallBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.FunctionBuilder;
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
import org.drools.rule.builder.dialect.asm.*;
import org.drools.rule.builder.dialect.mvel.MVELEnabledBuilder;
import org.drools.rule.builder.dialect.mvel.MVELFromBuilder;
import org.drools.rule.builder.dialect.mvel.MVELSalienceBuilder;

import static org.drools.rule.builder.dialect.DialectUtil.getUniqueLegalName;

public class JavaDialect
    implements
    Dialect {

    public static final String                   ID                            = "java";

    private final static String                  EXPRESSION_DIALECT_NAME       = "mvel";

    // builders
    protected static PatternBuilder              PATTERN_BUILDER               = new PatternBuilder();
    protected static QueryBuilder                QUERY_BUILDER                 = new QueryBuilder();
    protected static SalienceBuilder             SALIENCE_BUILDER              = new MVELSalienceBuilder();
    protected static EnabledBuilder              ENABLED_BUILDER               = new MVELEnabledBuilder();
    protected static JavaAccumulateBuilder       ACCUMULATE_BUILDER            = new JavaAccumulateBuilder();

//    public static RuleConditionBuilder        EVAL_BUILDER                  = new JavaEvalBuilder();
//    public static RuleConditionBuilder        EVAL_BUILDER                  = new ASMEvalBuilder();
    protected static RuleConditionBuilder        EVAL_BUILDER                  = new ASMEvalStubBuilder();

//    public static PredicateBuilder            PREDICATE_BUILDER             = new JavaPredicateBuilder();
//    public static PredicateBuilder            PREDICATE_BUILDER             = new ASMPredicateBuilder();
    protected static PredicateBuilder            PREDICATE_BUILDER             = new ASMPredicateStubBuilder();

//    public static ReturnValueBuilder          RETURN_VALUE_BUILDER          = new JavaReturnValueBuilder();
//    public static ReturnValueBuilder          RETURN_VALUE_BUILDER          = new ASMReturnValueBuilder();
    protected static ReturnValueBuilder          RETURN_VALUE_BUILDER          = new ASMReturnValueStubBuilder();

//    public static ConsequenceBuilder          CONSEQUENCE_BUILDER           = new JavaConsequenceBuilder();
//    public static ConsequenceBuilder          CONSEQUENCE_BUILDER           = new ASMConsequenceBuilder();
    protected static ConsequenceBuilder          CONSEQUENCE_BUILDER           = new ASMConsequenceStubBuilder();

    protected static JavaRuleClassBuilder        RULE_CLASS_BUILDER            = new JavaRuleClassBuilder();
    protected static MVELFromBuilder             FROM_BUILDER                  = new MVELFromBuilder();
    protected static JavaFunctionBuilder         FUNCTION_BUILDER              = new JavaFunctionBuilder();
    protected static CollectBuilder              COLLECT_BUIDER                = new CollectBuilder();
    protected static ForallBuilder               FORALL_BUILDER                = new ForallBuilder();
    protected static EntryPointBuilder           ENTRY_POINT_BUILDER           = new EntryPointBuilder();
    protected static WindowReferenceBuilder      WINDOW_REFERENCE_BUILDER      = new WindowReferenceBuilder();
    protected static GroupElementBuilder         GE_BUILDER                    = new GroupElementBuilder();

    // a map of registered builders
    private static Map<Class<?>, EngineElementBuilder> builders;

    public static void setPatternBuilder( PatternBuilder PATTERN_BUILDER ) {
        JavaDialect.PATTERN_BUILDER = PATTERN_BUILDER;
    }

    public static void setGEBuilder( GroupElementBuilder GE_BUILDER ) {
        JavaDialect.GE_BUILDER = GE_BUILDER;
    }

    static {
        initBuilder();
    }

    //
    private static final JavaExprAnalyzer            analyzer                      = new JavaExprAnalyzer();

    private final JavaDialectConfiguration           configuration;

    private JavaCompiler                             compiler;
    private final Package                            pkg;
    private final List<String>                       generatedClassList;
    private final MemoryResourceReader               src;
    private final PackageStore                       packageStoreWrapper;
    private final Map<String, ErrorHandler>          errorHandlers;
    private final List<KnowledgeBuilderResult>       results;
    private final PackageBuilder                     packageBuilder;

    private final PackageRegistry                    packageRegistry;

    public JavaDialect(PackageBuilder builder,
                       PackageRegistry pkgRegistry,
                       Package pkg) {
        this.packageBuilder = builder;
        this.pkg = pkg;
        this.packageRegistry = pkgRegistry;

        this.configuration = (JavaDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "java" );

        this.errorHandlers = new HashMap<String, ErrorHandler>();
        this.results = new ArrayList<KnowledgeBuilderResult>();

        this.src = new MemoryResourceReader();

        this.generatedClassList = new ArrayList<String>();

        JavaDialectRuntimeData data;

        // initialie the dialect runtime data if it doesn't already exist
        if ( pkg.getDialectRuntimeRegistry().getDialectData( ID ) == null ) {
            data = new JavaDialectRuntimeData();
            this.pkg.getDialectRuntimeRegistry().setDialectData( ID,
                                                                 data );
            data.onAdd( this.pkg.getDialectRuntimeRegistry(),
                        this.packageBuilder.getRootClassLoader() );
        } else {
            data = (JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( ID );
        }

        this.packageStoreWrapper = new PackageStore( data,
                                                     this.results );

        loadCompiler();
    }

    public static synchronized void initBuilder() {
        if ( builders != null ) {
            return;
        }
        reinitBuilder();
    }

    public static synchronized void reinitBuilder() {

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        builders = new HashMap<Class<?>, EngineElementBuilder>();

        builders.put( CollectDescr.class,
                      COLLECT_BUIDER );

        builders.put( ForallDescr.class,
                      FORALL_BUILDER );

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

        builders.put( QueryDescr.class,
                      QUERY_BUILDER );

        builders.put( FromDescr.class,
                      FROM_BUILDER );

        builders.put( AccumulateDescr.class,
                      ACCUMULATE_BUILDER );

        builders.put( EvalDescr.class,
                      EVAL_BUILDER );

        builders.put( EntryPointDescr.class,
                      ENTRY_POINT_BUILDER );

        builders.put( WindowReferenceDescr.class,
                      WINDOW_REFERENCE_BUILDER );
    }

    public Map<Class<?>, EngineElementBuilder> getBuilders() {
        return builders;
    }

    public void init(final RuleDescr ruleDescr) {
        final String ruleClassName = getUniqueLegalName( this.pkg.getName(),
                                                         ruleDescr.getName(),
                                                         "java",
                                                         "Rule",
                                                         this.src );
        ruleDescr.setClassName( StringUtils.ucFirst( ruleClassName ) );
    }

    public void init(final ProcessDescr processDescr) {
        final String processDescrClassName = getUniqueLegalName( this.pkg.getName(),
                                                                 processDescr.getName(),
                                                                 "java",
                                                                 "Process",
                                                                 this.src );
        processDescr.setClassName( StringUtils.ucFirst( processDescrClassName ) );
    }

    public String getExpressionDialectName() {
        return EXPRESSION_DIALECT_NAME;
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
                                            final Map<String,Class<?>> localTypes ) {
        JavaAnalysisResult result = null;
        try {
            result = analyzer.analyzeExpression((String) content,
                    availableIdentifiers);
        } catch ( final Exception e ) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                    descr,
                    e,
                    "Unable to determine the used declarations.\n" + e));
        }
        return result;
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                       final BaseDescr descr,
                                       final String text,
                                       final BoundIdentifiers availableIdentifiers) {
        JavaAnalysisResult result = null;
        try {
            result = analyzer.analyzeBlock( text,
                                            availableIdentifiers );
        } catch ( final Exception e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          e,
                                                          "Unable to determine the used declarations.\n" + e ) );
        }
        return result;
    }

    /**
     * Returns the current type resolver instance
     *
     * @return
     */
    public TypeResolver getTypeResolver() {
        return this.packageRegistry.getTypeResolver();
    }

    public RuleConditionBuilder getBuilder(final Class clazz) {
        return (RuleConditionBuilder) builders.get( clazz );
    }

    public PatternBuilder getPatternBuilder() {
        return PATTERN_BUILDER;
    }

    public QueryBuilder getQueryBuilder() {
        return QUERY_BUILDER;
    }

    public SalienceBuilder getSalienceBuilder() {
        return SALIENCE_BUILDER;
    }

    public EnabledBuilder getEnabledBuilder() {
        return ENABLED_BUILDER;
    }

    public AccumulateBuilder getAccumulateBuilder() {
        return ACCUMULATE_BUILDER;
    }

    public RuleConditionBuilder getEvalBuilder() {
        return EVAL_BUILDER;
    }

    public PredicateBuilder getPredicateBuilder() {
        return PREDICATE_BUILDER;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return RETURN_VALUE_BUILDER;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return CONSEQUENCE_BUILDER;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        return RULE_CLASS_BUILDER;
    }

    public FunctionBuilder getFunctionBuilder() {
        return FUNCTION_BUILDER;
    }

    public FromBuilder getFromBuilder() {
        return FROM_BUILDER;
    }

    public EntryPointBuilder getEntryPointBuilder() {
        return ENTRY_POINT_BUILDER;
    }

    /**
     * This actually triggers the compiling of all the resources.
     * Errors are mapped back to the element that originally generated the semantic
     * code.
     */
    public void compileAll() {
        if ( this.generatedClassList.isEmpty() ) {
            return;
        }
        final String[] classes = new String[this.generatedClassList.size()];
        this.generatedClassList.toArray( classes );

        File dumpDir = this.configuration.getPackageBuilderConfiguration().getDumpDir();
        if ( dumpDir != null ) {
            dumpResources( classes,
                           dumpDir );
        }

        final CompilationResult result = this.compiler.compile( classes,
                                                                this.src,
                                                                this.packageStoreWrapper,
                                                                this.packageBuilder.getRootClassLoader() );

        //this will sort out the errors based on what class/file they happened in
        if ( result.getErrors().length > 0 ) {
            for ( int i = 0; i < result.getErrors().length; i++ ) {
                final CompilationProblem err = result.getErrors()[i];
                final ErrorHandler handler = this.errorHandlers.get( err.getFileName() );
                handler.addError( err );
            }

            final Collection errors = this.errorHandlers.values();
            for (Object error : errors) {
                final ErrorHandler handler = (ErrorHandler) error;
                if (handler.isInError()) {
                    this.results.add(handler.getError());
                }
            }
        }

        // We've compiled everthing, so clear it for the next set of additions
        this.generatedClassList.clear();
    }

    /**
     * @param classes
     * @param dumpDir
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void dumpResources(final String[] classes,
                               File dumpDir) {
        for (String aClass : classes) {
            File target = new File(dumpDir,
                    aClass);
            FileOutputStream out = null;
            try {
                File parent = target.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                target.createNewFile();
                out = new FileOutputStream(target);
                out.write(this.src.getBytes(aClass));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * This will add the rule for compiling later on.
     * It will not actually call the compiler
     */
    public void addRule(final RuleBuildContext context) {
        final Rule rule = context.getRule();
        final RuleDescr ruleDescr = context.getRuleDescr();

        RuleClassBuilder classBuilder = context.getDialect().getRuleClassBuilder();

        String ruleClass = classBuilder.buildRule( context );
        // return if there is no ruleclass name;
        if ( ruleClass == null ) {
            return;
        }

        // The compilation result is for the entire rule, so difficult to associate with any descr
        addClassCompileTask( this.pkg.getName() + "." + ruleDescr.getClassName(),
                             ruleDescr,
                             ruleClass,
                             this.src,
                             new RuleErrorHandler( ruleDescr,
                                                   rule,
                                                   "Rule Compilation error" ) );

        JavaDialectRuntimeData data = (JavaDialectRuntimeData) this.pkg.getDialectRuntimeRegistry().getDialectData( ID );

        for ( Map.Entry<String, String> invokers : context.getInvokers().entrySet() ) {
            final String className = invokers.getKey();

            // Check if an invoker - returnvalue, predicate, eval or consequence has been associated
            // If so we add it to the PackageCompilationData as it will get wired up on compilation
            final Object invoker = context.getInvokerLookups().get( className );
            if ( invoker != null ) {
                data.putInvoker( className,
                                 invoker );
            }
            final String text = invokers.getValue();

            final BaseDescr descr = (BaseDescr) context.getDescrLookups().get( className );
            addClassCompileTask( className,
                                 descr,
                                 text,
                                 this.src,
                                 new RuleInvokerErrorHandler( descr,
                                                              rule,
                                                              "Unable to generate rule invoker." ) );

        }

        // setup the line mappins for this rule
        final String name = this.pkg.getName() + "." + StringUtils.ucFirst( ruleDescr.getClassName() );
        final LineMappings mapping = new LineMappings( name );
        mapping.setStartLine( ruleDescr.getConsequenceLine() );
        mapping.setOffset( ruleDescr.getConsequenceOffset() );

        this.pkg.getDialectRuntimeRegistry().getLineMappings().put( name,
                                                                    mapping );

    }

    public void addFunction(final FunctionDescr functionDescr,
                            final TypeResolver typeResolver,
                            final Resource resource) {

        //System.out.println( functionDescr + " : " + typeResolver );
        final String functionClassName = this.pkg.getName() + "." + StringUtils.ucFirst( functionDescr.getName() );
        functionDescr.setClassName( functionClassName );

        this.pkg.addStaticImport( functionClassName + "." + functionDescr.getName() );

        Function function = new Function( functionDescr.getNamespace(), functionDescr.getName(), ID );
        if ( resource != null && ((InternalResource) resource).hasURL() ) {
            function.setResource( resource );
        }
        this.pkg.addFunction( function );

        final String functionSrc = getFunctionBuilder().build( this.pkg,
                                                               functionDescr,
                                                               typeResolver,
                                                               this.pkg.getDialectRuntimeRegistry().getLineMappings(),
                                                               this.results );

        addClassCompileTask( functionClassName,
                             functionDescr,
                             functionSrc,
                             this.src,
                             new FunctionErrorHandler( functionDescr,
                                                       "Function Compilation error" ) );

        final LineMappings mapping = new LineMappings( functionClassName );
        mapping.setStartLine( functionDescr.getLine() );
        mapping.setOffset( functionDescr.getOffset() );
        this.pkg.getDialectRuntimeRegistry().getLineMappings().put( functionClassName,
                                                                    mapping );
    }

    public void preCompileAddFunction(FunctionDescr functionDescr,
                                      TypeResolver typeResolver) {
        final String functionClassName = this.pkg.getName() + "." + StringUtils.ucFirst( functionDescr.getName() );
        this.pkg.addStaticImport( functionClassName + "." + functionDescr.getName() );
    }

    public void postCompileAddFunction(FunctionDescr functionDescr,
                                       TypeResolver typeResolver) {
        final String functionClassName = this.pkg.getName() + "." + StringUtils.ucFirst( functionDescr.getName() );
        ImportDescr importDescr = new ImportDescr(functionClassName + "." + functionDescr.getName());
        importDescr.setResource(functionDescr.getResource());
        importDescr.setNamespace(functionDescr.getNamespace());
        this.packageRegistry.addStaticImport( importDescr );
    }

    public void addSrc(String resourceName,
                       byte[] content) {

        src.add( resourceName,
                 content );

        this.errorHandlers.put( resourceName,
                                new SrcErrorHandler( "Src compile error" ) );

        addClassName( resourceName );

    }

    /**
     * This adds a compile "task" for when the compiler of
     * semantics (JCI) is called later on with compileAll()\
     * which actually does the compiling.
     * The ErrorHandler is required to map the errors back to the
     * element that caused it.
     */
    public void addClassCompileTask(final String className,
                                     final BaseDescr descr,
                                     final String text,
                                     final MemoryResourceReader src,
                                     final ErrorHandler handler) {

        final String fileName = className.replace( '.',
                                                   '/' ) + ".java";

        if (src != null) {
            src.add( fileName,
                     text.getBytes() );
        } else {
            this.src.add( fileName,
                          text.getBytes() );
        }

        this.errorHandlers.put( fileName,
                                handler );

        addClassName( fileName );
    }

    public void addClassName(final String className) {
        this.generatedClassList.add( className );
    }

    private void loadCompiler() {
        switch ( this.configuration.getCompiler() ) {
            case JavaDialectConfiguration.JANINO : {
                this.compiler = JavaCompilerFactory.getInstance().createCompiler( "janino" );
                break;
            }
            case JavaDialectConfiguration.ECLIPSE :
            default : {
                this.compiler = JavaCompilerFactory.getInstance().createCompiler( "eclipse" );
                JavaCompilerSettings settings = this.compiler.createDefaultSettings();

                String lngLevel = this.configuration.getJavaLanguageLevel();
                settings.setTargetVersion( lngLevel );

                settings.setSourceVersion( lngLevel );
                break;
            }
        }
    }

    public void addImport(ImportDescr importDescr) {
        // we don't need to do anything here
    }

    public void addStaticImport(ImportDescr importDescr) {
        // we don't need to do anything here
    }

    public List<KnowledgeBuilderResult> getResults() {
        return this.results;
    }

    public String getId() {
        return ID;
    }

    public PackageRegistry getPackageRegistry() {
        return this.packageRegistry;
    }

}
