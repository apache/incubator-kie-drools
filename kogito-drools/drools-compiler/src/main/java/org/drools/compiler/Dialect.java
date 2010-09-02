package org.drools.compiler;

import java.util.List;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.drools.io.Resource;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EnabledBuilder;
import org.drools.rule.builder.EngineElementBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.SalienceBuilder;

/**
 * A Dialect implementation handles the building and execution of code
 * expressions and blocks for a rule. This api is considered unstable, and
 * subject to change. Those wishing to implement their own dialects should look
 * ove the MVEL and Java dialect implementations.
 * 
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

    EngineElementBuilder getBuilder(Class<?> clazz);

    AnalysisResult analyzeExpression(final PackageBuildContext context,
                                     final BaseDescr descr,
                                     final Object content,
                                     final Map<String, Class<?>>[] availableIdentifiers);

    AnalysisResult analyzeBlock(final PackageBuildContext context,
                                final BaseDescr descr,
                                final String text,
                                final Map<String, Class<?>>[] availableIdentifiers);

    void compileAll();

    void addRule(final RuleBuildContext context);

    void addFunction(FunctionDescr functionDescr,
                     TypeResolver typeResolver,
                     Resource resource);

    public void addImport(String importEntry);

    public void addStaticImport(String importEntry);

    List getResults();

    void init(RuleDescr ruleDescr);

    void init(ProcessDescr processDescr);

    /**
     * An interface with the results from the expression/block analysis
     * 
     * @author etirelli
     */
    public static interface AnalysisResult {

        /**
         * Returns the list<String> of all used identifiers
         * 
         * @return
         */
        public List<String> getIdentifiers();

        /**
         * Returns the array of lists<String> of bound identifiers
         * 
         * @return
         */
        public List<String>[] getBoundIdentifiers();

        /**
         * Returns the list<String> of not bounded identifiers
         * 
         * @return
         */
        public List<String> getNotBoundedIdentifiers();

        /**
         * Returns the list<String> of declared local variables
         * 
         * @return
         */
        public List<String> getLocalVariables();

    }

    void postCompileAddFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver);

    void preCompileAddFunction(FunctionDescr functionDescr,
                               TypeResolver typeResolver);

}
