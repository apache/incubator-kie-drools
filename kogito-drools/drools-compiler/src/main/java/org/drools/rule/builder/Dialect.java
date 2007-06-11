package org.drools.rule.builder;

import java.util.List;
import java.util.Map;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TypeResolver;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;

public interface Dialect {
    // this is needed because some dialects use other dialects
    // to build complex expressions. Example: java dialect uses MVEL
    // to execute complex expressions 
    String getExpressionDialectName();

    Map getBuilders();

    TypeResolver getTypeResolver();

    ClassFieldExtractorCache getClassFieldExtractorCache();    
    
    SalienceBuilder getSalienceBuilder();
    
    PatternBuilder getPatternBuilder();    
    
    QueryBuilder getQueryBuilder();
    
    ConditionalElementBuilder getEvalBuilder();

    AccumulateBuilder getAccumulateBuilder();

    PredicateBuilder getPredicateBuilder();
    
    ReturnValueBuilder getReturnValueBuilder();

    ConsequenceBuilder getConsequenceBuilder();

    RuleClassBuilder getRuleClassBuilder();

    FromBuilder getFromBuilder();
    
    Object getBuilder(Class clazz);

    List[] getExpressionIdentifiers(final RuleBuildContext context,
                              final BaseDescr descr,
                              final Object content);
    
    List[] getBlockIdentifiers(final RuleBuildContext context,
                               final BaseDescr descr,
                               final String text);    

    void compileAll();

    void addRule(final RuleBuildContext context);

    void addFunction(final FunctionDescr functionDescr,
                     TypeResolver typeResolver);
    
    public void addImport(String importEntry);
    
    public void addStaticImport(String importEntry);

    List getResults();

    void init(Package pkg);

    void init(RuleDescr ruleDescr);
}
