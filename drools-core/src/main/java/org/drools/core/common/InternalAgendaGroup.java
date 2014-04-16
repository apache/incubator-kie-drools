/*
 * Copyright 2010 JBoss Inc
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

import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;

import java.util.Map;

public interface InternalAgendaGroup extends AgendaGroup {

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

    void addNodeInstance(Long processInstanceId, String nodeInstanceId);

    void removeNodeInstance(Long processInstanceId, String nodeInstanceId);

    public Activation[] getActivations();

    Map<Long, String> getNodeInstances();

    void setWorkingMemory(InternalWorkingMemory workingMemory);

    InternalWorkingMemory getWorkingMemory();


    void hasRuleFlowListener(boolean hasRuleFlowLister);

    boolean isRuleFlowListener();
}
