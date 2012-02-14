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
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.client.form.PhantomPanel;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.CSSPanelRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form layout item. Represents a css based layout
 */
@Reflectable
public class CSSLayoutFormItem extends LayoutFormItem implements HasSourceReference {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private FlowPanel panel = new FlowPanel() {
        @Override
        public boolean remove(Widget w) {
            if (w instanceof FBFormItem) {
                removeItem((FBFormItem) w);
            }
            return super.remove(w);
        }
    };
    private LinkElement link = Document.get().createLinkElement(); 

    private String cssStylesheetUrl;
    private String cssClassName;
    private String id;

    public CSSLayoutFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public CSSLayoutFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        setSize("190px", "90px");
        panel.setSize(getWidth(), getHeight());
        this.link.setRel("Stylesheet");
        this.link.setType("text/css");
        this.link.setMedia("screen");
        panel.getElement().insertFirst(this.link);
        add(panel);
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cssStylesheetUrl", this.cssStylesheetUrl);
        map.put("cssClassName", this.cssClassName);
        map.put("id", this.id);
        map.put("width", getWidth());
        map.put("height", getHeight());
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> map) {
        this.cssStylesheetUrl = extractString(map.get("cssStylesheetUrl"));
        this.cssClassName = extractString(map.get("cssClassName"));
        this.id = extractString(map.get("id"));
        setHeight(extractString(map.get("height")));
        setWidth(extractString(map.get("width")));
        
        populate(this.panel, this.link);
    }

    private void populate(FlowPanel panel, LinkElement style) {
        if (getHeight() != null) {
            panel.setHeight(getHeight());
        }
        if (this.cssClassName != null) {
            panel.setStyleName(cssClassName);
        }
        if (getWidth() != null) {
            panel.setWidth(getWidth());
        }
        if (this.cssStylesheetUrl != null) {
            style.setHref(this.cssStylesheetUrl);
        }
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        CSSPanelRepresentation rep = super.getRepresentation(new CSSPanelRepresentation());
        List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();
        for (FBFormItem item : getItems()) {
            items.add(item.getRepresentation());
        }
        rep.setItems(items);
        rep.setId(this.id);
        rep.setCssClassName(this.cssClassName);
        rep.setCssStylesheetUrl(this.cssStylesheetUrl);
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof CSSPanelRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "CSSPanelRepresentation"));
        }
        super.populate(rep);
        CSSPanelRepresentation crep = (CSSPanelRepresentation) rep;
        this.cssClassName = crep.getCssClassName();
        this.id = crep.getId();
        this.cssStylesheetUrl = crep.getCssStylesheetUrl();
        super.getItems().clear();
        populate(this.panel, this.link);
        if (crep.getItems() != null) {
            for (FormItemRepresentation item : crep.getItems()) {
                add(super.createItem(item));
            }
        }
    }
    
    @Override
    public FBFormItem cloneItem() {
        CSSLayoutFormItem clone = super.cloneItem(new CSSLayoutFormItem(getFormEffects()));
        clone.cssClassName = this.cssClassName;
        clone.cssStylesheetUrl = this.cssStylesheetUrl;
        clone.id = this.id;
        clone.populate(clone.panel, clone.link);
        for (FBFormItem item : getItems()) {
            clone.add(item.cloneItem());
        }
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        FlowPanel fp = new FlowPanel();
        fp.getElement().insertFirst(this.link.cloneNode(false));
        populate(fp, this.link);
        super.populateActions(fp.getElement());
        for (FBFormItem item : getItems()) {
            fp.add(item.cloneDisplay(data));
        }
        return fp;
    }
    
    @Override
    public HasWidgets getPanel() {
        return this.panel;
    }
    
    @Override
    public boolean add(FBFormItem item) {
        panel.add(item);
        return super.add(item);
    }
    
    @Override
    public void add(PhantomPanel phantom, int x, int y) {
        this.panel.add(phantom);
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
    
    @Override
    public void setSourceReference(String sourceReference) {
        this.cssStylesheetUrl = sourceReference;
        this.link.setHref(this.cssStylesheetUrl);
    }
    
    @Override
    public String getSourceReference() {
        return this.cssStylesheetUrl;
    }
    
    @Override
    public List<String> getAllowedTypes() {
        ArrayList<String> retval = new ArrayList<String>();
        retval.add("css");
        return retval;
    }
}
