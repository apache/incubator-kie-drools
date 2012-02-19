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
package org.jbpm.formbuilder.client.effect;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.RoleUtils;
import org.jbpm.formbuilder.client.bus.MenuItemAddedEvent;
import org.jbpm.formbuilder.client.bus.MenuItemRemoveEvent;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.menu.items.CustomMenuItem;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtent.reflection.client.Reflectable;

/**
 * Allows to save a menu item from right button menu
 */
@Reflectable
public class SaveAsMenuOptionFormEffect extends FBFormEffect {

    private String newMenuOptionName;
    private String groupName;
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public SaveAsMenuOptionFormEffect() {
        super("Save as menu option", true);
    }

    public void setNewMenuOptionName(String newMenuOptionName) {
        this.newMenuOptionName = newMenuOptionName;
    }
    
    public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
    
    @Override
    protected void createStyles() {
        final FBFormItem formItem = super.getItem();
        FBMenuItem menuItem = new CustomMenuItem(
                formItem.getRepresentation(), 
                newMenuOptionName, 
                formItem.getFormEffects(), 
                groupName
        );
        if (formItem.getEventActions() != null) {
            for (String evtName : formItem.getEventActions().keySet()) {
                menuItem.addAllowedEvent(evtName);
            }
        }
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("menuItem", menuItem);
        dataSnapshot.put("groupName", groupName);
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {  }
            @Override
            public void undoAction(UndoableEvent event) {
                FBMenuItem menuItem = (FBMenuItem) event.getData("menuItem");
                String groupName = (String) event.getData("groupName");
                MenuItemRemoveEvent mevent = new MenuItemRemoveEvent(menuItem, groupName);
                CommonGlobals.getInstance().getEventBus().fireEvent(mevent);
            }
            @Override
            public void doAction(UndoableEvent event) {
                FBMenuItem menuItem = (FBMenuItem) event.getData("menuItem");
                String groupName = (String) event.getData("groupName");
                MenuItemAddedEvent mevent = new MenuItemAddedEvent(menuItem, groupName);
                CommonGlobals.getInstance().getEventBus().fireEvent(mevent);
            }
        }));
    }

    @Override
    public PopupPanel createPanel() {
        final PopupPanel panel = new PopupPanel(true);
        panel.setSize("250px", "90px");
        VerticalPanel vPanel = new VerticalPanel();
        Grid grid = new Grid(2, 2);
        final TextBox optionNameBox = new TextBox();
        final TextBox groupNameBox = new TextBox();
        grid.setWidget(0, 0, new Label(i18n.MenuOptionName()));
        grid.setWidget(0, 1, optionNameBox);
        grid.setWidget(1, 0, new Label(i18n.MenuOptionGroup()));
        grid.setWidget(1, 1, groupNameBox);
        
        Button applyButton = new Button(i18n.ConfirmButton());
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setNewMenuOptionName(optionNameBox.getValue());
                setGroupName(groupNameBox.getValue());
                createStyles();
                panel.hide();
            }
        });
        vPanel.add(grid);
        vPanel.add(applyButton);
        panel.add(vPanel);
        return panel;
    }

    @Override
    public boolean isValidForItem(FBFormItem item) {
    	return super.isValidForItem(item) && RoleUtils.getInstance().hasDesignPrivileges();
    }
}
