/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.backend.util;

import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Template value provider backed by a Decision Table
 */
public class GuidedDTTemplateDataProvider
    implements
    TemplateDataProvider {

    private Map<String, DTCellValue52> templateKeysToValueMap = new HashMap<String, DTCellValue52>();

    public GuidedDTTemplateDataProvider(List<BaseColumn> columns,
                                        List<DTCellValue52> rowData) {
        if ( columns == null ) {
            throw new NullPointerException( "columns cannot be null" );
        }
        if ( rowData == null ) {
            throw new NullPointerException( "rowData cannot be null" );
        }

        if ( rowData.size() != columns.size() ) {
            throw new IllegalArgumentException( "rowData contains a different number of columns to those provided" );
        }

        //Extract keys and values in constructor rather than on demand for speed
        for ( int index = 0; index < columns.size(); index++ ) {
            BaseColumn column = columns.get( index );
            if ( column instanceof BRLConditionVariableColumn ) {
                BRLConditionVariableColumn brlCondition = (BRLConditionVariableColumn) column;
                templateKeysToValueMap.put( brlCondition.getVarName(),
                                            rowData.get( index ) );

            } else if ( column instanceof BRLActionVariableColumn ) {
                BRLActionVariableColumn brlAction = (BRLActionVariableColumn) column;
                templateKeysToValueMap.put( brlAction.getVarName(),
                                            rowData.get( index ) );

            }
        }
    }

    public String getTemplateKeyValue(String key) {
        if ( templateKeysToValueMap.containsKey( key ) ) {
            return getStringValue( templateKeysToValueMap.get( key ) );
        }
        return "";
    }

    private String getStringValue(DTCellValue52 cell) {
        if ( !cell.hasValue() ) {
            return "";
        }
        return GuidedDTDRLUtilities.convertDTCellValueToString( cell );
    }

}
