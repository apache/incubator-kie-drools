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
package org.jbpm.formapi.shared.menu;

import java.util.List;

public class MenuOptionDescription {
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
}
