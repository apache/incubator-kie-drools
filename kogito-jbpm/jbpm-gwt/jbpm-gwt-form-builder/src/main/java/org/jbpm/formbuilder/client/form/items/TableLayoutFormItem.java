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
import org.jbpm.formapi.shared.api.items.TableRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a table
 */
@Reflectable
public class TableLayoutFormItem extends LayoutFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private Grid grid = new Grid(1, 1) {
        @Override
        public boolean remove(Widget widget) {
            if (widget instanceof FBFormItem) {
                boolean found = false;
                int row = 0, column = 0;
                while (row < super.getRowCount() && !found) {
                    for (column = 0; column < super.getColumnCount() && !found; ) {
                        Widget cellWidget = super.getWidget(row, column);
                        if (cellWidget != null && cellWidget.equals(widget)) {
                            found = true;
                        }
                        if (found) break; else column++;
                    }
                    if (found) break; else row++;
                }
                if (found) {
                    super.remove(widget);
                    getCellFormatter().getElement(row, column).setInnerHTML("&nbsp");
                }
                return found;
            } else if (widget instanceof PhantomPanel) {
                boolean retval = false;
                int row = 0, column = 0;
                while (row < super.getRowCount() && !retval) {
                    for (column = 0; column < super.getColumnCount() && !retval; column++) {
                        if (super.getWidget(row, column) != null && isPhantom(super.getWidget(row, column))) {
                            getCellFormatter().getElement(row, column).setInnerHTML("&nbsp");
                            break;
                        }
                    }
                    if (retval) break; else row++; 
                }
                if (retval) {
                    super.remove(widget);
                    getCellFormatter().getElement(row, column).setInnerHTML("&nbsp");
                }
                return retval;
            } else {
                return super.remove(widget);
            }
        }
    };
    
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private Integer borderWidth = 1;
    private Integer cellpadding = null;
    private Integer cellspacing = null;
    private Integer columns = 1;
    private Integer rows = 1;
    private String title = null;

    public TableLayoutFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public TableLayoutFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        grid.setBorderWidth(this.borderWidth);
        add(grid);
        setSize("90px", "90px");
        grid.setSize(getWidth(), getHeight());
    }
    
    @Override
    public HasWidgets getPanel() {
        return grid;
    }
    
    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        
        this.borderWidth = extractInt(asPropertiesMap.get("borderWidth"));
        this.cellpadding = extractInt(asPropertiesMap.get("cellpadding"));
        this.cellspacing = extractInt(asPropertiesMap.get("cellspacing"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.title = extractString(asPropertiesMap.get("title"));
        this.columns = extractInt(asPropertiesMap.get("columns"));
        this.rows = extractInt(asPropertiesMap.get("rows"));
        
        populate(this.grid);
    }

    private void populate(Grid grid) {
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
        if (this.columns != null && this.columns > 0) {
            grid.resizeColumns(this.columns);
        }
        if (this.rows != null && this.rows > 0) {
            grid.resizeRows(this.rows);
        }
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
        map.put("columns", this.columns);
        map.put("rows", this.rows);
        return map;
    }
    
    @Override
    public boolean add(FBFormItem child) {
        boolean added = false;
        for (int i = 0; i < grid.getRowCount() && !added; i++) {
            for (int j = 0; j < grid.getColumnCount() && !added; j++) {
                if (grid.getWidget(i, j) == null || isWhiteSpace(grid.getWidget(i, j))) {
                    added = true;
                    int index = (i * grid.getColumnCount()) + j;
                    if (super.size() > index) { 
                        super.insert(index-1, child);
                    } else {
                        super.add(child);
                    }
                    grid.setWidget(i, j, child);
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
        while (row < grid.getRowCount() && !found) {
            for (column = 0; column < grid.getColumnCount() && !found; column++) {
                Element cellElement = grid.getCellFormatter().getElement(row, column);
                if (x > cellElement.getAbsoluteLeft() && x < cellElement.getAbsoluteRight() &&
                    y > cellElement.getAbsoluteTop() && y < cellElement.getAbsoluteBottom() &&
                    (grid.getWidget(row, column) == null || 
                            isWhiteSpace(grid.getWidget(row, column)) || 
                            isPhantom(grid.getWidget(row, column)))
                    ) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                row++;
            }
        }
        if (found && !isPhantom(grid.getWidget(row, column))) {
            grid.setWidget(row, column, phantom);
        }
    }
    
    public int getRowForYCoordinate(int y) {
        for (int row = 0; row < grid.getRowCount(); row++) {
            Element rowElement = grid.getRowFormatter().getElement(row);
            if (y > rowElement.getAbsoluteTop() && y < rowElement.getAbsoluteBottom()) {
                return row;
            }
        }
        return -1;
    }
    
    public List<FBFormItem> removeRow(int rowNumber) {
        List<FBFormItem> retval = null;
        if (rowNumber < grid.getRowCount()) {
            retval = new ArrayList<FBFormItem>(grid.getColumnCount());
            for (int column = 0; column < grid.getColumnCount(); column++) {
                Widget widget = grid.getWidget(rowNumber, column);
                if (widget instanceof FBFormItem) {
                    retval.add((FBFormItem) widget);
                } else {
                    retval.add(null);
                }
                grid.remove(widget);
            }
            grid.removeRow(rowNumber);
            this.rows--;
        }
        return retval;
    }
    
    public void addRow(int beforeRowNumber) {
        grid.insertRow(beforeRowNumber);
        this.rows++;
    }
    
    public void insertRowElements(int rowNumber, List<FBFormItem> rowItems) {
        FBFormItem prevItem = null;
        for (int i = rowNumber - 1; i > 0 && prevItem == null; i--) {
            for (int j = grid.getColumnCount(); j > 0 && prevItem == null; j--) {
                Widget widget = grid.getWidget(i, j);
                if (widget != null && widget instanceof FBFormItem) {
                    prevItem = (FBFormItem) widget;
                }
            }
        }
        int index = prevItem == null ? -1 : super.getItems().indexOf(prevItem);
        int colNumber = 0;
        for (FBFormItem newItem : rowItems) {
            if (newItem != null) {
                if (index < 0) {
                    super.add(newItem);
                } else {
                    super.insert(index, newItem);
                }
                index++;
                grid.setWidget(rowNumber, colNumber, newItem);
            }
            colNumber++;
        }
    }
    
    public void insertColumnElements(int colNumber, List<FBFormItem> columnItems) {
        int rowNumber = 0;
        for (FBFormItem newItem : columnItems) {
            FBFormItem prevItem = null;
            for (int i = grid.getRowCount(); i > 0 && prevItem == null; i--) {
                for (int j = colNumber - 1; j > 0 && prevItem == null; j--) {
                    Widget widget = grid.getWidget(i, j);
                    if (widget != null && widget instanceof FBFormItem) {
                        prevItem = (FBFormItem) widget;
                    }
                }
            }
            int index = prevItem == null ? -1 : super.getItems().indexOf(prevItem);
            if (newItem != null) {
                if (index < 0) {
                    super.add(newItem);
                } else {
                    super.insert(index, newItem);
                }
                grid.setWidget(rowNumber, colNumber, newItem);
            }
            rowNumber++;
        }
    }
    
    public int getColumnForXCoordinate(int x) {
        if (grid.getRowCount() > 0) {
            for (int column = 0; column < grid.getColumnCount(); column++) {
                Element cellElement = grid.getCellFormatter().getElement(0, column);
                if (x > cellElement.getAbsoluteLeft() && x < cellElement.getAbsoluteRight()) {
                    return column;
                }
            }
        }
        return -1;
    }
    
    public void addColumn(int beforeColumnNumber) {
        if (beforeColumnNumber < grid.getColumnCount()) {
            this.columns++;
            grid.resizeColumns(grid.getColumnCount() + 1);
            for (int row = 0; row < grid.getRowCount(); row++) {
                for (int column = grid.getColumnCount() - 1; column > beforeColumnNumber && column < grid.getColumnCount(); column++) {
                    if (column > 0) {
                        Widget widget = grid.getWidget(row, column - 1);
                        if (widget != null) {
                            grid.setWidget(row, column, widget);
                            if (grid.getWidget(row, column - 1) != null) {
                                grid.getWidget(row, column - 1).getElement().getParentElement().setInnerHTML("&nbsp;");
                            }
                        }
                    }
                }
            }
        }
    }
    
    public List<FBFormItem> removeColumn(int columnNumber) {
        List<FBFormItem> retval = null;
        if (columnNumber < grid.getColumnCount()) {
            retval = new ArrayList<FBFormItem>(grid.getRowCount());
            for (int row = 0; row < grid.getRowCount(); row++) {
                for (int column = columnNumber + 1; column < grid.getColumnCount(); column++) {
                    Widget widget = grid.getWidget(row, column);
                    if (column == columnNumber + 1 && widget instanceof FBFormItem) {
                        retval.add((FBFormItem) widget);
                    } else if (column == columnNumber + 1) {
                        retval.add(null);
                    }
                    grid.setWidget(row, column - 1, widget);
                    remove(widget);
                }
            }
            grid.resizeColumns(grid.getColumnCount() - 1);
            this.columns--;
        }
        return retval;
    }
    
    protected boolean isPhantom(Widget widget) {
        return widget != null && widget instanceof PhantomPanel;
    }
    
    @Override
    public void replacePhantom(FBFormItem item) {
        boolean found = false;
        int row = 0, column = 0;
        while (row < grid.getRowCount()) {
            for (column = 0; column < grid.getColumnCount() && !found; column++) {
                if (isPhantom(grid.getWidget(row, column))) {
                    found= true;
                    break;
                }
            }
            if (found) break; else row++;
        }
        if (found) {
            int index = (row * grid.getColumnCount()) + column;
            if (super.size() > index) { 
                super.insert(index, item);
            } else {
                super.add(item);
            }
            grid.setWidget(row, column, null);
            grid.setWidget(row, column, item);
        } else {
            add(item);
        }
    }
    
    @Override
    public boolean removeItem(FBFormItem item) {
        return false;
    }
    
    @Override
    public FormItemRepresentation getRepresentation() {
        TableRepresentation rep = super.getRepresentation(new TableRepresentation());
        rep.setRows(this.rows);
        rep.setColumns(this.columns);
        rep.setBorderWidth(this.borderWidth);
        rep.setCellPadding(this.cellpadding);
        rep.setCellSpacing(this.cellspacing);
        for (int index = 0; index < this.columns * this.rows; index++) {
            int column = index%this.columns;
            int row = index/this.columns;
            Widget widget = grid.getWidget(row, column);
            if (widget != null && widget instanceof FBFormItem) {
                FBFormItem item = (FBFormItem) widget;
                FormItemRepresentation subRep = item.getRepresentation();
                rep.setElement(row, column, subRep);
            }
        }
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof TableRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "TableRepresentation"));
        }
        super.populate(rep);
        TableRepresentation trep = (TableRepresentation) rep;
        this.rows = trep.getRows();
        this.columns = trep.getColumns();
        this.borderWidth = trep.getBorderWidth();
        this.cellpadding = trep.getCellPadding();
        this.cellspacing = trep.getCellSpacing();
        populate(this.grid);
        this.grid.clear();
        super.getItems().clear();
        if (trep.getWidth() != null) {
            setWidth(trep.getWidth());
        }
        if (trep.getHeight() != null) {
            setHeight(trep.getHeight());
        }
        if (trep.getElements() != null) {
            for (int rowindex = 0; rowindex < trep.getElements().size(); rowindex++) {
                List<FormItemRepresentation> row = trep.getElements().get(rowindex);
                if(row != null) {
                    for (int cellindex = 0; cellindex < row.size(); cellindex++) {
                        FormItemRepresentation cell = row.get(cellindex);
                        FBFormItem subItem = super.createItem(cell);
                        this.grid.setWidget(rowindex, cellindex, subItem);
                        super.add(subItem);
                    }
                }
            }
        }
    }
    
    private void addItemToCollection(FBFormItem item) {
        super.add(item);
    }
    
    @Override
    public FBFormItem cloneItem() {
        TableLayoutFormItem clone = new TableLayoutFormItem(getFormEffects());
        clone.borderWidth = this.borderWidth;
        clone.cellpadding = this.cellpadding;
        clone.cellspacing = this.cellspacing;
        clone.columns = this.columns;
        clone.setHeight(getHeight());
        clone.rows = this.rows;
        clone.title = this.title;
        clone.setWidth(getWidth());
        clone.populate(clone.grid);
        for (int index = 0; index < clone.columns * clone.rows; index++) {
            int column = index%clone.columns;
            int row = index/clone.columns;
            FBFormItem item = (FBFormItem) this.grid.getWidget(row, column);
            if (item != null) {
                clone.grid.setWidget(row, column, item.cloneItem());
            }
        }
        List<FBFormItem> items = this.getItems();
        if (items != null) {
            for (FBFormItem item : items) {
                clone.addItemToCollection(item);
            }
        }
        return clone;
    }
    
    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        Grid g = new Grid(this.rows, this.columns);
        populate(g);
        for (int index = 0; index < this.columns * this.rows; index++) {
            int column = index%this.columns;
            int row = index/this.columns;
            FBFormItem item = (FBFormItem) this.grid.getWidget(row, column);
            if (item != null) {
                g.setWidget(row, column, item.cloneDisplay(data));
            }
        }
        super.populateActions(g.getElement());
        return g;
    }
}
