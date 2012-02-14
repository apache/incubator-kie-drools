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
package org.jbpm.formbuilder.client.form;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.form.FBCompositeItem;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.PhantomPanel;
import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formapi.common.handler.ControlKeyHandler;
import org.jbpm.formapi.common.handler.EventHelper;
import org.jbpm.formapi.common.handler.RightClickEvent;
import org.jbpm.formapi.common.handler.RightClickHandler;
import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formapi.shared.api.FBValidation;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formapi.shared.api.OutputData;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.ui.FormItemAddedEvent;
import org.jbpm.formbuilder.client.bus.ui.FormItemRemovedEvent;
import org.jbpm.formbuilder.client.menu.FormDataPopupPanel;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Display class for a {@link FormRepresentation}
 */
public class FBForm extends FlowPanel implements FBCompositeItem {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private String name;
    private String taskId;
    private String processId;
    private String method;
    private String enctype;
    private String action;
    private Map<String, InputData> inputs;
    private Map<String, OutputData> outputs;
    private List<FBScript> onLoadScripts = new ArrayList<FBScript>();
    private List<FBScript> onSubmitScripts = new ArrayList<FBScript>();
    
    private List<FBFormItem> formItems = new ArrayList<FBFormItem>();
    private List<FBValidationItem> validationItems = new ArrayList<FBValidationItem>();
    
    private final FormDataPopupPanel popup = new FormDataPopupPanel();
    
    private boolean saved = false;
    private long lastModified = 0L;
    
    public FBForm() {
        super();
        EventHelper.addRightClickHandler(this, new RightClickHandler() {
            @Override
            public void onRightClick(RightClickEvent event) {
                popup.setPopupPosition(event.getX(), event.getY());
                popup.show();
            }
        });
        EventHelper.addKeyboardPasteHandler(this, new ControlKeyHandler() {
            @Override
            public void onKeyboardControl() {
                CommonGlobals.getInstance().paste().append(null).execute();
            }
        });
    }
    
    @Override
    public void onBrowserEvent(Event event) {
        EventHelper.onBrowserEvent(this, event);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.popup.setName(name);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
        this.popup.setTaskId(taskId);
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
        this.popup.setProcessId(processId);
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
        this.popup.setAction(action);
    }
    
    public String getEnctype() {
        return enctype;
    }
    
    public void setEnctype(String enctype) {
        this.enctype = enctype;
        this.popup.setEnctype(enctype);
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
        this.popup.setMethod(method);
    }
    
    @Override
    public List<FBFormItem> getItems() {
        return formItems;
    }

    @Override
    public void setItems(List<FBFormItem> items) {
        this.formItems = items;
    }

    public List<FBValidationItem> getValidationItems() {
        return validationItems;
    }

    public void setValidationItems(List<FBValidationItem> validationItems) {
        this.validationItems = validationItems;
    }
    
    @Override
    public boolean remove(Widget w) {
        if (w instanceof FBFormItem) {
            FBFormItem item = (FBFormItem) w;
            this.formItems.remove(item);
        }
        return super.remove(w);
    }
    
    @Override
    public void add(Widget w) {
        if (w instanceof FBFormItem) {
            FBFormItem formItem = (FBFormItem) w;
            int index = getItemPosition(formItem);
            if (index == getWidgetCount()) {
                this.formItems.add(formItem);
                super.add(w);
            } else {
                insert(w, index);
            }
        } else {
            super.add(w);
        }
    }
    
    @Override
    public void add(PhantomPanel phantom, int x, int y) {
        for (int index = 0; index < getWidgetCount(); index++) {
            Widget item = getWidget(index);
            int left = item.getAbsoluteLeft();
            int right = left + item.getOffsetWidth();
            int top = item.getAbsoluteTop();
            int bottom = top + item.getOffsetHeight();
            if (x > left && x < right && y > top && y < bottom) {
                insert(phantom, index);
                break;
            } else if (x > right && y > top && y < bottom && index < (getWidgetCount() - 1)) {
                insert(phantom, index + 1);
                break;
            } else if (index == (getWidgetCount() - 1)) {
                add(phantom);
                break;
            }
        }
    }
    
    @Override
    public void insert(Widget widget, int beforeIndex) {
        if (widget instanceof FBFormItem) {
            List<FBFormItem> posteriorItems = new ArrayList<FBFormItem>(
                    this.formItems.subList(beforeIndex, this.formItems.size()));
            this.formItems.removeAll(posteriorItems);
            this.formItems.add((FBFormItem) widget);
            this.formItems.addAll(posteriorItems);
        }
        super.insert(widget, beforeIndex);
    }
    
    protected int getItemPosition(FBFormItem newItem) {
        int index = getWidgetCount();
        if (index == 0) {
            return index;
        }
        if (this.formItems.size() == 0) {
            return 0;
        }
        ListIterator<FBFormItem> it = this.formItems.listIterator(this.formItems.size() - 1);
        while (it.hasPrevious() && index > 0) {
            FBFormItem item = it.previous();
            boolean leftOfItem = item.getAbsoluteLeft() > newItem.getDesiredX() && newItem.getDesiredX() > 0;
            boolean aboveItem = item.getAbsoluteTop() > newItem.getDesiredY() && newItem.getDesiredY() > 0;
            if (aboveItem || leftOfItem) {
                index--;
            }
        }
        return index;
    }
    
