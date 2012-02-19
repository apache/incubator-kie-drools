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

import java.util.List;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchResultsView extends VerticalPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final IoAssociationView.Presenter presenter;
    
    public SearchResultsView(IoAssociationView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setTasks(List<TaskRef> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            clear();
            add(new Label(i18n.NoIoRefsFound()));
        } else {
            clear();
            boolean even = false;
            for (TaskRef task : tasks) {
                final TaskRow row = presenter.newTaskRow(task, even);
                even = !even;
                add(row);
            }
        }
    }

    public void setSelectedTask(TaskRef selectedTask) {
        if (selectedTask != null) {
            TaskRow selectedRow = null;
            for (Widget widget : this) {
                TaskRow row = (TaskRow) widget;
                if (row.getIoRef().equals(selectedTask)) {
                    selectedRow = row;
                    break;
                }
            }
            clear();
            if (selectedRow == null) {
                selectedRow = new TaskRow(selectedTask, true);
            }
            selectedRow.getFocus().removeHandler();
            selectedRow.getBlur().removeHandler();
            selectedRow.showInputs();
            selectedRow.showOutputs();
            selectedRow.showMetaData();
            selectedRow.clearRightClickHandlers();
            presenter.addQuickFormHandling(selectedRow);
            add(selectedRow);
        }
    }
}
