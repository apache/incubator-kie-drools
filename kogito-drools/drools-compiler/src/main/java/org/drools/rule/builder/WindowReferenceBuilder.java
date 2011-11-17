package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.WindowReferenceDescr;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.WindowReference;

/**
 * A class capable of building window source references
 */
public class WindowReferenceBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr,
                                      Pattern prefixPattern) {
        final WindowReferenceDescr window = (WindowReferenceDescr) descr;

        return new WindowReference( window.getName() );
    }

}
