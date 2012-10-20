package org.drools.common;

import org.drools.core.util.LinkedListNode;

public interface BeliefSet {
    public BeliefSystem getBeliefSystem();
    
    public InternalFactHandle getFactHandle();
    
    public LinkedListNode getFirst();
    
    public void add(LinkedListNode node);
    public void remove(LinkedListNode node);
    
    public boolean isEmpty();
    //public boolean isPropagated();
    public int size();

    public void clear();
  
}
