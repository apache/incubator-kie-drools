package org.drools.rule.builder.dialect.java;

import org.drools.reteoo.builder.EvalBuilder;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.Dialect;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuilder;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.dialect.mvel.MVELFromBuilder;

public class JavaDialect
    implements
    Dialect {

    private JavaAccumulateBuilder accumulate = new JavaAccumulateBuilder();
    private JavaConsequenceBuilder consequence = new JavaConsequenceBuilder();
    private JavaEvalBuilder eval = new JavaEvalBuilder();
    private JavaPredicateBuilder predicate = new JavaPredicateBuilder();
    private JavaReturnValueBuilder returnValue = new JavaReturnValueBuilder();
    private JavaRuleClassBuilder rule = new JavaRuleClassBuilder();
    private MVELFromBuilder from = new MVELFromBuilder();
    
    
    public AccumulateBuilder getAccumulateBuilder() {
        return this.accumulate;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.consequence;
    }

    public ConditionalElementBuilder getEvalBuilder() {
        return this.eval;
    }

    public PredicateBuilder getPredicateBuilder() {
        return this.predicate;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return this.returnValue;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        return this.rule;
    }
    
    public FromBuilder getFromBuilder() {
        return this.from;
    }

}
