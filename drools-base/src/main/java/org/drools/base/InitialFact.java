package org.drools.base;

import java.io.Serializable;

/**
 * Initial fact, automatically put into the network. This fact is needed by 'not' CEs
 * when they are the CEs in the rule.
 */
public interface InitialFact
    extends
    Serializable {

}
