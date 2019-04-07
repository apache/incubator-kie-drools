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

package org.jbpm.kie.services.impl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserTaskInstanceWithPotOwnerDesc extends UserTaskInstanceDesc implements org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc, Serializable {

    private static final long serialVersionUID = -7648591623748193144L;
    
    private List<String> potentialOwners = new ArrayList<String>();
    private String correlationKey;
    private Date lastModificationDate;
    private String lastModificationUser;
    private String subject;
    private Map<String,Object> inputdata;
    private Map<String,Object> outputdata;
    private String processInstanceDescription;

    public UserTaskInstanceWithPotOwnerDesc(Long taskId, String name, String formName, 
                                            String subject, String actualOwner, String potOwner, 
                                            String correlationKey, Date createdOn, String createdBy, 
                                            Date expirationDate, Date lastModificationDate, String lastModificationUser,
                                            Integer priority, String status, Long processInstanceId, 
                                            String processId, String deploymentId, String processInstanceDescription) {
        super(taskId, status, actualOwner, name, priority, createdBy, processId, processInstanceId, createdOn, formName, deploymentId, expirationDate);
        this.potentialOwners.add(potOwner);
        this.correlationKey = correlationKey;
        this.lastModificationDate = lastModificationDate;
        this.lastModificationUser = lastModificationUser;
        this.subject = subject;
        this.processInstanceDescription = processInstanceDescription;
    }
    
    public UserTaskInstanceWithPotOwnerDesc(String actualOwner, String createdBy,
                                            Date createdOn,Date expirationDate,
                                            Long taskId, String name, 
                                            Integer priority, Long processInstanceId,
                                            String processId, String status,
                                            String potOwner, String formName,
                                            String correlationKey, String subject,
                                            String deploymentId, String processInstanceDescription) {
              super(taskId, status, actualOwner, name, priority, createdBy, processId, processInstanceId, createdOn, formName, deploymentId,expirationDate);
              this.potentialOwners.add(potOwner);
              this.correlationKey = correlationKey;
              this.processInstanceDescription = processInstanceDescription;
              this.subject = subject;
    }
    
    @Override
    public List<String> getPotentialOwners() {
        
        return this.potentialOwners;
    }
    
    @Override
    public String getCorrelationKey() {
        
        return this.correlationKey;
    }

    @Override
    public Date getLastModificationDate() {
        
        return this.lastModificationDate;
    }
    
    @Override
    public String getSubject() {
        return subject;
    }
    
    @Override
    public String getLastModificationUser() {
        return this.lastModificationUser;
    }

    public void setPotentialOwners(List<String> potOwners) {
        this.potentialOwners = potOwners;
    }
    
    public void addPotOwner(String potOwners) {
        this.potentialOwners.add(potOwners);
    }
    
    public void setCorrelationKey(String correlationKey) {
        this.correlationKey = correlationKey;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public void setLastModificationUser(String lastModificationUser) {
        this.lastModificationUser = lastModificationUser;
    }

    public Map<String,Object> getInputdata() {
        return inputdata;
    }

    public void setInputdata(Map<String,Object> inputdata) {
        this.inputdata = inputdata;
    }

    public Map<String,Object> getOutputdata() {
        return outputdata;
    }

    public void setOutputdata(Map<String,Object> outputdata) {
        this.outputdata = outputdata;
    }
    
    public void addInputdata(String variable, Object variableValue) {
        if (this.inputdata == null) {
            this.inputdata = new HashMap<String, Object>();
        }
        this.inputdata.put(variable, variableValue);
    }
    
    public void addOutputdata(String variable, Object variableValue) {
        if (this.outputdata == null) {
            this.outputdata = new HashMap<String, Object>();
        }
        this.outputdata.put(variable, variableValue);
    }

    public String getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }
 
}
