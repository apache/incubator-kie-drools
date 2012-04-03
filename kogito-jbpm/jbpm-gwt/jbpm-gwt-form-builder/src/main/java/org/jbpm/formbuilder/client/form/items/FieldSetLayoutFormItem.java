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
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.client.form.PhantomPanel;
import org.jbpm.formapi.common.panels.FieldSetPanel;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.FieldSetPanelRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.I18NUtils;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class FieldSetLayoutFormItem extends LayoutFormItem implements I18NFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private final I18NUtils utils = new I18NUtils();
    
    private String cssClassName;
    private String id;
    private String legend;

    private FieldSetPanel panel = new FieldSetPanel() {
        @Override
        public boolean remove(Widget w) {
            if (w instanceof FBFormItem) {
                removeItem((FBFormItem) w);
            }
            return super.remove(w);
        }
    };
    
    public FieldSetLayoutFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public FieldSetLayoutFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        setSize("250px", "120px");
        panel.setSize(getWidth(), getHeight());
        add(panel);
    }

    @Override
    public void replacePhantom(FBFormItem item) {
        PhantomPanel phantom = null;
        for (Widget widget : panel) {
            if (widget instanceof PhantomPanel) {
                phantom = (PhantomPanel) widget;
                break;
            }
        }
        if (phantom != null) {
            int index = panel.getWidgetIndex(phantom);
            super.insert(index, item);
            remove(phantom);
        } else {
            add(item);
        }
    }

    @Override
    public boolean add(FBFormItem item) {
        this.panel.add(item);
        return super.add(item);
    }
    
    @Override
    public void add(PhantomPanel phantom, int x, int y) {
        for (int index = 0; index < panel.getWidgetCount(); index++) {
            Widget item = panel.getWidget(index);
            int left = item.getAbsoluteLeft();
            int right = left + item.getOffsetWidth();
            int top = item.getAbsoluteTop();
            int bottom = top + item.getOffsetHeight();
            if (x > left && x < right && y > top && y < bottom) {
                panel.insert(phantom, index);
                break;
            }
        }
    }

    @Override
    public HasWidgets getPanel() {
        return this.panel;
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> formItemPropertiesMap = new HashMap<String, Object>();
        formItemPropertiesMap.put("height", getHeight());
        formItemPropertiesMap.put("width", getWidth());
        formItemPropertiesMap.put("cssClassName", this.cssClassName);
        formItemPropertiesMap.put("legend", this.legend);
        formItemPropertiesMap.put("id", id);
        return formItemPropertiesMap;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.cssClassName = extractString(asPropertiesMap.get("cssClassName"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.legend = extractString(asPropertiesMap.get("legend"));
        
        populate(this.panel);

    }

    private void populate(FieldSetPanel panel) {
        if (getHeight() != null) {
            panel.setHeight(getHeight());
        }
        if (this.cssClassName != null) {
            panel.setStyleName(cssClassName);
        }
        if (this.legend != null) {
            panel.setLegend(legend);
        }
        if (this.id != null) {
            panel.setId(id);
        }
        if (getWidth() != null) {
            panel.setWidth(getWidth());
        }
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof FieldSetPanelRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "FieldSetPanelRepresentation"));
        }
        super.populate(rep);
        FieldSetPanelRepresentation fsrep = (FieldSetPanelRepresentation) rep;
        
        this.cssClassName = fsrep.getCssClassName();
        this.id = fsrep.getId();
        this.legend = fsrep.getLegend();
        if (fsrep.getWidth() != null && !"".equals(fsrep.getWidth())) {
            setWidth(fsrep.getWidth());
        }
        if (fsrep.getHeight() != null && !"".equals(fsrep.getHeight())) {
            setHeight(fsrep.getHeight());
        }
        
        populate(this.panel);
        
        if (fsrep.getItems() != null) {
            for (FormItemRepresentation item : fsrep.getItems()) {
                add(super.createItem(item));
            }
        }
        
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        FieldSetPanelRepresentation rep = super.getRepresentation(new FieldSetPanelRepresentation());
        rep.setCssClassName(this.cssClassName);
        rep.setId(this.id);
        rep.setHeight(getHeight());
        rep.setWidth(getWidth());
        rep.setLegend(this.legend);
        List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();
        for (FBFormItem item : getItems()) {
            items.add(item.getRepresentation());
        }
        rep.setI18n(getI18nMap());
        rep.setItems(items);
        return rep;
    }

    @Override
    public FBFormItem cloneItem() {
        FieldSetLayoutFormItem clone = super.cloneItem(new FieldSetLayoutFormItem(getFormEffects()));
        clone.cssClassName = this.cssClassName;
        clone.id = this.id;
        clone.legend = this.legend;
        clone.populate(clone.panel);
        for (FBFormItem item : getItems()) {
            clone.add(item.cloneItem());
        }
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> formData) {
        FieldSetPanel fsp = new FieldSetPanel();
        populate(fsp);
        String value = (String) getInputValue(formData);
        if (value != null) {
            fsp.setLegend(value);
        } else {
            String locale = (String) formData.get(FormBuilderGlobals.BASE_LOCALE);
            fsp.setLegend(this.legend);
            if (locale != null) {
                String i18nText = getI18n(locale);
                if (i18nText != null && !"".equals(i18nText)) {
                    fsp.setLegend(i18nText);
                }
            }
        }
        super.populateActions(fsp.getElement());
        for (FBFormItem item : getItems()) {
            fsp.add(item.cloneDisplay(formData));
        }
        return fsp;
    }

    @Override
    public boolean containsLocale(String localeName) {
        return utils.containsLocale(localeName);
    }
    
    @Override
    public Format getFormat() {
        return utils.getFormat();
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
        utils.saveI18nMap(i18nMap);
    }
    
    @Override
    public void setFormat(Format format) {
        utils.setFormat(format);
    }
}
