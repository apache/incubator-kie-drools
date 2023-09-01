package org.kie.dmn.api.feel.runtime.events;

/**
 * A general interface for a FEEL event listener
 */
@FunctionalInterface
public interface FEELEventListener {

    void onEvent( FEELEvent event );
}
