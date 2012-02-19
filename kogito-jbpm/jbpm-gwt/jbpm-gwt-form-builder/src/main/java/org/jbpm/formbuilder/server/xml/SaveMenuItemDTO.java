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


@XmlRootElement (name = "menuItem") public class SaveMenuItemDTO {

    private String _groupName;
    private String _name;
    private String _clone;
    private List<FormEffectDTO> _effect = new ArrayList<FormEffectDTO>();
    private List<String> _allowedEvent = new ArrayList<String>();
    
    public SaveMenuItemDTO() {
        // jaxb needs a default constructor
    }
    
    @XmlElement
    public String getGroupName() {
        return _groupName;
    }
    
    public void setGroupName(String groupName) {
        this._groupName = groupName;
    }
    
    @XmlElement
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        this._name = name;
    }
    
    @XmlElement
    public String getClone() {
        return _clone;
    }
    
    public void setClone(String clone) {
        this._clone = clone;
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
}
