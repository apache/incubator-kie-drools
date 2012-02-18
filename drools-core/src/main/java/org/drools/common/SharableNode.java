package org.drools.common;

public interface SharableNode<T extends NetworkNode> {
    void sharedWith(T node);
}
