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
package org.jbpm.formbuilder.client.options;

import java.util.List;

import org.jbpm.formbuilder.client.command.BaseCommand;

/**
 * Command nesting for a menu option. Used by {@link OptionsViewImpl} to show menu bar items
 */
public class MainMenuOption {

    private String html;
    private BaseCommand command;
    private List<MainMenuOption> subMenu;
    private boolean enabled = true;

    public String getHtml() {
        return html;
    }
    
    public void setHtml(String html) {
        this.html = html;
    }
    
    public BaseCommand getCommand() {
        return command;
    }
    
    public void setCommand(BaseCommand command) {
        this.command = command;
    }
    
    public List<MainMenuOption> getSubMenu() {
        return subMenu;
    }
    
    public void setSubMenu(List<MainMenuOption> subMenu) {
        this.subMenu = subMenu;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
