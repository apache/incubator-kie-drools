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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.bus.ExistingTasksResponseEvent;
import org.jbpm.formbuilder.client.bus.ExistingTasksResponseHandler;
import org.jbpm.formbuilder.client.bus.ui.TaskNameFilterEvent;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class AdvancedSearchView extends Grid {
    
    private static final String FILE_TYPE = "file";
    private static final String BPMN2_TYPE = "bpmn2";
    
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private final Map<String, List<TaskRef>> processes = new HashMap<String, List<TaskRef>>();
    
    private final TextBox queryName = new TextBox();
    private final ListBox queryType = new ListBox();
    private final ListBox querySubType = new ListBox();
    private final Button searchButton = new Button(i18n.SearchButton());
    
    public AdvancedSearchView() {
        super(3, 2);
        queryType.addItem("");
        queryType.addItem(i18n.BPMN2IOReferences(), BPMN2_TYPE);
        queryType.addItem(i18n.FileIOReferences(), FILE_TYPE);
        setWidget(0, 0, new Label(i18n.TypeLabel()));
        setWidget(0, 1, queryType);
        setWidget(1, 0, new Label(i18n.QueryLabel()));
        setWidget(1, 1, queryName);
        setWidget(2, 0, new HTML("&nbsp;"));
        queryType.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String value = queryType.getValue(queryType.getSelectedIndex());
                fireTypeSelection(value);
            }
        });
        searchButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String query = queryName.getValue();
                if (queryType.getSelectedIndex() > 0) {
                    query += " iotype:" + queryType.getValue(queryType.getSelectedIndex());
                }
                bus.fireEvent(new TaskNameFilterEvent(query));
            }
        });
        querySubType.setWidth("150px");
        querySubType.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int index = querySubType.getSelectedIndex();
                if (index > 0) {
                    String value = querySubType.getValue(index);
                    bus.fireEventFromSource(
                            new ExistingTasksResponseEvent(processes.get(value), value), 
                            AdvancedSearchView.this
                    );
                }
            }
        });
        FormBuilderService server = FormBuilderGlobals.getInstance().getService();
        bus.addHandlerToSource(ExistingTasksResponseEvent.TYPE, server, new ExistingTasksResponseHandler() {
            @Override
            public void onEvent(ExistingTasksResponseEvent event) {
                List<TaskRef> tasks = event.getTasks();
                processes.clear();
                if (tasks != null) {
                    for (TaskRef task : tasks) {
                        String processId = task.getProcessId();
                        List<TaskRef> processTasks = processes.get(processId);
                        if (processTasks == null) {
                            processTasks = new ArrayList<TaskRef>();
                        }
                        processTasks.add(task);
                        processes.put(processId, processTasks);
                    }
                    for (Map.Entry<String, List<TaskRef>> entry : processes.entrySet()) {
                        querySubType.addItem(entry.getKey(), entry.getKey());
                    }
                }
            }
        });
        setWidget(2, 1, searchButton);
    }
    
    public void fireTypeSelection(String value) {
        bus.fireEvent(new TaskNameFilterEvent("iotype:"+value));
        if (value == null || "".equals(value)) {
            setWidget(1, 0, new Label(i18n.QueryLabel()));
            setWidget(1, 1, queryName);
        } else if (BPMN2_TYPE.equals(value)) {
            setWidget(1, 0, new Label(i18n.ProcessesLabel()));
            querySubType.clear();
            querySubType.addItem("...");
            setWidget(1, 1, querySubType);
        } else {
            setWidget(1, 0, new Label(i18n.QueryLabel()));
            setWidget(1, 1, queryName);
        }
    }
}
