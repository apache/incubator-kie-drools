/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import java.util.Map;

import org.drools.core.rule.consequence.Activation;

public interface InternalAgendaGroup extends org.kie.api.runtime.rule.AgendaGroup {

    /**
     * Static reference to determine the default <code>AgendaGroup</code> name.
     */
    String MAIN = "MAIN";

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

    void add(Activation activation);

    Activation peek();

    Activation remove();

    void remove(Activation activation);

    void setActive(boolean activate);

    
    Activation[] getAndClear();

    void setActivatedForRecency(long recency);
    
    long getActivatedForRecency();
    
    void setClearedForRecency(long recency);
    
    long getClearedForRecency();

    void addNodeInstance(Object processInstanceId, String nodeInstanceId);

    void removeNodeInstance(Object processInstanceId, String nodeInstanceId);

    Activation[] getActivations();

    Map<Object, String> getNodeInstances();

    void visited();

    void setReteEvaluator(ReteEvaluator reteEvaluator);

    void hasRuleFlowListener(boolean hasRuleFlowLister);

    boolean isRuleFlowListener();

    boolean isSequential();
}
