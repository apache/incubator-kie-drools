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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement (name ="properties") public class PropertiesDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { PropertiesDTO.class, PropertiesItemDTO.class };
    
    private List<PropertiesItemDTO> _property = new ArrayList<PropertiesItemDTO>();

    public PropertiesDTO() {
        // jaxb needs a default constructor
    }
    
    public PropertiesDTO(Map<String, String> props) {
        for (Map.Entry<String, String> entry : props.entrySet()) {
            _property.add(new PropertiesItemDTO(entry.getKey(), entry.getValue()));
        }
    }

    @XmlElement
    public List<PropertiesItemDTO> getProperty() {
        return _property;
    }

    public void setProperty(List<PropertiesItemDTO> property) {
        this._property = property;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PropertiesDTO)) return false;
        PropertiesDTO other = (PropertiesDTO) obj;
        return (other._property == null && this._property == null) || (this._property != null && this._property.equals(other._property));
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = _property == null ? 0 : _property.hashCode();
        result = result * 37 + aux;
        return result;
    }
}
