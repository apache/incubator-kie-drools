/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.api.pmml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ParameterInfo")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="parameterInfo")
public class ParameterInfo<T> {
    @XmlAttribute(name="correlationId", required=false)
    private String correlationId;
    @XmlAttribute(name="name", required=true)
    private String name;
    private String capitalizedName;
    @XmlElement(name="type")
    private Class<T> type;
    @XmlElement(name="value", type=Object.class)
    private T value;

    public ParameterInfo() {
        // required for JAXB
    }
    
    public ParameterInfo(String correlationId, String name, Class<T> type, T value) {
        this.correlationId = correlationId;
        this.name = name;
        this.capitalizedName = name.substring(0, 1).toUpperCase()+name.substring(1);
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getCapitalizedName() {
        return capitalizedName;
    }

    public void setName(String name) {
        this.name = name;
        this.capitalizedName = name.substring(0, 1).toUpperCase()+name.substring(1);
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        if (this.type == null) {
            this.type = (Class<T>) value.getClass();
        }
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ParameterInfo other = (ParameterInfo) obj;
        if (correlationId == null) {
            if (other.correlationId != null) {
                return false;
            }
        } else if (!correlationId.equals(other.correlationId)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (value == null) {
            return other.value == null;
        } else {
            return value.equals(other.value);
        }
    }

    @Override
    public String toString() {
        return "ParameterInfo( " + "correlationId=" + correlationId + ", " +
                "name=" + name + ", " +
                "type=" + (type != null ? type.getName() : "undetermined") + ", " +
                "value=" + value + " )";
    }
}
