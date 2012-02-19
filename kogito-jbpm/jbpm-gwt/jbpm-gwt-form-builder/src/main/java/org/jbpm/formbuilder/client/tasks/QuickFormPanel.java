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
package org.jbpm.formbuilder.client.tasks;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.bus.ui.UpdateFormViewEvent;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QuickFormPanel extends PopupPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    
    private final Button okButton = new Button(i18n.ConfirmButton());
    
    private final List<TaskPropertyRef> selectedInputs = new ArrayList<TaskPropertyRef>();
    private final List<TaskPropertyRef> selectedOutputs = new ArrayList<TaskPropertyRef>();
    
    public QuickFormPanel(final TaskRow row) {
        VerticalPanel vPanel = new VerticalPanel();
        List<TaskPropertyRef> inputs = row.getIoRef().getInputs();
        vPanel.add(new Label(i18n.QuickFormInputsToBeAdded()));
        vPanel.add(toGrid(inputs, selectedInputs));
        List<TaskPropertyRef> outputs = row.getIoRef().getOutputs();
        vPanel.add(new Label(i18n.QuickFormOutputsToBeAdded()));
        vPanel.add(toGrid(outputs, selectedOutputs));
        Label warning = new Label(i18n.QuickFormWarning());
        vPanel.add(warning);
        HorizontalPanel buttons = new HorizontalPanel();
        Button cancelButton = new Button(i18n.CancelButton());
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TaskRef trimmedIo = new TaskRef();
                trimmedIo.setPackageName(row.getIoRef().getPackageName());
                trimmedIo.setProcessId(row.getIoRef().getProcessId());
                trimmedIo.setProcessName(row.getIoRef().getProcessName());
                trimmedIo.setTaskId(row.getIoRef().getTaskId());
                trimmedIo.setInputs(new ArrayList<TaskPropertyRef>(getSelectedInputs()));
                trimmedIo.setOutputs(new ArrayList<TaskPropertyRef>(getSelectedOutputs()));
                FormRepresentation form = server.toBasicForm(trimmedIo);
                bus.fireEvent(new UpdateFormViewEvent(form));
                hide();
            }
        });
        buttons.add(okButton);
        buttons.add(cancelButton);
        vPanel.add(buttons);
        setSize("300px", "300px");
        setWidget(vPanel);
    }
    
    private Grid toGrid(List<TaskPropertyRef> ioList, final List<TaskPropertyRef> selectedIos) {
        Grid grid = new Grid(ioList == null ? 1 : ioList.size(), 2);
        if (ioList != null) {
            for (int index = 0; index < ioList.size(); index++) {
                final TaskPropertyRef io = ioList.get(index);
                CheckBox checkBox = new CheckBox();
                checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        Boolean val = event.getValue();
                        if (val == null || val == false) {
                            if (selectedIos.contains(io)) { 
                                selectedIos.remove(io);
                            }
                        } else {
                            if (!selectedIos.contains(io)) {
                                selectedIos.add(io);
                            }
                        }
                    }
                });
                checkBox.setValue(Boolean.TRUE);
                selectedIos.add(io);
                grid.setWidget(index, 0, checkBox);
                grid.setWidget(index, 1, new Label(io.getName()));
            }
        }
        return grid;
    }
    
    public HandlerRegistration addOkHandler(ClickHandler handler) {
        return okButton.addClickHandler(handler);
    }
    
    public List<TaskPropertyRef> getSelectedInputs() {
        return selectedInputs;
    }
    
    public List<TaskPropertyRef> getSelectedOutputs() {
        return selectedOutputs;
    }
}
