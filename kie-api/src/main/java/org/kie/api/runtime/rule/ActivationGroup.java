package org.kie.api.runtime.rule;

public interface ActivationGroup {
    /**
     * @return
     *      The ActivationGroup name
     */
    public String getName();

    public void clear();
}
