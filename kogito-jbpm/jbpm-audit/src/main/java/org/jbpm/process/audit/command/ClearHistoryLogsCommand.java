/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.audit.command;

import org.jbpm.process.audit.AuditLogService;
import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ClearHistoryLogsCommand extends AuditCommand<Void> {

	/** generated serial version UID */
    private static final long serialVersionUID = 9066179664390664420L;

    public ClearHistoryLogsCommand() {
	}
	
    public Void execute(Context cntxt) {
        setLogEnvironment(cntxt);
        this.auditLogService.clear();
        return null;
    }
    
    public String toString() {
        return AuditLogService.class.getSimpleName() + ".clear()";
    }
}
