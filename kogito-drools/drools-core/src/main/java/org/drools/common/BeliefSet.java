package org.drools.common;

import org.drools.core.util.LinkedListNode;

public interface BeliefSet {
    public LinkedListNode getFirst();
    
    public void add(LinkedListNode node);
    public void remove(LinkedListNode node);
    
    public boolean isEmpty();
    //public boolean isPropagated();
    public int size();
}
