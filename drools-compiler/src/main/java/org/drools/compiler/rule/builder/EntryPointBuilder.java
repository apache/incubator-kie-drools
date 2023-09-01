package org.drools.compiler.rule.builder;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;

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
