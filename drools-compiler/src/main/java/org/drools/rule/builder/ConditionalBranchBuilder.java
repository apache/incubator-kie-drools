package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConditionalBranchDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.NamedConsequenceDescr;
import org.drools.rule.ConditionalBranch;
import org.drools.rule.EvalCondition;
import org.drools.rule.GroupElement;
import org.drools.rule.NamedConsequence;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;

import java.util.List;

public class ConditionalBranchBuilder implements RuleConditionBuilder {

    public ConditionalBranch build(RuleBuildContext context, BaseDescr descr) {
        return build( context, descr, null );
    }

    public ConditionalBranch build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        ConditionalBranchDescr conditionalBranch = (ConditionalBranchDescr) descr;

        RuleConditionBuilder evalBuilder = (RuleConditionBuilder) context.getDialect().getBuilder( EvalDescr.class );
        EvalCondition condition = (EvalCondition) evalBuilder.build(context, conditionalBranch.getCondition(), getLastPattern(context));

        NamedConsequenceBuilder namedConsequenceBuilder = (NamedConsequenceBuilder) context.getDialect().getBuilder( NamedConsequenceDescr.class );
        NamedConsequence consequence = namedConsequenceBuilder.build(context, conditionalBranch.getConsequence());

        ConditionalBranchDescr elseBranchDescr = conditionalBranch.getElseBranch();
        return new ConditionalBranch( condition, consequence, elseBranchDescr != null ? build(context, elseBranchDescr, prefixPattern) : null );
    }

    private Pattern getLastPattern(RuleBuildContext context) {
        GroupElement ge = (GroupElement)context.getBuildStack().peek();
        List<RuleConditionElement> siblings = ge.getChildren();
        return (Pattern) siblings.get(siblings.size()-1);
    }
}
