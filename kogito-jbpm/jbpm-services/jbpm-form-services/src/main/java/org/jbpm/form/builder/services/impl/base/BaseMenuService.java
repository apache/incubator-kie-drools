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
package org.jbpm.form.builder.services.impl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.services.api.MenuService;
import org.jbpm.form.builder.services.model.menu.MenuItemDescription;


public abstract class BaseMenuService implements MenuService {

    protected void removeFromMap(String groupName, MenuItemDescription item, Map<String, List<MenuItemDescription>> items) {
        String group = groupName == null ? "Custom" : groupName;
        List<MenuItemDescription> customItems = items.get(group);
        if (customItems == null) {
            customItems = new ArrayList<MenuItemDescription>();
        }
        MenuItemDescription serverItem = null;
        for (MenuItemDescription subItem : customItems) {
            if (subItem.getName().equals(item.getName())) {
                serverItem = subItem;
                break;
            }
        }
        customItems.remove(serverItem);
        if (customItems.isEmpty()) {
            items.remove(group);
        } else {
            items.put(group, customItems);
        }
    }

    protected void addToMap(String groupName, MenuItemDescription item, Map<String, List<MenuItemDescription>> items) {
        String group = groupName == null ? "Custom" : groupName;
        List<MenuItemDescription> customItems = items.get(group);
        if (customItems == null) {
            customItems = new ArrayList<MenuItemDescription>();
        }
        customItems.add(item);
        items.put(group, customItems);
    }
}
