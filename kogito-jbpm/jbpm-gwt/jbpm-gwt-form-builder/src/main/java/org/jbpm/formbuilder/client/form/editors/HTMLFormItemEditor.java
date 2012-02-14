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
import org.jbpm.formbuilder.client.form.items.HTMLFormItem;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Inplace editor for {@link HTMLFormItem}
 */
public class HTMLFormItemEditor extends FBInplaceEditor {

    private final HTMLFormItem formItem;
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private VerticalPanel panel = new VerticalPanel();
    private TextArea editorArea = new TextArea();
    private Button htmlButton = new Button(i18n.HTMLEditorHTML());
    private Button textButton = new Button(i18n.HTMLEditorText());
    private Button doneButton = new Button(i18n.ConfirmButton());
    
    private FocusWrapper wrapper = new FocusWrapper();
    
    public HTMLFormItemEditor(HTMLFormItem formItem) {
        this.formItem = formItem;
        HorizontalPanel buttonPanel = new HorizontalPanel();
        editorArea.setValue(this.formItem.getHtmlContent());
        editorArea.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                wrapper.setValue(false);
            }
        });
        editorArea.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                wrapper.setValue(true);
            }
        });
        editorArea.unsinkEvents(Event.ONKEYPRESS | Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU);
        this.htmlButton.setEnabled(false);
        buttonPanel.add(createTextButton());
        buttonPanel.add(createHtmlButton());
        editorArea.setCharacterWidth(50);
        editorArea.setVisibleLines(5);
        panel.add(buttonPanel);
        panel.add(editorArea);
        panel.add(createDoneButton());
        add(panel);
    }

    @Override
    public void focus() {
        editorArea.setFocus(true);
    }
    
    @Override
    public boolean isFocused() {
        return wrapper.getValue();
    }
    
    private Button createDoneButton() {
        this.doneButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (textButton.isEnabled()) {
                    String htmlContent = editorArea.getValue();
                    formItem.setHtmlContent(htmlContent);
                } else {
                    String textContent = editorArea.getValue();
                    formItem.setTextContent(textContent);
                }
                formItem.reset();
            }
        });
        return this.doneButton;
    }
    private Button createHtmlButton() {
        this.htmlButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                formItem.setTextContent(editorArea.getValue());
                editorArea.setValue(formItem.getHtmlContent());
                textButton.setEnabled(true);
                htmlButton.setEnabled(false);
            }
        });
        return this.htmlButton;
    }

    private Button createTextButton() {
        this.textButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                formItem.setHtmlContent(editorArea.getValue());
                editorArea.setValue(formItem.getTextContent());
                htmlButton.setEnabled(true);
                textButton.setEnabled(false);
            }
        });
        return this.textButton;
    }
}
