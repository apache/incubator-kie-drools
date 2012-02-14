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

import org.jbpm.formapi.shared.menu.MenuOptionDescription;

public class MenuOptionDTO {
    
    private String _name;
    private String _commandClass;
    private List<MenuOptionDTO> _menuOption = new ArrayList<MenuOptionDTO>();

    public MenuOptionDTO() {
        // jaxb needs a default constructor
    }
    
    public MenuOptionDTO(MenuOptionDescription option) {
        this._name = option.getHtml();
        if (option.getCommandClass() != null) {
            this._commandClass = option.getCommandClass();
        }
        if (option.getSubMenu() != null) {
            for (MenuOptionDescription opt : option.getSubMenu()) {
                _menuOption.add(new MenuOptionDTO(opt));
            }
        }
    }

    @XmlAttribute
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    @XmlAttribute
    public String getCommandClass() {
        return _commandClass;
    }

    public void setCommandClass(String commandClass) {
        this._commandClass = commandClass;
    }

    @XmlElement 
    public List<MenuOptionDTO> getMenuOption() {
        return _menuOption;
    }

    public void setMenuOption(List<MenuOptionDTO> menuOption) {
        this._menuOption = menuOption;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_commandClass == null) ? 0 : _commandClass.hashCode());
        result = prime * result
                + ((_menuOption == null) ? 0 : _menuOption.hashCode());
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
        MenuOptionDTO other = (MenuOptionDTO) obj;
        if (_commandClass == null) {
            if (other._commandClass != null)
                return false;
        } else if (!_commandClass.equals(other._commandClass))
            return false;
        if (_menuOption == null) {
            if (other._menuOption != null)
                return false;
        } else if (!_menuOption.equals(other._menuOption))
            return false;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        return true;
    }
}
