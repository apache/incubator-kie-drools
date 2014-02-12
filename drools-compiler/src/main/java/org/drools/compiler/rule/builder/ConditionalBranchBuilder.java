package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;
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

        String consequenceName = conditionalBranch.getConsequence().getName();
        if ( !context.getRuleDescr().getNamedConsequences().keySet().contains( consequenceName ) ) {
            DroolsError err = new RuleBuildError( context.getRule(), context.getParentDescr(), null,
                                                  "Unknown consequence name: " + consequenceName );
            context.addError( err  );
            return null;
        }

        RuleConditionBuilder evalBuilder = (RuleConditionBuilder) context.getDialect().getBuilder( EvalDescr.class );
        EvalCondition condition = (EvalCondition) evalBuilder.build(context, conditionalBranch.getCondition(), getLastPattern(context));

        NamedConsequenceBuilder namedConsequenceBuilder = (NamedConsequenceBuilder) context.getDialect().getBuilder( NamedConsequenceDescr.class );
        NamedConsequence consequence = namedConsequenceBuilder.build(context, conditionalBranch.getConsequence());

        ConditionalBranchDescr elseBranchDescr = conditionalBranch.getElseBranch();
        return new ConditionalBranch( condition, consequence, elseBranchDescr != null ? build(context, elseBranchDescr, prefixPattern) : null );
    }

    private Pattern getLastPattern(RuleBuildContext context) {
        GroupElement ge = (GroupElement)context.getBuildStack().peek();
        Pattern lastPattern = getLastPattern(ge.getChildren());
        if (lastPattern == null) {
            RuleImpl parent = context.getRule().getParent();
            if (parent != null) {
                lastPattern = getLastPattern(parent.getLhs().getChildren());
            }
        }
        return lastPattern;
    }

    private Pattern getLastPattern(List<RuleConditionElement> siblings) {
        for (int i = siblings.size()-1; i >= 0; i--) {
            RuleConditionElement element = siblings.get(i);
            if (element instanceof Pattern) {
                return (Pattern) element;
            }
        }
        return null;
    }
}
