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
import org.jbpm.formapi.client.form.I18NFormItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.HeaderRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.I18NUtils;
import org.jbpm.formbuilder.client.form.editors.HeaderInplaceEditor;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a header or title
 */
@Reflectable
public class HeaderFormItem extends FBFormItem implements I18NFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final HTML header = new HTML("<h1>" + i18n.MenuItemHeader() + "</h1>");
    private final I18NUtils utils = new I18NUtils();
    
    private String id;
    private String name;
    private String cssClassName;

    public HeaderFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public HeaderFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        add(getHeader());
        setWidth("99%");
        setHeight("30px");
        getHeader().setSize(getWidth(), getHeight());
    }
    
    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> formItemPropertiesMap = new HashMap<String, Object>();
        formItemPropertiesMap.put("id", id);
        formItemPropertiesMap.put("name", name);
        formItemPropertiesMap.put("width", getWidth());
        formItemPropertiesMap.put("height", getHeight());
        formItemPropertiesMap.put("cssClassName", cssClassName);
        return formItemPropertiesMap;
    }

    @Override
    public FBInplaceEditor createInplaceEditor() {
        return new HeaderInplaceEditor(this);
    }

    @Override
    public void saveValues(Map<String, Object> propertiesMap) {
        this.id = extractString(propertiesMap.get("id"));
        this.name = extractString(propertiesMap.get("name"));
        setWidth(extractString(propertiesMap.get("width")));
        setHeight(extractString(propertiesMap.get("height")));
        this.cssClassName = extractString(propertiesMap.get("cssClassName"));
        populate(getHeader());
    }

    private void populate(HTML html) {
        if (getWidth() != null) {
            html.setWidth(getWidth());
        }
        if (this.getHeight() != null) {
            html.setHeight(getHeight());
        }
        if (this.cssClassName != null) {
            html.setStyleName(this.cssClassName);
        }
    }
    
    public HTML getHeader() {
        return this.header;
    }
    
    public void setContent(String html) {
        getHeader().setHTML(html);
    }
    
    @Override
    public void addEffect(FBFormEffect effect) {
        super.addEffect(effect);
        effect.setWidget(this.header);
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        HeaderRepresentation rep = super.getRepresentation(new HeaderRepresentation());
        rep.setValue(this.header.getText());
        rep.setStyleClass(this.cssClassName);
        rep.setCssId(this.id);
        rep.setCssName(this.name);
        rep.setI18n(getI18nMap());
        rep.setFormat(getFormat() == null ? null : getFormat().toString());
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof HeaderRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "HeaderRepresentation"));
        }
        super.populate(rep);
        HeaderRepresentation hrep = (HeaderRepresentation) rep;
        this.cssClassName = hrep.getCssName();
        this.id = hrep.getCssId();
        saveI18nMap(hrep.getI18n());
        if (hrep.getValue().startsWith("<h1>")) {
            setContent(hrep.getValue());
        } else {
            setContent("<h1>" + hrep.getValue() + "</h1>");
        }
        if (hrep.getFormat() != null && !"".equals(hrep.getFormat())) {
            setFormat(Format.valueOf(hrep.getFormat()));
        }
    }
    
    @Override
    public FBFormItem cloneItem() {
        HeaderFormItem clone = super.cloneItem(new HeaderFormItem(getFormEffects()));
        clone.cssClassName = this.cssClassName;
        clone.id = this.id;
        clone.name = this.name;
        clone.setContent(this.header.getHTML());
        clone.saveI18nMap(getI18nMap());
        clone.setFormat(getFormat());
        clone.populate(this.header);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        HTML html = new HTML(this.header.getHTML());
        populate(html);
        String value = (String) getInputValue(data);
        if (value != null) {
            html.setHTML("<h1>" + value + "</h1>");
        } else {
            String locale = (String) data.get(FormBuilderGlobals.BASE_LOCALE);
            html.setHTML(this.header.getHTML());
            if (locale != null) {
                String i18nText = getI18n(locale);
                if (i18nText != null && !"".equals(i18nText)) {
                    html.setHTML("<h1>" + i18nText + "</h1>");
                }
            }
        }
        super.populateActions(html.getElement());
        return html;
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
                this.header.setHTML("<h1>" + defaultI18n + "</h1>");
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
    }
}
