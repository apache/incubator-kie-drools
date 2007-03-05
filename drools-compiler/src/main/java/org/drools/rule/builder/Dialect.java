package org.drools.rule.builder;

import java.util.List;

import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.builder.EvalBuilder;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public interface Dialect {
    ConditionalElementBuilder getEvalBuilder();
    
    AccumulateBuilder getAccumulateBuilder();
    
    PredicateBuilder getPredicateBuilder();
    
    ReturnValueBuilder getReturnValueBuilder();
    
    ConsequenceBuilder getConsequenceBuilder();    
    
    RuleClassBuilder getRuleClassBuilder();
    
    FromBuilder getFromBuilder();
    
    void compileAll();
    
    void addRuleSemantics(final RuleBuilder builder,
                          final Rule rule,
                          final RuleDescr ruleDescr);
    
    void addFunction(final FunctionDescr functionDescr,
                     TypeResolver typeResolver);
    
    List getResults();
    
    void init(Package pkg);
    
    void init(RuleDescr ruleDescr);
}
