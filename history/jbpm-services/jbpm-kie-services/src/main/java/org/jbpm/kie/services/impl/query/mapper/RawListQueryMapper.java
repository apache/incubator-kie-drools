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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.jbpm.services.api.query.QueryResultMapper;

/**
 * Dedicated mapper that transforms DataSet to List of lists where each 
 * nested list represents all values of given row.
 *
 */
public class RawListQueryMapper extends AbstractQueryMapper<List<Object>> implements QueryResultMapper<List<List<Object>>> {
    
    private static final long serialVersionUID = 5935133069234696714L;

    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead 
     */
    public RawListQueryMapper() {
    }
    
    /**
     * Default access to get instance of the mapper
     * @return
     */
    public static RawListQueryMapper get() {
        return new RawListQueryMapper();
    }

    @Override
    public List<List<Object>> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<List<Object>> mappedResult = new ArrayList<List<Object>>();
            
            if (dataSetResult != null) {
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    List<Object> row = buildInstance(dataSetResult, i);
                    mappedResult.add(row);
                
                }
            }
            
            return mappedResult;
        }
        
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }
    
    protected List<Object> buildInstance(DataSet dataSetResult, int index) {
        List<Object> row = new ArrayList<Object>();
        
        for (DataColumn column : dataSetResult.getColumns()) {
            row.add(dataSetResult.getColumnById(column.getId()).getValues().get(index));
        }
        
        return row;
    }

    @Override
    public String getName() {
        return "RawList";
    }

    @Override
    public Class<?> getType() {
        return List.class;
    }

    @Override
    public QueryResultMapper<List<List<Object>>> forColumnMapping(Map<String, String> columnMapping) {
        return new RawListQueryMapper();
    }

}
