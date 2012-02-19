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
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.client.form.PhantomPanel;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.MenuPanelRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class MenuLayoutFormItem extends LayoutFormItem {
	
	private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
	
	private final HorizontalPanel panel = new HorizontalPanel();
	
	private String type;
	private String cssClassName;
	private String id;
	private String dir;

	public MenuLayoutFormItem() {
		this(new ArrayList<FBFormEffect>());
	}
	
	public MenuLayoutFormItem(List<FBFormEffect> formEffects) {
		super(formEffects);
		this.panel.setBorderWidth(0);
		setSize("200px", "90px");
		this.panel.setSize(getWidth(), getHeight());
		add(this.panel);
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
        formItemPropertiesMap.put("dir", this.dir);
        formItemPropertiesMap.put("id", this.id);
        formItemPropertiesMap.put("type", this.type);
        return formItemPropertiesMap;
	}

	@Override
	public void saveValues(Map<String, Object> asPropertiesMap) {
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.cssClassName = extractString(asPropertiesMap.get("cssClassName"));
        this.dir = extractString(asPropertiesMap.get("dir"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.type = extractString(asPropertiesMap.get("type"));
        
        populate(this.panel);

	}

	private void populate(HorizontalPanel panel) {
		if (getHeight() != null) {
			panel.setHeight(getHeight());
		}
		if (this.cssClassName != null) {
			panel.setStyleName(cssClassName);
		}
		if (this.dir != null) {
			panel.getElement().setDir(dir);
		}
		if (this.id != null) {
			panel.getElement().setId(id);
		}
		if (getWidth() != null) {
			panel.setWidth(getWidth());
		}
		if (this.type != null) {
			panel.setStyleName(type + " " + panel.getStyleName());
		}
	}

	@Override
	public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof MenuPanelRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "MenuPanelRepresentation"));
        }
        super.populate(rep);
        MenuPanelRepresentation mrep = (MenuPanelRepresentation) rep;
        
    	this.cssClassName = mrep.getCssClassName();
    	this.id = mrep.getId();
    	this.dir = mrep.getDir();
    	this.type = mrep.getType();
        if (mrep.getWidth() != null && !"".equals(mrep.getWidth())) {
            setWidth(mrep.getWidth());
        }
        if (mrep.getHeight() != null && !"".equals(mrep.getHeight())) {
            setHeight(mrep.getHeight());
        }
        
        populate(this.panel);
        
        if (mrep.getItems() != null) {
            for (FormItemRepresentation item : mrep.getItems()) {
                add(super.createItem(item));
            }
        }
	}
	
	@Override
	public FormItemRepresentation getRepresentation() {
		MenuPanelRepresentation rep = super.getRepresentation(new MenuPanelRepresentation());
		rep.setCssClassName(this.cssClassName);
		rep.setId(this.id);
		rep.setHeight(getHeight());
		rep.setWidth(getWidth());
		rep.setDir(this.dir);
		rep.setType(this.type);
		List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();
		for (FBFormItem item : getItems()) {
			items.add(item.getRepresentation());
		}
		rep.setItems(items);
		return rep;
	}

	@Override
	public FBFormItem cloneItem() {
		MenuLayoutFormItem clone = super.cloneItem(new MenuLayoutFormItem(getFormEffects()));
        clone.cssClassName = this.cssClassName;
        clone.id = this.id;
        clone.dir = this.dir;
        clone.type = this.type;
        clone.populate(clone.panel);
        for (FBFormItem item : getItems()) {
            clone.add(item.cloneItem());
        }
        return clone;
	}

	@Override
	public Widget cloneDisplay(Map<String, Object> formData) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setBorderWidth(0);
        populate(hp);
        super.populateActions(hp.getElement());
        for (FBFormItem item : getItems()) {
            hp.add(item.cloneDisplay(formData));
        }
		return hp;
	}

}
