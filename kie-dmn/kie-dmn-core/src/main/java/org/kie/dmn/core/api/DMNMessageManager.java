package org.kie.dmn.core.api;

import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageContainer;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

/**
 * An internal interface for objects that also support
 * managing message lists
 */
public interface DMNMessageManager extends DMNMessageContainer {

    void addAll(List<? extends DMNMessage> messages);

    /**
     * Internal utility method.
     */
    void addAllUnfiltered(List<? extends DMNMessage> messages);

    DMNMessage addMessage(DMNMessage msg);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent);

}
