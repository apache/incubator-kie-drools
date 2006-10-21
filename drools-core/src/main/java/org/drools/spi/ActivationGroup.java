package org.drools.spi;

import org.drools.util.Iterator;

public interface ActivationGroup {
    public String getName();

    public void addActivation(Activation activation);

    public void removeActivation(Activation activation);

    public Iterator iterator();

    public boolean isEmpty();

    public int size();

    public void clear();
}
