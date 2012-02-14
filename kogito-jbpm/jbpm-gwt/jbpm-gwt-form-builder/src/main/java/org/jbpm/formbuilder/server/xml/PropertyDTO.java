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
import javax.xml.bind.annotation.XmlType;

import org.jbpm.formbuilder.shared.task.TaskPropertyRef;

@XmlType public class PropertyDTO {

    private String _name;
    private String _source;
    
    public PropertyDTO() {
        // jaxb needs a default constructor
    }
    
    public PropertyDTO(TaskPropertyRef ref) {
        this._name = ref.getName();
        this._source = ref.getSourceExpresion();
    }
    
    @XmlAttribute 
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        this._name = name;
    }
    
    @XmlAttribute 
    public String getSource() {
        return _source;
    }

    public void setSource(String source) {
        this._source = source;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        result = prime * result + ((_source == null) ? 0 : _source.hashCode());
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
        PropertyDTO other = (PropertyDTO) obj;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        if (_source == null) {
            if (other._source != null)
                return false;
        } else if (!_source.equals(other._source))
            return false;
        return true;
    }
}
