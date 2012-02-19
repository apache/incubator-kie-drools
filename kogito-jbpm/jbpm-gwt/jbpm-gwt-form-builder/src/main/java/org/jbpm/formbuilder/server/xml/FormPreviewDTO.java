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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jbpm.formapi.shared.api.FormRepresentation;

@XmlRootElement (name = "formPreview") public class FormPreviewDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { FormPreviewDTO.class, FormPreviewParameterDTO.class };
    
    private List<FormPreviewParameterDTO> _input = new ArrayList<FormPreviewParameterDTO>();
    private String _representation;
    private FormRepresentation _form;
    
    public FormPreviewDTO() {
        // jaxb needs a default constructor
    }

    @XmlElement
    public List<FormPreviewParameterDTO> getInput() {
        return _input;
    }

    public void setInput(List<FormPreviewParameterDTO> input) {
        this._input = input;
    }

    @XmlElement
    public String getRepresentation() {
        return _representation;
    }

    public void setRepresentation(String representation) {
        this._representation = representation;
    }

    @XmlTransient
    public FormRepresentation getForm() {
        return _form;
    }

    public void setForm(FormRepresentation form) {
        this._form = form;
    }

    public Map<String, Object> getInputsAsMap() {
        Map<String, Object> retval = null;
        if (_input != null) {
            retval = new HashMap<String, Object>();
            for (FormPreviewParameterDTO input : _input) {
                if (input != null) {
                    retval.put(input.getKey(), input.getValue());
                }
            }
        }
        return retval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_form == null) ? 0 : _form.hashCode());
        result = prime * result + ((_input == null) ? 0 : _input.hashCode());
        result = prime * result
                + ((_representation == null) ? 0 : _representation.hashCode());
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
        FormPreviewDTO other = (FormPreviewDTO) obj;
        if (_form == null) {
            if (other._form != null)
                return false;
        } else if (!_form.equals(other._form))
            return false;
        if (_input == null) {
            if (other._input != null)
                return false;
        } else if (!_input.equals(other._input))
            return false;
        if (_representation == null) {
            if (other._representation != null)
                return false;
        } else if (!_representation.equals(other._representation))
            return false;
        return true;
    }
}
