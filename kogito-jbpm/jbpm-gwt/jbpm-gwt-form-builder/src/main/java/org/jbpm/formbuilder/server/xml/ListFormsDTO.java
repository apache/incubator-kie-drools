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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;

@XmlRootElement (name = "listForms") public class ListFormsDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { ListFormsDTO.class, FormDefDTO.class };
    
    private List<FormDefDTO> _form = new ArrayList<FormDefDTO>();

    public ListFormsDTO() {
        // jaxb needs a default constructor
    }
    
    public ListFormsDTO(List<FormRepresentation> forms) throws FormEncodingException {
        if (forms != null) {
            for (FormRepresentation form : forms) {
                _form.add(new FormDefDTO(form));
            }
        }
    }
    
    public ListFormsDTO(FormRepresentation form) throws FormEncodingException {
        if (form != null) {
            _form.add(new FormDefDTO(form));
        }
    }
    
    @XmlElement
    public List<FormDefDTO> getForm() {
        return _form;
    }

    public void setForm(List<FormDefDTO> form) {
        this._form = form;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_form == null) ? 0 : _form.hashCode());
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
        ListFormsDTO other = (ListFormsDTO) obj;
        if (_form == null) {
            if (other._form != null)
                return false;
        } else if (!_form.equals(other._form))
            return false;
        return true;
    }
}
