/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.builder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.ImportError;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.CollectBuilder;
import org.drools.compiler.rule.builder.ConditionalBranchBuilder;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.EngineElementBuilder;
import org.drools.compiler.rule.builder.EntryPointBuilder;
import org.drools.compiler.rule.builder.ForallBuilder;
import org.drools.compiler.rule.builder.FromBuilder;
import org.drools.compiler.rule.builder.GroupElementBuilder;
import org.drools.compiler.rule.builder.NamedConsequenceBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.PredicateBuilder;
import org.drools.compiler.rule.builder.QueryBuilder;
import org.drools.compiler.rule.builder.ReturnValueBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleClassBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.compiler.rule.builder.WindowReferenceBuilder;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.mvel.java.JavaFunctionBuilder;
import org.drools.core.addon.TypeResolver;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.LineMappings;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.StringUtils;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.mvel2.MVEL;
import org.mvel2.optimizers.OptimizerFactory;

import static org.drools.core.rule.constraint.EvaluatorHelper.WM_ARGUMENT;

public class MVELDialect
        implements
        Dialect,
        Externalizable {

    private String id = "mvel";

    private final static String EXPRESSION_DIALECT_NAME = "MVEL";

    protected static final PatternBuilder PATTERN_BUILDER = new PatternBuilder();
    protected static final QueryBuilder QUERY_BUILDER = new QueryBuilder();
    protected static final MVELAccumulateBuilder ACCUMULATE_BUILDER = new MVELAccumulateBuilder();
    protected static final SalienceBuilder SALIENCE_BUILDER = new MVELSalienceBuilder();
    protected static final EnabledBuilder ENABLED_BUILDER = new MVELEnabledBuilder();
    protected static final MVELEvalBuilder EVAL_BUILDER = new MVELEvalBuilder();
    protected static final MVELReturnValueBuilder RETURN_VALUE_BUILDER = new MVELReturnValueBuilder();
    protected static final MVELConsequenceBuilder CONSEQUENCE_BUILDER = new MVELConsequenceBuilder();

    protected static final MVELFromBuilder FROM_BUILDER = new MVELFromBuilder();
    protected static final JavaFunctionBuilder FUNCTION_BUILDER = new JavaFunctionBuilder();
    protected static final CollectBuilder COLLECT_BUILDER = new CollectBuilder();

    protected static final ForallBuilder FORALL_BUILDER = new ForallBuilder();
    protected static final EntryPointBuilder ENTRY_POINT_BUILDER = new EntryPointBuilder();
    protected static final WindowReferenceBuilder WINDOW_REFERENCE_BUILDER = new WindowReferenceBuilder();

    protected static final GroupElementBuilder GE_BUILDER = new GroupElementBuilder();
    protected static final NamedConsequenceBuilder NAMED_CONSEQUENCE_BUILDER = new NamedConsequenceBuilder();
    protected static final ConditionalBranchBuilder CONDITIONAL_BRANCH_BUILDER = new ConditionalBranchBuilder();

    // a map of registered builders
    private static Map<Class<?>, EngineElementBuilder> builders;

    static {
        initBuilder();
    }

    private final Map interceptors = MVELCompilationUnit.INTERCEPTORS;

    protected List<KnowledgeBuilderResult> results;
    // private final JavaFunctionBuilder function = new JavaFunctionBuilder();

    protected MemoryResourceReader src;

    protected InternalKnowledgePackage pkg;
    private MVELDialectConfiguration configuration;

    private PackageRegistry packageRegistry;

    private boolean strictMode;
    private int languageLevel;

    private MVELDialectRuntimeData data;

    static {
        // always use mvel reflective optimizer
        OptimizerFactory.setDefaultOptimizer(OptimizerFactory.SAFE_REFLECTIVE);
    }

    public MVELDialect(ClassLoader rootClassLoader,
                       KnowledgeBuilderConfigurationImpl pkgConf,
                       PackageRegistry pkgRegistry,
                       InternalKnowledgePackage pkg) {
        this(rootClassLoader,
             pkgConf,
             pkgRegistry,
             pkg,
             "mvel");
    }

    public MVELDialect(ClassLoader rootClassLoader,
                       KnowledgeBuilderConfigurationImpl pkgConf,
                       PackageRegistry pkgRegistry,
                       InternalKnowledgePackage pkg,
                       String id) {
        this.id = id;
        this.pkg = pkg;
        this.packageRegistry = pkgRegistry;
        this.configuration = (MVELDialectConfiguration) pkgConf.getDialectConfiguration("mvel");
        setLanguageLevel(this.configuration.getLangLevel());
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
        if (pkg.getDialectRuntimeRegistry().getDialectData(getId()) == null) {
            data = new MVELDialectRuntimeData();
            this.pkg.getDialectRuntimeRegistry().setDialectData(getId(),
                                                                data);
            data.onAdd(this.pkg.getDialectRuntimeRegistry(),
                       rootClassLoader);
        } else {
            data = ( MVELDialectRuntimeData ) this.pkg.getDialectRuntimeRegistry().getDialectData("mvel");
        }

        this.results = new ArrayList<KnowledgeBuilderResult>();
        this.src = new MemoryResourceReader();
        if (this.pkg != null) {
            this.addImport(new ImportDescr(this.pkg.getName() + ".*"));
        }
        this.addImport(new ImportDescr("java.lang.*"));
    }

    public MVELDialect() {
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        results = (List<KnowledgeBuilderResult>) in.readObject();
        src = (MemoryResourceReader) in.readObject();
        pkg = (InternalKnowledgePackage) in.readObject();
        packageRegistry = (PackageRegistry) in.readObject();
        configuration = (MVELDialectConfiguration) in.readObject();
        strictMode = in.readBoolean();
        data = ( MVELDialectRuntimeData ) this.pkg.getDialectRuntimeRegistry().getDialectData("mvel");
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(results);
        out.writeObject(src);
        out.writeObject(pkg);
        out.writeObject(packageRegistry);
        out.writeObject(configuration);
        out.writeBoolean(strictMode);
        out.writeObject(data);
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
        if (builders != null) {
            return;
        }
        reinitBuilder();
    }

    public static void reinitBuilder() {

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        builders = new HashMap<Class<?>, EngineElementBuilder>();

        builders.put(AndDescr.class,
                     GE_BUILDER);

        builders.put(OrDescr.class,
                     GE_BUILDER);

        builders.put(NotDescr.class,
                     GE_BUILDER);

        builders.put(ExistsDescr.class,
                     GE_BUILDER);

        builders.put(PatternDescr.class,
                     PATTERN_BUILDER);

        builders.put(FromDescr.class,
                     FROM_BUILDER);

        builders.put(QueryDescr.class,
                     QUERY_BUILDER);

        builders.put(AccumulateDescr.class,
                     ACCUMULATE_BUILDER);

        builders.put(EvalDescr.class,
                     EVAL_BUILDER);

        builders.put(CollectDescr.class,
                     COLLECT_BUILDER);

        builders.put(ForallDescr.class,
                     FORALL_BUILDER);

        builders.put(FunctionDescr.class,
                     FUNCTION_BUILDER);

        builders.put(EntryPointDescr.class,
                     ENTRY_POINT_BUILDER);

        builders.put(WindowReferenceDescr.class,
                     WINDOW_REFERENCE_BUILDER);

        builders.put(NamedConsequenceDescr.class,
                     NAMED_CONSEQUENCE_BUILDER);

        builders.put(ConditionalBranchDescr.class,
                     CONDITIONAL_BRANCH_BUILDER);
    }

    public void init(RuleDescr ruleDescr) {
        // MVEL:test null to Fix failing test on
        // org.kie.rule.builder.dialect.
        // mvel.MVELConsequenceBuilderTest.testImperativeCodeError()

        // @todo: why is this here, MVEL doesn't compile anything? mdp
        String pkgName = this.pkg == null ? "" : this.pkg.getName();
        final String ruleClassName = DialectUtil.getUniqueLegalName(pkgName,
                                                                    ruleDescr.getName(),
                                                                    ruleDescr.getConsequence().hashCode(),
                                                                    "mvel",
                                                                    "Rule",
                                                                    this.src);
        ruleDescr.setClassName(StringUtils.ucFirst(ruleClassName));
    }

    public void init(final ProcessDescr processDescr) {
        final String processDescrClassName = DialectUtil.getUniqueLegalName(this.pkg.getName(),
                                                                            processDescr.getName(),
                                                                            processDescr.getProcessId().hashCode(),
                                                                            "mvel",
                                                                            "Process",
                                                                            this.src);
        processDescr.setClassName(StringUtils.ucFirst(processDescrClassName));
    }

    public String getExpressionDialectName() {
        return EXPRESSION_DIALECT_NAME;
    }

    public void addRule(RuleBuildContext context) {
        // MVEL: Compiler change
        final RuleDescr ruleDescr = context.getRuleDescr();

        // setup the line mappins for this rule
        final String name = this.pkg.getName() + "." + StringUtils.ucFirst(ruleDescr.getClassName());
        final LineMappings mapping = new LineMappings(name);
        mapping.setStartLine(ruleDescr.getConsequenceLine());
        mapping.setOffset(ruleDescr.getConsequenceOffset());

        context.getPkg().getDialectRuntimeRegistry().getLineMappings().put(name,
                                                                           mapping);
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
        if (importEntry.endsWith(".*")) {
            importEntry = importEntry.substring(0,
                                                importEntry.length() - 2);
            data.addPackageImport(importEntry);
        } else {
            try {
                Class cls = this.packageRegistry.getTypeResolver().resolveType(importEntry);
                data.addImport(cls.getSimpleName(), cls);
            } catch (ClassNotFoundException e) {
                this.results.add(new ImportError(importDescr, 1));
            }
        }
    }

    public void addStaticImport(ImportDescr importDescr) {
        String staticImportEntry = importDescr.getTarget();
        if (staticImportEntry.endsWith("*")) {
            addStaticPackageImport(importDescr);
            return;
        }

        int index = staticImportEntry.lastIndexOf('.');
        String className = staticImportEntry.substring(0, index);
        String methodName = staticImportEntry.substring(index + 1);

        Class cls = loadImportedClass(className);
        if (cls != null) {

            // First try and find a matching method
            for (Method method : cls.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    this.data.addImport(methodName, method);
                    return;
                }
            }
        }

        // we never managed to make the import, so log an error
        this.results.add(new ImportError(importDescr, -1));
    }

    public void addStaticPackageImport(ImportDescr importDescr) {
        String staticImportEntry = importDescr.getTarget();
        int index = staticImportEntry.lastIndexOf('.');
        String className = staticImportEntry.substring(0, index);
        Class cls = loadImportedClass(className);

        if (cls == null) {
            results.add(new ImportError(importDescr, -1));
            return;
        }

        for (Method method : cls.getDeclaredMethods()) {
            if ((method.getModifiers() | Modifier.STATIC) > 0) {
                this.data.addImport(method.getName(), method);
            }
        }
    }

    private Class<?> loadImportedClass(String className) {
        try {
            return pkg.getTypeResolver().resolveType(className);
        } catch (ClassNotFoundException e) { }
        try {
            return this.packageRegistry.getPackageClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) { }
        return null;
    }

    @Override
    public boolean isStrictMode() {
        return strictMode;
    }

    @Override
    public boolean isJava() {
        return false;
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

    public static AnalysisResult analyzeExpression(final PackageBuildContext context,
                                                   final BaseDescr descr,
                                                   final Object content,
                                                   final BoundIdentifiers availableIdentifiers,
                                                   final Map<String, Class<?>> localTypes) {

        AnalysisResult result = null;
        // the following is required for proper error handling
        BaseDescr temp = context.getParentDescr();
        context.setParentDescr(descr);
        try {
            result = MVELExprAnalyzer.analyzeExpression(context,
                                                        (String) content,
                                                        availableIdentifiers,
                                                        localTypes,
                                                        "drools",
                                                        KnowledgeHelper.class);
        } catch (final Exception e) {
            AsmUtil.copyErrorLocation(e, descr);
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 descr,
                                                 null,
                                                 "Unable to determine the used declarations.\n" + e.getMessage()));
        } finally {
            // setting it back to original parent descr
            context.setParentDescr(temp);
        }
        return result;
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                       final BaseDescr descr,
                                       final String text,
                                       final BoundIdentifiers availableIdentifiers) {
        return analyzeBlock(context,
                            text,
                            availableIdentifiers,
                            null,
                            "drools",
                            KnowledgeHelper.class);
    }

    public AnalysisResult analyzeBlock(final PackageBuildContext context,
                                       final String text,
                                       final BoundIdentifiers availableIdentifiers,
                                       final Map<String, Class<?>> localTypes,
                                       String contextIndeifier,
                                       Class kcontextClass) {

        return MVELExprAnalyzer.analyzeExpression(context,
                                                  text,
                                                  availableIdentifiers,
                                                  localTypes,
                                                  contextIndeifier,
                                                  kcontextClass);
    }

    public static MVELCompilationUnit getMVELCompilationUnit(final String expression,
                                                             final AnalysisResult analysis,
                                                             Declaration[] previousDeclarations,
                                                             Declaration[] localDeclarations,
                                                             final Map<String, Class<?>> otherInputVariables,
                                                             final PackageBuildContext context,
                                                             String contextIndeifier,
                                                             Class kcontextClass,
                                                             boolean readLocalsFromTuple,
                                                             MVELCompilationUnit.Scope scope) {
        Map<String, Class> resolvedInputs = new LinkedHashMap<String, Class>();
        List<String> ids = new ArrayList<String>();

        if (analysis.getBoundIdentifiers().getThisClass() != null || (localDeclarations != null && localDeclarations.length > 0)) {
            Class cls = analysis.getBoundIdentifiers().getThisClass();
            ids.add("this");
            resolvedInputs.put("this",
                               (cls != null) ? cls : Object.class); // the only time cls is null is in accumumulate's acc/reverse
        }
        ids.add(contextIndeifier);
        resolvedInputs.put(contextIndeifier,
                           kcontextClass);
        ids.add("kcontext");
        resolvedInputs.put("kcontext",
                           kcontextClass);

        if (scope.hasRule()) {
            ids.add("rule");
            resolvedInputs.put("rule",
                               Rule.class);
        }

        List<String> strList = new ArrayList<String>();
        for (String identifier : analysis.getIdentifiers()) {
            Class<?> type = identifier.equals( WM_ARGUMENT ) ? InternalWorkingMemory.class : analysis.getBoundIdentifiers().resolveVarType(identifier);
            if (type != null) {
                strList.add(identifier);
                ids.add(identifier);
                resolvedInputs.put(identifier, type);
            }
        }
        String[] globalIdentifiers = strList.toArray(new String[strList.size()]);

        strList.clear();
        for (String op : analysis.getBoundIdentifiers().getOperators().keySet()) {
            strList.add(op);
            ids.add(op);
            resolvedInputs.put(op, context.getConfiguration().getComponentFactory().getExpressionProcessor().getEvaluatorWrapperClass());
        }
        EvaluatorWrapper[] operators = new EvaluatorWrapper[strList.size()];
        for (int i = 0; i < operators.length; i++) {
            operators[i] = analysis.getBoundIdentifiers().getOperators().get(strList.get(i));
        }

        if (previousDeclarations != null) {
            for (Declaration decl : previousDeclarations) {
                if (analysis.getBoundIdentifiers().getDeclrClasses().containsKey(decl.getIdentifier())) {
                    ids.add(decl.getIdentifier());
                    resolvedInputs.put(decl.getIdentifier(),
                                       decl.getDeclarationClass());
                }
            }
        }

        if (localDeclarations != null) {
            for (Declaration decl : localDeclarations) {
                if (analysis.getBoundIdentifiers().getDeclrClasses().containsKey(decl.getIdentifier())) {
                    ids.add(decl.getIdentifier());
                    resolvedInputs.put(decl.getIdentifier(),
                                       decl.getDeclarationClass());
                }
            }
        }

        // "not bound" identifiers could be drools, kcontext and rule
        // but in the case of accumulate it could be vars from the "init" section.        
        //String[] otherIdentifiers = otherInputVariables == null ? new String[]{} : new String[otherInputVariables.size()];
        strList = new ArrayList<String>();
        if (otherInputVariables != null) {
            MVELAnalysisResult mvelAnalysis = (MVELAnalysisResult) analysis;
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
        String[] otherIdentifiers = strList.toArray(new String[strList.size()]);

        String[] inputIdentifiers = new String[resolvedInputs.size()];
        String[] inputTypes = new String[resolvedInputs.size()];
        int i = 0;
        for (String id : ids) {
            inputIdentifiers[i] = id;
            inputTypes[i++] = resolvedInputs.get(id).getName();
        }

        String name;
        if (context != null && context.getPkg() != null && context.getPkg().getName() != null) {
            if (context instanceof RuleBuildContext) {
                name = context.getPkg().getName() + "." + ((RuleBuildContext) context).getRuleDescr().getClassName();
            } else {
                name = context.getPkg().getName() + ".Unknown";
            }
        } else {
            name = "Unknown";
        }
        return new MVELCompilationUnit(name,
                                       expression,
                                       globalIdentifiers,
                                       operators,
                                       previousDeclarations,
                                       localDeclarations,
                                       otherIdentifiers,
                                       inputIdentifiers,
                                       inputTypes,
                                       ((MVELAnalysisResult) analysis).isTypesafe(),
                                       readLocalsFromTuple);
    }

    public EngineElementBuilder getBuilder(final Class clazz) {
        return builders.get(clazz);
    }

    public Map<Class<?>, EngineElementBuilder> getBuilders() {
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
        throw new RuntimeException("mvel PredicateBuilder is no longer in use");
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

    public void clearResults() {
        this.results.clear();
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return RETURN_VALUE_BUILDER;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        throw new UnsupportedOperationException("MVELDialect.getRuleClassBuilder is not supported");
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
