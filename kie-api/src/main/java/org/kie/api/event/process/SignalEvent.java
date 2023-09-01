package org.kie.api.event.process;

/**
 * An event when a signal is thrown
 */
public interface SignalEvent extends ProcessNodeEvent {
    /**
     * The name of the signal
     *
     * @return signal name
     */
    String getSignalName();

    /**
     * Object associated with this signal
     *
     * @return signal object
     */
    Object getSignal();
}
