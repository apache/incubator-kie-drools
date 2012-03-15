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

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * IO associations view. Represents a search string to 
 * list view for common similar tasks, processes, and any
 * kind of input output that can be mapped later to an 
 * input/output representation. 
 */
public class IoAssociationViewImpl extends AbsolutePanel implements IoAssociationView {

    private VerticalPanel panel = new VerticalPanel();
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Presenter presenter;
    private final SearchFilterView filterView;
    private final SearchResultsView resultsView;
    public IoAssociationViewImpl() {
        this.filterView = new SearchFilterView();
        this.presenter = new IoAssociationPresenter(this);
        this.resultsView = new SearchResultsView(this.presenter);

        setSize("100%", "100%");
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        panel.add(new Label(i18n.SearchIOAssociations()));
        panel.add(filterView);
        panel.add(resultsView);
        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setSize("99%", "99%");
        add(scroll);
    }
    
    @Override
    public void setTasks(List<TaskRef> tasks) {
        resultsView.setTasks(tasks);
    }

    @Override
    public void setSelectedTask(TaskRef selectedTask) {
        resultsView.setSelectedTask(selectedTask);
        
    }

    @Override
    public void disableSearch() {
        panel.remove(filterView);
    }

    @Override
    public SearchFilterView getSearch() {
        return filterView;
    }
    
    @Override
    public TaskRow createTaskRow(TaskRef task, boolean even) {
        return new TaskRow(task, even);
    }
}
