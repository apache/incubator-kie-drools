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

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.common.handler.RightClickEvent;
import org.jbpm.formapi.common.handler.RightClickHandler;
import org.jbpm.formapi.common.panels.CommandPopupPanel;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.bus.ExistingTasksResponseEvent;
import org.jbpm.formbuilder.client.bus.ExistingTasksResponseHandler;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceEvent;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceHandler;
import org.jbpm.formbuilder.client.bus.ui.TaskNameFilterEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskNameFilterHandler;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;

/**
 * Tasks presenter. Handles server querying of existing tasks 
 * and view population
 */
public class IoAssociationPresenter implements IoAssociationView.Presenter {
    
    private final IoAssociationView view;
    
    private final FormBuilderService model = FormBuilderGlobals.getInstance().getService();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public IoAssociationPresenter(IoAssociationView tasksView) {
        this.view = tasksView;
        bus.addHandler(TaskNameFilterEvent.TYPE, new TaskNameFilterHandler() {
            @Override
            public void onEvent(TaskNameFilterEvent event) {
                String filter = event.getTaskNameFilter();
                try {
                    model.getExistingIoAssociations(filter);
                } catch (Exception e) {
                    bus.fireEvent(new NotificationEvent(Level.WARN, i18n.CouldntPopulateAutocomplete(), e));
                }
            }
        });
        ExistingTasksResponseHandler handler = new ExistingTasksResponseHandler() {
            @Override
            public void onEvent(ExistingTasksResponseEvent event) {
                view.setTasks(event.getTasks());
            }
        };
        bus.addHandlerToSource(ExistingTasksResponseEvent.TYPE, model, handler);
        bus.addHandlerToSource(ExistingTasksResponseEvent.TYPE, view.getSearch().getAdvancedView(), handler);
        bus.addHandler(TaskSelectedEvent.TYPE, new TaskSelectedHandler() {
            @Override
            public void onSelectedTask(TaskSelectedEvent event) {
                view.setSelectedTask(event.getSelectedTask());
            }
        });
        bus.addHandler(EmbededIOReferenceEvent.TYPE, new EmbededIOReferenceHandler() {
            @Override
            public void onEvent(EmbededIOReferenceEvent event) {
                if (event.getIoRef() != null) {
                    view.disableSearch();
                    bus.fireEvent(new TaskSelectedEvent(event.getIoRef()));
                }
            }
        });
    }
    
    @Override
    public TaskRow newTaskRow(final TaskRef task, boolean even) {
        TaskRow row = view.createTaskRow(task, even);
        row.addRightClickHandler(new RightClickHandler() {
            @Override
            public void onRightClick(RightClickEvent event) {
                final CommandPopupPanel panel = new CommandPopupPanel(true);
                panel.setPopupPosition(event.getX(), event.getY());
                panel.addItem(i18n.SelectIOObjectCommand(), new Command() {
                    @Override
                    public void execute() {
                        bus.fireEvent(new TaskSelectedEvent(task));
                        panel.hide();
                    }
                });
                panel.show();
            }
        });
        return row;
    }
    
    @Override
    public void addQuickFormHandling(final TaskRow row) {
        row.addRightClickHandler(new RightClickHandler() {
            @Override
            public void onRightClick(final RightClickEvent event) {
                final CommandPopupPanel panel = new CommandPopupPanel(true);
                panel.setPopupPosition(event.getX(), event.getY());
                panel.addItem(i18n.QuickFormIOObjectCommand(), new Command() {
                    @Override
                    public void execute() {
                        QuickFormPanel conf = new QuickFormPanel(row);
                        conf.setPopupPosition(event.getX(), event.getY());
                        conf.show();
                        panel.hide();
                    }
                });
                panel.show();
            }
        });
    }

}
