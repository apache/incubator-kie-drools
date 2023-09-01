package org.kie.dmn.feel.runtime.events;

import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report all matches for a decision table
 */
public class DecisionTableRulesMatchedEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String        nodeName;
    private final String        dtName;
    private final List<Integer> matches;

    public DecisionTableRulesMatchedEvent(Severity severity, String msg, String nodeName, String dtName, List<Integer> matches) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.dtName = dtName;
        this.matches = matches;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getDecisionTableName() {
        return dtName;
    }

    public List<Integer> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return "DecisionTableRulesMatchedEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", dtName='" + dtName + '\'' +
               ", matches='" + matches + '\'' +
               '}';
    }
}
