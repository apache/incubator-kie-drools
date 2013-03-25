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
import javax.persistence.Column;


public class VariableStateDesc implements Serializable{

    private String variableId;
    private String variableInstanceId;
    @Column(length = 5000)
    private String oldValue;
    @Column(length = 5000)
    private String newValue;
    private String domainId;
    private long processInstanceId;
    
    private Date dataTimeStamp;

    public VariableStateDesc() {
     
    }
    
    

    public VariableStateDesc(String variableId, String variableInstanceId, String oldValue, String newValue, String domainId, long processInstanceId, Date date) {
        this.variableId = variableId;
        this.variableInstanceId = variableInstanceId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.domainId = domainId;
        this.processInstanceId = processInstanceId;
        this.dataTimeStamp = date;
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

    public String getDomainId() {
        return domainId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    @Override
    public String toString() {
        return "VariableStateDesc["+dataTimeStamp.toString()+"]{variableId=" + variableId + ", variableInstanceId=" + variableInstanceId + ", oldValue=" + oldValue + ", newValue=" + newValue + ", domainId=" + domainId + ", processInstanceId=" + processInstanceId + '}';
    }
    
    

   
    
    
}
