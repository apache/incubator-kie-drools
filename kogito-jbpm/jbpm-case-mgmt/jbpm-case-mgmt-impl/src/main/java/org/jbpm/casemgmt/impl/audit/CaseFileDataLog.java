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

package org.jbpm.casemgmt.impl.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;

@Entity
@SequenceGenerator(name = "caseFileDataLogIdSeq", sequenceName = "CASE_FILE_DATA_LOG_ID_SEQ", allocationSize = 1)
public class CaseFileDataLog implements Serializable {

    private static final long serialVersionUID = 7667968668409641210L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "caseFileDataLogIdSeq")
    private long id;

    private String caseId;
    
    private String caseDefId;
    
    private String itemName;
    
    private String itemValue;
    
    private String itemType;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastModified;
    
    private String lastModifiedBy;

    public CaseFileDataLog() {
        
    }
    
    public CaseFileDataLog(String caseId, String caseDefId, String itemName) {
        this.caseId = caseId;
        this.caseDefId = caseDefId;
        this.itemName = itemName;
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getCaseId() {
        return caseId;
    }
    
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
    
    public String getCaseDefId() {
        return caseDefId;
    }
    
    public void setCaseDefId(String caseDefId) {
        this.caseDefId = caseDefId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemValue() {
        return itemValue;
    }
    
    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public Date getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caseDefId == null) ? 0 : caseDefId.hashCode());
        result = prime * result + ((caseId == null) ? 0 : caseId.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
        result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
        result = prime * result + ((itemValue == null) ? 0 : itemValue.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CaseFileDataLog other = (CaseFileDataLog) obj;
        if (caseDefId == null) {
            if (other.caseDefId != null)
                return false;
        } else if (!caseDefId.equals(other.caseDefId))
            return false;
        if (caseId == null) {
            if (other.caseId != null)
                return false;
        } else if (!caseId.equals(other.caseId))
            return false;
        if (id != other.id)
            return false;
        if (itemName == null) {
            if (other.itemName != null)
                return false;
        } else if (!itemName.equals(other.itemName))
            return false;
        if (itemType == null) {
            if (other.itemType != null)
                return false;
        } else if (!itemType.equals(other.itemType))
            return false;
        if (itemValue == null) {
            if (other.itemValue != null)
                return false;
        } else if (!itemValue.equals(other.itemValue))
            return false;
        if (lastModified == null) {
            if (other.lastModified != null)
                return false;
        } else if (!lastModified.equals(other.lastModified))
            return false;
        if (lastModifiedBy == null) {
            if (other.lastModifiedBy != null)
                return false;
        } else if (!lastModifiedBy.equals(other.lastModifiedBy))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CaseFileDataLog [id=" + id + ", caseId=" + caseId + ", caseDefId=" + caseDefId + ", itemName=" + itemName + ", itemValue=" + itemValue + ", itemType=" + itemType + ", lastModified=" + lastModified + ", lastModifiedBy=" + lastModifiedBy + "]";
    }
}
