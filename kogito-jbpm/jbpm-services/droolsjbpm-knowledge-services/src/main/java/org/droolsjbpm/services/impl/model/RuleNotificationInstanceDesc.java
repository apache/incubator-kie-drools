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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author salaboy
 */
@Entity
public class RuleNotificationInstanceDesc implements Serializable {

    @Id
    @GeneratedValue()
    private long pk;
    @Column(length = 5000)
    private String notification;
    
    private int sessionId;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataTimeStamp;
    
    

    public RuleNotificationInstanceDesc() {
        dataTimeStamp = new Date();
    }

    public RuleNotificationInstanceDesc(int sessionId, String notification) {
        this();
        this.sessionId = sessionId;
        this.notification = notification;
    }
    

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public void setDataTimeStamp(Date dataTimeStamp) {
        this.dataTimeStamp = dataTimeStamp;
    }

    @Override
    public String toString() {
        return "RuleNotificationInstanceDesc{" + "pk=" + pk + ", notification=" + notification + ", sessionId=" + sessionId + ", dataTimeStamp=" + dataTimeStamp + '}';
    }
    
}
