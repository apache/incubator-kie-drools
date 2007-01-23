package org.drools.spi;

import java.util.Iterator;


public interface RuleFlowGroup {
    public String getName();

    public void addActivation(Activation activation);

    public void removeActivation(Activation activation);

    public Iterator iterator();

    public boolean isEmpty();

    public int size();

    public void clear();	
	
	public abstract void addChild(final RuleFlowGroup child);

	public abstract boolean removeChild(final RuleFlowGroup child);

	public abstract void activate();

	public abstract void activateChildren();

	public abstract Activation[] getActivations();

}