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

import org.jbpm.formapi.shared.menu.MenuItemDescription;

@XmlRootElement (name ="menuGroups") public class ListMenuItemsDTO {

    public static final Class<?>[] RELATED_CLASSES = new Class<?>[] { ListMenuItemsDTO.class, MenuGroupDTO.class, MenuItemDTO.class, FormEffectDTO.class };
    
    private List<MenuGroupDTO> _menuGroup = new ArrayList<MenuGroupDTO>();
    
    public ListMenuItemsDTO() {
        // jaxb needs a default constructor
    }
    
    public ListMenuItemsDTO(Map<String, List<MenuItemDescription>> items) {
        for (String group : items.keySet()) {
            _menuGroup.add(new MenuGroupDTO(group, items.get(group)));
        }
    }

    @XmlElement
    public List<MenuGroupDTO> getMenuGroup() {
        return _menuGroup;
    }

    public void setMenuGroup(List<MenuGroupDTO> menuGroup) {
        this._menuGroup = menuGroup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_menuGroup == null) ? 0 : _menuGroup.hashCode());
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
        ListMenuItemsDTO other = (ListMenuItemsDTO) obj;
        if (_menuGroup == null) {
            if (other._menuGroup != null)
                return false;
        } else if (!_menuGroup.equals(other._menuGroup))
            return false;
        return true;
    }
}
