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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jbpm.formapi.shared.menu.ValidationDescription;

public class ValidationDTO {

    private String _className;
    private List<PropertiesItemDTO> _property = new ArrayList<PropertiesItemDTO>();

    public ValidationDTO() {
        // jaxb needs a default constructor
    }
    
    public ValidationDTO(ValidationDescription desc) {
        this._className = desc.getClassName();
        if (desc.getProperties() != null) {
            for (Map.Entry<String, String> entry : desc.getProperties().entrySet()) {
                getProperty().add(new PropertiesItemDTO(entry.getKey(), entry.getValue()));
            }
        }
    }
    
    @XmlAttribute
    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        this._className = className;
    }

    @XmlElement
    public List<PropertiesItemDTO> getProperty() {
        if (_property == null) {
            _property = new ArrayList<PropertiesItemDTO>();
        }
        return _property;
    }

    public void setProperty(List<PropertiesItemDTO> property) {
        this._property = property;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ValidationDTO)) return false;
        ValidationDTO other = (ValidationDTO) obj;
        boolean equals = (other._className == null && this._className == null) || (this._className != null && this._className.equals(other._className));
        if (!equals) return equals;
        equals = (other._property == null && this._property == null) || (this._property != null && this._property.equals(other._property));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = _className == null ? 0 : _className.hashCode();
        result = result * 37 + aux;
        aux = _property == null ? 0 : _property.hashCode();
        result = result * 37 + aux;
        return result;
    }
}
