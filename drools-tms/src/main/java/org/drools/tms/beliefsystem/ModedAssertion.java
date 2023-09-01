package org.drools.tms.beliefsystem;

import org.drools.core.util.LinkedListNode;

public interface ModedAssertion<M extends ModedAssertion> extends BeliefSystemMode, LinkedListNode<M> {

}
