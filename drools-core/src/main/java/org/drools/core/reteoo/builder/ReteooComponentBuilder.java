package org.drools.core.reteoo.builder;

import org.drools.base.rule.RuleConditionElement;

/**
 * An interface for Reteoo Component builders
 */
public interface ReteooComponentBuilder {

    /**
     * Builds and attach if needed the given RuleConditionalElement
     * 
     * @param context current build context
     * @param rce 
     */
    public void build(BuildContext context,
                      BuildUtils utils,
                      RuleConditionElement rce);

    /**
     * A boolean function that indicates if the builder requires a previous left 
     * (tuple) activation in order to corretly build the given component.
     * 
     * In other words, if it returns true and no previous TupleSource is already created,
     * an InitialFact pattern must be added with appropriate left input adapter for 
     * the network to be correctly built.
     * 
     * For instance, NOT / EXISTS / ACCUMULATE are examples of builders that must return true 
     * for this method, while PATTERN must return false.
     * 
     * @param rce the element to be built
     * 
     * @return true if a tuple source is required, false otherwise.
     */
    public boolean requiresLeftActivation(BuildUtils utils,
                                          RuleConditionElement rce);

}
