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

package org.jbpm.runtime.manager.impl.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.kie.internal.runtime.error.ExecutionError;


@Entity
@Table(name = "ExecutionErrorInfo", indexes = {@Index(name = "IDX_ErrorInfo_pInstId", columnList = "PROCESS_INST_ID"), @Index(name = "IDX_ErrorInfo_errorAck", columnList = "ERROR_ACK")})
@SequenceGenerator(name="execErrorInfoIdSeq", sequenceName="EXEC_ERROR_INFO_ID_SEQ", allocationSize=1)
public class ExecutionErrorInfo extends ExecutionError implements Serializable {

	private static final long serialVersionUID = 6669858787722894023L;
	
	private Long id;

    public ExecutionErrorInfo() {
        
    }
    
    public ExecutionErrorInfo(String errorId, String type, String deploymentId, Long processInstanceId, String processId, Long activityId, String activityName, Long jobId, String errorMessage, String error, Date errorDate, Long initActivityId) {
        super();
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
        this.errorDate = errorDate;
        this.acknowledged = new Short("0");
        this.initActivityId = initActivityId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="execErrorInfoIdSeq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name="ERROR_ID")
    public String getErrorId() {
        return this.errorId;
    }    

    @Column(name="ERROR_TYPE")
    @Override
    public String getType() {
        return super.getType();
    }

    @Column(name="DEPLOYMENT_ID")
    @Override
    public String getDeploymentId() {
        return super.getDeploymentId();
    }

    @Column(name="PROCESS_INST_ID")
    @Override
    public Long getProcessInstanceId() {
        return super.getProcessInstanceId();
    }
    
    @Column(name="ACTIVITY_ID")
    @Override
    public Long getActivityId() {
        return super.getActivityId();
    }

    @Column(name="ERROR_MSG")
    @Override
    public String getErrorMessage() {
        return super.getErrorMessage();
    }

    @Lob
    @Column(name="ERROR_INFO", length=65535)
    @Override
    public String getError() {
        return super.getError();
    }

    @Column(name="ERROR_ACK")
    @Override
    protected Short getAcknowledged() {
        return super.getAcknowledged();
    }

    @Column(name="ERROR_ACK_BY")
    @Override
    public String getAcknowledgedBy() {
        return super.getAcknowledgedBy();
    }

    @Column(name="ERROR_ACK_AT")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Override
    public Date getAcknowledgedAt() {
        return super.getAcknowledgedAt();
    }

    @Column(name="PROCESS_ID")
    @Override
    public String getProcessId() {
        return super.getProcessId();
    }

    @Column(name="ACTIVITY_NAME")
    @Override
    public String getActivityName() {
        return super.getActivityName();
    }

    @Column(name="ERROR_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Override
    public Date getErrorDate() {
        return super.getErrorDate();
    }
  
    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }
    
    @Column(name="JOB_ID")
    @Override
    public Long getJobId() {
        return super.getJobId();
    }

    @Column(name="INIT_ACTIVITY_ID")
    @Override
    public Long getInitActivityId() {
        return super.getInitActivityId();
    }

    @Override
    public String toString() {
        return "ExecutionErrorInfo [errorId=" + errorId + ", type=" + type + ", deploymentId=" + deploymentId + ", processInstanceId=" + processInstanceId + ", initActivityId=" + initActivityId + 
                ", processId=" + processId + ", activityId=" + activityId + ", activityName=" + activityName + ", errorMessage=" + errorMessage + 
                ", acknowledged=" + acknowledged + "]";
    } 
	
	
}
