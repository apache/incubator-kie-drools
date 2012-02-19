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

import java.util.Map;

import org.gwt.mosaic.ui.client.WindowPanel;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.UIUtils;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseHandler;
import org.jbpm.formbuilder.client.bus.PreviewFormResponseEvent;
import org.jbpm.formbuilder.client.bus.PreviewFormResponseHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Preview form as a given language base class.
 */
public abstract class PreviewFormCommand implements BaseCommand {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    protected final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    private final String saveType;
    
    public PreviewFormCommand(final String saveType) {
        this.saveType = saveType;
        this.bus.addHandler(GetFormRepresentationResponseEvent.TYPE, new GetFormRepresentationResponseHandler() {
            @Override
            public void onEvent(GetFormRepresentationResponseEvent event) {
                FormRepresentation form = event.getRepresentation();
                String type = event.getSaveType();
                if (saveType.equals(type)) {
                    popupInputMapPanel(form);
                }
            }
        });
        this.bus.addHandler(PreviewFormResponseEvent.TYPE, new PreviewFormResponseHandler() {
            @Override
            public void onServerResponse(PreviewFormResponseEvent event) {
                if (event.getPreviewType().equals(saveType)) {
                    refreshPopup(event.getUrl());
                }
            }
        });
    }
    
    @Override
    public void setItem(MenuItem item) {
        /* not implemented */
    }
    
    @Override
    public void setEmbeded(String profile) {
        // shouldn't be disabled on embeded if it doesn't save
    }
    
    @Override
    public void execute() {
        this.bus.fireEvent(new GetFormRepresentationEvent(this.saveType));
    }

    protected void refreshPopup(String url) {
        WindowPanel window = UIUtils.createWindow("Preview as " + this.saveType);
        window.getDesktopPanel().addStyleName("formDisplay");
        Frame content = new Frame(url);
        window.setWidget(content);
        int height = RootPanel.getBodyElement().getClientHeight();
        int width = RootPanel.getBodyElement().getClientWidth();
        int left = RootPanel.getBodyElement().getAbsoluteLeft();
        int top = RootPanel.getBodyElement().getAbsoluteTop();
        content.setPixelSize(width - 200, height - 200);
        window.showModal();
        window.setPopupPosition(left + 100, top + 100);
        window.setPixelSize(width - 200, height - 200);
    }

    public void popupInputMapPanel(final FormRepresentation form) {
        Map<String, InputData> inputs = form.getInputs();
        if (inputs == null || inputs.isEmpty()) {
            saveForm(form, null);
        } else {
            final InputMapPanel popup = new InputMapPanel(inputs);
            popup.addOkHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    saveForm(form, popup.getInputs());
                }
            });
            int height = RootPanel.getBodyElement().getClientHeight();
            int width = RootPanel.getBodyElement().getClientWidth();
            popup.setPopupPosition((width / 2) - 150, (height / 2) - 150);
            popup.show();
        }
    }
    
    public void saveForm(FormRepresentation form, Map<String, Object> inputMap) {
        try {
            server.generateForm(form, this.saveType, inputMap);
        } catch (FormBuilderException e) {
            bus.fireEvent(new NotificationEvent(Level.ERROR, i18n.UnexpectedWhilePreviewForm(this.saveType), e)); 
        }
    }
}
