package org.drools.compiler.rule.builder;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;

/**
 * An interface to define classes capable of building
 * specific conditional elements.
 */
public interface RuleConditionBuilder<T extends BaseDescr> extends EngineElementBuilder {

    RuleConditionElement build(final RuleBuildContext context,
                               final T descr);
    
    RuleConditionElement build(final RuleBuildContext context,
                               final T descr,
                               final Pattern prefixPattern);

}
