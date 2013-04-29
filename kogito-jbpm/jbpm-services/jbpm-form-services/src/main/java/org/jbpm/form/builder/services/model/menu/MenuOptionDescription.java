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
package org.jbpm.form.builder.services.model.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.services.model.FormBuilderDTOHelper;
import org.jbpm.form.builder.services.model.Mappable;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;

public class MenuOptionDescription implements Mappable {
    private String html;
    private List<MenuOptionDescription> subMenu;
    private String commandClass;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<MenuOptionDescription> getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(List<MenuOptionDescription> subMenu) {
        this.subMenu = subMenu;
    }

    public String getCommandClass() {
        return commandClass;
    }

    public void setCommandClass(String commandClass) {
        this.commandClass = commandClass;
    }

	@Override
	public Map<String, Object> getDataMap() {
		FormBuilderDTOHelper helper = new FormBuilderDTOHelper();
		helper.setString("html", html);
		helper.setString("commandClass", commandClass);
		if (subMenu != null) {
			List<Object> subItems = new ArrayList<Object>();
			for (MenuOptionDescription option : subMenu) {
				subItems.add(option.getDataMap());
			}
			helper.setList("subMenu", subItems);
		}
		return helper.getMap();
	}

	@Override
	public void setDataMap(Map<String, Object> dataMap) throws FormEncodingException {
		FormBuilderDTOHelper helper = new FormBuilderDTOHelper(dataMap);
		this.html = helper.getString("html");
		this.commandClass = helper.getString("commandClass");
		List<FormBuilderDTOHelper> subItems = helper.getListOfDtoHelpers("subMenu");
		this.subMenu.clear();
		if (subItems != null) {
			for (FormBuilderDTOHelper subHelper : subItems) {
				MenuOptionDescription option = new MenuOptionDescription();
				option.setDataMap(subHelper.getMap());
				this.subMenu.add(option);
			}
		}
	}
}
