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
package org.jbpm.formbuilder.client.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formapi.shared.api.OutputData;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.FormDataPopulatedEvent;
import org.jbpm.formbuilder.client.bus.FormDataPopulatedHandler;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationHandler;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseEvent;
import org.jbpm.formbuilder.client.bus.RegisterLayoutEvent;
import org.jbpm.formbuilder.client.bus.RegisterLayoutHandler;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.bus.ui.FormSavedEvent;
import org.jbpm.formbuilder.client.bus.ui.FormSavedHandler;
import org.jbpm.formbuilder.client.bus.ui.GetFormDisplayEvent;
import org.jbpm.formbuilder.client.bus.ui.GetFormDisplayHandler;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedHandler;
import org.jbpm.formbuilder.client.bus.ui.UpdateFormViewEvent;
import org.jbpm.formbuilder.client.bus.ui.UpdateFormViewHandler;
import org.jbpm.formbuilder.client.form.FBForm;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.shared.EventBus;

/**
 * layout presenter.
 * 
 * Registers the dropController to work on it when
 * started and when layout form items are added, to 
 * work on said layout form items.
 * 
 * Exposes the form representation and display, and
 * populates both when they are loaded from the server,
 * changed by another view or saved.
 */
public class LayoutPresenter {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final LayoutView layoutView;
    
