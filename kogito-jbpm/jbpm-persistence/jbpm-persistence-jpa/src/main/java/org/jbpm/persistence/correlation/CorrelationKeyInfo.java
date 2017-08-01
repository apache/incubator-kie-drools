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
package org.jbpm.persistence.correlation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.jbpm.persistence.api.PersistentCorrelationKey;
import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

@Entity
@SequenceGenerator(name="correlationKeyInfoIdSeq", sequenceName="CORRELATION_KEY_ID_SEQ")
public class CorrelationKeyInfo implements PersistentCorrelationKey, Serializable {

	private static final long serialVersionUID = 4469298702447675428L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="correlationKeyInfoIdSeq")
    @Column(name = "keyId")
    private long id;
    
    @Version
    @Column(name = "OPTLOCK")
    private int version;
    
    private long processInstanceId;
    
    private String name;
    
    @OneToMany(mappedBy="correlationKey", cascade=CascadeType.ALL)
    private List<CorrelationPropertyInfo> properties;
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<CorrelationProperty<?>> getProperties() {
        return new ArrayList<CorrelationProperty<?>>(this.properties);
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addProperty(CorrelationPropertyInfo property) {
        if (this.properties == null) {
            this.properties = new ArrayList<CorrelationPropertyInfo>();
        }
        property.setCorrelationKey(this);
        this.properties.add(property);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + (int) (processInstanceId ^ (processInstanceId >>> 32));
        result = prime * result
                + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CorrelationKeyInfo other = (CorrelationKeyInfo) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (processInstanceId != other.processInstanceId)
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CorrelationKey [name=" + name + ", properties="
                + properties + "]";
    }

    public long getId() {
        return id;
    }

	@Override
	public String toExternalForm() {
		return CorrelationKeyXmlAdapter.marshalCorrelationKey(this);
	}

}
