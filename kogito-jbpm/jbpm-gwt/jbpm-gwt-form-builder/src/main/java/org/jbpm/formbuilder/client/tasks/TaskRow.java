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
import java.util.Map;

import org.jbpm.formapi.common.handler.EventHelper;
import org.jbpm.formapi.common.handler.RightClickHandler;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TaskRow extends FocusPanel {
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final List<HandlerRegistration> rclickRegs = new ArrayList<HandlerRegistration>();

    private final TaskRef ioRef;
    private final VerticalPanel panel = new VerticalPanel();
    
    private final HandlerRegistration focus;
    private final HandlerRegistration blur;
    
    private final VerticalPanel inputs = new VerticalPanel();
    private final VerticalPanel outputs = new VerticalPanel();
    private final VerticalPanel metaData = new VerticalPanel();
    
    public TaskRow(TaskRef ioRef, boolean even) {
        this.ioRef = ioRef;
        addStyleName(even ? "even" : "odd");
        panel.add(new HTML("<strong>" + i18n.FormProcessId() + "</strong> " + ioRef.getProcessId()));
        panel.add(new HTML("<strong>" + i18n.FormTaskId() + "</strong> " + ioRef.getTaskId()));
        this.focus = addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                showInputs();
                showOutputs();
                showMetaData();
            }
        });
        this.blur = addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                hideInputs();
                hideOutputs();
                hideMetaData();
            }
        });
        add(panel);
    }
    
    public HandlerRegistration getFocus() {
        return focus;
    }
    
    public HandlerRegistration getBlur() {
        return blur;
    }
    
    protected VerticalPanel getPanel() {
        return panel;
    }
    
    public TaskRef getIoRef() {
        return ioRef;
    }
    
    @Override
    public void onBrowserEvent(Event event) {
        EventHelper.onBrowserEvent(this, event);
    }
    
    public HandlerRegistration addRightClickHandler(final RightClickHandler handler) {
        HandlerRegistration reg = EventHelper.addRightClickHandler(this, handler);
        rclickRegs.add(reg);
        return reg;
    }
    
    public void clearRightClickHandlers() {
        for (HandlerRegistration reg : rclickRegs) {
            reg.removeHandler();
        }
        rclickRegs.clear();
    }
    
    protected void showInputs() {
        List<TaskPropertyRef> inputs = this.ioRef.getInputs();
        this.inputs.clear();
        this.inputs.add(new HTML("<strong>" + i18n.InputsLabel() + "</strong>"));
        if (inputs == null || inputs.isEmpty()) {
            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
            hPanel.add(new Label(i18n.NoInputsLabel()));
            this.inputs.add(hPanel);
        } else {
            for (TaskPropertyRef input : inputs) {
                HorizontalPanel hPanel = new HorizontalPanel();
                hPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
                hPanel.add(new Label(input.getName()));
                this.inputs.add(hPanel);
            }
        }
        panel.add(this.inputs);
    }
    
    protected void showOutputs() {
        List<TaskPropertyRef> outputs = this.ioRef.getOutputs();
        this.outputs.clear();
        this.outputs.add(new HTML("<strong>" + i18n.OutputsLabel() + "</strong>"));
        if (outputs == null || outputs.isEmpty()) {
            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
            hPanel.add(new Label(i18n.NoOutputsLabel()));
            this.outputs.add(hPanel);
        } else {
            for (TaskPropertyRef output : outputs) {
                HorizontalPanel hPanel = new HorizontalPanel();
                hPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
                hPanel.add(new Label(output.getName()));
                this.outputs.add(hPanel);
            }
        }
        panel.add(this.outputs);
    }
    
    protected void showMetaData() {
        Map<String, String> metaData = this.ioRef.getMetaData();
        this.metaData.clear();
        this.metaData.add(new HTML("<strong>" + i18n.MetaDataLabel() + "</strong>"));
        if (metaData == null || metaData.isEmpty()) {
            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
            hPanel.add(new Label(i18n.NoMetaDataLabel()));
            this.metaData.add(hPanel);
        } else {
            for (Map.Entry<String, String> entry : metaData.entrySet()) {
                HorizontalPanel hPanel = new HorizontalPanel();
                hPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
                hPanel.add(new Label(entry.getKey() + "=" + entry.getValue()));
                this.metaData.add(hPanel);
            }
        }
        panel.add(this.metaData);
    }
    
    protected void hideInputs() {
        panel.remove(this.inputs);
    }
    
    protected void hideOutputs() {
        panel.remove(this.outputs);
    }
    
    protected void hideMetaData() {
        panel.remove(this.metaData);
    }
}
