package org.drools.spi;

import java.io.Serializable;
import java.util.Iterator;

public interface ActivationGroup
    extends
    Serializable {
    public String getName();

    public void addActivation(Activation activation);

    public void removeActivation(Activation activation);

    public Iterator iterator();

    public boolean isEmpty();

    public int size();

    public void clear();
}
