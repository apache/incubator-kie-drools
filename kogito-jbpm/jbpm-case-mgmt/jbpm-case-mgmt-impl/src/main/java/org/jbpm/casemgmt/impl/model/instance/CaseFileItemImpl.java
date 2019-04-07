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

package org.jbpm.casemgmt.impl.model.instance;

import java.util.Date;

import org.jbpm.casemgmt.api.model.CaseFileItem;


public class CaseFileItemImpl implements CaseFileItem {

    private static final long serialVersionUID = 6732603755233374841L;

    private String caseId;
    
    private String name;
    
    private String value;
    
    private String type;
    
    private String lastModifiedBy;
    
    private Date lastModified;
    
    public CaseFileItemImpl() {
        
    }
    
    public CaseFileItemImpl(String caseId, String name, String value, String type, String lastModifiedBy, Date lastModified) {
        super();
        this.caseId = caseId;
        this.name = name;
        this.value = value;
        this.type = type;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModified = lastModified;
    }

    @Override
    public String getCaseId() {
        return caseId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return "CaseFileItemImpl [caseId=" + caseId + ", name=" + name + ", value=" + value + ", type=" + type + ", lastModifiedBy=" + lastModifiedBy + ", lastModified=" + lastModified + "]";
    }

}
