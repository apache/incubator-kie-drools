/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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