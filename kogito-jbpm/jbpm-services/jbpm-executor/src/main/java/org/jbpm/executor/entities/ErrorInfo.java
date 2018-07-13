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

package org.jbpm.executor.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "ErrorInfo", indexes = {@Index(name = "IDX_ErrorInfo_Id", columnList = "REQUEST_ID")})
@SequenceGenerator(name="errorInfoIdSeq", sequenceName="ERROR_INFO_ID_SEQ")
public class ErrorInfo implements org.kie.internal.executor.api.ErrorInfo, Serializable {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorInfo.class);

    private static final long serialVersionUID = 1548071325967795108L;
    
    @Transient
	private final int MESSAGE_LOG_LENGTH = Integer.parseInt(System.getProperty("org.kie.executor.msg.length", "255"));
    @Transient
    private final int STACKTRACE_LOG_LENGTH = Integer.parseInt(System.getProperty("org.kie.executor.stacktrace.length", "5000"));
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="errorInfoIdSeq")
    private Long id;
    @Column(name="timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    private String message;
    @Column(length=5000)
    private String stacktrace;
    
    @ManyToOne
    @JoinColumn(name="REQUEST_ID", nullable=false)
    private RequestInfo requestInfo;
    
    public ErrorInfo() {
    }

    public ErrorInfo(String message, String stacktrace) {
        this.message = message;
        this.stacktrace = stacktrace;
        this.time = new Date();
        trimToSize();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }   

	@Override
    public String toString() {
        return "ErrorInfo{" + "id=" + id + ", time=" + time + ", message=" + message + ", stacktrace=" + stacktrace + ", requestInfo=" + requestInfo.getId() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ErrorInfo other = (ErrorInfo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        if ((this.stacktrace == null) ? (other.stacktrace != null) : !this.stacktrace.equals(other.stacktrace)) {
            return false;
        }
        if (this.requestInfo != other.requestInfo && (this.requestInfo == null || !this.requestInfo.equals(other.requestInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 37 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 37 * hash + (this.stacktrace != null ? this.stacktrace.hashCode() : 0);
        hash = 37 * hash + (this.requestInfo != null ? this.requestInfo.hashCode() : 0);
        return hash;
    }

    protected void trimToSize() {
    	if (this.message != null && this.message.length() > MESSAGE_LOG_LENGTH) {
    		logger.warn("trimming message as it's too long : {}", this.message.length());
    		this.message = message.substring(0, MESSAGE_LOG_LENGTH);
    	}
    	
    	if (this.stacktrace != null && this.stacktrace.length() > STACKTRACE_LOG_LENGTH) {
    		logger.warn("trimming stacktrace as it's too long : {}", this.stacktrace.length());
    		this.stacktrace = stacktrace.substring(0, STACKTRACE_LOG_LENGTH);
    	}
    }
    
}
