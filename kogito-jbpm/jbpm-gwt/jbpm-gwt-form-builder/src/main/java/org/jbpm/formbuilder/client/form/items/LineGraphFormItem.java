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
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.LineGraphRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;
import com.gwtent.reflection.client.Reflectable;

/**
 * UI form item. Represents a line graph
 */
@Reflectable
public class LineGraphFormItem extends FBFormItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private LineChart chart = new LineChart(DataTable.create(), Options.create());
    
    private List<List<String>> dataTableRep = new ArrayList<List<String>>();
    private List<Map.Entry<String, String>> dataStructRep = new ArrayList<Map.Entry<String, String>>();
    
    private String graphTitle;
    private String graphXTitle;
    private String graphYTitle;
    
    public LineGraphFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public LineGraphFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        setSize("200px", "150px");
        chart.setSize("200px", "150px");
        add(chart);
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("width", getWidth());
        map.put("height", getHeight());
        map.put("graphTitle", this.graphTitle);
        map.put("graphXTitle", this.graphXTitle);
        map.put("graphYTitle", this.graphYTitle);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        setWidth(extractString(asPropertiesMap.get("width")));
        setHeight(extractString(asPropertiesMap.get("height")));
        this.graphTitle = extractString(map.get("graphTitle"));
        this.graphXTitle = extractString(map.get("graphXTitle"));
        this.graphYTitle = extractString(map.get("graphYTitle"));
        populate(this.chart);
    }
    
    private int getIntValue(Object obj) {
        return Integer.valueOf(obj.toString());
    }
    
    private void populate(LineChart chart) {
        Options options = Options.create();
        options.setWidth(getOffsetWidth());
        options.setHeight(getOffsetHeight());
        options.setTitle(this.graphTitle);
        options.setTitleX(this.graphXTitle);
        options.setTitleY(this.graphYTitle);
        DataTable dataTable = DataTable.create();
        if (dataStructRep != null) {
            for (Map.Entry<String, String> col : dataStructRep) {
                dataTable.addColumn(ColumnType.valueOf(col.getValue()), col.getKey());
            }
        }
        if (dataTableRep != null) {
            for (List<String> row : dataTableRep) {
                dataTable.setValue(getIntValue(row.get(0)), getIntValue(row.get(1)), row.get(2));
            }
        }
        chart.draw(dataTable, options);  
    }

    @Override
    public FormItemRepresentation getRepresentation() {
        LineGraphRepresentation rep = super.getRepresentation(new LineGraphRepresentation());
        rep.setDataStructure(new ArrayList<Map.Entry<String, String>>(dataStructRep));
        rep.setDataTable(new ArrayList<List<String>>(dataTableRep));
        rep.setGraphTitle(graphTitle);
        rep.setGraphXTitle(graphXTitle);
        rep.setGraphYTitle(graphYTitle);
        return rep;
    }
    
    @Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof LineGraphRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "LineGraphRepresentation"));
        }
        super.populate(rep);
        LineGraphRepresentation grep = (LineGraphRepresentation) rep;
        this.dataStructRep = new ArrayList<Map.Entry<String, String>>(grep.getDataStructure());
        this.dataTableRep = new ArrayList<List<String>>(grep.getDataTable());
        this.graphTitle = grep.getGraphTitle();
        this.graphXTitle = grep.getGraphXTitle();
        this.graphYTitle = grep.getGraphYTitle();
        populate(this.chart);
    }

    @Override
    public FBFormItem cloneItem() {
        LineGraphFormItem item = super.cloneItem(new LineGraphFormItem(getFormEffects()));
        item.chart = (LineChart) cloneDisplay(null);
        item.dataStructRep = new ArrayList<Map.Entry<String, String>>(this.dataStructRep);
        item.dataTableRep = new ArrayList<List<String>>(this.dataTableRep);
        item.graphTitle = this.graphTitle;
        item.graphXTitle = this.graphXTitle;
        item.graphYTitle = this.graphYTitle;
        return item;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> data) {
        LineChart chart = new LineChart();
        populate(chart);
        if (getInput() != null && getInput().getName() != null) {
        	DataTable dataTable = DataTable.create();
        	Object myData = data.get(getInput().getName());
        	populateInput(dataTable, myData);
        	chart.draw(dataTable);
        }
        super.populateActions(chart.getElement());
        return chart;
    }

	private void populateInput(DataTable dataTable, Object myData) {
		if (myData.getClass().isArray()) {
    		Object[] myDataArray = (Object[]) myData;
    		int index = 0;
    		for (Object item : myDataArray) {
    			setRowDataFromInput(dataTable, index, item);
    			index++;
    		}
    	} else if (myData instanceof Collection) {
    		Collection<?> myDataCol = (Collection<?>) myData;
    		int index = 0;
    		for (Object item : myDataCol) {
    			setRowDataFromInput(dataTable, index, item);
    			index++;
    		}
    	} else if (myData instanceof Map) {
    		Map<?, ?> myDataMap = (Map<?, ?>) myData;
    		int index = 0;
    		for (Object item : myDataMap.values()) {
    			setRowDataFromInput(dataTable, index, item);
    			index++;
    		}
    	}
		
	}

	private void setRowDataFromInput(DataTable dataTable, int index, Object item) {
		if (item.getClass().isArray()) {
			Object[] subObjArray = (Object[]) item;
			int columnIndex = 0;
			for (Object subObj : subObjArray) {
				String value = subObj.toString();
				dataTable.setCell(index, columnIndex, value, value, null);
				columnIndex++;
			}
		} else if (item instanceof Collection) {
			Collection<?> subObjCol = (Collection<?>) item;
			int columnIndex = 0;
			for (Object subObj : subObjCol) {
				String value = subObj.toString();
				dataTable.setCell(index, columnIndex, value, value, null);
				columnIndex++;
			}
		} else if (item instanceof Map) {
			Map<?, ?> subObjMap = (Map<?, ?>) item;
			int columnIndex = 0;
			for (Object subObj : subObjMap.values()) {
				String value = subObj.toString();
				dataTable.setCell(index, columnIndex, value, value, null);
				columnIndex++;
			}

		} else {
			String value = item.toString();
			dataTable.setCell(index, 0, value, value, null);
		}
	}

}
