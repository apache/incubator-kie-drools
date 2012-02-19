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

import org.jbpm.formapi.shared.menu.FormEffectDescription;

public class FormEffectDTO {

    private String _className;

    public FormEffectDTO() {
        // jaxb needs a default constructor
    }
    
    public FormEffectDTO(FormEffectDescription effect) {
        this._className = effect.getClassName();
    }

    @XmlAttribute 
    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        this._className = className;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_className == null) ? 0 : _className.hashCode());
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
        FormEffectDTO other = (FormEffectDTO) obj;
        if (_className == null) {
            if (other._className != null)
                return false;
        } else if (!_className.equals(other._className))
            return false;
        return true;
    }
}
