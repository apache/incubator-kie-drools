package org.kie.dmn.core.api;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageContainer;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.model.v1_1.DMNElement;

import java.util.List;

/**
 * An internal interface for objects that also support
 * managing message lists
 */
public interface DMNMessageManager extends DMNMessageContainer {

    DMNMessage addMessage(DMNMessage msg);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNElement source);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNElement source, Throwable exception);

    DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNElement source, FEELEvent feelEvent);

}
