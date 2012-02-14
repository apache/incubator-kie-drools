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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.OptionsFormItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.ComboBoxRepresentation;
import org.jbpm.formapi.shared.api.items.OptionRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a combo box.
 */
@Reflectable
public class ComboBoxFormItem extends OptionsFormItem {
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private ListBox listBox = new ListBox();
    
    Map<String, String> items = new HashMap<String, String>();
    
    private Boolean multiple = null;
    private Integer visibleItems = null;
    private String title;
    private String name;
    private String id;

    public ComboBoxFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public ComboBoxFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(listBox);
        setWidth("50px");
        setHeight("21px");
        listBox.setSize(getWidth(), getHeight());
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.multiple = extractBoolean(asPropertiesMap.get("multipleSelect")); 
        this.visibleItems = extractInt(asPropertiesMap.get("verticalSize"));
        this.title = extractString(asPropertiesMap.get("title"));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.name = extractString(asPropertiesMap.get("name"));
        this.id = extractString(asPropertiesMap.get("id"));
        populate(this.listBox);
    }

    @SuppressWarnings("deprecation")
    private void populate(ListBox listBox) {
        if (this.multiple != null) {
            this.listBox.setMultipleSelect(this.multiple);
        }
        if (this.visibleItems != null && this.visibleItems > 0) {
            this.listBox.setVisibleItemCount(this.visibleItems);
        }
        if (title != null) {
            this.listBox.setTitle(title);
        }
        if (getWidth() != null) {
            this.listBox.setWidth(getWidth());
        }
        if (getHeight() != null) {
            this.listBox.setHeight(getHeight());
        }
    }
    
    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> itemPropertiesMap = new HashMap<String, Object>();
        itemPropertiesMap.put("multipleSelect", this.multiple);
        itemPropertiesMap.put("verticalSize", this.visibleItems);
        itemPropertiesMap.put("title", this.title);
        itemPropertiesMap.put("width", this.getWidth());
        itemPropertiesMap.put("height", this.getHeight());
        itemPropertiesMap.put("name", this.name);
        itemPropertiesMap.put("id", this.id);
        return itemPropertiesMap;
    }

    @Override
    public void addItem(String label, String value) {
        if (value == null || "".equals(value)) {
            listBox.addItem(label);
            items.put(label, label);
        } else {
            listBox.addItem(label, value);
            items.put(label, value);
        }
    }
    
    @Override
    public void deleteItem(String label) {
        if (label != null) {
            items.remove(label);
            int size = 0;
            do {
                size = listBox.getItemCount();
                for (int index = 0; index < listBox.getItemCount(); index++) {
                    if (listBox.getItemText(index).equals(label)) {
                        listBox.removeItem(index);
                        break;
                    }
                }
            } while (size != listBox.getItemCount());
        }
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        ComboBoxRepresentation rep = super.getRepresentation(new ComboBoxRepresentation());
        List<OptionRepresentation> elements = new ArrayList<OptionRepresentation>();
        for (String label : this.items.keySet()) {
            OptionRepresentation opt = new OptionRepresentation();
            opt.setLabel(label);
            opt.setValue(this.items.get(label));
            elements.add(opt);
        }
        rep.setElements(elements);
        rep.setName(this.name);
        rep.setId(this.id);
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof ComboBoxRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "TextFieldRepresentation"));
        }
        super.populate(rep);
        ComboBoxRepresentation crep = (ComboBoxRepresentation) rep;
        List<OptionRepresentation> options = crep.getElements();
        this.items.clear();
        if (options != null) {
            for (OptionRepresentation option : options) {
                this.items.put(option.getLabel(), option.getValue());
                this.listBox.addItem(option.getLabel(), option.getValue());
            }
        }
        this.listBox.clear();
        addItems(this.items, this.listBox);
        this.name = crep.getName();
        this.id = crep.getId();
        populate(this.listBox);
    }

    @Override
    public void addEffect(FBFormEffect effect) {
        super.addEffect(effect);
        effect.setWidget(this.listBox);
    }
    
    @Override
    public Map<String, String> getItems() {
        Map<String, String> items = new HashMap<String, String>();
        for (int index = 0; index < listBox.getItemCount(); index++) {
            items.put(listBox.getItemText(index), listBox.getValue(index));
        }
        return items;
    }
    
    public void addItems(Map<String, String> items, ListBox listBox) {
        for (Map.Entry<String, String> entry : items.entrySet()) {
            listBox.addItem(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public FBFormItem cloneItem() {
        ComboBoxFormItem clone = new ComboBoxFormItem(getFormEffects());
        clone.setHeight(this.getHeight());
        clone.id = this.id;
        clone.multiple = this.multiple;
        clone.name = this.name;
        clone.title = this.title;
        clone.visibleItems = this.visibleItems;
        clone.setWidth(this.getWidth());
        clone.populate(clone.listBox);
        clone.addItems(this.getItems(), clone.listBox);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        ListBox lb = new ListBox();
        populate(lb);
        addItems(getItems(), lb);
        Object input = getInputValue(data);
        String inputName = getInput() == null ? null : getInput().getName();
        if (input != null && inputName != null) {
            if (input.getClass().isArray()) {
                Object[] arr = (Object[]) input;
                for (Object obj : arr) {
                    lb.addItem(obj.toString(), obj.toString());
                }
            } else if (input instanceof Collection) {
                Collection<?> col = (Collection<?>) input;
                for (Object obj : col) {
                    lb.addItem(obj.toString(), obj.toString());
                }
            } else if (input instanceof Map) {
                Map<?,?> map = (Map<?,?>) input;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    lb.addItem(entry.getKey().toString(), entry.getValue().toString());
                }
            } else {
                String value = input.toString();
                for (int index = 0; index < lb.getItemCount(); index++) {
                    if (value != null && value.equals(lb.getValue(index))) {
                        lb.setSelectedIndex(index);
                        break;
                    }
                }
            }
        }
        super.populateActions(lb.getElement());
        return lb;
    }
}
