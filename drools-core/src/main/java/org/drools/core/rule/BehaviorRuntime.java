package org.drools.core.rule;

import org.drools.base.rule.Behavior;
import org.drools.base.rule.RuleComponent;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.kie.api.runtime.rule.FactHandle;

/**
 * An interface for all behavior implementations
 */
public interface BehaviorRuntime extends Behavior, RuleComponent, Cloneable {

    /**
     * Returns the type of the behavior
     */
    BehaviorType getType();

    /**
     * Creates the context object associated with this behavior.
     * The object is given as a parameter in all behavior call backs.
     */
    BehaviorContext createContext();

    /**
     * Makes the behavior aware of the new fact entering behavior's scope
     * 
     * @param context The behavior context object
     * @param fact The new fact entering behavior's scope
     * @param valueResolver The working memory session reference
     * 
     * @return true if the propagation should continue, false otherwise. I.e., 
     *         the behaviour has veto power over the fact propagation, and prevents
     *         the propagation to continue if returns false on this method. 
     */
    boolean assertFact(Object context,
                       FactHandle fact,
                       PropagationContext pctx,
                       ReteEvaluator reteEvaluator);

    /**
     * Removes a right tuple from the behavior's scope
     * 
     * @param context The behavior context object
     * @param fact The fact leaving the behavior's scope
     * @param valueResolver The working memory session reference
     */
    void retractFact(Object context,
                     FactHandle fact,
                     PropagationContext pctx,
                     ReteEvaluator reteEvaluator);

    /**
     * A callback method that allows behaviors to expire facts
     */
    void expireFacts(Object context,
                     PropagationContext pctx,
                     ReteEvaluator reteEvaluator);

    /**
     * Some behaviors might change the expiration offset for the 
     * associated fact type. Example: time sliding windows. 
     * 
     * For these behaviors, this method must return the expiration
     * offset associated to them.
     * 
     * @return the expiration offset for this behavior or -1 if 
     *         they don't have a time based expiration offset.
     */
    long getExpirationOffset();
    
}
