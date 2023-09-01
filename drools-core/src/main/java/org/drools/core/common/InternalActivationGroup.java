package org.drools.core.common;

import java.util.Iterator;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.ActivationGroup;

public interface InternalActivationGroup extends ActivationGroup {

    void addActivation(InternalMatch internalMatch);

    void removeActivation(InternalMatch internalMatch);
    
    LinkedList<ActivationGroupNode> getList();

    Iterator iterator();

    boolean isEmpty();

    int size();

    void reset();
    
    long getTriggeredForRecency();

    void setTriggeredForRecency(long executedForRecency);
}
