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
package org.jbpm.form.builder.services.model.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;

public class MIGPanelRepresentation extends FormItemRepresentation {

    class Cell {
        private int row;
        private int cellNumber;
        public Cell(int row, int cellNumber) {
            super();
            this.row = row;
            this.cellNumber = cellNumber;
        }

        public Cell() {
        }
        
        public int getRow() {
            return row;
        }
        public void setRow(int row) {
            this.row = row;
        }
        public int getCellNumber() {
            return cellNumber;
        }
        public void setCellNumber(int cellNumber) {
            this.cellNumber = cellNumber;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + cellNumber;
            result = prime * result + row;
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Cell other = (Cell) obj;
            if (cellNumber != other.cellNumber) return false;
            if (row != other.row) return false;
            return true;
        }
    }
    
    private final List<List<FormItemRepresentation>> elements;
    private final Map<Cell, Integer> colspans = new HashMap<Cell, Integer>();
    private final Map<Cell, Integer> rowspans = new HashMap<Cell, Integer>();

    private Integer rows;
    private Integer borderWidth;
    private Integer cellSpacing;
    private Integer cellPadding;
    private String title;
    
    public MIGPanelRepresentation() {
        super("migPanel");
        this.elements = new ArrayList<List<FormItemRepresentation>>();
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Integer getCellSpacing() {
        return cellSpacing;
    }

    public void setCellSpacing(Integer cellSpacing) {
        this.cellSpacing = cellSpacing;
    }

    public Integer getCellPadding() {
        return cellPadding;
    }

    public void setCellPadding(Integer cellPadding) {
        this.cellPadding = cellPadding;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setElement(int rowNumber, int cellNumber, FormItemRepresentation subRep, int colspan, int rowspan) {
        while (this.elements.size() <= rowNumber) {
            this.elements.add(new ArrayList<FormItemRepresentation>());
            this.rows = this.elements.size();
        }
        List<FormItemRepresentation> row = this.elements.get(rowNumber);
        while (row.size() <= cellNumber) {
            row.add(null);
        }
        row.set(cellNumber, subRep);
        this.elements.set(rowNumber, row);
        Cell cell = new Cell(rowNumber, cellNumber);
        this.colspans.put(cell, colspan);
        this.rowspans.put(cell, rowspan);
    }

    public List<List<FormItemRepresentation>> getElements() {
        return this.elements;
    }
    
    public int getColspan(int rowNumber, int cellNumber) {
        Cell index = new Cell(rowNumber, cellNumber);
        Integer colspan = colspans.get(index);
        return colspan == null ? 1 : colspan;
    }
    
    public int getRowspan(int rowNumber, int cellNumber) {
        Cell index = new Cell(rowNumber, cellNumber);
        Integer rowspan = rowspans.get(index);
        return rowspan == null ? 1 : rowspan;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("borderWidth", this.borderWidth);
        data.put("cellPadding", this.cellPadding);
        data.put("cellSpacing", this.cellSpacing);
        data.put("rows", this.rows);
        data.put("title", this.title);
        List<Object> colrowspanList = new ArrayList<Object>();
        List<Cell> cells = new ArrayList<Cell>();
        cells.addAll(colspans.keySet());
        cells.addAll(rowspans.keySet());
        for (Cell key : cells) {
             Integer colspan = colspans.get(key);
             Integer rowspan = rowspans.get(key);
             Map<String, Object> obj = new HashMap<String, Object>();
             obj.put("row", key.getRow());
             obj.put("cellNumber", key.getCellNumber());
             if (colspan != null) {
                 obj.put("colspan", colspan);
             }
             if (rowspan != null) {
                 obj.put("rowspan", rowspan);
             }
             colrowspanList.add(obj);
        }
        data.put("colrowspans", colrowspanList);
        List<List<Map<String, Object>>> mapElements = new ArrayList<List<Map<String, Object>>>();
        if (this.elements != null) {
            for (List<FormItemRepresentation> row : this.elements) {
                List<Map<String, Object>> mapRow = null;
                if (row != null) {
                    mapRow = new ArrayList<Map<String, Object>>();
                    for (FormItemRepresentation cell : row) {
                        mapRow.add(cell == null ? null : cell.getDataMap());
                    }
                }
                mapElements.add(mapRow);
            }
        }
        data.put("elements", mapElements);
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.borderWidth = data.get("borderWidth") == null ? null : ((Number) data.get("borderWidth")).intValue();
        this.cellPadding = data.get("cellPadding") == null ? null : ((Number) data.get("cellPadding")).intValue();
        this.cellSpacing = data.get("cellSpacing") == null ? null : ((Number) data.get("cellSpacing")).intValue();
        this.rows = data.get("rows") == null ? null : ((Number) data.get("rows")).intValue();
        this.title = (String) data.get("title");
        this.elements.clear();
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        List<List<Map<String, Object>>> mapElements = (List<List<Map<String, Object>>>) data.get("elements");
        if (mapElements != null) {
            for (List<Map<String, Object>> mapRow : mapElements) {
                List<FormItemRepresentation> row = new ArrayList<FormItemRepresentation>();
                if (mapRow != null) {
                    for (Map<String, Object> mapCell : mapRow) {
                        row.add((FormItemRepresentation) decoder.decode(mapCell));
                    }
                }
                this.elements.add(row);
            }
        }
        this.rowspans.clear();
        this.colspans.clear();
        Object colrowspanList = data.get("colrowspans");
        if (colrowspanList != null) {
            List<Object> colrow = (List<Object>) colrowspanList;
            for (Object obj : colrow) {
                Map<String, Object> cell = (Map<String, Object>) obj;
                Integer row = cell.get("row") == null ? null : ((Number) cell.get("row")).intValue();
                Integer cellNumber = cell.get("cellNumber") == null ? null : ((Number) cell.get("cellNumber")).intValue();
                Integer colspan = cell.get("colspan") == null ? null : ((Number) cell.get("colspan")).intValue();
                Integer rowspan = cell.get("rowspan") == null ? null : ((Number) cell.get("rowspan")).intValue();
                if (colspan != null && row != null && cellNumber != null) {
                    colspans.put(new Cell(row, cellNumber), colspan);
                }
                if (rowspan != null && row != null && cellNumber != null) {
                    rowspans.put(new Cell(row, cellNumber), rowspan);
                }

            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof MIGPanelRepresentation)) return false;
        MIGPanelRepresentation other = (MIGPanelRepresentation) obj;
        boolean equals = (this.rows == null && other.rows == null) || (this.rows != null && this.rows.equals(other.rows));
        if (!equals) return equals;
        equals = (this.title == null && other.title == null) || (this.title != null && this.title.equals(other.title));
        if (!equals) return equals;
        equals = (this.borderWidth == null && other.borderWidth == null) || 
            (this.borderWidth != null && this.borderWidth.equals(other.borderWidth));
        if (!equals) return equals;
        equals = (this.cellPadding == null && other.cellPadding == null) || 
            (this.cellPadding != null && this.cellPadding.equals(other.cellPadding));
        if (!equals) return equals;
        equals = (this.cellSpacing == null && other.cellSpacing == null) || 
            (this.cellSpacing != null && this.cellSpacing.equals(other.cellSpacing));
        if (!equals) return equals;
        equals = (this.elements == null && other.elements == null) || (this.elements != null && this.elements.equals(other.elements));
        if (!equals) return equals;
        equals = (this.rowspans == null && other.rowspans == null) || 
            (this.rowspans != null && this.rowspans.entrySet().equals(other.rowspans.entrySet()));
        if (!equals) return equals;
        equals = (this.colspans == null && other.colspans == null) || 
            (this.colspans != null && this.colspans.entrySet().equals(other.colspans.entrySet()));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.rows == null ? 0 : this.rows.hashCode();
        result = 37 * result + aux;
        aux = this.title == null ? 0 : this.title.hashCode();
        result = 37 * result + aux;
        aux = this.borderWidth == null ? 0 : this.borderWidth.hashCode();
        result = 37 * result + aux;
        aux = this.cellPadding == null ? 0 : this.cellPadding.hashCode();
        result = 37 * result + aux;
        aux = this.cellSpacing == null ? 0 : this.cellSpacing.hashCode();
        result = 37 * result + aux;
        aux = this.elements == null ? 0 : this.elements.hashCode();
        result = 37 * result + aux;
        aux = this.colspans == null ? 0 : this.colspans.hashCode();
        result = 37 * result + aux;
        aux = this.rowspans == null ? 0 : this.rowspans.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
