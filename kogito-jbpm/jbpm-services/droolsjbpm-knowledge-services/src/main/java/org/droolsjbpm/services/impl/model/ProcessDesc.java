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
    private String sessionName;
    private String originalPath;
    
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

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (this.pki ^ (this.pki >>> 32));
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 17 * hash + (this.packageName != null ? this.packageName.hashCode() : 0);
        hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 17 * hash + (this.knowledgeType != null ? this.knowledgeType.hashCode() : 0);
        hash = 17 * hash + (this.namespace != null ? this.namespace.hashCode() : 0);
        hash = 17 * hash + (this.domainName != null ? this.domainName.hashCode() : 0);
        hash = 17 * hash + this.sessionId;
        hash = 17 * hash + (this.sessionName != null ? this.sessionName.hashCode() : 0);
        hash = 17 * hash + (this.originalPath != null ? this.originalPath.hashCode() : 0);
        hash = 17 * hash + (this.dataTimeStamp != null ? this.dataTimeStamp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessDesc other = (ProcessDesc) obj;
        if (this.pki != other.pki) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.packageName == null) ? (other.packageName != null) : !this.packageName.equals(other.packageName)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.knowledgeType == null) ? (other.knowledgeType != null) : !this.knowledgeType.equals(other.knowledgeType)) {
            return false;
        }
        if ((this.namespace == null) ? (other.namespace != null) : !this.namespace.equals(other.namespace)) {
            return false;
        }
        if ((this.domainName == null) ? (other.domainName != null) : !this.domainName.equals(other.domainName)) {
            return false;
        }
        if (this.sessionId != other.sessionId) {
            return false;
        }
        if ((this.sessionName == null) ? (other.sessionName != null) : !this.sessionName.equals(other.sessionName)) {
            return false;
        }
        if ((this.originalPath == null) ? (other.originalPath != null) : !this.originalPath.equals(other.originalPath)) {
            return false;
        }
        if (this.dataTimeStamp != other.dataTimeStamp && (this.dataTimeStamp == null || !this.dataTimeStamp.equals(other.dataTimeStamp))) {
            return false;
        }
        return true;
    }

    

}
