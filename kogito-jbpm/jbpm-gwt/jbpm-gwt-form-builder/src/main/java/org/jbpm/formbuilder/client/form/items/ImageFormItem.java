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
import org.jbpm.formapi.client.form.HasSourceReference;
import org.jbpm.formapi.client.form.I18NFormItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.ImageRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.I18NUtils;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents an image
 */
@Reflectable
public class ImageFormItem extends FBFormItem implements I18NFormItem, HasSourceReference {

    private Image image = new Image();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final I18NUtils utils = new I18NUtils();
    
    private String altText;
    private String url;
    private String id;

    public ImageFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public ImageFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        image.setResource(FormBuilderResources.INSTANCE.defaultImage());
        add(image);
        setWidth("200px");
        setHeight("150px");
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("altText", this.altText);
        map.put("height", this.getHeight());
        map.put("width", this.getWidth());
        map.put("url", this.url);
        map.put("id", this.id);
        return map;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.altText = extractString(asPropertiesMap.get("altText"));
        Map<String, String> i18nMap = getI18nMap();
        i18nMap.put("default", this.altText);
        saveI18nMap(i18nMap);
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.url = extractString(asPropertiesMap.get("url"));
        this.id = extractString(asPropertiesMap.get("id"));
        populate(this.image);
    }

    private void populate(Image image) {
        if (this.altText != null) {
            image.setAltText(this.altText);
            image.setTitle(this.altText);
        }
        if (this.getHeight() != null) {
            image.setHeight(this.getHeight());
        }
        if (this.getWidth() != null) {
            image.setWidth(this.getWidth());
        }
        if (this.url != null && !"".equals(this.url)) {
            image.setUrl(this.url);
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        ImageRepresentation rep = super.getRepresentation(new ImageRepresentation());
        rep.setAltText(this.altText);
        rep.setUrl(this.url);
        rep.setId(this.id);
        rep.setI18n(getI18nMap());
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof ImageRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "ImageRepresentation"));
        }
        super.populate(rep);
        ImageRepresentation irep = (ImageRepresentation) rep;
        this.altText = irep.getAltText();
        this.url = irep.getUrl();
        this.id = irep.getId();
        saveI18nMap(irep.getI18n());
        if (this.altText == null || "".equals(this.altText)) {
            String i18nAltText = getI18n("default");
            if (i18nAltText != null) {
                this.altText = i18nAltText;
            }
        }
        populate(this.image);
    }

    @Override
    public FBFormItem cloneItem() {
        ImageFormItem clone = super.cloneItem(new ImageFormItem());
        clone.altText = this.altText;
        clone.setHeight(this.getHeight());
        clone.setWidth(this.getWidth());
        clone.id = this.id;
        clone.url = this.url;
        clone.saveI18nMap(getI18nMap());
        clone.populate(clone.image);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        Image im = new Image();
        populate(im);
        String locale = (String) data.get(FormBuilderGlobals.BASE_LOCALE);
        if (locale != null) {
            String i18nText = getI18n(locale);
            if (i18nText != null && !"".equals(i18nText)) {
                im.setAltText(i18nText);
                im.setTitle(i18nText);
            }
        }
        super.populateActions(im.getElement());
        return im;
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
                this.altText = defaultI18n;
            }
    
            utils.saveI18nMap(i18nMap);
        }
    }
    
    @Override
    public void setFormat(Format format) {
        // ignore
    }
    
    @Override
    public Format getFormat() {
        // ignore
        return null;
    }
    
    @Override
    public void setSourceReference(String sourceReference) {
        this.url = sourceReference;
        this.image.setUrl(this.url);
    }
    
    @Override
    public String getSourceReference() {
        return this.url;
    }
    
    @Override
    public List<String> getAllowedTypes() {
        ArrayList<String> retval = new ArrayList<String>();
        retval.add("jpeg");
        retval.add("jpg");
        retval.add("png");
        retval.add("gif");
        retval.add("svg");
        return retval;
    }
}
