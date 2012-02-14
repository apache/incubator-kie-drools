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
import org.jbpm.formapi.shared.api.items.TabbedPanelRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class TabbedLayoutFormItem extends LayoutFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private final List<FlowLayoutFormItem> tabs = new ArrayList<FlowLayoutFormItem>();
    private final List<TabLabelFormItem> titles = new ArrayList<TabLabelFormItem>();
    private String cssClassName;
    private String tabWidth;
    private String id;
    
    public class TabLabelFormItem extends LabelFormItem {

        public TabLabelFormItem(List<FBFormEffect> formEffects) {
            super(formEffects);
        }

        @Override
        public FBFormItem cloneItem() {
            LabelFormItem supItem = (LabelFormItem) super.cloneItem();
            TabLabelFormItem clone = new TabLabelFormItem(getFormEffects());
            try {
                clone.populate(supItem.getRepresentation());
            } catch (FormBuilderException e) { }
            return clone;
        }
        
        @Override
        public void saveValues(Map<String, Object> propertiesMap) {
            super.saveValues(propertiesMap);
            String width = extractString(propertiesMap.get("width"));
            String height = extractString(propertiesMap.get("height"));
            if (width != null && !"".equals(width)) {
                //all tabs have the same size
                for (TabLabelFormItem item : titles) {
                    item.setWidth(width);
                    TabbedLayoutFormItem.this.tabWidth = width;
                }
            }
            if (height != null && !"".equals(height)) {
                //all tabs have the same size
                for (TabLabelFormItem item : titles) {
                    item.setHeight(height);
                }
            }
        }
        
    }
    
    public class MyTabLayoutPanel extends TabLayoutPanel {

        public MyTabLayoutPanel(double barHeight, Unit barUnit) {
            super(barHeight, barUnit);
        }
        
        @Override
        public boolean remove(Widget widget) {
            if (widget instanceof FBFormItem) {
                TabbedLayoutFormItem.this.removeItem((FBFormItem) widget);
            }
            return super.remove(widget);
        }
    }
    
    private TabLayoutPanel panel = new MyTabLayoutPanel(21, Unit.PX);
    
    public TabbedLayoutFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        TabLabelFormItem tab1 = new TabLabelFormItem(getFormEffects());
        tab1.getLabel().setText("Tab 1");
        panel.add(new FlowLayoutFormItem(getFormEffects()), tab1);
        TabLabelFormItem tab2 = new TabLabelFormItem(getFormEffects());
        tab2.getLabel().setText("Tab 2");
        panel.add(new FlowLayoutFormItem(getFormEffects()), tab2);
        TabLabelFormItem tab3 = new TabLabelFormItem(getFormEffects());
        tab3.getLabel().setText("Tab 3");
        panel.add(new FlowLayoutFormItem(getFormEffects()), tab3);
        setSize("300px", "400px");
        panel.setSize("300px", "400px");
        add(panel);
    }

    public TabbedLayoutFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    @Override
    public void replacePhantom(FBFormItem item) {
        int selectedIndex = panel.getSelectedIndex();
        Widget tabWidget = panel.getWidget(selectedIndex);
        FlowLayoutFormItem tab = (FlowLayoutFormItem) tabWidget;
        tab.replacePhantom(item);
    }
    
    @Override
    public void add(PhantomPanel phantom, int x, int y) {
        int selectedIndex = panel.getSelectedIndex();
        Widget widget = panel.getWidget(selectedIndex);
        FlowLayoutFormItem tab = (FlowLayoutFormItem) widget;
        tab.add(phantom, x, y);
    }

    @Override
    public HasWidgets getPanel() {
        return this.panel;
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("height", getHeight());
        map.put("width", getWidth());
        map.put("cssClassName", this.cssClassName);
        map.put("id", this.id);
        map.put("numberOfTabs", panel.getWidgetCount());
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        String height = extractString(asPropertiesMap.get("height"));
        if (height != null && !"".equals(height)) {
            setHeight(height);
        }
        String width = extractString(asPropertiesMap.get("width"));
        if (width != null && !"".equals(width)) {
            setWidth(width);
        }
        this.cssClassName = extractString(asPropertiesMap.get("cssClassName"));
        this.id = extractString(asPropertiesMap.get("id"));
        Integer numberOfTabs = extractInt(asPropertiesMap.get("numberOfTabs"));
        
        if (numberOfTabs > panel.getWidgetCount()) {
            int qtyToAdd = numberOfTabs - panel.getWidgetCount();
            while (qtyToAdd > 0) {
                TabLabelFormItem label = new TabLabelFormItem(getFormEffects());
                FlowLayoutFormItem flow = new FlowLayoutFormItem(getFormEffects());
                label.getLabel().setText("Tab " + panel.getWidgetCount());
                panel.add(flow, label);
                qtyToAdd--;
            }
        } else if (numberOfTabs < panel.getWidgetCount()) {
            while (numberOfTabs != panel.getWidgetCount()) {
                panel.remove(panel.getWidgetCount() - 1);
                numberOfTabs--;
            }
        }
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        TabbedPanelRepresentation trep = super.getRepresentation(new TabbedPanelRepresentation());
        trep.setCssClassName(this.cssClassName);
        trep.setId(this.id);
        for (int index = 0; index < titles.size(); index++) {
            FlowLayoutFormItem tabContent = null;
            if (index < tabs.size()) {
                tabContent = tabs.get(index);
            }
            if (tabContent == null) {
                tabContent = new FlowLayoutFormItem(getFormEffects());
            }
            String tabTitle = titles.get(index).getLabel().getText();
            trep.putTab(index, tabTitle, tabContent.getRepresentation());
        }
        return trep;
    }
    
    private void populate(TabLayoutPanel panel) {
        panel.setHeight(getHeight());
        panel.setWidth(getWidth());
        panel.clear();
        for (int index = 0; index < this.titles.size() && index < this.tabs.size(); index++) {
            FlowLayoutFormItem flow = this.tabs.get(index);
            TabLabelFormItem label = this.titles.get(index);
            if (flow != null && label != null) {
                FlowLayoutFormItem newFlow = (FlowLayoutFormItem) flow.cloneItem();
                if (this.cssClassName != null && !"".equals(this.cssClassName)) {
                    newFlow.setStyleName(this.cssClassName);
                }
                panel.add(newFlow, label.cloneItem());
            }
        }
    }

    @Override
    public FBFormItem cloneItem() {
        TabbedLayoutFormItem clone = new TabbedLayoutFormItem(getFormEffects());
        clone.id = this.id;
        clone.cssClassName = this.cssClassName;
        for (TabLabelFormItem label : this.titles) {
            clone.titles.add((TabLabelFormItem) label.cloneItem());
        }
        for (FlowLayoutFormItem flow : this.tabs) {
            clone.tabs.add((FlowLayoutFormItem) flow.cloneItem());
        }
        populate(clone.panel);
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        TabLayoutPanel panel = new MyTabLayoutPanel(25, Unit.PX);
        panel.getElement().setId(this.id);
        panel.getElement().setClassName(this.cssClassName);
        for (int index = 0; index < this.titles.size() && index < this.tabs.size(); index++) {
            FlowLayoutFormItem flow = this.tabs.get(index);
            TabLabelFormItem label = this.titles.get(index);
            if (flow != null && label != null) {
                Widget newFlow = flow.cloneDisplay(data);
                if (this.cssClassName != null && !"".equals(this.cssClassName)) {
                    newFlow.setStyleName(this.cssClassName);
                }
                panel.add(newFlow, label.cloneDisplay(data));
            }
        }
        super.populateActions(panel.getElement());
        return panel;
    }


    @Override
    public boolean add(FBFormItem item) {
        int index = panel.getSelectedIndex();
        Widget widget = panel.getWidget(index);
        FlowLayoutFormItem tab = (FlowLayoutFormItem) widget;
        return tab.add(item);
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof TabbedPanelRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "TabbedPanelRepresentation"));
        }
        super.populate(rep);
        TabbedPanelRepresentation trep = (TabbedPanelRepresentation) rep;
        this.cssClassName = trep.getCssClassName();
        this.id = trep.getId();
        this.tabWidth = trep.getTabWidth();
        this.titles.clear();
        for (TabbedPanelRepresentation.IndexedString title : trep.getTabTitles()) {
            TabLabelFormItem label = new TabLabelFormItem(getFormEffects());
            label.getLabel().setText(title.getString());
            if (this.tabWidth != null && !"".equals(tabWidth)) {
                label.setWidth(this.tabWidth);
            }
            FormItemRepresentation subRep = trep.getTabContents().get(title);
            FlowLayoutFormItem subItem = (FlowLayoutFormItem) FBFormItem.createItem(subRep);
            if (this.cssClassName != null && !"".equals(this.cssClassName)) {
                subItem.setStyleName(this.cssClassName);
            }
            this.tabs.add(subItem);
            this.titles.add(label);
        }
        populate(this.panel);
    }

    public int getTabForCoordinates(int x, int y) {
        int tabNumber = 0;
        while (tabNumber < panel.getWidgetCount()) {
            Widget widget = this.panel.getTabWidget(tabNumber);
            int left = widget.getAbsoluteLeft();
            int top = widget.getAbsoluteTop();
            int right = left + widget.getOffsetWidth();
            int bottom = top + widget.getOffsetHeight();
            if (x > left && x < right && y > top && y < bottom) {
                return tabNumber;
            }
            tabNumber++;
        }
        return panel.getSelectedIndex();
    }

    public FBFormItem[] removeTab(int tabNumber) {
        FBFormItem[] retval = new FBFormItem[] { 
                titles.get(tabNumber), 
                tabs.get(tabNumber) 
        };
        titles.remove(tabNumber);
        tabs.remove(tabNumber);
        this.panel.remove(tabNumber);
        return retval;
    }

    public void insertTab(int tabNumber, TabLabelFormItem label, FlowLayoutFormItem panel) {
        if (label == null) {
            label = new TabLabelFormItem(getFormEffects());
            label.getLabel().setText("Tab " + (tabNumber + 1));
            if (this.tabWidth != null && !"".equals(tabWidth)) {
                label.setWidth(this.tabWidth);
            }
        }
        if (panel == null) {
            panel = new FlowLayoutFormItem(getFormEffects());
            if (this.cssClassName != null && !"".equals(this.cssClassName)) {
                panel.setStyleName(this.cssClassName);
            }
        }
        this.panel.insert(panel, label, tabNumber);
        List<TabLabelFormItem> nextLabels = this.titles.subList(tabNumber, this.titles.size() - 1);
        this.titles.removeAll(nextLabels);
        this.titles.add(label);
        this.titles.addAll(nextLabels);
        List<FlowLayoutFormItem> nextPanels = this.tabs.subList(tabNumber, this.tabs.size() - 1);
        this.tabs.removeAll(nextPanels);
        this.tabs.add(panel);
        this.tabs.addAll(nextPanels);
    }
    
    
}
