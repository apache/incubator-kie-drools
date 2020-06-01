/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util;

import java.lang.ref.WeakReference;

import org.drools.core.common.BaseNode;

public class NodeStats {

    private boolean started = false;
    private long evalCount = 0;
    private long startTime = 0;
    private WeakReference<BaseNode> nodeRef = null;

    public NodeStats(BaseNode node) {
        this.started = true;
        this.evalCount = 0;
        this.startTime = System.nanoTime();
        this.nodeRef = new WeakReference<>(node);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public long getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(long evalCount) {
        this.evalCount = evalCount;
    }

    public void incrementEvalCount() {
        this.evalCount++;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public BaseNode getNode() {
        return nodeRef.get();
    }

    public void setNode(BaseNode node) {
        this.nodeRef = new WeakReference<>(node);
    }
}
