package org.drools.core.common;

public interface RuleFlowGroup extends org.kie.api.runtime.rule.RuleFlowGroup {

    String getName();

    boolean isEmpty();

    int size();

    boolean isActive();

    boolean isAutoDeactivate();

    /**
     * Sets the auto-deactivate status of this RuleFlowGroup.
     * If this is set to true, an active RuleFlowGroup automatically
     * deactivates if it has no more activations.  If it had no
     * activations when it was activated, it will be deactivated immediately.
     */
    void setAutoDeactivate(boolean autoDeactivate);
}
