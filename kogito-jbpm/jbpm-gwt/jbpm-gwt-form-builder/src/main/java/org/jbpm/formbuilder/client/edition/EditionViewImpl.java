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
package org.jbpm.formbuilder.client.edition;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.command.DisposeDropController;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Edition panel
 */
public class EditionViewImpl extends ScrollPanel implements EditionView {

    private SimplePanel panel = new SimplePanel();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Presenter presenter;
    
    public EditionViewImpl() {
        setSize("100%", "100%");
        setAlwaysShowScrollBars(false);
        panel.setSize("100%", "100%");
        add(panel);
        
        PickupDragController dragController = CommonGlobals.getInstance().getDragController();
        dragController.registerDropController(new DisposeDropController(this));
        
        this.presenter = new EditionPresenter(this);
    }
    
    @Override
    public void selectTab() {
        Widget parent = getParent();
        while (!(parent instanceof TabLayoutPanel)) {
            parent = parent.getParent();
        }
        TabLayoutPanel tab = (TabLayoutPanel) parent;
        tab.selectTab(this);
    }
    
    @Override
    public void populate(final FBFormItem itemSelected) {
        final Map<String, Object> map = itemSelected.getFormItemPropertiesMap();
        final Grid grid = new Grid(map.size() + 2, 2);
        grid.setWidget(0, 0, new HTML("<strong>" + i18n.EditionPropertyName() + "</strong>"));
        grid.setWidget(0, 1, new HTML("<strong>" + i18n.EditionPropertyValue() + "</strong>"));
        int index = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            grid.setWidget(index, 0, new Label(entry.getKey()));
            TextBox textBox = new TextBox();
            textBox.setText(entry.getValue() == null ? "" : entry.getValue().toString());
            grid.setWidget(index, 1, textBox);
            index++;
        }
        Button saveButton = new Button(i18n.SaveChangesButton());
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onSaveChanges(map, asPropertiesMap(grid), itemSelected);
            }
        });
        
        Button resetButton = new Button(i18n.ResetChangesButton());
        resetButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onResetChanges(itemSelected.cloneItem(), asPropertiesMap(grid));
            }
        });
        
        grid.setWidget(index, 0, saveButton);
        grid.setWidget(index, 1, resetButton);
        
        panel.clear();
        panel.add(grid);
        setVerticalScrollPosition(0);
    }

    @Override
    public void clear() {
        panel.clear();
    }
    
    private Map<String, Object> asPropertiesMap(Grid grid) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int row = 1; row < grid.getRowCount() - 1; row++) {
            map.put(
                ((Label) grid.getWidget(row, 0)).getText(), 
                ((HasValue<?>) grid.getWidget(row, 1)).getValue()
            );
        }
        return map;
    }
    
}
