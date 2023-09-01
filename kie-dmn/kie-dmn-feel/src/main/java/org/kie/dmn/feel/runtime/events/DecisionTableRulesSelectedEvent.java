package org.kie.dmn.feel.runtime.events;

import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report all rules selected as a result for a decision table.
 *
 * For a unique hit decision table, the same rule will be matched and selected,
 * but for decision tables with different hit policies, the rules actually
 * selected might be a subset of the rules matched.
 * 
 * In some Multiple hit policies, the aggregated result may not correspond to 
 * a unique index; in this case, despite the tables evaluating to an actual result,
 * the {@link #getFired()} may be unspecified or an empty list.
 */
public class DecisionTableRulesSelectedEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String        nodeName;
    private final String        dtName;
    private final List<Integer> fired;

    public DecisionTableRulesSelectedEvent(Severity severity, String msg, String nodeName, String dtName, List<Integer> fired) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.dtName = dtName;
        this.fired = fired;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getDecisionTableName() { return dtName; }

    public List<Integer> getFired() {
        return fired;
    }

    @Override
    public String toString() {
        return "DecisionTableRulesMatchedEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", dtName='" + dtName + '\'' +
               ", fired='" + fired + '\'' +
               '}';
    }
}
