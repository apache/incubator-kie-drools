package org.drools.ruleunits.dsl.patterns;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.constraints.Constraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.PatternDSL.pattern;

public abstract class SinglePatternDef<A> implements InternalPatternDef {
    protected final RuleDefinition rule;
    protected final Variable<A> variable;
    protected final List<Constraint> constraints = new ArrayList<>();

    protected SinglePatternDef(RuleDefinition rule, Variable<A> variable) {
        this.rule = rule;
        this.variable = variable;
    }

    protected List<Constraint> getConstraints() {
        return constraints;
    }

    public Variable getVariable() {
        return variable;
    }

    public <G> void execute(G globalObject, Block1<G> block) {
        rule.execute(globalObject, block);
    }

    @Override
    public ViewItem toExecModelItem() {
        PatternDSL.PatternDef patternDef = pattern(getVariable());
        for (Constraint constraint : getConstraints()) {
            constraint.addConstraintToPattern(patternDef);
        }
        return patternDef;
    }
}
