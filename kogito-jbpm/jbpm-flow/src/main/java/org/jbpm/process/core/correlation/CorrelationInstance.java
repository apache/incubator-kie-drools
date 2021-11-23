/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.core.correlation;

import java.util.HashMap;
import java.util.Map;

public class CorrelationInstance {

    private String id;
    private String name;

    private Map<String, Object> properties;

    public CorrelationInstance(String id, String name) {
        this.id = id;
        this.name = name;
        properties = new HashMap<>();
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
        CorrelationInstance other = (CorrelationInstance) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        if ((other.properties == null && properties != null) || (other.properties != null && properties == null)) {
            return false;
        }

        if (properties.size() != other.properties.size()) {
            return false;
        }

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (!entry.getValue().equals(other.properties.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "CorrelationInstance [id=" + id + ", name=" + name + ", properties=" + properties + "]";
    }

}