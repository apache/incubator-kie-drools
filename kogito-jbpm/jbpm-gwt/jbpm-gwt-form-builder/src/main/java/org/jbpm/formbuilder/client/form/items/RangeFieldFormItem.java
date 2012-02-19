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
import org.jbpm.formapi.common.panels.RangeBox;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.RangeFieldRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class RangeFieldFormItem extends FBFormItem {

	private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
	
	private final RangeBox rangeBox = new RangeBox();
	
	private Double defaultValue = null;
    private String name = null;
    private String id = null;
    private String title = null;
    private Double max = null;
    private Double min = null;
    private Double step = null;
	
	public RangeFieldFormItem() {
		this(new ArrayList<FBFormEffect>());
	}
	
	public RangeFieldFormItem(List<FBFormEffect> formEffects) {
		super(formEffects);
		add(rangeBox);
        setWidth("150px");
        setHeight("25px");
        rangeBox.setWidth(getWidth());
        rangeBox.setHeight(getHeight());
	}
	
	@Override
	public Map<String, Object> getFormItemPropertiesMap() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("defaultValue", this.defaultValue);
        map.put("name", this.name);
        map.put("height", getHeight());
        map.put("width", getWidth());
        map.put("max", this.max);
        map.put("min", this.min);
        map.put("step", this.step);
        map.put("title", this.title);
        map.put("id", this.id);
        return map;
	}

	@Override
	public void saveValues(Map<String, Object> asPropertiesMap) {
		this.defaultValue = extractDouble(asPropertiesMap.get("defaultValue"));
        this.name = extractString(asPropertiesMap.get("name"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.title = extractString(asPropertiesMap.get("title"));
        this.max = extractDouble(asPropertiesMap.get("max"));
        this.min = extractDouble(asPropertiesMap.get("min"));
        this.step = extractDouble(asPropertiesMap.get("step"));
        this.id = extractString(asPropertiesMap.get("id"));
        
        populate(this.rangeBox);
	}
	
	private void populate(RangeBox rangeBox) {
        if (this.defaultValue != null) {
            rangeBox.setValue(this.defaultValue);
        }
        if (this.name != null) {
            rangeBox.setName(this.name);
        }
        if (getHeight() != null) {
            rangeBox.setHeight(getHeight());
        }
        if (getWidth() != null) {
            rangeBox.setWidth(getWidth());
        }
        if (this.title != null) {
            rangeBox.setTitle(this.title);
        }
        if (this.max != null) {
            rangeBox.setMax(this.max);
        }
        if (this.max != null) {
            rangeBox.setMin(this.min);
        }
        if (this.max != null) {
            rangeBox.setStep(this.step);
        }
    }

	@Override
    public FormItemRepresentation getRepresentation() {
        RangeFieldRepresentation rep = super.getRepresentation(new RangeFieldRepresentation());
        rep.setDefaultValue(this.defaultValue);
        rep.setName(this.name);
        rep.setId(this.id);
        rep.setMax(this.max);
        rep.setMin(this.min);
        rep.setStep(this.step);
        return rep;
    }

	@Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof RangeFieldRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "RangeFieldRepresentation"));
        }
        super.populate(rep);
        RangeFieldRepresentation rrep = (RangeFieldRepresentation) rep;
        this.defaultValue = rrep.getDefaultValue();
        this.name = rrep.getName();
        this.id = rrep.getId();
        this.max = rrep.getMax();
        this.min = rrep.getMin();
        this.step = rrep.getStep();
        if (rrep.getWidth() != null && !"".equals(rrep.getWidth())) {
            setWidth(rrep.getWidth());
        }
        if (rrep.getHeight() != null && !"".equals(rrep.getHeight())) {
            setHeight(rrep.getHeight());
        }
        populate(this.rangeBox);
    }
	
	@Override
    public FBFormItem cloneItem() {
        RangeFieldFormItem clone = new RangeFieldFormItem(getFormEffects());
        clone.defaultValue = this.defaultValue;
        clone.setHeight(this.getHeight());
        clone.id = this.id;
        clone.max = this.max;
        clone.min = this.min;
        clone.step = this.step;
        clone.name = this.name;
        clone.title = this.title;
        clone.setWidth(this.getWidth());
        clone.populate(clone.rangeBox);
        return clone;
    }

	@Override
	public Widget cloneDisplay(Map<String, Object> formData) {
		RangeBox tb = new RangeBox();
        populate(tb);
        Object input = getInputValue(formData);
        if (input != null) {
            String inputValue = input.toString();
			tb.setValue(inputValue.equals("") ? null : Double.valueOf(inputValue));
        }
        if (getOutput() != null && getOutput().getName() != null) {
            tb.setName(getOutput().getName());
        }
        super.populateActions(tb.getElement());
        return tb;
	}

}
