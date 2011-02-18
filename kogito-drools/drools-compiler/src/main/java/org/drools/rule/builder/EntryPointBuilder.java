package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.rule.EntryPoint;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;

/**
 * A class capable of building entry point instances
 */
public class EntryPointBuilder
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
        final EntryPointDescr entryDescr = (EntryPointDescr) descr;

        return new EntryPoint( entryDescr.getEntryId() );
    }

}
