package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;

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

        return new EntryPointId( entryDescr.getEntryId() );
    }

}
