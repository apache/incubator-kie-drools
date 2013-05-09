/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.commons.shared.auditlog;

import java.util.List;

/**
 * An Audit Log that filters entries added to it depending on the type of
 * entries the Log is configured to receive. Users of this log need therefore
 * not filter which entries should be appended as this is handled by the log
 * itself.
 */
public interface AuditLog
        extends
        List<AuditLogEntry> {

    /**
     * Get the AuditLogFilter in operation on the AuditLog
     * @return
     */
    public AuditLogFilter getAuditLogFilter();

    /**
     * Add a new AuditLogEntry at the beginning of the list. This is different
     * behaviour to a regular List but it prevents the need to sort entries in
     * descending order.
     */
    public boolean add( AuditLogEntry e );

}
