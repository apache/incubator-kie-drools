/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.metric.util;

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

    public long getEvalCount() {
        return evalCount;
    }

    public void incrementEvalCount() {
        this.evalCount++;
    }

    public long getStartTime() {
        return startTime;
    }

    public BaseNode getNode() {
        return nodeRef.get();
    }

    @Override
    public String toString() {
        return "NodeStats [started=" + started + ", evalCount=" + evalCount + ", startTime=" + startTime + ", node=" + (nodeRef == null ? "null" : nodeRef.get()) + "]";
    }
}
