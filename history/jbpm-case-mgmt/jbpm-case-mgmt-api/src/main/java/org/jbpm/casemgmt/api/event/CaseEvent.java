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

package org.jbpm.casemgmt.api.event;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;

/**
 * Represents generic event of case operation
 */
public class CaseEvent {

    private String caseId;
    private String user; 
    private CaseFileInstance caseFile;
    
    public CaseEvent(String user, String caseId, CaseFileInstance caseFile) {
        this.user = user;
        this.caseId = caseId;
        this.caseFile = caseFile;
    }

    /**
     * Returns case identifier
     */
    public String getCaseId() {
        return this.caseId;
    }
    
    /**
     * Returns user who performed that operation
     */
    public String getUser() {
        return this.user;
    }

    public CaseFileInstance getCaseFile() {
        return caseFile;
    }    
    
}
