package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.core.rule.ConditionalBranch;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.NamedConsequence;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;

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
        for (int i = siblings.size()-1; i >= 0; i--) {
            RuleConditionElement element = siblings.get(i);
            if (element instanceof Pattern) {
                return (Pattern) element;
            }
        }
        throw new RuntimeException("Cannot find a Pattern in the RuleBuildContext");
    }
}
