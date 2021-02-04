/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.node;

import org.drools.core.spi.KogitoProcessContext;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.kie.kogito.event.process.ContextAwareEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

/**
 * Runtime counterpart of a milestone node.
 */
public class MilestoneNodeInstance extends StateBasedNodeInstance {

    private static final long serialVersionUID = 510L;

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    @Override
    public void internalTrigger( KogitoNodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getStringId()) == null) {
            return;
        }
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "A MilestoneNode only accepts default incoming connections!");
        }
        if (isCompleted()) {
            triggerCompleted();
        } else {
            addCompletionEventListener();
        }
    }

    private boolean isCompleted() {
        KogitoProcessContext context = new KogitoProcessContext(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        return getMilestoneNode().canComplete(context);
    }

    @Override
    public void addEventListeners() {
        super.addEventListeners();
        addCompletionEventListener();
    }

    private void addCompletionEventListener() {
        getProcessInstance().getKnowledgeRuntime().getProcessRuntime().addEventListener( ContextAwareEventListener.using( listener -> {
            if (isCompleted()) {
                triggerCompleted();
                getProcessInstance().getKnowledgeRuntime().getProcessRuntime().removeEventListener(listener);
            }
        }));
    }

    @Override
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener(getActivationEventType(), this, true);
    }

    private String getActivationEventType() {
        return "RuleFlow-Milestone-" + getProcessInstance().getProcessId()
                + "-" + getMilestoneNode().getUniqueId();
    }
}
