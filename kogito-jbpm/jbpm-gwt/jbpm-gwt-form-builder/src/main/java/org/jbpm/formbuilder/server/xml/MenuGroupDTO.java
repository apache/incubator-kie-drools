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

import org.jbpm.formapi.shared.menu.MenuItemDescription;

public class MenuGroupDTO {

    private List<MenuItemDTO> _menuItem = new ArrayList<MenuItemDTO>();
    private String _name;
    
    public MenuGroupDTO() {
        // jaxb needs default constructors
    }
    
    public MenuGroupDTO(String name, List<MenuItemDescription> items) {
        this._name = name;
        if (items != null) {
            for (MenuItemDescription item : items) {
                _menuItem.add(new MenuItemDTO(item));
            }
        }
    }

    @XmlElement
    public List<MenuItemDTO> getMenuItem() {
        return _menuItem;
    }

    public void setMenuItem(List<MenuItemDTO> menuItem) {
        this._menuItem = menuItem;
    }

    @XmlAttribute 
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_menuItem == null) ? 0 : _menuItem.hashCode());
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
        MenuGroupDTO other = (MenuGroupDTO) obj;
        if (_menuItem == null) {
            if (other._menuItem != null)
                return false;
        } else if (!_menuItem.equals(other._menuItem))
            return false;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        return true;
    }
}
