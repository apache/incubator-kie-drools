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
import org.jbpm.formbuilder.client.menu.FormDataPopupPanel;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtent.reflection.client.Reflectable;

/**
 * Handles the action of saving a form on the server
 */
@Reflectable
public class SaveFormCommand implements BaseCommand {

    private static final String SAVE_TYPE = SaveFormCommand.class.getName();
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final FormBuilderService service = FormBuilderGlobals.getInstance().getService();
    
    public SaveFormCommand() {
        super();
        bus.addHandler(GetFormRepresentationResponseEvent.TYPE, new GetFormRepresentationResponseHandler() {
            @Override
            public void onEvent(GetFormRepresentationResponseEvent event) {
                if (SAVE_TYPE.equals(event.getSaveType())) {
                    FormRepresentation form = event.getRepresentation();
                    showSavePanel(form);
                }
            }
        });
    }
    
    private void showSavePanel(final FormRepresentation form) {
        if (form.getName() == null || "".equals(form.getName())) {
            final FormDataPopupPanel panel = new FormDataPopupPanel(true);
            panel.setAction(form.getAction());
            panel.setEnctype(form.getEnctype());
            panel.setMethod(form.getMethod());
            panel.setName(form.getName());
            panel.setTaskId(form.getTaskId());
            panel.setModal(true);
            panel.setPopupPosition(
                    RootPanel.getBodyElement().getClientWidth() / 2 - 150, 
                    RootPanel.getBodyElement().getClientHeight() / 2 - 150);
            panel.show();
            panel.addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(CloseEvent<PopupPanel> event) {
                    saveForm(panel, form);
                }
            });
        }
    }
    
    public void saveForm(FormDataPopupPanel panel, FormRepresentation form) {
        String formName = panel.getFormName();
        if (formName != null && !"".equals(formName)) {
            try {
                form.populate(panel.getFormName(), panel.getTaskId(), panel.getProcessId(),
                        panel.getAction(), panel.getMethod(), panel.getEnctype(), 
                        panel.getDocumentation());
                service.saveForm(form);
                bus.fireEvent(new NotificationEvent(Level.INFO, i18n.FormSavedSuccessfully(form.getName())));
            } catch (FormBuilderException e) {
                bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.ProblemSavingForm(form.getName()), e));
            }
        } else {
            bus.fireEvent(new NotificationEvent(Level.WARN, i18n.DefineFormNameFirst()));
        }
    }
    
    @Override
    public void execute() {
        bus.fireEvent(new GetFormRepresentationEvent(SAVE_TYPE));
    }

    private MenuItem item = null;
    
    @Override
    public void setItem(MenuItem item) {
        this.item = item;
        item.setEnabled(true);
    }
    
    @Override
    public void setEmbeded(String profile) {
        if (item != null) {
            //When embedded on guvnor, saving is done through guvnor
            if (profile != null && "guvnor".equals(profile)) {
                item.getParentMenu().removeItem(item);
            }
        }
    }
}
