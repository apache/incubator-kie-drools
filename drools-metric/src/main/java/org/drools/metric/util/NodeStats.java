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
