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
import org.jbpm.formapi.shared.api.items.NumberFieldRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class NumberFieldFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final DoubleBox doubleBox = new DoubleBox();
    
    private Double defaultContent = null;
    private String name = null;
    private String id = null;
    private String title = null;
    private Integer maxlength = null;
    
    public NumberFieldFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public NumberFieldFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(doubleBox);
        setWidth("150px");
        setHeight("25px");
        doubleBox.setWidth(getWidth());
        doubleBox.setHeight(getHeight());
    }
    
    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("inputDefaultContent", this.defaultContent);
        map.put("name", this.name);
        map.put("height", getHeight());
        map.put("width", getWidth());
        map.put("maxlength", this.maxlength);
        map.put("title", this.title);
        map.put("id", this.id);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.defaultContent = extractDouble(asPropertiesMap.get("inputDefaultContent"));
        this.name = extractString(asPropertiesMap.get("name"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.title = extractString(asPropertiesMap.get("title"));
        this.maxlength = extractInt(asPropertiesMap.get("maxlength"));
        this.id = extractString(asPropertiesMap.get("id"));
        
        populate(this.doubleBox);
    }

    private void populate(DoubleBox doubleBox) {
        if (this.defaultContent != null) {
            doubleBox.setValue(this.defaultContent);
        }
        if (this.name != null) {
            doubleBox.setName(this.name);
        }
        if (getHeight() != null) {
            doubleBox.setHeight(getHeight());
        }
        if (getWidth() != null) {
            doubleBox.setWidth(getWidth());
        }
        if (this.title != null) {
            doubleBox.setTitle(this.title);
        }
        if (this.maxlength != null) {
            doubleBox.setMaxLength(this.maxlength);
        }
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        NumberFieldRepresentation rep = super.getRepresentation(new NumberFieldRepresentation());
        rep.setDefaultValue(this.defaultContent);
        rep.setName(this.name);
        rep.setId(this.id);
        rep.setMaxLength(this.maxlength);
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof NumberFieldRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "TextFieldRepresentation"));
        }
        super.populate(rep);
        NumberFieldRepresentation nrep = (NumberFieldRepresentation) rep;
        this.defaultContent = nrep.getDefaultValue();
        this.name = nrep.getName();
        this.id = nrep.getId();
        this.maxlength = nrep.getMaxLength();
        if (nrep.getWidth() != null && !"".equals(nrep.getWidth())) {
            setWidth(nrep.getWidth());
        }
        if (nrep.getHeight() != null && !"".equals(nrep.getHeight())) {
            setHeight(nrep.getHeight());
        }
        populate(this.doubleBox);
    }
    
    @Override
    public FBFormItem cloneItem() {
        NumberFieldFormItem clone = super.cloneItem(new NumberFieldFormItem());
        clone.defaultContent = this.defaultContent;
        clone.setHeight(this.getHeight());
        clone.id = this.id;
        clone.maxlength = this.maxlength;
        clone.name = this.name;
        clone.title = this.title;
        clone.setWidth(this.getWidth());
        clone.populate(clone.doubleBox);
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> formData) {
        DoubleBox tb = new DoubleBox();
        populate(tb);
        Object input = getInputValue(formData);
        if (input != null) {
            String s = input.toString();
            tb.setValue(s.equals("") ? null : Double.valueOf(s));
        }
        if (getOutput() != null && getOutput().getName() != null) {
            tb.setName(getOutput().getName());
        }
        super.populateActions(tb.getElement());
        return tb;
    }

}
