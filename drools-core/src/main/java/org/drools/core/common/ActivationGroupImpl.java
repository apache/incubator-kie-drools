package org.drools.core.common;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.LinkedList;

public class ActivationGroupImpl
    implements
    InternalActivationGroup {
    private final String                          name;

    private final LinkedList<ActivationGroupNode> list;

    private final ActivationsManager activationsManager;
    
    private long triggeredForRecency;

    public ActivationGroupImpl(ActivationsManager activationsManager, String name) {
        this.activationsManager = activationsManager;
        this.name = name;
        this.list = new LinkedList();
        this.triggeredForRecency = -1;
    }

    public String getName() {
        return this.name;
    }

    public void addActivation(final InternalMatch internalMatch) {
        final ActivationGroupNode node = new ActivationGroupNode(internalMatch,
                                                                 this );
        internalMatch.setActivationGroupNode(node);
        this.list.add( node );
    }

    public void removeActivation(final InternalMatch internalMatch) {
        final ActivationGroupNode node = internalMatch.getActivationGroupNode();
        this.list.remove( node );
        internalMatch.setActivationGroupNode(null);
    }

    public java.util.Iterator iterator() {
        return this.list.javaUtilIterator();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        activationsManager.clearAndCancelActivationGroup( name );
    }

    public void reset() {
        list.clear();
    }

    public LinkedList<ActivationGroupNode> getList() {
        return list;
    }

    public long getTriggeredForRecency() {
        return triggeredForRecency;
    }

    public void setTriggeredForRecency(long executedForRecency) {
        this.triggeredForRecency = executedForRecency;
    }

    @Override
    public String toString() {
        return "activation-group: " + name;
    }
}
