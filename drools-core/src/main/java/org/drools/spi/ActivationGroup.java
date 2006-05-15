package org.drools.spi;

import java.util.Iterator;

public interface ActivationGroup {
    public String getName();
    
    public void addActivation(Activation activation);
    
    public void removeActivation(Activation activation);
    
    public Iterator iterator();
    
    public boolean isEmpty();
    
    public int size();
    
    public void clear();
}
