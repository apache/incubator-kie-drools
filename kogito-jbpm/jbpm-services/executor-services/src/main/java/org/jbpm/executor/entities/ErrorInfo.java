/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author salaboy
 */
@Entity
public class ErrorInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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

    
    
}
