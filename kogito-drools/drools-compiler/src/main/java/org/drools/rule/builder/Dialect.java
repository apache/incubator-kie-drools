package org.drools.rule.builder;

import org.drools.reteoo.builder.EvalBuilder;

public interface Dialect {
    ConditionalElementBuilder getEvalBuilder();
    
    AccumulateBuilder getAccumulateBuilder();
    
    PredicateBuilder getPredicateBuilder();
    
    ReturnValueBuilder getReturnValueBuilder();
    
    ConsequenceBuilder getConsequenceBuilder();    
    
    RuleClassBuilder getRuleClassBuilder();
    
    FromBuilder getFromBuilder();
    
}
