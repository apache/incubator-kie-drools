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
import org.jbpm.formapi.client.form.I18NFormItem;
import org.jbpm.formapi.client.form.OptionsFormItem;
import org.jbpm.formapi.common.panels.ListWidget;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.SummaryRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.I18NUtils;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class SummaryFormItem extends OptionsFormItem implements I18NFormItem {

	private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
	
	private final ListWidget listWidget = new ListWidget();
	private final I18NUtils utils = new I18NUtils();

	private int scrollTop;
	private int scrollLeft;
	private String id;
	private String dir;
	private String cssClassName;

	public SummaryFormItem() {
		this(new ArrayList<FBFormEffect>());
	}
	
	public SummaryFormItem(List<FBFormEffect> formEffects) {
		super(formEffects);
		add(listWidget);
	}

	@Override
	public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> formItemPropertiesMap = new HashMap<String, Object>();
        formItemPropertiesMap.put("scrollTop", String.valueOf(scrollTop));
        formItemPropertiesMap.put("scrollLeft", String.valueOf(scrollLeft));
        formItemPropertiesMap.put("dir", dir);
        formItemPropertiesMap.put("id", id);
        formItemPropertiesMap.put("width", getWidth());
        formItemPropertiesMap.put("height", getHeight());
        formItemPropertiesMap.put("cssClassName", cssClassName);
        return formItemPropertiesMap;
	}

	@Override
	public void saveValues(Map<String, Object> propertiesMap) {
        this.id = extractString(propertiesMap.get("id"));
        this.setWidth(extractString(propertiesMap.get("width")));
        this.setHeight(extractString(propertiesMap.get("height")));
        this.cssClassName = extractString(propertiesMap.get("cssClassName"));
        this.dir = extractString(propertiesMap.get("dir"));
    	this.scrollTop = extractInt(propertiesMap.get("scrollTop"));
    	this.scrollLeft = extractInt(propertiesMap.get("scrollLeft"));
        
        populate(listWidget);
	}

    private void populate(ListWidget list) {
        if (this.getWidth() != null) {
            list.setWidth(this.getWidth());
        }
        if (this.getHeight() != null) {
            list.setHeight(this.getHeight());
        }
        if (this.cssClassName != null) {
            list.setStyleName(this.cssClassName);
        }
        if (this.id != null) {
        	list.setId(this.id);
        }
        if (this.dir != null) {
        	list.setDir(this.dir);
        }
        if (this.scrollTop >= 0) {
        	list.setScrollTop(this.scrollTop);
        }
        if (this.scrollLeft >= 0) {
        	list.setScrollLeft(this.scrollLeft);
        }
    }
    
	@Override
	public FormItemRepresentation getRepresentation() {
		SummaryRepresentation rep = super.getRepresentation(new SummaryRepresentation());
		rep.setCssClassName(this.cssClassName);
		rep.setDir(this.dir);
		rep.setId(this.id);
		rep.setScrollLeft(this.scrollLeft);
		rep.setScrollTop(this.scrollTop);
		rep.setI18n(this.getI18nMap());
		rep.setItems(this.listWidget.getItems());
		return rep;
	}

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof SummaryRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "SummaryRepresentation"));
        }
        super.populate(rep);
        SummaryRepresentation srep = (SummaryRepresentation) rep;
        this.cssClassName = srep.getCssClassName();
        this.dir = srep.getDir();
        if (srep.getWidth() != null && !"".equals(srep.getWidth())) {
            setWidth(srep.getWidth());
        }
        if (srep.getHeight() != null && !"".equals(srep.getHeight())) {
            setHeight(srep.getHeight());
        }
        
        this.utils.saveI18nMap(srep.getI18n());
        this.id = srep.getId();
        this.scrollLeft = srep.getScrollLeft();
        this.scrollTop = srep.getScrollTop();
        
        populate(this.listWidget);
    }


	@Override
	public Widget cloneDisplay(Map<String, Object> formData) {
		ListWidget lw = new ListWidget();
        populate(lw);
        Object value = getInputValue(formData);
        if (value != null) {
        	if (value.getClass().isArray()) {
        		Object[] arr = (Object[]) value;
        		for (Object obj : arr) {
        			lw.addItem(String.valueOf(obj));
        		}
        	} else if (value instanceof Collection) {
        		Collection<?> coll = (Collection<?>) value;
        		for (Object obj : coll) {
        			lw.addItem(String.valueOf(obj));
        		}
        	} else if (value instanceof Map) {
        		Map<?,?> map = (Map<?,?>) value;
        		for (Object obj : map.values()) {
        			lw.addItem(String.valueOf(obj));
        		}
        	}
        } else {
            String locale = (String) formData.get(FormBuilderGlobals.BASE_LOCALE);
            if (locale == null) {
            	for (String item : this.listWidget.getItems()) {
            		lw.addItem(item);
            	}
            } else {
                String i18nText = getI18n(locale);
                if (i18nText != null && !"".equals(i18nText)) {
                	String[] items = i18nText.split(","); //TODO specify i18n items should be comma separated
                	if (items != null) {
                		for (String item : items) {
                			lw.addItem(item);
                		}
                	}
                }
            }
        }
        super.populateActions(lw.getElement());
        return lw;
	}

	@Override
	public FBFormItem cloneItem() {
		SummaryFormItem clone = super.cloneItem(new SummaryFormItem());
        clone.cssClassName = this.cssClassName;
        clone.id = this.id;
        clone.dir = this.dir;
        clone.scrollLeft = this.scrollLeft;
        clone.scrollTop = this.scrollTop;
        clone.setHeight(this.getHeight());
        clone.setWidth(this.getWidth());
        clone.populate(clone.listWidget);
        clone.saveI18nMap(getI18nMap());
        clone.setFormat(getFormat());
        return clone;
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
	
	@Override
	public void addItem(String label, String value) {
		this.listWidget.addItem(label);
	}
	
	@Override
	public void deleteItem(String label) {
		this.listWidget.removeItem(label);
	}
	
	@Override
	public Map<String, String> getItems() {
		Map<String, String> items = new HashMap<String, String>();
		for (String label : this.listWidget.getItems()) {
			items.put(label, label);
		}
		return items;
	}
}
