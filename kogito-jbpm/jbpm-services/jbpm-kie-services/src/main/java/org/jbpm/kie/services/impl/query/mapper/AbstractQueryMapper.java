/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.query.mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;


public abstract class AbstractQueryMapper<T> {
    
    
    protected abstract T buildInstance(DataSet dataSetResult, int index);

    protected Long getColumnLongValue(DataSet currentDataSet, String columnId, int index){
        DataColumn column = currentDataSet.getColumnById( columnId );
        if (column == null) {
            return null;
        }
        
        Object value = column.getValues().get(index);
        if (value instanceof String) {
            value = Long.parseLong((String) value);
        }
        return value != null ? ((Number) value).longValue() : null;
    }

    protected String getColumnStringValue(DataSet currentDataSet, String columnId, int index){
        DataColumn column = currentDataSet.getColumnById( columnId );
        if (column == null) {
            return null;
        }
        
        Object value = column.getValues().get(index);
        return value != null ? value.toString() : null;
    }

    protected Date getColumnDateValue(DataSet currentDataSet,String columnId, int index){
        DataColumn column = currentDataSet.getColumnById( columnId );
        if (column == null) {
            return null;
        }
        try{
            return (Date) column.getValues().get( index );
        } catch ( Exception e ){

        }
        return null;
    }

    protected int getColumnIntValue(DataSet currentDataSet,String columnId, int index){
        DataColumn column = currentDataSet.getColumnById( columnId );
        if (column == null) {
            return -1;
        }
        
        Object value = column.getValues().get(index);
        return value != null ? ((Number) value).intValue() : -1;
    }
    
    protected Double getColumnDoubleValue(DataSet currentDataSet, String columnId, int index){
        DataColumn column = currentDataSet.getColumnById( columnId );
        if (column == null) {
            return null;
        }
        
        Object value = column.getValues().get(index);
        return value != null ? ((Number) value).doubleValue() : null;
    }
    
    protected Map<String, Object> readVariables(Map<String, String> variablesMap, DataSet currentDataSet, int i) {
        Map<String, Object> variables = new HashMap<String, Object>();
        
        for (Entry<String, String> entry : variablesMap.entrySet()) {
            // now add variable
            String varName = entry.getKey();
            Object varValue;
            if (entry.getValue().equalsIgnoreCase(Long.class.getSimpleName())) {
                varValue = getColumnLongValue(currentDataSet, varName, i);
            } else if (entry.getValue().equalsIgnoreCase(Integer.class.getSimpleName())) {
                varValue = getColumnIntValue(currentDataSet, varName, i);
            } else if (entry.getValue().equalsIgnoreCase(Date.class.getSimpleName())) {
                varValue = getColumnDateValue(currentDataSet, varName, i);
            } else if (entry.getValue().equalsIgnoreCase(Double.class.getSimpleName())) {
                varValue = getColumnDoubleValue(currentDataSet, varName, i);
            } else {
                varValue = getColumnStringValue(currentDataSet, varName, i);
            }
            
            variables.put(varName, varValue);
        }
        
        return variables;
    }
      
}
