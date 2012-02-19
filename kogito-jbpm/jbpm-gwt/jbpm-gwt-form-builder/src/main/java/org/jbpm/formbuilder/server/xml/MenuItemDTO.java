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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.menu.FormEffectDescription;
import org.jbpm.formapi.shared.menu.MenuItemDescription;

public class MenuItemDTO {

    private String _className;
    private String _optionName;
    private String _itemJson;
    private List<FormEffectDTO> _effect = new ArrayList<FormEffectDTO>();
    private List<String> _allowedEvent = new ArrayList<String>();

    public MenuItemDTO() {
        // jaxb needs a default constructor
    }
    
    public MenuItemDTO(MenuItemDescription item) {
        this._className = item.getClassName();
        this._optionName = item.getName();
        for (FormEffectDescription eff : item.getEffects()) {
            _effect.add(new FormEffectDTO(eff));
        }
        if (item.getAllowedEvents() != null) {
            _allowedEvent.addAll(item.getAllowedEvents());
        }
        try {
            String json = FormEncodingFactory.getEncoder().encode(item.getItemRepresentation());
            this._itemJson = json;
        } catch (FormEncodingException e) {
            
        }
    }

    @XmlElement 
    public String getItemJson() {
        return _itemJson;
    }

    public void setItemJson(String itemJson) {
        this._itemJson = itemJson;
    }

    @XmlAttribute 
    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        this._className = className;
    }

    @XmlElement 
    public List<FormEffectDTO> getEffect() {
        return _effect;
    }

    public void setEffect(List<FormEffectDTO> effect) {
        this._effect = effect;
    }

    @XmlElement
    public List<String> getAllowedEvent() {
        return _allowedEvent;
    }
    
    public void setAllowedEvent(List<String> allowedEvent) {
        this._allowedEvent = allowedEvent;
    }
    
    @XmlAttribute
    public String getOptionName() {
        return _optionName;
    }
    
    public void setOptionName(String optionName) {
        this._optionName = optionName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_allowedEvent == null) ? 0 : _allowedEvent.hashCode());
        result = prime * result
                + ((_className == null) ? 0 : _className.hashCode());
        result = prime * result + ((_effect == null) ? 0 : _effect.hashCode());
        result = prime * result
                + ((_itemJson == null) ? 0 : _itemJson.hashCode());
        result = prime * result
                + ((_optionName == null) ? 0 : _optionName.hashCode());
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
        MenuItemDTO other = (MenuItemDTO) obj;
        if (_allowedEvent == null) {
            if (other._allowedEvent != null)
                return false;
        } else if (!_allowedEvent.equals(other._allowedEvent))
            return false;
        if (_className == null) {
            if (other._className != null)
                return false;
        } else if (!_className.equals(other._className))
            return false;
        if (_effect == null) {
            if (other._effect != null)
                return false;
        } else if (!_effect.equals(other._effect))
            return false;
        if (_itemJson == null) {
            if (other._itemJson != null)
                return false;
        } else if (!_itemJson.equals(other._itemJson))
            return false;
        if (_optionName == null) {
            if (other._optionName != null)
                return false;
        } else if (!_optionName.equals(other._optionName))
            return false;
        return true;
    }
}
