package org.drools.compiler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.TypeResolver;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.FromBuilder;
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

    Map getBuilders();

    TypeResolver getTypeResolver();

    ClassFieldAccessorCache getClassFieldExtractorCache();

    SalienceBuilder getSalienceBuilder();

    PatternBuilder getPatternBuilder();

    QueryBuilder getQueryBuilder();

    RuleConditionBuilder getEvalBuilder();

    AccumulateBuilder getAccumulateBuilder();

    PredicateBuilder getPredicateBuilder();

    ReturnValueBuilder getReturnValueBuilder();

    ConsequenceBuilder getConsequenceBuilder();

    ActionBuilder getActionBuilder();

    ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder();

    RuleClassBuilder getRuleClassBuilder();

    ProcessClassBuilder getProcessClassBuilder();

    FromBuilder getFromBuilder();

    EntryPointBuilder getEntryPointBuilder();

    RuleConditionBuilder getBuilder(Class clazz);

    AnalysisResult analyzeExpression(final PackageBuildContext context,
                                     final BaseDescr descr,
                                     final Object content,
                                     final Set[] availableIdentifiers);

    AnalysisResult analyzeBlock(final PackageBuildContext context,
                                final BaseDescr descr,
                                final String text,
                                final Set[] availableIdentifiers);

    void compileAll();

    void addRule(final RuleBuildContext context);

    void addProcess(final ProcessBuildContext context);

    void addFunction(final FunctionDescr functionDescr,
                     TypeResolver typeResolver);

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
        public List getIdentifiers();

        /**
         * Returns the array of lists<String> of bound identifiers
         * 
         * @return
         */
        public List[] getBoundIdentifiers();

        /**
         * Returns the list<String> of not bounded identifiers
         * 
         * @return
         */
        public List getNotBoundedIdentifiers();

        /**
         * Returns the list<String> of declared local variables
         * 
         * @return
         */
        public List getLocalVariables();

    }

    void postCompileAddFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver);

    void preCompileAddFunction(FunctionDescr functionDescr,
                               TypeResolver typeResolver);

}
