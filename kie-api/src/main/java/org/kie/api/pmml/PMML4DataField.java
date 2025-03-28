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
import jakarta.xml.bind.annotation.XmlRootElement;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@XmlRootElement(name="baseDataField")
@XmlAccessorType(XmlAccessType.FIELD)
@Role(Type.EVENT)
public class PMML4DataField extends PMML4AbstractField {
    @XmlAttribute(name="name")
    private String name;
    @XmlAttribute(name="context")
    private String context;
    @XmlAttribute(name="valid")
    private boolean valid;
    @XmlAttribute(name="missing")
    private boolean missing;

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isMissing() {
        return missing;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + (valid ? 1231 : 1237);
        result = prime * result + (missing ? 1231 : 1237);
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
        PMML4DataField other = (PMML4DataField) obj;
        if (context == null) {
            if (other.context != null) {
                return false;
            }
        } else if (!context.equals(other.context)) {
            return false;
        }
        if (missing != other.missing) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (valid != other.valid) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder("PMML4DataField - [");
        bldr.append("context=").append(context)
            .append(", name=").append(name)
            .append(", missing=").append(missing)
            .append(", valid=").append(valid)
            .append("]");
        return bldr.toString();
    }

}
