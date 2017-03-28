package org.kie.dmn.core.api;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageContainer;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.List;

/**
 * An internal interface for objects that also support
 * managing message lists
 */
public interface DMNMessageManager extends DMNMessageContainer {

    void addAll( List<DMNMessage> messages );

    DMNMessage addMessage(DMNMessage msg);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent);

}
