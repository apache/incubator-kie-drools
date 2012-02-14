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
package org.jbpm.formbuilder.client.menu;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.FormDataPopulatedEvent;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * form data popup panel. UI to alter form properties (enctype, action, name, etc)
 */
public class FormDataPopupPanel extends PopupPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private final ListBox enctype = new ListBox(false);
    private final ListBox method = new ListBox(false);
    private final TextBox action = new TextBox();
    private final TextBox taskId = new TextBox();
    private final TextBox processId = new TextBox();
    private final TextBox name = new TextBox();
    private final TextArea documentation = new TextArea();
    
    public FormDataPopupPanel() {
        this(false);
    }
    
    public FormDataPopupPanel(boolean showForSavingForm) {
        super(true);
        setStyleName("commandPopupPanel");
        VerticalPanel vPanel = new VerticalPanel();
        vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        Grid grid = new Grid(7, 2);
        
        enctype.addItem("multipart/form-data");
        enctype.addItem("application/x-www-form-urlencoded");
        enctype.setSelectedIndex(0);
        
        action.setValue("complete");
        
        method.addItem("POST");
        method.addItem("GET");
        method.setSelectedIndex(0);

        if (showForSavingForm) {
            method.setEnabled(false);
            enctype.setEnabled(false);
            action.setEnabled(false);
            taskId.setEnabled(false);
            processId.setEnabled(false);
            documentation.setCharacterWidth(30);
            documentation.setVisibleLines(4);
            grid.setWidget(0, 0, new Label(i18n.CheckInComment()));
            grid.setWidget(0, 1, documentation);
        } else {
            grid.setWidget(0, 0, new HTML("&nbsp;"));
            grid.setWidget(0, 1, new HTML("&nbsp;"));
        }
        
        
        grid.setWidget(1, 0, new Label(i18n.FormAction()));
        grid.setWidget(1, 1, action);
        grid.setWidget(2, 0, new Label(i18n.FormMethod()));
        grid.setWidget(2, 1, method);
        grid.setWidget(3, 0, new Label(i18n.FormEnctype()));
        grid.setWidget(3, 1, enctype);
        grid.setWidget(4, 0, new Label(i18n.FormProcessId()));
        grid.setWidget(4, 1, processId);
        grid.setWidget(5, 0, new Label(i18n.FormTaskId()));
        grid.setWidget(5, 1, taskId);
        grid.setWidget(6, 0, new Label(i18n.FormName()));
        grid.setWidget(6, 1, name);
        
        vPanel.add(grid);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(new Button(i18n.ConfirmButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                bus.fireEvent(new FormDataPopulatedEvent(action.getValue(), 
                        method.getValue(method.getSelectedIndex()), taskId.getValue(),
                        processId.getValue(), enctype.getValue(enctype.getSelectedIndex()), 
                        name.getValue()));
                hide();
            }
        }));
        buttonPanel.add(new Button(i18n.CancelButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        }));
        vPanel.add(buttonPanel);
        vPanel.setStyleName("commandContent");
        setWidget(vPanel);
    }
    
    public void setTaskId(String taskId) {
        this.taskId.setValue(taskId);
    }
    
    public void setProcessId(String processId) {
        this.processId.setValue(processId);
    }

    public void setEnctype(String enctype) {
        for (int index = 0; index < this.enctype.getItemCount(); index++) {
            if (this.enctype.getValue(index).equals(enctype)) {
                this.enctype.setSelectedIndex(index);
                break;
            }
            
        }
    }

    public void setMethod(String method) {
        for (int index = 0; index < this.method.getItemCount(); index++) {
            if (this.method.getValue(index).equals(method)) {
                this.method.setSelectedIndex(index);
                break;
            }
        }
    }

    public void setAction(String action) {
        this.action.setValue(action);
    }

    public void setName(String name) {
        this.name.setValue(name);
    }
    
    public String getFormName() {
        return name.getValue();
    }
    
    public String getAction() {
        return action.getValue();
    }
    
    public String getTaskId() {
        return taskId.getValue();
    }
    
    public String getProcessId() {
        return processId.getValue();
    }
    
    public String getMethod() {
        return method.getValue(method.getSelectedIndex());
    }
    
    public String getEnctype() {
        return enctype.getValue(enctype.getSelectedIndex());
    }
    
    public String getDocumentation() {
        return documentation.getValue();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        int left = getPopupLeft();
        int top = getPopupTop();
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        
        boolean changed = false;
        
        if (left + width > Window.getClientWidth()) {
            left -= width;
            changed = true;
        }
        if (top + height > Window.getClientHeight()) {
            top -= height;
            changed = true;
        }
        if (changed) {
            setPopupPosition(left, top);
        }
    }
    
}
