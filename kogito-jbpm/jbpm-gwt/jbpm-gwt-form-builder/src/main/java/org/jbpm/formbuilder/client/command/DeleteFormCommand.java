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
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseHandler;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtent.reflection.client.Reflectable;

/**
 * Handles the action of deleting a form on the server.
 */
@Reflectable
public class DeleteFormCommand implements BaseCommand {

    private static final String DELETE_TYPE = DeleteFormCommand.class.getName();
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final FormBuilderService service = FormBuilderGlobals.getInstance().getService();
    
    public DeleteFormCommand() {
        super();
        bus.addHandler(GetFormRepresentationResponseEvent.TYPE, new GetFormRepresentationResponseHandler() {
            @Override
            public void onEvent(GetFormRepresentationResponseEvent event) {
                if (DELETE_TYPE.equals(event.getSaveType())) {
                    FormRepresentation form = event.getRepresentation();
                    showDeletePanel(form);
                }
            }
        });
    }

    @Override
    public void execute() {
        bus.fireEvent(new GetFormRepresentationEvent(DELETE_TYPE));
    }

    private MenuItem item = null;
    
    @Override
    public void setItem(MenuItem item) {
        this.item = item;
        item.setEnabled(true);
    }

    @Override
    public void setEmbeded(String profile) {
        if (profile != null && "guvnor".equals(profile)) {
            //if embedded from guvnor, deletion shouldn't be available
            if (item != null) {
                item.getParentMenu().removeItem(item);
            }
        }
    }

    private void showDeletePanel(final FormRepresentation form) {
        final PopupPanel panel = new PopupPanel();
        if (form.isSaved()) {
            VerticalPanel vpanel = new VerticalPanel();
            vpanel.add(new Label(i18n.WarningDeleteForm(form.getName())));
            HorizontalPanel hpanel = new HorizontalPanel();
            Button confirmButton = new Button(i18n.ConfirmButton());
            confirmButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    deleteForm(form);
                    panel.hide();
                }
            });
            hpanel.add(confirmButton);
            Button cancelButton = new Button(i18n.CancelButton());
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    panel.hide();
                }
            });
            hpanel.add(cancelButton);
            vpanel.add(hpanel);
            panel.setWidget(vpanel);
        } else {
            VerticalPanel vpanel = new VerticalPanel();
            vpanel.add(new Label(i18n.FormWasNeverSaved()));
            Button closeButton = new Button(i18n.CloseButton());
            closeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    panel.hide();
                }
            });
            vpanel.add(closeButton);
            panel.setWidget(vpanel);
        }
        int height = RootPanel.getBodyElement().getClientHeight();
        int width = RootPanel.getBodyElement().getClientWidth();
        panel.setPopupPosition((width / 2) - 150, (height - 100) / 2);
        panel.show();
    }
    
    private void deleteForm(FormRepresentation form) {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("form", form);
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void undoAction(UndoableEvent event) {
                FormRepresentation form = (FormRepresentation) event.getData("form");
                if (form != null) {
                    try {
                        service.saveForm(form);
                    } catch (FormBuilderException e) {
                        bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ProblemRestoringForm(), e));
                    }
                }
            }
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override
            public void doAction(UndoableEvent event) {
                FormRepresentation form = (FormRepresentation) event.getData("form");
                try {
                    service.deleteForm(form);
                } catch (FormBuilderException e) {
                    event.setData("form", null);
                    bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ProblemDeletingForm(), e));
                }
            }
        }));
    }
}
