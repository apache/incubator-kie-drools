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

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.client.form.PhantomPanel;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.MIGPanelRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.ChangeColspanFormEffect;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a flexible table (mig layout like)
 */
@Reflectable
public class MIGLayoutFormItem extends LayoutFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private FlexTable table = new FlexTable() {
        @Override
        public boolean remove(Widget widget) {
            if (widget instanceof FBFormItem) {
                MIGLayoutFormItem.super.remove((FBFormItem) widget);
            }
            return super.remove(widget);
        }
    };
    
    private Integer borderWidth = 1;
    private Integer cellpadding = null;
    private Integer cellspacing = null;
    private Integer rows = 1;
    private Integer columns = 1;
    private String title = null;

    public MIGLayoutFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public MIGLayoutFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        table.setBorderWidth(this.borderWidth);
        table.insertRow(table.getRowCount());
        table.addCell(0);
        add(table);
        setSize("90px", "90px");
        table.setSize(getWidth(), getHeight());
    }
    
    @Override
    public void replacePhantom(FBFormItem item) {
        if (!item.hasEffectOfType(ChangeColspanFormEffect.class)) {
            item.addEffect(new ChangeColspanFormEffect());
        }
        boolean found = false;
        int row = 0, column = 0;
        while (row < table.getRowCount()) {
            for (column = 0; column < table.getCellCount(row) && !found; column++) {
                if (isPhantom(table.getWidget(row, column))) {
                    found= true;
                    break;
                }
            }
            if (found) break; else row++;
        }
        if (found) {
            int index = (row * table.getCellCount(row)) + column;
            if (super.size() > index) { 
                super.insert(index, item);
            } else {
                super.add(item);
            }
            table.setWidget(row, column, null);
            table.setWidget(row, column, item);
        } else {
            add(item);
        }

    }
    
    @Override
    public boolean add(FBFormItem child) {
        if (!child.hasEffectOfType(ChangeColspanFormEffect.class)) {
            child.addEffect(new ChangeColspanFormEffect());
        }
        boolean added = false;
        for (int i = 0; i < table.getRowCount() && !added; i++) {
            for (int j = 0; j < table.getCellCount(i) && !added; j++) {
                if (table.getWidget(i, j) == null || isWhiteSpace(table.getWidget(i, j))) {
                    added = true;
                    int index = (i * table.getCellCount(i)) + j;
                    if (super.size() > index) { 
                        super.insert(index-1, child);
                    } else {
                        super.add(child);
                    }
                    table.addCell(i);
                    table.setWidget(i, j, child);
                    break;
                }
            }
        }
        if (!added) {
            bus.fireEvent(new NotificationEvent(NotificationEvent.Level.WARN, i18n.TableFull()));
            return false;
        }
        return true;
    }

    @Override
    public void add(PhantomPanel phantom, int x, int y) {
        int row = 0, column = 0;
        boolean found = false;
        while (row < table.getRowCount() && !found) {
            for (column = 0; column < table.getCellCount(row) && !found; column++) {
                Element cellElement = table.getCellFormatter().getElement(row, column);
                if (x > cellElement.getAbsoluteLeft() && x < cellElement.getAbsoluteRight() &&
                    y > cellElement.getAbsoluteTop() && y < cellElement.getAbsoluteBottom() &&
                    (table.getWidget(row, column) == null || isWhiteSpace(table.getWidget(row, column)) || isPhantom(table.getWidget(row, column)))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                row++;
            }
        }
        if (found && !isPhantom(table.getWidget(row, column))) {
            table.setWidget(row, column, phantom);
        }
    }

    @Override
    public HasWidgets getPanel() {
        return table;
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("borderWidth", this.borderWidth);
        map.put("cellpadding", this.cellpadding);
        map.put("cellspacing", this.cellspacing);
        map.put("height", getHeight());
        map.put("width", getWidth());
        map.put("title", this.title);
        map.put("rows", this.rows);
        map.put("columns", this.columns);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.borderWidth = extractInt(asPropertiesMap.get("borderWidth"));
        this.cellpadding = extractInt(asPropertiesMap.get("cellpadding"));
        this.cellspacing = extractInt(asPropertiesMap.get("cellspacing"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.title = extractString(asPropertiesMap.get("title"));
        this.rows = extractInt(asPropertiesMap.get("rows"));
        this.columns = extractInt(asPropertiesMap.get("columns"));
        populate(this.table);
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        MIGPanelRepresentation rep = super.getRepresentation(new MIGPanelRepresentation());
        rep.setRows(this.rows);
        rep.setBorderWidth(this.borderWidth);
        rep.setCellPadding(this.cellpadding);
        rep.setCellSpacing(this.cellspacing);
        rep.setTitle(this.title);
        for (int r = 0; r < table.getRowCount(); r++) {
            for (int c = 0; c < table.getCellCount(r); c++) {
                Widget widget = table.getWidget(r, c);
                int colspan = table.getFlexCellFormatter().getColSpan(r, c);
                int rowspan = table.getFlexCellFormatter().getRowSpan(r, c);
                if (widget != null && widget instanceof FBFormItem) {
                    FBFormItem item = (FBFormItem) widget;
                    FormItemRepresentation subRep = item.getRepresentation();
                    rep.setElement(r, c, subRep, colspan, rowspan);
                }
            }
        }
        return rep;
    }

    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof MIGPanelRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "MIGPanelRepresentation"));
        }
        super.populate(rep);
        MIGPanelRepresentation mprep = (MIGPanelRepresentation) rep;
        this.rows = mprep.getRows();
        this.borderWidth = mprep.getBorderWidth();
        this.cellpadding = mprep.getCellPadding();
        this.cellspacing = mprep.getCellSpacing();
        populate(this.table);
        this.table.clear();
        super.getItems().clear();
        if (mprep.getWidth() != null) {
            setWidth(mprep.getWidth());
        }
        if (mprep.getHeight() != null) {
            setHeight(mprep.getHeight());
        }
        if (mprep.getElements() != null) {
            for (int rowindex = 0; rowindex < mprep.getElements().size(); rowindex++) {
                List<FormItemRepresentation> row = mprep.getElements().get(rowindex);
                if(row != null) {
                    for (int cellindex = 0; cellindex < row.size(); cellindex++) {
                        FormItemRepresentation cell = row.get(cellindex);
                        FBFormItem subItem = super.createItem(cell);
                        this.table.setWidget(rowindex, cellindex, subItem);
                        int colspan = mprep.getColspan(rowindex, cellindex);
                        int rowspan = mprep.getRowspan(rowindex, cellindex);
                        if (colspan > 1) {
                            this.table.getFlexCellFormatter().setColSpan(rowindex, cellindex, colspan);
                        }
                        if (rowspan > 1) {
                            this.table.getFlexCellFormatter().setRowSpan(rowindex, cellindex, rowspan);
                        }
                        super.add(subItem);
                    }
                }
            }
        }
    }
    
    private void populate(FlexTable grid) {
        if (this.borderWidth != null && this.borderWidth > 0) {
            grid.setBorderWidth(this.borderWidth);
        }
        if (this.cellpadding != null && this.cellpadding >= 0) {
            grid.setCellPadding(this.cellpadding);
        }
        if (this.cellspacing != null && this.cellspacing >= 0) {
            grid.setCellSpacing(this.cellspacing);
        }
        if (getHeight() != null) {
            grid.setHeight(getHeight());
        }
        if (getWidth() != null) {
            grid.setWidth(getWidth());
        }
        if (this.title != null) {
            grid.setTitle(this.title);
        }
        if (this.rows != null && this.rows > 0) {
            while (this.rows > grid.getRowCount()) {
                grid.insertRow(grid.getRowCount());
                int columnCount = 0;
                FlexCellFormatter formatter = grid.getFlexCellFormatter();
                for (int cell = 0; cell < grid.getCellCount(grid.getRowCount() - 1); cell++) {
                    columnCount += formatter.getColSpan(grid.getRowCount() - 1, cell);
                }
                while (this.columns > columnCount) {
                    grid.addCell(grid.getRowCount() - 1); //at least one cell per column. Modified later by colspans
                    columnCount++;
                }
            }
            while (this.rows < grid.getRowCount()) {
                grid.removeRow(grid.getRowCount() - 1);
            }
        }
        if (this.columns != null && this.columns > 0) {
            for (int row = 0; row < grid.getRowCount(); row++) {
                int columnCount = 0;
                FlexCellFormatter formatter = grid.getFlexCellFormatter();
                for (int cell = 0; cell < grid.getCellCount(row); cell++) {
                    columnCount += formatter.getColSpan(row, cell);
                }
                while (this.columns > columnCount) {
                    grid.addCell(row);
                    columnCount++;
                }
                while (this.columns < columnCount) {
                    int cellCount = grid.getCellCount(row);
                    if (cellCount > 0) {
                        int cellColumns = formatter.getColSpan(row, cellCount - 1);
                        if (cellColumns > 1 && columnCount - cellColumns >= this.columns) {
                            grid.removeCell(row, cellCount - 1);
                        } else {
                            grid.removeCell(row, cellCount - 1);
                        }
                    }
                    columnCount--;
                }
            }
        }
    }
    
    @Override
    public FBFormItem cloneItem() {
        MIGLayoutFormItem clone = super.cloneItem(new MIGLayoutFormItem());
        clone.borderWidth = this.borderWidth;
        clone.cellpadding = this.cellpadding;
        clone.cellspacing = this.cellspacing;
        clone.rows = this.rows;
        clone.title = this.title;
        clone.populate(clone.table);
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int column = 0; column < table.getCellCount(row); column++) {
                FBFormItem item = (FBFormItem) this.table.getWidget(row, column);
                if (item != null) {
                    clone.table.addCell(row);
                    int colspan = table.getFlexCellFormatter().getColSpan(row, column);
                    clone.table.getFlexCellFormatter().setColSpan(row, column, colspan);
                    clone.table.setWidget(row, column, item.cloneItem());
                }
            }
        }
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        FlexTable ft = new FlexTable();
        populate(ft);
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int column = 0; column < table.getCellCount(row); column++) {
                FBFormItem item = (FBFormItem) this.table.getWidget(row, column);
                if (item != null) {
                    ft.addCell(row);
                    int colspan = table.getFlexCellFormatter().getColSpan(row, column);
                    ft.getFlexCellFormatter().setColSpan(row, column, colspan);
                    ft.setWidget(row, column, item.cloneDisplay(data));
                }
            }
        }
        super.populateActions(ft.getElement());
        return ft;
    }

    protected boolean isPhantom(Widget widget) {
        return widget != null && widget instanceof PhantomPanel;
    }

    public void setSpan(FBFormItem item, Integer colspan, Integer rowspan) {
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getCellCount(row); col++) {
                Widget widget = table.getWidget(row, col);
                if (widget != null && widget.equals(item)) {
                    if (colspan != null && colspan > 0) {
                        table.getFlexCellFormatter().setColSpan(row, col, colspan);
                    }
                    if (rowspan != null && rowspan > 0) {
                        table.getFlexCellFormatter().setRowSpan(row, col, rowspan);
                    }
                    break;
                }
            }
        }
    }
    
    public int getColSpan(FBFormItem item) {
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getCellCount(row); col++) {
                Widget widget = table.getWidget(row, col);
                if (widget != null && widget.equals(item)) {
                    int colSpan = table.getFlexCellFormatter().getColSpan(row, col);
                    if (colSpan <= 0) colSpan = 1;
                    return colSpan;
                }
            }
        }
        return 1;
    }
    
    public int getRowSpan(FBFormItem item) {
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getCellCount(row); col++) {
                Widget widget = table.getWidget(row, col);
                if (widget != null && widget.equals(item)) {
                    int rowSpan = table.getFlexCellFormatter().getRowSpan(row, col);
                    if (rowSpan <= 0) rowSpan = 1;
                    return rowSpan;
                }
            }
        }
        return 1;
    }
}
