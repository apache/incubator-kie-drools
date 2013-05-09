package org.drools.workbench.models.guided.dtable.shared.auditlog;

/**
 * Events recorded for the Decision Table audit log
 */
//DO NOT CHANGE THE NAMES OF THESE ENUMS TO PRESERVE COMPATIBILITY OF EXISTING AUDIT LOGS IN FUTURE RELEASES
public enum DecisionTableAuditEvents {
    INSERT_ROW,
    INSERT_COLUMN,
    DELETE_ROW,
    DELETE_COLUMN,
    UPDATE_COLUMN
}