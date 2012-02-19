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
package org.jbpm.formapi.shared.api.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class TableRepresentation extends FormItemRepresentation {

    private final List<List<FormItemRepresentation>> elements;
    
    private Integer rows = 0;
    private Integer columns = 0;

    private Integer borderWidth;
    private Integer cellPadding;
    private Integer cellSpacing;
    
    public TableRepresentation() {
        super("table");
        this.elements = new ArrayList<List<FormItemRepresentation>>();
    }
    
    public void setRows(Integer rows) {
        this.rows = rows;
        checkRoom();
    }
    
    public void setColumns(Integer columns) {
        this.columns = columns;
        checkRoom();
    }
    
    private void checkRoom() {
        if (this.rows > 0 && this.columns > 0) {
            for (int index = 0; index < this.rows; index++) {
                List<FormItemRepresentation> row = new ArrayList<FormItemRepresentation>(this.columns);
                for (int subIndex = 0; subIndex < this.columns; subIndex++) {
                    row.add(null);
                }
                this.elements.add(row);
            }
        }
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Integer getCellPadding() {
        return cellPadding;
    }

    public void setCellPadding(Integer cellPadding) {
        this.cellPadding = cellPadding;
    }

    public Integer getCellSpacing() {
        return cellSpacing;
    }

    public void setCellSpacing(Integer cellSpacing) {
        this.cellSpacing = cellSpacing;
    }

    public List<List<FormItemRepresentation>> getElements() {
        return elements;
    }
    
    public void setElement(int rowNumber, int colNumber, FormItemRepresentation subRep) {
        while (this.elements.size() <= rowNumber) {
            this.elements.add(new ArrayList<FormItemRepresentation>());
            this.rows = this.elements.size();
        }
        List<FormItemRepresentation> row = this.elements.get(rowNumber);
        while (row.size() <= colNumber) {
            row.add(null);
            this.columns = row.size();
        }
        row.set(colNumber, subRep);
        this.elements.set(rowNumber, row);
    }

    public Integer getRows() {
        return rows;
    }

    public Integer getColumns() {
        return columns;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("borderWidth", this.borderWidth);
        data.put("cellPadding", this.cellPadding);
        data.put("cellSpacing", this.cellSpacing);
        data.put("rows", this.rows);
        data.put("columns", this.columns);
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
        this.columns = data.get("columns") == null ? null : ((Number) data.get("columns")).intValue();
        this.rows = data.get("rows") == null ? null : ((Number) data.get("rows")).intValue();
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
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof TableRepresentation)) return false;
        TableRepresentation other = (TableRepresentation) obj;
        boolean equals = (this.rows == null && other.rows == null) || (this.rows != null && this.rows.equals(other.rows));
        if (!equals) return equals;
        equals = (this.columns == null && other.columns == null) || (this.columns != null && this.columns.equals(other.columns));
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
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.rows == null ? 0 : this.rows.hashCode();
        result = 37 * result + aux;
        aux = this.columns == null ? 0 : this.columns.hashCode();
        result = 37 * result + aux;
        aux = this.borderWidth == null ? 0 : this.borderWidth.hashCode();
        result = 37 * result + aux;
        aux = this.cellPadding == null ? 0 : this.cellPadding.hashCode();
        result = 37 * result + aux;
        aux = this.cellSpacing == null ? 0 : this.cellSpacing.hashCode();
        result = 37 * result + aux;
        aux = this.elements == null ? 0 : this.elements.hashCode();
        result = 37 * result + aux;
        return result;
    }

}
