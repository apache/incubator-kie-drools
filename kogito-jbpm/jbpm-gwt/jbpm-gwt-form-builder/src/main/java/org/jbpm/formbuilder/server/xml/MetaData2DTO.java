/*
 * Copyright 2011 JBoss Inc
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
package org.jbpm.formbuilder.server.xml;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

public class MetaData2DTO {

    private String _key;
    private String _value;

    public MetaData2DTO() {
        // jaxb requires a default constructor
    }
    
    public MetaData2DTO(Map.Entry<String, String> entry) {
        this._key = entry.getKey();
        this._value = entry.getValue();
    }

    @XmlAttribute 
    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        this._key = key;
    }

    @XmlAttribute 
    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        this._value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_key == null) ? 0 : _key.hashCode());
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
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
        MetaData2DTO other = (MetaData2DTO) obj;
        if (_key == null) {
            if (other._key != null)
                return false;
        } else if (!_key.equals(other._key))
            return false;
        if (_value == null) {
            if (other._value != null)
                return false;
        } else if (!_value.equals(other._value))
            return false;
        return true;
    }
}
