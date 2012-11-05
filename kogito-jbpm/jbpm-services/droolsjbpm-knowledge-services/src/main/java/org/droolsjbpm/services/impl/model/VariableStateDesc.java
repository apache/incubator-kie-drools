/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author salaboy
 */
@Entity
public class VariableStateDesc implements Serializable{
    @Id
    @GeneratedValue()
    private long pk;
    private String variableId;
    private String variableInstanceId;
    private String oldValue;
    private String newValue;
    private String domainName;
    private int sessionId;
    private long processInstanceId;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataTimeStamp;

    public VariableStateDesc() {
        this.dataTimeStamp = new Date();
    }
    
    

    public VariableStateDesc(String variableId, String variableInstanceId, String oldValue, String newValue, String domainName, int sessionId, long processInstanceId) {
        this.variableId = variableId;
        this.variableInstanceId = variableInstanceId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.sessionId = sessionId;
        this.processInstanceId = processInstanceId;
        this.domainName = domainName;
        this.dataTimeStamp = new Date();
    }

   
    public String getVariableId() {
        return variableId;
    }

    public String getVariableInstanceId() {
        return variableInstanceId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public int getSessionId() {
        return sessionId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public String getDomainName() {
        return domainName;
    }

    @Override
    public String toString() {
        return "VariableStateDesc["+dataTimeStamp.toString()+"]{" + "pk=" + pk + ", variableId=" + variableId + ", variableInstanceId=" + variableInstanceId + ", oldValue=" + oldValue + ", newValue=" + newValue + ", domainName=" + domainName + ", sessionId=" + sessionId + ", processInstanceId=" + processInstanceId + '}';
    }
    
    

   
    
    
}
