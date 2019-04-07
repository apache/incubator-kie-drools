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

package org.jbpm.casemgmt.impl.generator;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames="caseIdPrefix")})
@SequenceGenerator(name="caseIdInfoIdSeq", sequenceName="CASE_ID_INFO_ID_SEQ")
public class CaseIdInfo implements Serializable {
 
    private static final long serialVersionUID = -6342444682573333987L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="caseIdInfoIdSeq")    
    private Long id;
    
    private String caseIdPrefix;
    
    private Long currentValue;

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCaseIdPrefix() {
        return caseIdPrefix;
    }
    
    public void setCaseIdPrefix(String caseIdPrefix) {
        this.caseIdPrefix = caseIdPrefix;
    }
    
    public Long getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caseIdPrefix == null) ? 0 : caseIdPrefix.hashCode());
        result = prime * result + ((currentValue == null) ? 0 : currentValue.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        CaseIdInfo other = (CaseIdInfo) obj;
        if (caseIdPrefix == null) {
            if (other.caseIdPrefix != null)
                return false;
        } else if (!caseIdPrefix.equals(other.caseIdPrefix))
            return false;
        if (currentValue == null) {
            if (other.currentValue != null)
                return false;
        } else if (!currentValue.equals(other.currentValue))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CaseIdInfo [caseIdPrefix=" + caseIdPrefix + ", currentValue=" + currentValue + "]";
    }
        
}
