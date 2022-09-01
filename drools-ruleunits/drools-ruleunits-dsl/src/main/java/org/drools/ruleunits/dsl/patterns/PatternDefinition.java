package org.drools.ruleunits.dsl.patterns;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.constraints.Constraint;

import static org.drools.model.PatternDSL.pattern;

public abstract class PatternDefinition<A> {
    protected final RuleFactory.RuleDefinition rule;
    protected final Variable<A> variable;
    protected final List<Constraint> constraints = new ArrayList<>();

    protected PatternDefinition(RuleFactory.RuleDefinition rule, Variable<A> variable) {
        this.rule = rule;
        this.variable = variable;
    }

    protected List<Constraint> getConstraints() {
        return constraints;
    }

    protected Variable getVariable() {
        return variable;
    }

    public <G> void execute(G globalObject, Block1<G> block) {
        rule.setConsequence(globalObject, block);
    }

    public RuleItemBuilder toExecModelItem() {
        PatternDSL.PatternDef patternDef = pattern(getVariable());
        for (Constraint constraint : getConstraints()) {
            constraint.addConstraintToPattern(patternDef);
        }
        return patternDef;
    }
}
