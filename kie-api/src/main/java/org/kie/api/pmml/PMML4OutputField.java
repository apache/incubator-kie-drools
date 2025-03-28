/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.api.pmml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@XmlRootElement(name="baseOutputField")
@XmlAccessorType(XmlAccessType.FIELD)
@Role(Type.EVENT)
public class PMML4OutputField extends PMML4DataField {
    @XmlElement(name="warning")
    private String warning = "No warning";
    @XmlAttribute(name="target")
    private boolean target = false;
    
    public String getWarning() {
        return warning;
    }
    public void setWarning(String warning) {
        this.warning = warning;
    }
    public boolean isTarget() {
        return target;
    }
    public void setTarget(boolean target) {
        this.target = target;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (target ? 1231 : 1237);
        result = prime * result + ((warning == null) ? 0 : warning.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PMML4OutputField other = (PMML4OutputField) obj;
        if (target != other.target) {
            return false;
        }
        if (warning == null) {
            if (other.warning != null) {
                return false;
            }
        } else if (!warning.equals(other.warning)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder("PMML4OutputField - [");
        bldr.append(super.toString())
            .append(", warning=").append(warning)
            .append(", target=").append(target)
            .append("]");
        return bldr.toString();
    }
    
}
