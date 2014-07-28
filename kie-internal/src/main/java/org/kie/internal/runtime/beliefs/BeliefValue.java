package org.kie.internal.runtime.beliefs;

public interface BeliefValue {
    //int getBeliefType();
    public Object getBeliefSystem();

    public BeliefValue getNextBeliefValue();
}
