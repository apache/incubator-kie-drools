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
import org.jbpm.formapi.shared.api.items.HiddenRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a hidden field
 */
@Reflectable
public class HiddenFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private Hidden hidden = new Hidden();
    
    private String id;
    private String name;
    private String value;

    public HiddenFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public HiddenFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        Grid border = new Grid(1, 1);
        border.setSize("100px", "20px");
        border.setBorderWidth(1);
        border.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        border.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        border.setWidget(0, 0, new Image(FormBuilderResources.INSTANCE.hiddenFieldIcon()));
        add(border);
        setSize("100px", "20px");
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", this.value);
        map.put("name", this.name);
        map.put("id", this.id);
        return map;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.value = extractString(asPropertiesMap.get("value"));
        this.name = extractString(asPropertiesMap.get("name"));
        this.id = extractString(asPropertiesMap.get("id"));
        populate(this.hidden);
    }

    private void populate(Hidden hidden) {
        if (this.id != null) {
            hidden.setID(id);
        }
        if (this.name != null && !"".equals(this.name)) {
            hidden.setName(name);
        }
        if (this.value != null) {
            hidden.setValue(value);
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        HiddenRepresentation rep = super.getRepresentation(new HiddenRepresentation());
        rep.setId(id);
        rep.setName(name);
        rep.setValue(value);
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof HiddenRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "HiddenRepresentation"));
        }
        super.populate(rep);
        HiddenRepresentation hrep = (HiddenRepresentation) rep;
        this.id = hrep.getId();
        this.name = hrep.getName();
        this.value = hrep.getValue();
        populate(this.hidden);
    }

    @Override
    public FBFormItem cloneItem() {
        HiddenFormItem clone = new HiddenFormItem(getFormEffects());
        clone.id = this.id;
        clone.name = this.name;
        clone.value = this.value;
        clone.populate(clone.hidden);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        Hidden hi = new Hidden();
        populate(hi);
        Object input = getInputValue(data);
        if (input != null) {
            hi.setValue(input.toString());
        }
        if (getOutput() != null && getOutput().getName() != null && !"".equals(getOutput().getName())) {
            hi.setName(getOutput().getName());
        }
        super.populateActions(hi.getElement());
        return hi;
    }
}
