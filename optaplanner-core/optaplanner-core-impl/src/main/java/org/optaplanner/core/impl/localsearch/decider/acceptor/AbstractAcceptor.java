package org.optaplanner.core.impl.localsearch.decider.acceptor;

import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link Acceptor}.
 *
 * @see Acceptor
 */
public abstract class AbstractAcceptor<Solution_> extends LocalSearchPhaseLifecycleListenerAdapter<Solution_>
        implements Acceptor<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    // ************************************************************************
    // Worker methods
    // ************************************************************************

}
