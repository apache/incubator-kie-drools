package org.drools.core.reteoo.builder;

import org.drools.base.rule.GroupElement;
import org.drools.base.rule.NamedConsequence;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.time.impl.Timer;

public class NamedConsequenceBuilder implements ReteooComponentBuilder {

    public void build(BuildContext context, BuildUtils utils, RuleConditionElement rce) {
        NamedConsequence namedConsequence = (NamedConsequence) rce;

        Timer timer = context.getRule().getTimer();

        ReteooRuleBuilder.buildTerminalNodeForConsequence(context, (GroupElement) context.peek(), context.getSubRuleIndex(),
                                                          namedConsequence, timer, utils);
        context.terminate(); // assumes named consequences, not in a conditional branch are always terminal.
    }

    public boolean requiresLeftActivation(BuildUtils utils, RuleConditionElement rce) {
        return false;
    }
}
