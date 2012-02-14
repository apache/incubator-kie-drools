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
import org.jbpm.formapi.shared.api.items.HorizontalPanelRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents an horizontal layout
 */
@Reflectable
public class HorizontalLayoutFormItem extends LayoutFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private HorizontalPanel panel = new HorizontalPanel();

    public HorizontalLayoutFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public HorizontalLayoutFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        panel.setBorderWidth(1);
        add(panel);
        setSize("90px", "30px");
        panel.setSize(getWidth(), getHeight());
    }
    
    private Integer borderWidth;
    private Integer spacing;
    private String cssClassName;
    private String horizontalAlignment;
    private String verticalAlignment;
    private String title;
    private String id;
    
    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> formItemPropertiesMap = new HashMap<String, Object>();
        formItemPropertiesMap.put("borderWidth", borderWidth);
        formItemPropertiesMap.put("height", getHeight());
        formItemPropertiesMap.put("width", getWidth());
        formItemPropertiesMap.put("spacing", spacing);
        formItemPropertiesMap.put("cssClassName", cssClassName);
        formItemPropertiesMap.put("horizontalAlignment", horizontalAlignment);
        formItemPropertiesMap.put("verticalAlignment", verticalAlignment);
        formItemPropertiesMap.put("title", title);
        formItemPropertiesMap.put("id", id);
        return formItemPropertiesMap;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.borderWidth = extractInt(asPropertiesMap.get("borderWidth"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.spacing = extractInt(asPropertiesMap.get("spacing"));
        this.cssClassName = extractString(asPropertiesMap.get("cssClassName"));
        this.horizontalAlignment = extractString(asPropertiesMap.get("horizontalAlignment"));
        this.verticalAlignment = extractString(asPropertiesMap.get("verticalAlignment"));
        this.title = extractString(asPropertiesMap.get("title"));
        this.id = extractString(asPropertiesMap.get("id"));
        
        populate(this.panel);
    }

    private void populate(HorizontalPanel panel) {
        if (this.borderWidth != null) {
            panel.setBorderWidth(this.borderWidth);
        }
        if (this.getHeight() != null && !"".equals(this.getHeight())) {
            panel.setHeight(this.getHeight());
        }
        if (this.getWidth() != null && !"".equals(this.getWidth())) {
            panel.setWidth(this.getWidth());
        }
        if (this.spacing != null) {
            panel.setSpacing(this.spacing);
        }
        if (this.cssClassName != null) {
            panel.setStyleName(this.cssClassName);
        }
        if (this.horizontalAlignment != null) {
            try {
                Direction d = Direction.valueOf(horizontalAlignment);
                panel.setHorizontalAlignment(HorizontalAlignmentConstant.startOf(d));
            } catch (IllegalArgumentException e) { }
        }
        if (this.title != null) {
            panel.setTitle(this.title);
        }
    }
    
    @Override
    public HasWidgets getPanel() {
        return panel;
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        HorizontalPanelRepresentation rep = super.getRepresentation(new HorizontalPanelRepresentation());
        rep.setBorderWidth(this.borderWidth);
        rep.setCssClassName(this.cssClassName);
        rep.setHorizontalAlignment(this.horizontalAlignment);
        rep.setId(this.id);
        rep.setSpacing(this.spacing);
        rep.setTitle(this.title);
        rep.setVerticalAlignment(this.verticalAlignment);
        for (FBFormItem item : getItems()) {
            rep.addItem(item.getRepresentation());
        }
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof HorizontalPanelRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "HorizontalPanelRepresentation"));
        }
        super.populate(rep);
        HorizontalPanelRepresentation hrep = (HorizontalPanelRepresentation) rep;
        this.borderWidth = hrep.getBorderWidth();
        this.cssClassName = hrep.getCssClassName();
        this.horizontalAlignment = hrep.getHorizontalAlignment();
        this.id = hrep.getId();
        this.spacing = hrep.getSpacing();
        this.title = hrep.getTitle();
        this.verticalAlignment = hrep.getVerticalAlignment();
        this.panel.clear();
        super.getItems().clear();
        populate(this.panel);
        if (hrep.getItems() != null) {
            for (FormItemRepresentation item : hrep.getItems()) {
                add(super.createItem(item));
            }
        }
    }
    
    @Override
    public FBFormItem cloneItem() {
        HorizontalLayoutFormItem clone = new HorizontalLayoutFormItem(getFormEffects());
        clone.borderWidth = this.borderWidth;
        clone.cssClassName = this.cssClassName;
        clone.setHeight(this.getHeight());
        clone.horizontalAlignment = this.horizontalAlignment;
        clone.id = this.id;
        clone.spacing = this.spacing;
        clone.title = this.title;
        clone.verticalAlignment = this.verticalAlignment;
        clone.setWidth(this.getWidth());
        clone.populate(clone.panel);
        for (FBFormItem item : getItems()) {
            clone.add(item.cloneItem());
        }
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        HorizontalPanel hp = new HorizontalPanel();
        populate(hp);
        super.populateActions(hp.getElement());
        for (FBFormItem item : getItems()) {
            hp.add(item.cloneDisplay(data));
        }
        return hp;
    }
    
    @Override
    public boolean add(FBFormItem item) {
    	this.panel.add(item);
    	return super.add(item);
    }

    @Override
    public void add(PhantomPanel phantom, int x, int y) {
    	Widget beforeWidget = null;
    	for (Widget widget : this.panel) {
    		int left = widget.getAbsoluteLeft();
    		int right = left + widget.getOffsetWidth();
    		if (x > left && x < right) {
    			beforeWidget = widget;
    			break;
    		}
    	}
    	if (beforeWidget == null) {
    		this.panel.add(phantom);
    	} else {
    		int index = this.panel.getWidgetIndex(beforeWidget);
			this.panel.insert(phantom, index);
    	}
    }
    
    @Override
    public void replacePhantom(FBFormItem item) {
        PhantomPanel phantom = null;
        for (Widget widget : this.panel) {
            if (widget instanceof PhantomPanel) {
                phantom = (PhantomPanel) widget;
                break;
            }
        }
        if (phantom == null) {
            add(item);
        } else {
            int index = this.panel.getWidgetIndex(phantom);
            this.panel.remove(phantom);
            super.insert(index, item);
        }
    }
}
