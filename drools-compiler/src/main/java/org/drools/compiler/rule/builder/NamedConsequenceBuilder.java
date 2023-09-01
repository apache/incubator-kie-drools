package org.drools.compiler.rule.builder;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.NamedConsequenceDescr;
import org.drools.base.rule.NamedConsequence;
import org.drools.base.rule.Pattern;

public class NamedConsequenceBuilder implements RuleConditionBuilder {

    public NamedConsequence build(RuleBuildContext context, BaseDescr descr) {
        return build( context, descr, null );
    }

    public NamedConsequence build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        NamedConsequenceDescr namedConsequence = (NamedConsequenceDescr) descr;
        return new NamedConsequence( namedConsequence.getName(), namedConsequence.isBreaking() );
    }
}
