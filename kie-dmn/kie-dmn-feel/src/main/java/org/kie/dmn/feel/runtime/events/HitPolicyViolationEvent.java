package org.kie.dmn.feel.runtime.events;

import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report a hit policy violation on a decision table
 */
public class HitPolicyViolationEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String nodeName;
    private final List<Integer> offendingRules;

    public HitPolicyViolationEvent(Severity severity, String msg, String nodeName, List<Integer> offendingRules ) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.offendingRules = offendingRules;
    }

    public String getNodeName() {
        return nodeName;
    }

    public List<Integer> getOffendingRules() {
        return offendingRules;
    }

    @Override
    public String toString() {
        return "HitPolicyViolationEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", offendingRules=" + (offendingRules != null ? offendingRules.toString() : "[]") +
               '}';
    }
}
