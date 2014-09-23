package org.drools.core.beliefsystem;

import org.drools.core.util.LinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

public interface ModedAssertion<M extends ModedAssertion> extends Mode, LinkedListNode<M> {

}
