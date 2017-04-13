/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.api.event;

/**
 * Represents generic event of case operation
 */
public class CaseEvent {

    private String caseId;
    private String user; 
    
    public CaseEvent(String user, String caseId) {
        this.user = user;
        this.caseId = caseId;
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
}
