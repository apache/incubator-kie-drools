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
package org.jbpm.formbuilder.client.form.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.FBInplaceEditor;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.HTMLRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.editors.HTMLFormItemEditor;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a piece of user entered HTML
 */
@Reflectable
public class HTMLFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private HTML html = new HTML();

    public HTMLFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public HTMLFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        html.setHTML("<div style=\"background-color: #DDDDDD; \">HTML: Click to edit</div>");
        add(html);
        setWidth("200px");
        setHeight("100px");
        setSize(getWidth(),getHeight());
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("width", getWidth());
        map.put("height", getHeight());
        return map;
    }
    
    @Override
    public FBInplaceEditor createInplaceEditor() {
        return new HTMLFormItemEditor(this);
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        populate(this.html);
    }

    private void populate(HTML html) {
        if (getWidth() != null) {
            html.setWidth(getWidth());
        }
        if (getHeight() != null) {
            html.setHeight(getHeight());
        }
    }

    public void setContent(String html) {
        this.html.setHTML(html);
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        HTMLRepresentation rep = super.getRepresentation(new HTMLRepresentation());
        rep.setContent(html.getHTML());
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof HTMLRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "HTMLRepresentation"));
        }
        super.populate(rep);
        HTMLRepresentation hrep = (HTMLRepresentation) rep;
        this.setContent(hrep.getContent());
    }
    
    @Override
    public FBFormItem cloneItem() {
        HTMLFormItem clone = new HTMLFormItem(getFormEffects());
        clone.setHeight(getHeight());
        clone.setWidth(getWidth());
        clone.setContent(this.html.getHTML());
        clone.populate(clone.html);
        return clone;
    }

    public String getTextContent() {
        return this.html.getText();
    }
    
    public String getHtmlContent() {
        return this.html.getHTML();
    }
    
    public void setTextContent(String textContent) {
        this.html.setText(textContent);
    }
    
    public void setHtmlContent(String htmlContent) {
        this.html.setHTML(htmlContent);
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        HTML html = new HTML();
        html.setHTML(this.html.getHTML());
        populate(html);
        super.populateActions(html.getElement());
        return html;
    }
}
