package org.drools.core.common;

import org.drools.core.phreak.RuleAgendaItem;

import java.util.Collection;
import java.util.Map;

public interface InternalAgendaGroup extends org.kie.api.runtime.rule.AgendaGroup {

    /**
     * @return
     *     The int total number of activations
     */
    int size();

    /**
     * @return
     *     boolean value indicating if this AgendaGroup is empty or not
     */
    boolean isEmpty();

    /**
     *
     * @return
     *     boolean value indicating if the AgendaGroup is active and thus being evaluated.
     */
    boolean isActive();

    void setAutoFocusActivator(PropagationContext ctx);


    PropagationContext getAutoFocusActivator();

    /**
     * Sets the auto-deactivate status of this RuleFlowGroup.
     * If this is set to true, an active RuleFlowGroup automatically
     * deactivates if it has no more activations.  If it had no
     * activations when it was activated, it will be deactivated immediately.
     */
    void setAutoDeactivate(boolean autoDeactivate);

    boolean isAutoDeactivate();

    void reset();

    void add(RuleAgendaItem activation);

    RuleAgendaItem peek();

    RuleAgendaItem remove();

    void remove(RuleAgendaItem activation);

    void setActive(boolean activate);

    void setActivatedForRecency(long recency);
    
    long getActivatedForRecency();
    
    void setClearedForRecency(long recency);
    
    long getClearedForRecency();

    void addNodeInstance(Object processInstanceId, String nodeInstanceId);

    void removeNodeInstance(Object processInstanceId, String nodeInstanceId);

    Collection<RuleAgendaItem> getActivations();

    Map<Object, String> getNodeInstances();

    void visited();

    void setReteEvaluator(ReteEvaluator reteEvaluator);

    void hasRuleFlowListener(boolean hasRuleFlowLister);

    boolean isRuleFlowListener();

    boolean isSequential();
}