    public LayoutPresenter(LayoutView view) {
    	final PickupDragController dragController = CommonGlobals.getInstance().getDragController();
        this.layoutView = view;
        this.layoutView.startDropController(dragController, layoutView);
        
        this.bus.addHandler(RegisterLayoutEvent.TYPE, new RegisterLayoutHandler() {
            @Override
            public void onEvent(RegisterLayoutEvent event) {
                LayoutFormItem item = event.getLayout();
                layoutView.startDropController(dragController, item);
            }
        });
        
        this.bus.addHandler(GetFormRepresentationEvent.TYPE, new GetFormRepresentationHandler() {
            @Override
            public void onEvent(GetFormRepresentationEvent event) {
                FBForm formDisplay = layoutView.getFormDisplay();
                FormRepresentation rep = formDisplay.createRepresentation();
                bus.fireEvent(new GetFormRepresentationResponseEvent(rep, event.getSaveType()));
            }
        });
        
        this.bus.addHandler(GetFormDisplayEvent.TYPE, new GetFormDisplayHandler() {
            @Override
            public void onEvent(GetFormDisplayEvent event) {
                event.setFormDisplay(layoutView.getFormDisplay());
            }
        });
        
        this.bus.addHandler(FormDataPopulatedEvent.TYPE, new FormDataPopulatedHandler() {
            @Override
            public void onEvent(FormDataPopulatedEvent event) {
                Map<String, Object> dataSnapshot = new HashMap<String, Object>();
                dataSnapshot.put("oldName", layoutView.getFormDisplay().getName());
                dataSnapshot.put("oldAction", layoutView.getFormDisplay().getAction());
                dataSnapshot.put("oldProcessId", layoutView.getFormDisplay().getProcessId());
                dataSnapshot.put("oldTaskId", layoutView.getFormDisplay().getTaskId());
                dataSnapshot.put("oldMethod", layoutView.getFormDisplay().getMethod());
                dataSnapshot.put("oldEnctype", layoutView.getFormDisplay().getEnctype());
                dataSnapshot.put("newName", event.getName());
                dataSnapshot.put("newAction", event.getAction());
                dataSnapshot.put("newProcessId", event.getProcessId());
                dataSnapshot.put("newTaskId", event.getTaskId());
                dataSnapshot.put("newMethod", event.getMethod());
                dataSnapshot.put("newEnctype", event.getEnctype());
                bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
                    @Override
                    public void onEvent(UndoableEvent event) {  }
                    @Override
                    public void undoAction(UndoableEvent event) {
                        String name = (String) event.getData("oldName");
                        String action = (String) event.getData("oldAction");
                        String taskId = (String) event.getData("oldTaskId");
                        String processId = (String) event.getData("oldProcessId");
                        String method = (String) event.getData("oldMethod");
                        String enctype = (String) event.getData("oldEnctype");
                        populateFormData(action, processId, taskId, name, method, enctype);
                    }
                    @Override
                    public void doAction(UndoableEvent event) {
                        String name = (String) event.getData("newName");
                        String action = (String) event.getData("newAction");
                        String taskId = (String) event.getData("newTaskId");
                        String processId = (String) event.getData("newProcessId");
                        String method = (String) event.getData("newMethod");
                        String enctype = (String) event.getData("newEnctype");
                        populateFormData(action, processId, taskId, name, method, enctype);
                    }
                }));
            }
        });
        this.bus.addHandler(TaskSelectedEvent.TYPE, new TaskSelectedHandler() {
            @Override
            public void onSelectedTask(TaskSelectedEvent event) {
                Map<String, Object> dataSnapshot = new HashMap<String, Object>();
                dataSnapshot.put("oldTaskID", layoutView.getFormDisplay().getTaskId());
                dataSnapshot.put("oldProcessID", layoutView.getFormDisplay().getProcessId());
                dataSnapshot.put("oldTaskInputs", layoutView.getFormDisplay().getInputs());
                dataSnapshot.put("oldTaskOutputs", layoutView.getFormDisplay().getOutputs());
                if (event.getSelectedTask() != null) {
                    dataSnapshot.put("newTaskID", event.getSelectedTask().getTaskId());
                    Map<String, InputData> inputs = new HashMap<String, InputData>();
                    Map<String, OutputData> outputs = new HashMap<String, OutputData>();
                    if (event.getSelectedTask().getInputs() != null) {
                        for (TaskPropertyRef input : event.getSelectedTask().getInputs()) {
                            InputData in = new InputData();
                            in.setName(input.getName());
                            in.setValue(input.getSourceExpresion());
                            inputs.put(input.getName(), in);
                        }
                    }
                    if (event.getSelectedTask().getOutputs() != null) {
                        for (TaskPropertyRef output : event.getSelectedTask().getOutputs()) {
                            OutputData out = new OutputData();
                            out.setName(output.getName());
                            out.setValue(output.getSourceExpresion());
                            outputs.put(output.getName(), out);
                        }
                    }
                    dataSnapshot.put("newTaskInputs", inputs);
                    dataSnapshot.put("newTaskOutputs", outputs);
                }
                dataSnapshot.put("newTaskID", event.getSelectedTask() == null ? null : event.getSelectedTask().getTaskId());
                dataSnapshot.put("newProcessID", event.getSelectedTask() == null ? null : event.getSelectedTask().getProcessId());
                dataSnapshot.put("newTaskInputs", event.getSelectedTask() == null ? null : event.getSelectedTask().getInputs());
                dataSnapshot.put("newTaskOutputs", event.getSelectedTask() == null ? null : event.getSelectedTask().getOutputs());
                bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
                    @Override
                    public void onEvent(UndoableEvent event) { }
                    @Override
                    @SuppressWarnings("unchecked")
                    public void doAction(UndoableEvent event) {
                        String value = (String) event.getData("newTaskID");
                        String procId = (String) event.getData("newProcessID");
                        List<TaskPropertyRef> inputs = (List<TaskPropertyRef>) event.getData("newTaskInputs");
                        List<TaskPropertyRef> outputs = (List<TaskPropertyRef>) event.getData("newTaskOutputs");
                        layoutView.getFormDisplay().setTaskId(value);
                        layoutView.getFormDisplay().setProcessId(procId);
                        layoutView.getFormDisplay().setInputs(toInputs(inputs));
                        layoutView.getFormDisplay().setOutputs(toOutputs(outputs));
                    }
                    @Override
                    @SuppressWarnings("unchecked")
                    public void undoAction(UndoableEvent event) {
                        String value = (String) event.getData("oldTaskID");
                        String procId = (String) event.getData("oldProcessID");
                        List<TaskPropertyRef> inputs = (List<TaskPropertyRef>) event.getData("oldTaskInputs");
                        List<TaskPropertyRef> outputs = (List<TaskPropertyRef>) event.getData("oldTaskOutputs");
                        layoutView.getFormDisplay().setTaskId(value);
                        layoutView.getFormDisplay().setProcessId(procId);
                        layoutView.getFormDisplay().setInputs(toInputs(inputs));
                        layoutView.getFormDisplay().setOutputs(toOutputs(outputs));
                    }
                }));
            }
        });
        
        bus.addHandler(FormSavedEvent.TYPE, new FormSavedHandler() {
            @Override
            public void onEvent(FormSavedEvent event) {
                layoutView.getFormDisplay().setSaved(true);
            }
        });
        
        bus.addHandler(UpdateFormViewEvent.TYPE, new UpdateFormViewHandler() {
            @Override
            public void onEvent(UpdateFormViewEvent event) {
                Map<String, Object> dataSnapshot = new HashMap<String, Object>();
                dataSnapshot.put("newForm", event.getFormRepresentation());
                dataSnapshot.put("oldForm", layoutView.getFormDisplay().createRepresentation());
                bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
                    @Override
                    public void undoAction(UndoableEvent event) {
                        FormRepresentation oldForm = (FormRepresentation) event.getData("oldForm");
                        try {
                            layoutView.getFormDisplay().populate(oldForm);
                        } catch (FormBuilderException e) {
                            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntPopulateWithForm(), e));
                        }
                    }
                    @Override
                    public void onEvent(UndoableEvent event) { }
                    @Override
                    public void doAction(UndoableEvent event) {
                        FormRepresentation newForm = (FormRepresentation) event.getData("newForm");
                        try {
                            layoutView.getFormDisplay().populate(newForm);
                        } catch (FormBuilderException e) {
                            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntPopulateWithForm(), e));
                        }
                    }
                }));
            }
        });
    }

    private Map<String, InputData> toInputs(List<TaskPropertyRef> inputs) {
        Map<String, InputData> retval = new HashMap<String, InputData>();
        if (inputs != null) {
            for (TaskPropertyRef ref : inputs) {
                InputData input = new InputData();
                input.setName(ref.getName());
                input.setValue(ref.getSourceExpresion());
                retval.put(ref.getName(), input);
            }
        }
        return retval;
    }
    
    private Map<String, OutputData> toOutputs(List<TaskPropertyRef> outputs) {
        Map<String, OutputData> retval = new HashMap<String, OutputData>();
        if (outputs != null) {
            for (TaskPropertyRef ref : outputs) {
                OutputData output = new OutputData();
                output.setName(ref.getName());
                output.setValue(ref.getSourceExpresion());
                retval.put(ref.getName(), output);
            }
        }
        return retval;
    }
    
    private void populateFormData(String action, String processId, 
            String taskId, String name, String method, String enctype) {
        
        if (action != null && !"".equals(action)) {
            layoutView.getFormDisplay().setAction(action);
        }
        if (taskId != null && !"".equals(taskId)) {
            layoutView.getFormDisplay().setTaskId(taskId);
        }
        if (processId != null && !"".equals(processId)) {
            layoutView.getFormDisplay().setProcessId(processId);
        }
        if (name != null && !"".equals(name)) {
            layoutView.getFormDisplay().setName(name);
        }
        layoutView.getFormDisplay().setMethod(method);
        layoutView.getFormDisplay().setEnctype(enctype);
    }
}
