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
package org.jbpm.formbuilder.client.form.editors;

import org.jbpm.formapi.client.form.FBInplaceEditor;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.items.ServerTransformationFormItem;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Inplace editor fro {@link ServerTransformationFormItem}
 */
public class ServerScriptEditor extends FBInplaceEditor {

    private final ServerTransformationFormItem item;
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Button cancelButton = new Button(i18n.CancelButton());
    private final Button okButton = new Button(i18n.OkButton());
    private final TextArea editionArea = new TextArea();
    
    private final FocusWrapper wrapper = new FocusWrapper();
    
    public ServerScriptEditor(ServerTransformationFormItem item) {
        VerticalPanel panel = new VerticalPanel();
        this.item = item;
        this.editionArea.setCharacterWidth(50);
        this.editionArea.setVisibleLines(10);
        this.editionArea.setValue(item.getScriptContent());
        editionArea.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                wrapper.setValue(false);
            }
        });
        editionArea.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                wrapper.setValue(true);
            }
        });
        panel.add(this.editionArea);
        panel.add(createButtonsPanel());
        add(panel);
    }
    
    @Override
    public void focus() {
        this.editionArea.setFocus(true);
    }
    
    @Override
    public boolean isFocused() {
        return wrapper.getValue();
    }
    
    private HorizontalPanel createButtonsPanel() { 
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                item.setScriptContent(editionArea.getValue());
                item.reset();
            }
        });
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                item.reset();
            }
        });
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }
}
