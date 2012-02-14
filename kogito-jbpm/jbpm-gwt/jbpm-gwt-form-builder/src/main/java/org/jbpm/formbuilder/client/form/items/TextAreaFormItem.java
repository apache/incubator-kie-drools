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
import org.jbpm.formapi.shared.api.items.TextAreaRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a text area
 */
@Reflectable
public class TextAreaFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private TextArea area = new TextArea();
    
    private String defaultValue;
    private Integer rows = 3;
    private Integer cols = 30;
    private String name;
    private String id;

    public TextAreaFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public TextAreaFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        area.setVisibleLines(this.rows);
        area.setCharacterWidth(this.cols);
        add(area);
    }
    
    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("defaultValue", this.defaultValue);
        map.put("rows", this.rows);
        map.put("cols", this.cols);
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("width", this.getWidth());
        map.put("height", this.getHeight());
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        Integer rows = extractInt(asPropertiesMap.get("rows"));
        Integer cols = extractInt(asPropertiesMap.get("cols"));
        this.defaultValue = extractString(asPropertiesMap.get("defaultValue"));
        this.name = extractString(asPropertiesMap.get("name"));
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        if (rows != null && rows > 0) {
            this.rows = rows;
        }
        if (cols != null && cols > 0) {
            this.cols = cols;
        }
        populate(this.area);
    }

    private void populate(TextArea area) {
        if (this.rows != null) {
            area.setVisibleLines(this.rows);
        }
        if (this.cols != null) {
            area.setCharacterWidth(this.cols);
        }
        if (this.defaultValue != null) {
            area.setValue(this.defaultValue);
        }
        if (this.name != null) {
            area.setName(this.name);
        }
        if (getWidth() != null) {
            area.setWidth(getWidth());
        }
        if (getHeight() != null) {
            area.setHeight(getHeight());
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        TextAreaRepresentation rep = super.getRepresentation(new TextAreaRepresentation());
        rep.setCols(this.cols);
        rep.setId(this.id);
        rep.setName(this.name);
        rep.setRows(this.rows);
        rep.setValue(this.defaultValue);
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof TextAreaRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "TextAreaRepresentation"));
        }
        super.populate(rep);
        TextAreaRepresentation trep = (TextAreaRepresentation) rep;
        this.cols = trep.getCols();
        this.id = trep.getId();
        this.name = trep.getName();
        this.rows = trep.getRows();
        this.defaultValue = trep.getValue();
        populate(this.area);
    }
    
    @Override
    public FBFormItem cloneItem() {
        TextAreaFormItem clone = new TextAreaFormItem(getFormEffects());
        clone.cols = this.cols;
        clone.defaultValue = this.defaultValue;
        clone.id = this.id;
        clone.name = this.name;
        clone.rows = this.rows;
        clone.populate(clone.area);
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        TextArea ta = new TextArea();
        populate(ta);
        Object input = getInputValue(data);
        if (input != null) {
            ta.setValue(input.toString());
        }
        if (getOutput() != null && getOutput().getName() != null) {
            ta.setName(getOutput().getName());
        }
        super.populateActions(ta.getElement());
        return ta;
    }

    public String getInputValue() {
        return area.getValue();
    }
}
