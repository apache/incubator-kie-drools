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
import org.jbpm.formapi.client.form.I18NFormItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.CompleteButtonRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.I18NUtils;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a complete button
 */
@Reflectable
public class CompleteButtonFormItem extends FBFormItem implements I18NFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private Button button = new Button(i18n.CompleteButton());
    private final I18NUtils utils = new I18NUtils();

    private String innerText = i18n.CompleteButton();
    private String name;
    private String id;
    private String cssStyleName;

    public CompleteButtonFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public CompleteButtonFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(button);
        setHeight("27px");
        setWidth("100px");
        button.setSize(getWidth(), getHeight());
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        setHeight(extractString(asPropertiesMap.get("height")));
        setWidth(extractString(asPropertiesMap.get("width")));
        this.name = extractString(asPropertiesMap.get("name"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.innerText = extractString(asPropertiesMap.get("innerText"));
        this.cssStyleName = extractString(asPropertiesMap.get("cssStyleName"));
        
        populate(this.button);
    }

    private void populate(Button button) {
        if (getHeight() != null) {
            button.setHeight(getHeight());
        }
        if (getWidth() != null) {
            button.setWidth(getWidth());
        }
        if (this.innerText != null) {
            button.setText(this.innerText);
        }
        if (this.cssStyleName != null) {
            button.setStyleName(this.cssStyleName);
        }
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("height", getHeight());
        map.put("width", getWidth());
        map.put("innerText", this.innerText);
        map.put("cssStyleName", this.cssStyleName);
        map.put("name", this.name);
        map.put("id", this.id);
        return map;
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        CompleteButtonRepresentation rep = super.getRepresentation(new CompleteButtonRepresentation());
        rep.setText(this.innerText);
        rep.setName(this.name);
        rep.setId(this.id);
        rep.setI18n(getI18nMap());
        rep.setFormat(getFormat() == null ? null : getFormat().toString());
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof CompleteButtonRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "CompleteButtonRepresentation"));
        }
        super.populate(rep);
        CompleteButtonRepresentation crep = (CompleteButtonRepresentation) rep;
        this.innerText = crep.getText();
        this.name = crep.getName();
        this.id = crep.getId();
        this.saveI18nMap(crep.getI18n());
        if (crep.getFormat() != null && !"".equals(crep.getFormat())) {
            this.setFormat(Format.valueOf(crep.getFormat()));
        }
        populate(this.button);
    }
    
    @Override
    public FBFormItem cloneItem() {
        CompleteButtonFormItem clone = new CompleteButtonFormItem(getFormEffects());
        clone.cssStyleName = this.cssStyleName;
        clone.setHeight(this.getHeight());
        clone.setWidth(this.getWidth());
        clone.id = this.id;
        clone.innerText = this.innerText;
        clone.saveI18nMap(getI18nMap());
        clone.name = this.name;
        clone.populate(clone.button);
        clone.setFormat(getFormat());
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(final Map<String, Object> data) {
        Button bt = new Button();
        populate(bt);
        Object input = getInputValue(data);
        if (input != null) {
            bt.setText(input.toString());
        }
        if (getOutput() != null && getOutput().getName() != null) {
            ButtonElement.as(bt.getElement()).setName(getOutput().getName());
        }
        bt.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormPanel form = (FormPanel) data.get(FormBuilderGlobals.FORM_PANEL_KEY);
                form.submit();
            }
        });
        super.populateActions(bt.getElement());
        return bt;
    }
    
    @Override
    public boolean containsLocale(String localeName) {
        return utils.containsLocale(localeName);
    }
    
    @Override
    public String getI18n(String key) {
        return utils.getI18n(key);
    }
    
    @Override
    public Map<String, String> getI18nMap() {
        return utils.getI18nMap();
    }
    
    @Override
    public void saveI18nMap(Map<String, String> i18nMap) {
        if (i18nMap != null) {
            String defaultI18n = i18nMap.get("default");
            if (defaultI18n != null && !"".equals(defaultI18n)) {
                this.innerText = defaultI18n;
                populate(this.button);
            }
            utils.saveI18nMap(i18nMap);
        }
    }
    
    @Override
    public Format getFormat() {
        return utils.getFormat();
    }
    
    @Override
    public void setFormat(Format format) {
        utils.setFormat(format);
    };
}
