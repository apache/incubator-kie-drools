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
import org.jbpm.formapi.shared.api.items.CheckBoxRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a checkbox
 */
@Reflectable
public class CheckBoxFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private CheckBox checkBox = new CheckBox();
    
    private String formValue;
    private Boolean checked;
    private String name;
    private String id;

    public CheckBoxFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public CheckBoxFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(checkBox);
        setWidth("15px");
        setHeight("15px");
        checkBox.setSize(getWidth(), getHeight());
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("formValue", this.formValue);
        map.put("checked", this.checked);
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("height", getHeight());
        map.put("width", getWidth());
        return map;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.formValue = extractString(asPropertiesMap.get("formValue"));
        this.name = extractString(asPropertiesMap.get("name"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.checked = extractBoolean(asPropertiesMap.get("checked"));
        setHeight(extractString(asPropertiesMap.get("height")));
        setWidth(extractString(asPropertiesMap.get("width")));
        populate(this.checkBox);
    }

    private void populate(CheckBox checkBox) {
        if (this.formValue != null) {
            checkBox.setFormValue(formValue);
        }
        if (this.name != null) {
            checkBox.setName(name);
        }
        if (this.checked != null) {
            checkBox.setValue(checked);
        }
        if (getWidth() != null) {
            checkBox.setWidth(getWidth());
        }
        if (getHeight() != null) {
            checkBox.setHeight(getHeight());
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        CheckBoxRepresentation rep = super.getRepresentation(new CheckBoxRepresentation());
        rep.setFormValue(formValue);
        rep.setName(name);
        rep.setId(id);
        rep.setChecked(checked);
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof CheckBoxRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "CheckBoxRepresentation"));
        }
        super.populate(rep);
        CheckBoxRepresentation crep = (CheckBoxRepresentation) rep;
        this.formValue = crep.getFormValue();
        this.name = crep.getName();
        this.id = crep.getId();
        this.checked = crep.getChecked();
        populate(this.checkBox);
    }

    @Override
    public FBFormItem cloneItem() {
        CheckBoxFormItem clone = new CheckBoxFormItem(getFormEffects());
        clone.setWidth(getWidth());
        clone.setHeight(getHeight());
        clone.checked = this.checked;
        clone.formValue = this.formValue;
        clone.id = this.id;
        clone.name = this.name;
        clone.populate(clone.checkBox);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        CheckBox cb = new CheckBox();
        populate(cb);
        Object input = getInputValue(data);
        if (input != null) {
            cb.setValue(Boolean.valueOf(input.toString()));
        }
        if (getOutput() != null && getOutput().getName() != null) {
            cb.setName(getOutput().getName());
        }
        super.populateActions(cb.getElement());
        return cb;
    }
}
