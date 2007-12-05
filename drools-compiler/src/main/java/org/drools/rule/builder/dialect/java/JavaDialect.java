package org.drools.rule.builder.dialect.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.ruleflow.common.core.Process;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TypeResolver;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.JavaCompiler;
import org.drools.commons.jci.compilers.JavaCompilerFactory;
import org.drools.commons.jci.compilers.JavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.compiler.Dialect;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.PackageBuilder.ErrorHandler;
import org.drools.compiler.PackageBuilder.FunctionErrorHandler;
import org.drools.compiler.PackageBuilder.RuleErrorHandler;
import org.drools.compiler.PackageBuilder.RuleInvokerErrorHandler;
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
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.LineMappings;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.CollectBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.ForallBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.FunctionBuilder;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.rule.builder.ProcessClassBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.ReturnValueEvaluatorBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELFromBuilder;
import org.drools.rule.builder.dialect.mvel.MVELSalienceBuilder;
import org.drools.compiler.PackageBuilder.ProcessInvokerErrorHandler;
import org.drools.compiler.PackageBuilder.ProcessErrorHandler;
import org.drools.util.StringUtils;

public class JavaDialect
    implements
    Dialect {

    public static final String                ID                          = "java";

    private final static String               EXPRESSION_DIALECT_NAME     = "MVEL";
    // builders
    private final PatternBuilder              pattern                     = new PatternBuilder();
    private final QueryBuilder                query                       = new QueryBuilder();
    private final SalienceBuilder             salience                    = new MVELSalienceBuilder();
    private final JavaAccumulateBuilder       accumulate                  = new JavaAccumulateBuilder();
    private final JavaEvalBuilder             eval                        = new JavaEvalBuilder();
    private final JavaPredicateBuilder        predicate                   = new JavaPredicateBuilder();
    private final JavaReturnValueBuilder      returnValue                 = new JavaReturnValueBuilder();
    private final JavaConsequenceBuilder      consequence                 = new JavaConsequenceBuilder();
    private final JavaActionBuilder           actionBuilder               = new JavaActionBuilder();
    private final ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = new JavaReturnValueEvaluatorBuilder();
    private final JavaRuleClassBuilder        ruleClassBuilder            = new JavaRuleClassBuilder();
    private final JavaProcessClassBuilder     processClassBuilder         = new JavaProcessClassBuilder();
    private final MVELFromBuilder             from                        = new MVELFromBuilder();
    private final JavaFunctionBuilder         function                    = new JavaFunctionBuilder();
    private final CollectBuilder              collect                     = new CollectBuilder();
    private final ForallBuilder               forall                      = new ForallBuilder();

    //
    private KnowledgeHelperFixer              knowledgeHelperFixer;
    private DeclarationTypeFixer              typeFixer;
    private JavaExprAnalyzer                  analyzer;

    private JavaDialectConfiguration          configuration;

    private Package                           pkg;
    private JavaCompiler                      compiler;
    private List                              generatedClassList;
    private MemoryResourceReader              src;
    private PackageStore                      packageStoreWrapper;
    private Map                               errorHandlers;
    private List                              results;

    private TypeResolver                      typeResolver;
    private ClassFieldExtractorCache          classFieldExtractorCache;

    // a map of registered builders
    private Map                               builders;

    public JavaDialect() {

    }

    public void init(PackageBuilder builder) {
        this.pkg = builder.getPackage();
        this.configuration = (JavaDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "java" );
        this.typeResolver = builder.getTypeResolver();
        this.classFieldExtractorCache = builder.getClassFieldExtractorCache();

        this.knowledgeHelperFixer = new KnowledgeHelperFixer();
        this.typeFixer = new DeclarationTypeFixer();
        this.analyzer = new JavaExprAnalyzer();

        if ( pkg != null ) {
            init( pkg );
        }

        initBuilder();

        loadCompiler();
    }

    public void initBuilder() {
        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        this.builders = new HashMap();

        this.builders.put( CollectDescr.class,
                           collect );

        this.builders.put( ForallDescr.class,
                           forall );

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

        this.builders.put( QueryDescr.class,
                           getQueryBuilder() );

        this.builders.put( FromDescr.class,
                           getFromBuilder() );

        this.builders.put( AccumulateDescr.class,
                           getAccumulateBuilder() );

        this.builders.put( EvalDescr.class,
                           getEvalBuilder() );
    }

    public Map getBuilders() {
        return this.builders;
    }

    public void init(final Package pkg) {

        this.pkg = pkg;
        //TODO Consider lazy init for these as they might have been initialized from the constructor and maybe used meanwhile
        this.errorHandlers = new HashMap();
        this.results = new ArrayList();

        this.src = new MemoryResourceReader();

        this.generatedClassList = new ArrayList();

        this.packageStoreWrapper = new PackageStore( pkg.getPackageCompilationData(),
                                                     this.results );
    }

    public void init(final RuleDescr ruleDescr) {
        final String ruleClassName = getUniqueLegalName( this.pkg.getName(),
                                                         ruleDescr.getName(),
                                                         "java",
                                                         this.src );
        ruleDescr.setClassName( StringUtils.ucFirst( ruleClassName ) );
    }    

    public String getExpressionDialectName() {
        return EXPRESSION_DIALECT_NAME;
    }

    public AnalysisResult analyzeExpression(final PackageBuildContext context,
                                            final BaseDescr descr,
                                            final Object content,
                                            final Set[] availableIdentifiers) {
        JavaAnalysisResult result = null;
        try {
            //new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()}
            result = this.analyzer.analyzeExpression( (String) content,
                                                      availableIdentifiers );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          e,
                                                          "Unable to determine the used declarations.\n" + e ) );
        }
        return result;
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                       final BaseDescr descr,
                                       final String text,
                                       final Set[] availableIdentifiers) {
        JavaAnalysisResult result = null;
        try {
            // new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} 
            result = this.analyzer.analyzeBlock( text,
                                                 availableIdentifiers );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          e,
                                                          "Unable to determine the used declarations.\n" + e ) );
        }
        return result;
    }

    /**
     * Returns the current type resolver instance
     * @return
     */
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    /**
     * Returns the cache of field extractors
     * @return
     */
    public ClassFieldExtractorCache getClassFieldExtractorCache() {
        return this.classFieldExtractorCache;
    }

    /**
     * Returns the Knowledge Helper Fixer
     * @return
     */
    public KnowledgeHelperFixer getKnowledgeHelperFixer() {
        return this.knowledgeHelperFixer;
    }

    /**
     * @return the typeFixer
     */
    public DeclarationTypeFixer getTypeFixer() {
        return this.typeFixer;
    }

    public RuleConditionBuilder getBuilder(final Class clazz) {
        return (RuleConditionBuilder) this.builders.get( clazz );
    }

    public PatternBuilder getPatternBuilder() {
        return this.pattern;
    }

    public QueryBuilder getQueryBuilder() {
        return this.query;
    }

    public SalienceBuilder getSalienceBuilder() {
        return this.salience;
    }

    public AccumulateBuilder getAccumulateBuilder() {
        return this.accumulate;
    }

    public RuleConditionBuilder getEvalBuilder() {
        return this.eval;
    }

    public PredicateBuilder getPredicateBuilder() {
        return this.predicate;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return this.returnValue;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.consequence;
    }

    public ActionBuilder getActionBuilder() {
        return this.actionBuilder;
    }

    public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
        return this.returnValueEvaluatorBuilder;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        return this.ruleClassBuilder;
    }

    public ProcessClassBuilder getProcessClassBuilder() {
        return this.processClassBuilder;
    }

    public FunctionBuilder getFunctionBuilder() {
        return this.function;
    }

    public FromBuilder getFromBuilder() {
        return this.from;
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

        final CompilationResult result = this.compiler.compile( classes,
                                                                this.src,
                                                                this.packageStoreWrapper,
                                                                this.pkg.getPackageCompilationData().getClassLoader() );

        //this will sort out the errors based on what class/file they happened in
        if ( result.getErrors().length > 0 ) {
            for ( int i = 0; i < result.getErrors().length; i++ ) {
                final CompilationProblem err = result.getErrors()[i];

                //                System.out.println("Line: "+err.getStartLine());
                //                LineMappings maps = this.pkg.getPackageCompilationData().getLineMappings( err.getFileName().replace( '/', '.' ).substring( 0, err.getFileName().length() - 5 ) );
                //                int line = err.getStartLine() + maps.getStartLine() - maps.getOffset() -1;
                //                System.out.println("Map:  "+line);
                final ErrorHandler handler = (ErrorHandler) this.errorHandlers.get( err.getFileName() );
                if ( handler instanceof RuleErrorHandler ) {
                    final RuleErrorHandler rh = (RuleErrorHandler) handler;
                }
                handler.addError( err );
            }

            final Collection errors = this.errorHandlers.values();
            for ( final Iterator iter = errors.iterator(); iter.hasNext(); ) {
                final ErrorHandler handler = (ErrorHandler) iter.next();
                if ( handler.isInError() ) {
                    if ( !(handler instanceof RuleInvokerErrorHandler) ) {
                        this.results.add( handler.getError() );
                    } else {
                        //we don't really want to report invoker errors.
                        //mostly as they can happen when there is a syntax error in the RHS
                        //and otherwise, it is a programmatic error in drools itself.
                        //throw new RuntimeException( "Warning: An error occurred compiling a semantic invoker. Errors should have been reported elsewhere." + handler.getError() );
                    }
                }
            }
        }

        // We've compiled everthing, so clear it for the next set of additions
        this.generatedClassList.clear();
    }

    /**
     * This will add the rule for compiling later on.
     * It will not actually call the compiler
     */
    public void addRule(final RuleBuildContext context) {
        RuleClassBuilder classBuilder = context.getDialect().getRuleClassBuilder();

        String ruleClass = classBuilder.buildRule( context );
        // return if there is no ruleclass name;
        if ( ruleClass == null ) {
            return;
        }

        final Rule rule = context.getRule();
        final RuleDescr ruleDescr = context.getRuleDescr();

        // The compilation result is for the entire rule, so difficult to associate with any descr
        addClassCompileTask( this.pkg.getName() + "." + ruleDescr.getClassName(),
                             ruleDescr,
                             ruleClass,
                             this.src,
                             new RuleErrorHandler( ruleDescr,
                                                   rule,
                                                   "Rule Compilation error" ) );

        for ( final Iterator it = context.getInvokers().keySet().iterator(); it.hasNext(); ) {
            final String className = (String) it.next();

            // Check if an invoker - returnvalue, predicate, eval or consequence has been associated
            // If so we add it to the PackageCompilationData as it will get wired up on compilation
            final Object invoker = context.getInvokerLookups().get( className );
            if ( invoker != null ) {
                this.pkg.getPackageCompilationData().putInvoker( className,
                                                                 invoker );
            }
            final String text = (String) context.getInvokers().get( className );

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

        context.getPkg().getPackageCompilationData().getLineMappings().put( name,
                                                                            mapping );

    }

    /**
     * This will add the rule for compiling later on.
     * It will not actually call the compiler
     */
    public void addProcess(final ProcessBuildContext context) {
        ProcessClassBuilder classBuilder = context.getDialect().getProcessClassBuilder();

        String processClass = classBuilder.buildRule( context );

        final Process process = context.getProcess();
        final ProcessDescr processDescr = context.getProcessDescr();

        // The compilation result is for the entire rule, so difficult to associate with any descr
        addClassCompileTask( this.pkg.getName() + "." + processDescr.getClassName(),
                             processDescr,
                             processClass,
                             this.src,
                             new ProcessErrorHandler( processDescr,
                                                      process,
                                                      "Process Compilation error" ) );

        for ( final Iterator it = context.getInvokers().keySet().iterator(); it.hasNext(); ) {
            final String className = (String) it.next();

            // Check if an invoker - Action has been associated
            // If so we add it to the PackageCompilationData as it will get wired up on compilation
            final Object invoker = context.getInvokerLookups().get( className );
            if ( invoker != null ) {
                this.pkg.getPackageCompilationData().putInvoker( className,
                                                                 invoker );
            }
            final String text = (String) context.getInvokers().get( className );

            final BaseDescr descr = (BaseDescr) context.getDescrLookups().get( className );
            addClassCompileTask( className,
                                 descr,
                                 text,
                                 this.src,
                                 new ProcessInvokerErrorHandler( processDescr,
                                                                 process,
                                                                 "Unable to generate action invoker." ) );

        }

        // setup the line mappins for this rule
        // @TODO must setup mappings
        //        final String name = this.pkg.getName() + "." + StringUtils.ucFirst( ruleDescr.getClassName() );
        //        final LineMappings mapping = new LineMappings( name );
        //        mapping.setStartLine( ruleDescr.getConsequenceLine() );
        //        mapping.setOffset( ruleDescr.getConsequenceOffset() );
        //
        //        context.getPkg().getPackageCompilationData().getLineMappings().put( name,
        //                                                                           mapping );

    }

    public void addFunction(final FunctionDescr functionDescr,
                            final TypeResolver typeResolver) {

        //System.out.println( functionDescr + " : " + typeResolver );
        final String functionClassName = this.pkg.getName() + "." + StringUtils.ucFirst( functionDescr.getName() );
        this.pkg.addFunction( functionDescr.getName() );

        final String functionSrc = getFunctionBuilder().build( this.pkg,
                                                               functionDescr,
                                                               typeResolver,
                                                               pkg.getPackageCompilationData().getLineMappings(),
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
        pkg.getPackageCompilationData().getLineMappings().put( functionClassName,
                                                               mapping );
    }

    /**
     * This adds a compile "task" for when the compiler of
     * semantics (JCI) is called later on with compileAll()\
     * which actually does the compiling.
     * The ErrorHandler is required to map the errors back to the
     * element that caused it.
     */
    private void addClassCompileTask(final String className,
                                     final BaseDescr descr,
                                     final String text,
                                     final MemoryResourceReader src,
                                     final ErrorHandler handler) {

        final String fileName = className.replace( '.',
                                                   '/' ) + ".java";

        src.add( fileName,
                 text.getBytes() );

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

    public void addImport(String importEntry) {
        // we don't need to do anything here
    }

    public void addStaticImport(String staticImportEntry) {
        // we don't need to do anything here
    }

    public List getResults() {
        return this.results;
    }

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     *
     * TODO: move out to shared utility class
     *
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    public static String getUniqueLegalName(final String packageName,
                                            final String name,
                                            final String ext,
                                            final ResourceReader src) {
        // replaces all non alphanumeric or $ chars with _
        String newName = "Rule_" + name.replaceAll( "[[^\\w]$]",
                                                    "_" );

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists ) {

            counter++;
            final String fileName = packageName.replaceAll( "\\.",
                                                            "/" ) + newName + "_" + counter + ext;

            //MVEL:test null to Fix failing test on org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilderTest.testImperativeCodeError()
            exists = src != null && src.isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 ) {
            newName = newName + "_" + counter;
        }

        return newName;
    }

    public String getId() {
        return ID;
    }
}
