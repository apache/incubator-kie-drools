package org.drools.core.common;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.AbstractBaseLinkedListNode;

public class ActivationNode extends AbstractBaseLinkedListNode<ActivationNode> {

    private static final long     serialVersionUID = 510l;

    private final InternalMatch internalMatch;
    private final Object parentContainer;

    public ActivationNode(final InternalMatch internalMatch,
                          final Object parentContainer) {
        super();
        this.internalMatch = internalMatch;
        this.internalMatch.setActivationNode(this);
        this.parentContainer = parentContainer;
    }

    public InternalMatch getActivation() {
        return this.internalMatch;
    }

    public Object getParentContainer() {
        return this.parentContainer;
    }

}
