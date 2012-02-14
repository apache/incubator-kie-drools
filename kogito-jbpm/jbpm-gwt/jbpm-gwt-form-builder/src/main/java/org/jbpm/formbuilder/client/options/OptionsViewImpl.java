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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formbuilder.client.command.BaseCommand;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Options view. Shows menu bar options
 */
public class OptionsViewImpl extends SimplePanel implements OptionsView {

    private final MenuBar bar = new MenuBar(false);
    private final List<MenuItem> items = new ArrayList<MenuItem>(); 
    
    public OptionsViewImpl() {
        super();
        setSize("1000px", "30px");
        add(bar);
        
        new OptionsPresenter(this);
    }

    public void addItems(List<MainMenuOption> options) {
        toMenuBar(this.bar, options);
    }

    @Override
    public void addItem(MainMenuOption option) {
        toMenuBar(this.bar, option);
    }

    @Override
    public List<MenuItem> getItems() {
        return this.items;
    }
    
    protected MenuBar toMenuBar(MenuBar popup, List<MainMenuOption> menu) {
        for (MainMenuOption option : menu) {
            toMenuBar(popup, option);
        }
        return popup;
    }
    
    protected void toMenuBar(MenuBar popup, MainMenuOption option) {
        String html = option.getHtml();
        BaseCommand cmd = option.getCommand();
        List<MainMenuOption> subMenu = option.getSubMenu();
        MenuItem item = null;
        if (cmd == null && subMenu != null && !subMenu.isEmpty()) {
            item = popup.addItem(new SafeHtmlBuilder().appendHtmlConstant(html).toSafeHtml(), toMenuBar(new MenuBar(true), subMenu));
        } else if (cmd != null && (subMenu == null || subMenu.isEmpty())) {
            item = popup.addItem(new SafeHtmlBuilder().appendHtmlConstant(html).toSafeHtml(), cmd);
            cmd.setItem(item);
        }
        if (item != null) {
            this.items.add(item);
            if (!option.isEnabled()) {
                item.setEnabled(false);
            }
        }
        
    }
}
