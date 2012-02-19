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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;

@XmlRootElement (name = "listFormItems") public class ListFormsItemsDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { ListFormsItemsDTO.class, FormItemDefDTO.class };
    
    private List<FormItemDefDTO> _formItem = new ArrayList<FormItemDefDTO>();

    public ListFormsItemsDTO() {
        // jaxb needs a default constructor
    }
    
    public ListFormsItemsDTO(Map<String, FormItemRepresentation> formItems) throws FormEncodingException {
        if (formItems != null) {
            for (Map.Entry<String, FormItemRepresentation> entry : formItems.entrySet()) {
                _formItem.add(new FormItemDefDTO(entry.getKey(), entry.getValue()));
            }
        }
    }
    
    public ListFormsItemsDTO(String formItemId, FormItemRepresentation formItem) throws FormEncodingException {
        if (formItem != null) {
            _formItem.add(new FormItemDefDTO(formItemId, formItem));
        }
    }
    
    @XmlElement
    public List<FormItemDefDTO> getFormItem() {
        return _formItem;
    }

    public void setFormItem(List<FormItemDefDTO> formItem) {
        this._formItem = formItem;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_formItem == null) ? 0 : _formItem.hashCode());
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
        ListFormsItemsDTO other = (ListFormsItemsDTO) obj;
        if (_formItem == null) {
            if (other._formItem != null)
                return false;
        } else if (!_formItem.equals(other._formItem))
            return false;
        return true;
    }
}
