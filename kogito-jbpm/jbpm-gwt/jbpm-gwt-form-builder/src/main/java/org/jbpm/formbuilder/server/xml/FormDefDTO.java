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

import javax.xml.bind.annotation.XmlElement;

import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;

public class FormDefDTO {

    private String _json;
    
    public FormDefDTO() {
        // jaxb needs a default constructor
    }
    
    public FormDefDTO(FormRepresentation form) throws FormEncodingException {
        FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
        this._json = encoder.encode(form);
    }
    
    @XmlElement
    public String getJson() {
        return _json;
    }
    
    public void setJson(String json) {
        this._json = json;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_json == null) ? 0 : _json.hashCode());
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
        FormDefDTO other = (FormDefDTO) obj;
        if (_json == null) {
            if (other._json != null)
                return false;
        } else if (!_json.equals(other._json))
            return false;
        return true;
    }
}
