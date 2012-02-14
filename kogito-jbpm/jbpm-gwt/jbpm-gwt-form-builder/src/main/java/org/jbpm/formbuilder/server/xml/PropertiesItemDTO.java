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

import javax.xml.bind.annotation.XmlAttribute;

public class PropertiesItemDTO {

    private String _key;
    private String _value;
    
    public PropertiesItemDTO() {
        // jaxb needs a default constructor
    }
    
    public PropertiesItemDTO(String key, String value) {
        this._key = key;
        this._value = value;
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
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PropertiesItemDTO)) return false;
        PropertiesItemDTO other = (PropertiesItemDTO) obj;
        boolean equals = (other._key == null && this._key == null) || (this._key != null && this._key.equals(other._key));
        if (!equals) return equals;
        equals = (other._value == null && this._value == null) || (this._value != null && this._value.equals(other._value));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = _key == null ? 0 : _key.hashCode();
        result = result * 37 + aux;
        aux = _value == null ? 0 : _value.hashCode();
        result = result * 37 + aux;
        return result;
    }
}
