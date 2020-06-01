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
