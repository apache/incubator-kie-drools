package org.kie.dmn.feel.runtime.events;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report a syntax error as returned by the parser
 */
public class InvalidInputEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String nodeName;
    private final String inputName;
    private final String validInputs;

    public InvalidInputEvent(Severity severity, String msg, String nodeName, String inputName, String validInputs) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.inputName = inputName;
        this.validInputs = validInputs;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getInputName() {
        return inputName;
    }

    public String getValidInputs() {
        return validInputs;
    }

    @Override
    public String toString() {
        return "InvalidInputEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", inputName='" + inputName + '\'' +
               ", validInputs=" + validInputs +
               '}';
    }
}
