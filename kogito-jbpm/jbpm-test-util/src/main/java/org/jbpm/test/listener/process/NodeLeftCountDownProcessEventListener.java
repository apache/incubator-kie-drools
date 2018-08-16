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

package org.jbpm.test.listener.process;

import org.kie.api.event.process.ProcessNodeLeftEvent;


public class NodeLeftCountDownProcessEventListener extends NodeCountDownProcessEventListener {
    
    private boolean reactOnBeforeNodeLeft = false;
    
    public NodeLeftCountDownProcessEventListener() {
        
    }
    
    public NodeLeftCountDownProcessEventListener(String nodeName, int threads) {
        super(nodeName, threads);
    }
    
    public NodeLeftCountDownProcessEventListener(String nodeName, int threads, boolean reactOnBeforeNodeLeft) {
        super(nodeName, threads);
        this.reactOnBeforeNodeLeft = reactOnBeforeNodeLeft;
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        if (nodeName.equals(event.getNodeInstance().getNodeName())) {
            countDown();
        }
    }
    
    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        if (reactOnBeforeNodeLeft && nodeName.equals(event.getNodeInstance().getNodeName())) {
            countDown();
        }
    }
}
