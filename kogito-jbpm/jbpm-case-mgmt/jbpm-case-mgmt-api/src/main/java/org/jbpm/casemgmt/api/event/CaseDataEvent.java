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

import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;

/**
 * Represents occurrence of case data (that is part of case file) related operation
 */
public class CaseDataEvent extends CaseEvent {

    private Map<String, Object> data;
    private String definitionId;

    public CaseDataEvent(String user, String caseId, CaseFileInstance caseFile, String definitionId, Map<String, Object> data) {
        super(user, caseId, caseFile);
        this.definitionId = definitionId;
        this.data = data;
    }
    
    /**
     * Returns data affected (added or removed)
     */
    public Map<String, Object> getData() {
        return data;
    }

    
    public String getDefinitionId() {
        return definitionId;
    }

    @Override
    public String toString() {
        return "CaseDataEvent [data=" + data + ", caseId=" + getCaseId() + "]";
    }    
    
}
