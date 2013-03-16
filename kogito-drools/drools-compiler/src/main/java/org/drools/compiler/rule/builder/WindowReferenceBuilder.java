package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.WindowReferenceDescr;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.WindowReference;

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
