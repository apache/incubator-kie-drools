/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.error;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ExecutionError implements Serializable {

    private static final long serialVersionUID = 740619427108653407L;
    
    protected String errorId;
    
    protected String type;
    protected String deploymentId;
    protected Long processInstanceId;
    protected String processId;
    protected Long activityId;
    protected String activityName;
    protected Long jobId;
    
    protected String errorMessage;
    protected String error;
    
    protected Short acknowledged = 0;
    protected String acknowledgedBy;
    protected Date acknowledgedAt;
    
    protected Date errorDate;  
    
    protected Long initActivityId;

    public ExecutionError() {
        errorId = UUID.randomUUID().toString();
    }
    
    public ExecutionError(String errorId, String type, String deploymentId, Long processInstanceId, String processId, Long activityId, String activityName, Long jobId, String errorMessage, 
            short acknowledged, String acknowledgedBy, Date acknowledgedAt, 
            Date errorDate) {
        this(errorId, type, deploymentId, processInstanceId, processId, activityId, activityName, jobId, errorMessage, null, acknowledged, acknowledgedBy, acknowledgedAt, errorDate);
    }
    
    public ExecutionError(String errorId, String type, String deploymentId, Long processInstanceId, String processId, Long activityId, String activityName, Long jobId, String errorMessage, String error, 
            short acknowledged, String acknowledgedBy, Date acknowledgedAt, 
            Date errorDate) {
        this.errorId = errorId;
        this.type = type;
        this.deploymentId = deploymentId;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.activityId = activityId;
        this.activityName = activityName;
        this.jobId = jobId;
        this.errorMessage = errorMessage;
        this.error = error;
        this.acknowledged = acknowledged;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = acknowledgedAt;
        this.errorDate = errorDate;
    }

    public String getErrorId() {
        return errorId;
    }
    
    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDeploymentId() {
        return deploymentId;
    }
    
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
    public Long getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
       
    protected Short getAcknowledged() {
        return acknowledged;
    }
    
    protected void setAcknowledged(Short acknowledged) {
        this.acknowledged = acknowledged;
    }
    
    public boolean isAcknowledged() {
        if (acknowledged == null) {
            return false;
        }
        return (acknowledged == 1) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setAcknowledged(boolean acknowledged) {
        setAcknowledged(acknowledged ? new Short("1") : new Short("0")); 
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }
    
    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }
    
    public Date getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public void setAcknowledgedAt(Date acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public String getActivityName() {
        return activityName;
    }
    
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public static Builder builder() {
        return new Builder();
    }
   
    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }    
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public Long getInitActivityId() {
        return initActivityId;
    }

    
    public void setInitActivityId(Long initActivityId) {
        this.initActivityId = initActivityId;
    }

    @Override
    public String toString() {
        return "ExecutionError [type=" + type + ", deploymentId=" + deploymentId + ", processInstanceId=" + processInstanceId + 
                ", processId=" + processId + ", activityId=" + activityId + ", activityName=" + activityName + 
                ", errorMessage=" + errorMessage + ", acknowledged=" + acknowledged + ", acknowledgedBy=" + acknowledgedBy + 
                ", acknowledgedAt=" + acknowledgedAt + "]";
    }


    public static class Builder {
        private ExecutionError error = new ExecutionError();

        public ExecutionError build() {
            return error;
        }

        public Builder type(String type) {
            error.setType(type);
            return this;
        }
        
        public Builder deploymentId(String deploymentId) {
            error.setDeploymentId(deploymentId);
            return this;
        }
        
        public Builder message(String message) {
            error.setErrorMessage(message);
            return this;
        }
        
        public Builder error(String errorStr) {
            error.setError(errorStr);
            return this;
        }
        
        public Builder acknowledgedBy(String user) {
            error.setAcknowledgedBy(user);
            return this;
        }
        
        public Builder processInstanceId(Long piId) {
            error.setProcessInstanceId(piId);
            return this;
        }
        
        public Builder activityId(Long activityId) {
            error.setActivityId(activityId);
            return this;
        }
        
        public Builder acknowledged(boolean acknowledged) {
            error.setAcknowledged(acknowledged ? new Short("1") : new Short("0"));
            return this;
        }
        
        public Builder acknowledgedAt(Date acknowledgedAt) {
            error.setAcknowledgedAt(acknowledgedAt);
            return this;
        }
        
        public Builder processId(String processId) {
            error.setProcessId(processId);
            return this;
        }
        
        public Builder activityName(String activityName) {
            error.setActivityName(activityName);
            return this;
        }
        
        public Builder errorDate(Date errorDate) {
            error.setErrorDate(errorDate);
            return this;
        }
        
        public Builder jobId(Long jobId) {
            error.setJobId(jobId);
            return this;
        }
        
        public Builder initActivityId(Long initActivityId) {
            error.setInitActivityId(initActivityId);
            return this;
        }
           
    }
}
