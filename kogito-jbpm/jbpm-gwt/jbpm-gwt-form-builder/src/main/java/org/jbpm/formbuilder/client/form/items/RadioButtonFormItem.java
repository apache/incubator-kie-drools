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
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.RadioButtonRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a radio button
 */
@Reflectable
public class RadioButtonFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private RadioButton button = new RadioButton("");
    
    private String name;
    private String id;
    private String value;
    private Boolean selected = Boolean.FALSE;

    public RadioButtonFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public RadioButtonFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(button);
        setHeight("15px");
        setWidth("15px");
        button.setSize(getWidth(), getHeight());
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("value", this.value);
        map.put("selected", this.selected);
        map.put("width", this.getWidth());
        map.put("height", this.getHeight());
        return map;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.name = extractString(asPropertiesMap.get("name"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.value = extractString(asPropertiesMap.get("value"));
        this.selected = extractBoolean(asPropertiesMap.get("selected"));
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        populate(this.button);
    }
    
    private void populate(RadioButton button) {
        if (this.name != null) {
            button.setName(this.name);
        }
        if (this.value != null) {
            button.setFormValue(this.value);
        }
        if (this.selected != null) {
            button.setValue(this.selected);
        }
        if (this.getWidth() != null) {
            button.setWidth(this.getWidth());
        }
        if (this.getHeight() != null) {
            button.setHeight(this.getHeight());
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        RadioButtonRepresentation rep = super.getRepresentation(new RadioButtonRepresentation());
        rep.setId(this.id);
        rep.setName(this.name);
        rep.setSelected(this.selected);
        rep.setValue(this.value);
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof RadioButtonRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "RadioButtonRepresentation"));
        }
        super.populate(rep);
        RadioButtonRepresentation rrep = (RadioButtonRepresentation) rep;
        this.id = rrep.getId();
        this.name = rrep.getName();
        this.selected = rrep.getSelected();
        this.value = rrep.getValue();
        populate(this.button);
    }
    
    @Override
    public FBFormItem cloneItem() {
        RadioButtonFormItem clone = new RadioButtonFormItem(getFormEffects());
        clone.id = this.id;
        clone.name = this.name;
        clone.selected = this.selected;
        clone.value = this.value;
        clone.setHeight(this.getHeight());
        clone.setWidth(this.getWidth());
        clone.populate(clone.button);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        RadioButton rb = new RadioButton("");
        populate(rb);
        Object input = getInputValue(data);
        if (input != null) {
            rb.setValue(Boolean.valueOf(input.toString()));
        }
        if (getOutput() != null && getOutput().getName() != null) {
            rb.setName(getOutput().getName());
        }
        super.populateActions(rb.getElement());
        return rb;
    }
}
