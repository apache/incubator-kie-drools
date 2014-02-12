/*
 * Copyright 2005 JBoss Inc
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

package org.drools.core.spi;

import java.util.Iterator;

import org.drools.core.common.ActivationNode;

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
