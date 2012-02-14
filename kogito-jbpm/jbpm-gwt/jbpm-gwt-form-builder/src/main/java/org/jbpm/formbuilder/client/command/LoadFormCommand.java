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
package org.jbpm.formbuilder.client.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.bus.LoadServerFormEvent;
import org.jbpm.formbuilder.client.bus.LoadServerFormHandler;
import org.jbpm.formbuilder.client.bus.LoadServerFormResponseEvent;
import org.jbpm.formbuilder.client.bus.LoadServerFormResponseHandler;
import org.jbpm.formbuilder.client.bus.ui.UpdateFormViewEvent;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtent.reflection.client.Reflectable;

/**
 * Handles the action of loading a form
 */
@Reflectable
public class LoadFormCommand implements BaseCommand {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final FormBuilderService service = FormBuilderGlobals.getInstance().getService();
    
    public LoadFormCommand() {
        bus.addHandler(LoadServerFormEvent.TYPE, new LoadServerFormHandler() {
            @Override
            public void onEvent(LoadServerFormEvent event) {
                String formName = event.getFormName();
                if (formName != null) {
                    try {
                        service.getForm(formName);
                    } catch (FormBuilderException e) {
                        bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntLoadForm(formName), e));
                    }
                } else {
                    try {
                        service.getForms();
                    } catch (FormBuilderException e) {
                        bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.CouldntLoadAllForms(), e));
                    }
                }
            }
        });
        bus.addHandler(LoadServerFormResponseEvent.TYPE, new LoadServerFormResponseHandler() {
            @Override
            public void onListForms(LoadServerFormResponseEvent event) {
                popupFormSelection(event.getList());
            }
            @Override
            public void onGetForm(LoadServerFormResponseEvent event) {
                populateFormView(event.getForm());
            }
        });
    }

    private void populateFormView(FormRepresentation form) {
        bus.fireEvent(new UpdateFormViewEvent(form));
    }

    private void popupFormSelection(List<FormRepresentation> forms) {
        final Map<String, FormRepresentation> formMap = new HashMap<String, FormRepresentation>();
        final ListBox names = new ListBox();
        for (FormRepresentation form : forms) {
            names.addItem(form.getName());
            formMap.put(form.getName(), form);
        }
        final PopupPanel panel = new PopupPanel(false, true);
        VerticalPanel vPanel = new VerticalPanel();
        HorizontalPanel selectPanel = new HorizontalPanel();
        selectPanel.add(new Label(i18n.SelectAFormLabel()));
        selectPanel.add(names);
        vPanel.add(selectPanel);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button loadButton = new Button(i18n.LoadButton());
        loadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String formName = names.getValue(names.getSelectedIndex());
                populateFormView(formMap.get(formName));
                panel.hide();
            }
        });
        Button cancelButton = new Button(i18n.CancelButton());
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                panel.hide();
            }
        });
        buttonPanel.add(new HTML("&nbsp;"));
        buttonPanel.add(loadButton);
        buttonPanel.add(cancelButton);
        vPanel.add(buttonPanel);
        panel.add(vPanel);
        panel.setPopupPosition(
                RootPanel.getBodyElement().getClientWidth() / 2 - 150, 
                RootPanel.getBodyElement().getClientHeight() / 2 - 150);
        panel.show();
    }
    
    @Override
    public void execute() {
        bus.fireEvent(new LoadServerFormEvent());
    }

    private MenuItem item = null;
    
    @Override
    public void setItem(MenuItem item) {
        this.item = item;
        item.setEnabled(true);
    }

    @Override
    public void setEmbeded(String profile) {
        //if embedded loading another form shouldn't be available
        if (item != null) {
            item.getParentMenu().removeItem(item);
        }
    }
}
