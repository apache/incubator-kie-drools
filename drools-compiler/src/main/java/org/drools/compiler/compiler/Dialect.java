package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.EngineElementBuilder;
import org.drools.compiler.rule.builder.EntryPointBuilder;
import org.drools.compiler.rule.builder.FromBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.PredicateBuilder;
import org.drools.compiler.rule.builder.QueryBuilder;
import org.drools.compiler.rule.builder.ReturnValueBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleClassBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.base.TypeResolver;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.List;
import java.util.Map;

/**
 * A Dialect implementation handles the building and execution of code
 * expressions and blocks for a rule. This api is considered unstable, and
 * subject to change. Those wishing to implement their own dialects should look
 * ove the MVEL and Java dialect implementations.
 */
public interface Dialect {
    String getId();

    // this is needed because some dialects use other dialects
    // to build complex expressions. Example: java dialect uses MVEL
    // to execute complex expressions
    String getExpressionDialectName();

    Map<Class<?>,EngineElementBuilder> getBuilders();

    TypeResolver getTypeResolver();

    SalienceBuilder getSalienceBuilder();

    EnabledBuilder getEnabledBuilder();

    PatternBuilder getPatternBuilder();

    QueryBuilder getQueryBuilder();

    RuleConditionBuilder getEvalBuilder();

    AccumulateBuilder getAccumulateBuilder();

    PredicateBuilder getPredicateBuilder();

    ReturnValueBuilder getReturnValueBuilder();

    ConsequenceBuilder getConsequenceBuilder();

    RuleClassBuilder getRuleClassBuilder();

    FromBuilder getFromBuilder();

    EntryPointBuilder getEntryPointBuilder();

    EngineElementBuilder getBuilder(Class clazz);

    AnalysisResult analyzeExpression(final PackageBuildContext context,
                                     final BaseDescr descr,
                                     final Object content,
                                     final BoundIdentifiers availableIdentifiers);

    AnalysisResult analyzeBlock(final PackageBuildContext context,
                                final BaseDescr descr,
                                final String text,
                                final BoundIdentifiers availableIdentifiers);

    void compileAll();

    void addRule(final RuleBuildContext context);

    void addFunction(FunctionDescr functionDescr,
                     TypeResolver typeResolver,
                     Resource resource);

    public void addImport(ImportDescr importDescr);

    public void addStaticImport(ImportDescr importDescr);

    List<KnowledgeBuilderResult> getResults();

    void clearResults();

    void init(RuleDescr ruleDescr);

    void init(ProcessDescr processDescr);

    void postCompileAddFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver);

    void preCompileAddFunction(FunctionDescr functionDescr,
                               TypeResolver typeResolver);
    
    PackageRegistry getPackageRegistry();

}
