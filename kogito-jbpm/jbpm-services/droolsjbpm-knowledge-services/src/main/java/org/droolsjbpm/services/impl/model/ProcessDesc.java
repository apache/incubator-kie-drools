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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author salaboy
 */
@Entity
public class ProcessDesc implements Serializable {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long pki;
    
    private String id;
    private String name;
    private String version;
    private String packageName;
    private String type;
    private String knowledgeType;
    private String namespace;
    private String domainName;
    private int    sessionId;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataTimeStamp;


    public ProcessDesc() {
        this.dataTimeStamp = new Date();
    }

    public ProcessDesc(long pk, String id, String name, String version, String packageName, String type, String knowledgeType, String namespace, String domainName, Date dataTimeStamp) {
        this.pki = pk;
        this.id = id;
        this.name = name;
        this.version = version;
        this.packageName = packageName;
        this.type = type;
        this.knowledgeType = knowledgeType;
        this.namespace = namespace;
        this.domainName = domainName;
        this.dataTimeStamp = dataTimeStamp;
    }

    
    public ProcessDesc(String id, String name, String version, String packageName, String type, String knowledgeType, String namespace, String domainName) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.packageName = packageName;
        this.type = type;
        this.knowledgeType = knowledgeType;
        this.namespace = namespace;
        this.domainName = domainName;
        this.dataTimeStamp = new Date();
    }

    public long getPki() {
        return pki;
    }

    public void setPki(long pk) {
        this.pki = pk;
    }
    
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getType() {
        return type;
    }

    public String getKnowledgeType() {
        return knowledgeType;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
      this.domainName = domainName;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public int getSessionId() {
      return sessionId;
    }

    public void setSessionId(int sessionId) {
      this.sessionId = sessionId;
    }
 
    @Override
    public String toString() {
        return "ProcessDesc["+dataTimeStamp.toString()+"]{" + "pk=" + pki + ", id=" + id + ", name=" + name + ", version=" + version + ", sessionId=" + sessionId + ", packageName=" + packageName + ", type=" + type + ", knowledgeType=" + knowledgeType + ", namespace=" + namespace + ", domainName=" + domainName + '}';
    }
    

    
    
}
