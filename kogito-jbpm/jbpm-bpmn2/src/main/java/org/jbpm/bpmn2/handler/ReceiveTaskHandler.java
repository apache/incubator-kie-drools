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

package org.jbpm.bpmn2.handler;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

public class ReceiveTaskHandler implements KogitoWorkItemHandler {
    
    // TODO: use correlation instead of message id
    private Map<String, String> waiting = new HashMap<>();
    private KogitoProcessRuntime kruntime;
    
    public ReceiveTaskHandler(KogitoProcessRuntime kruntime) {
        this.kruntime = kruntime;
    }
    
    public void setKnowledgeRuntime(KogitoProcessRuntime kruntime) {
    	this.kruntime = kruntime;
    }

    public void executeWorkItem( KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        String messageId = (String) workItem.getParameter("MessageId");
        waiting.put(messageId, workItem.getStringId());
    }
    
    public void messageReceived(String messageId, Object message) {
        String workItemId = waiting.get(messageId);
        if (workItemId == null) {
            return;
        }
        Map<String, Object> results = new HashMap<>();
        results.put("Message", message);
        kruntime.getWorkItemManager().completeWorkItem(workItemId, results);
    }

    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
    	String messageId = (String) workItem.getParameter("MessageId");
        waiting.remove(messageId);
    }

}