    public void addValidation(FBValidationItem item) {
        this.validationItems.add(item);
    }

    public void onFormLoad() {
        
    }
    
    public void onFormSubmit() {
        
    }
    
    public void setInputs(Map<String, InputData> inputs) {
        this.inputs = inputs;
    }
    
    public Map<String, InputData> getInputs() {
        return inputs;
    }
    
    public void setOutputs(Map<String, OutputData> outputs) {
        this.outputs = outputs;
    }
    
    public Map<String, OutputData> getOutputs() {
        return outputs;
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
        this.lastModified = System.currentTimeMillis();
    }
    
    public FormRepresentation createRepresentation() {
        FormRepresentation rep = new FormRepresentation();
        rep.setName(name);
        rep.setTaskId(taskId);
        rep.setProcessName(processId);
        rep.setAction(action);
        rep.setMethod(method);
        rep.setEnctype(enctype);
        for (FBFormItem item : formItems) {
            rep.addFormItem(item.getRepresentation());
        }
        for (FBValidationItem item : validationItems) {
            rep.addFormValidation(item.createValidation());
        }
        rep.setInputs(inputs);
        rep.setOutputs(outputs);
        rep.setSaved(saved);
        rep.setLastModified(lastModified);
        rep.setOnLoadScripts(this.onLoadScripts);
        rep.setOnSubmitScripts(this.onSubmitScripts);
        return rep;
    }

    public void populate(FormRepresentation rep) throws FormBuilderException {
        setName(rep.getName());
        setTaskId(rep.getTaskId());
        setProcessId(rep.getProcessName());
        setAction(rep.getAction());
        setMethod(rep.getMethod());
        setEnctype(rep.getEnctype());
        for (FBFormItem item : new ArrayList<FBFormItem>(formItems)) {
            remove(item);
            bus.fireEvent(new FormItemRemovedEvent(item));
        }
        for (FormItemRepresentation itemRep : rep.getFormItems()) {
            FBFormItem item = FBFormItem.createItem(itemRep);
            item.populate(itemRep);
            add(item);
            ensureMinimumSize(item);
            bus.fireEvent(new FormItemAddedEvent(item, this));
        }
        for (FBValidation validationRep : rep.getFormValidations()) {
            FBValidationItem validation = FBValidationItem.createValidation(validationRep);
            addValidation(validation);
        }
        setInputs(rep.getInputs());
        setOutputs(rep.getOutputs());
        this.saved = rep.isSaved();
        this.lastModified = rep.getLastModified();
        this.onLoadScripts.clear();
        if (rep.getOnLoadScripts() != null) {
            for (FBScript onLoad : rep.getOnLoadScripts()) {
                this.onLoadScripts.add(onLoad);
            }
        }
        this.onSubmitScripts.clear();
        if (rep.getOnSubmitScripts() != null) {
            for (FBScript onSubmit : rep.getOnSubmitScripts()) {
                this.onSubmitScripts.add(onSubmit);
            }
        }
    }
    
    protected void ensureMinimumSize(FBFormItem item) {
        if (item.getHeight() != null && !"".equals(item.getHeight()) && item.getHeight().endsWith("px")) {
            int actualHeight = item.getWidget().getOffsetHeight();
            int settedHeight = Integer.valueOf(item.getHeight().replace("px", ""));
            if (actualHeight > 0 && settedHeight < actualHeight) {
                item.setHeight("" + actualHeight + "px");
            }
        }
        if (item.getWidth() != null && !"".equals(item.getWidth()) && item.getWidth().endsWith("px")) {
            int actualWidth = item.getWidget().getOffsetWidth();
            int settedWidth = Integer.valueOf(item.getWidth().replace("px", ""));
            if (actualWidth > 0 && settedWidth < actualWidth) {
                item.setWidth("" + actualWidth + "px");
            }
        }
    }
    
    @Override
    public void replacePhantom(FBFormItem item) {
        PhantomPanel phantom = null;
        for (Widget widget : this) {
            if (widget instanceof PhantomPanel) {
                phantom = (PhantomPanel) widget;
                break;
            }
        }
        if (phantom != null) {
            int index = getWidgetIndex(phantom);
            remove(phantom);
            insert(item, index);
        } else {
            add(item);
        }
    }

    public FormPanel asFormPanel(Map<String, Object> data) {
        FormPanel panel = new FormPanel();
        data.put(FormBuilderGlobals.FORM_PANEL_KEY, panel);
        panel.setAction(this.action);
        panel.setEncoding(this.enctype);
        panel.setMethod(this.method);
        FlowPanel flow = new FlowPanel();
        FormElement el = FormElement.as(panel.getElement());
        el.setName(this.name);
        flow.add(new HTML("<!-- process name: " + getProcessId() + ", task name: " + getTaskId() + " -->"));
        for (FBFormItem item : getItems()) {
            flow.add(item.cloneDisplay(data));
        }
        panel.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                for (FBValidationItem item : getValidationItems()) {
                    if (!item.createValidation().isValid(null)) {
                        Window.alert("Validation " + item.getName() + " failed");
                        event.cancel();
                    }
                }
            }
        });
        panel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(SubmitCompleteEvent event) {
                RootPanel.get().getElement().setInnerHTML(event.getResults());
            }
        });
        panel.setWidget(flow);
        return panel;
    }
}
