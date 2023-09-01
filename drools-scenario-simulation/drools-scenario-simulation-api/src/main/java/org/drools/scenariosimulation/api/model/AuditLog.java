package org.drools.scenariosimulation.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java representation of a full <b>audit</b> report
 */
public class AuditLog {

    /**
     * The <code>List</code> of audit log lines
     */
    private List<AuditLogLine> auditLogLines = new ArrayList<>();

    /**
     * @return an <b>unmodifiable</b> version of {@link AuditLog#auditLogLines}
     */
    public List<AuditLogLine> getAuditLogLines() {
        return Collections.unmodifiableList(auditLogLines);
    }

    /**
     * Add an <code>AuditLogLine</code> to the end of {@link AuditLog#auditLogLines}
     * @param toAdd
     */
    public void addAuditLogLine(AuditLogLine toAdd) {
        auditLogLines.add(toAdd);
    }

    /**
     * Add a <code>List&lt;AuditLogLine&gt;</code> to the end of {@link AuditLog#auditLogLines}
     * @param toAdd
     */
    public void addAuditLogLines(List<AuditLogLine> toAdd) {
        auditLogLines.addAll(toAdd);
    }
}
