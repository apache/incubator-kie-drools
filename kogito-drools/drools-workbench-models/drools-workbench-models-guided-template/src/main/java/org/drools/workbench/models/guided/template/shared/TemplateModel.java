/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.template.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.visitors.RuleModelVisitor;

public class TemplateModel
        extends RuleModel {

    public static final String ID_COLUMN_NAME = "__ID_KOL_NAME__";
    private Map<String, List<String>> table = new HashMap<String, List<String>>();

    //Pre-6.0 this was a long, however private long fields cannot be set using JSNI (as used by Errai-marshalling).
    //It has consequentially been set to an int. This will only cause problems for tables >2 billion rows (unlikely!).
    //XStream serializes the field as <idCol>value</idCol> so there should be no serialization issues.
    private int idCol = 0;

    private int rowsCount = 0;

    /**
     * Append a row of data
     * @param rowId
     * @param row
     * @return
     */
    public String addRow( String rowId,
                          String[] row ) {
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        if ( row.length != vars.size() - 1 ) {
            throw new IllegalArgumentException( "Invalid numbers of columns: " + row.length + " expected: "
                                                        + (vars.size() - 1) );
        }
        if ( rowId == null || rowId.length() == 0 ) {
            rowId = getNewIdColValue();
        }
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            List<String> list = table.get( entry.getKey().getVarName() );
            if ( list == null ) {
                list = new ArrayList<String>();
                table.put( entry.getKey().getVarName(),
                           list );
            }
            if ( rowsCount != list.size() ) {
                throw new IllegalArgumentException( "invalid list size for " + entry.getKey() + ", expected: "
                                                            + rowsCount + " was: " + list.size() );
            }
            if ( ID_COLUMN_NAME.equals( entry.getKey().getVarName() ) ) {
                list.add( rowId );
            } else {
                list.add( row[ entry.getValue() ] );
            }
        }
        rowsCount++;
        return rowId;
    }

    /**
     * Add a row of data at the specified index
     * @param index
     * @param row
     * @return
     */
    public String addRow( int index,
                          String[] row ) {
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        if ( row.length != vars.size() - 1 ) {
            throw new IllegalArgumentException( "Invalid numbers of columns: " + row.length + " expected: "
                                                        + vars.size() );
        }
        String rowId = getNewIdColValue();
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            List<String> list = table.get( entry.getKey().getVarName() );
            if ( list == null ) {
                list = new ArrayList<String>();
                table.put( entry.getKey().getVarName(),
                           list );
            }
            if ( rowsCount != list.size() ) {
                throw new IllegalArgumentException( "invalid list size for " + entry.getKey() + ", expected: "
                                                            + rowsCount + " was: " + list.size() );
            }
            if ( ID_COLUMN_NAME.equals( entry.getKey().getVarName() ) ) {
                list.add( index,
                          rowId );
            } else {
                list.add( index,
                          row[ entry.getValue() ] );
            }
        }
        rowsCount++;
        return rowId;
    }

    public String addRow( String[] row ) {
        return addRow( null,
                       row );
    }

    public void clearRows() {
        if ( rowsCount > 0 ) {
            for ( List<String> col : table.values() ) {
                col.clear();
            }
            rowsCount = 0;
        }
    }

    public int getColsCount() {
        return getInterpolationVariables().size() - 1;
    }

    private Map<InterpolationVariable, Integer> getInterpolationVariables() {
        Map<InterpolationVariable, Integer> result = new HashMap<InterpolationVariable, Integer>();
        new RuleModelVisitor( result ).visit( this );

        InterpolationVariable id = new InterpolationVariable( ID_COLUMN_NAME,
                                                              DataType.TYPE_NUMERIC_LONG );
        result.put( id,
                    result.size() );
        return result;
    }

    public InterpolationVariable[] getInterpolationVariablesList() {
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        InterpolationVariable[] ret = new InterpolationVariable[ vars.size() - 1 ];
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            if ( !ID_COLUMN_NAME.equals( entry.getKey().getVarName() ) ) {
                ret[ entry.getValue() ] = entry.getKey();
            }
        }
        return ret;
    }

    private String getNewIdColValue() {
        idCol++;
        return String.valueOf( idCol );
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public Map<String, List<String>> getTable() {
        return table;
    }

    public String[][] getTableAsArray() {
        if ( rowsCount <= 0 ) {
            return new String[ 0 ][ 0 ];
        }

        //Refresh against interpolation variables
        putInSync();

        String[][] ret = new String[ rowsCount ][ table.size() - 1 ];
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();
        for ( Map.Entry<InterpolationVariable, Integer> entry : vars.entrySet() ) {
            InterpolationVariable var = entry.getKey();
            String varName = var.getVarName();
            if ( ID_COLUMN_NAME.equals( varName ) ) {
                continue;
            }
            int idx = entry.getValue();
            for ( int row = 0; row < rowsCount; row++ ) {
                ret[ row ][ idx ] = table.get( varName ).get( row );
            }
        }
        return ret;
    }

    public void putInSync() {
        //Nothing to synchronize
        if ( table == null ) {
            return;
        }

        //vars.KeySet is a set of InterpolationVariable, whereas table.keySet is a set of String
        Map<InterpolationVariable, Integer> vars = getInterpolationVariables();

        // Retain all columns in table that are in vars
        Set<String> requiredVars = new HashSet<String>();
        for ( InterpolationVariable var : vars.keySet() ) {
            if ( table.containsKey( var.getVarName() ) ) {
                requiredVars.add( var.getVarName() );
            }
        }
        table.keySet().retainAll( requiredVars );

        // Add empty columns for all vars that are not in table
        List<String> aux = new ArrayList<String>( rowsCount );
        for ( int i = 0; i < rowsCount; i++ ) {
            aux.add( "" );
        }
        for ( InterpolationVariable var : vars.keySet() ) {
            if ( !requiredVars.contains( var.getVarName() ) ) {
                table.put( var.getVarName(),
                           new ArrayList<String>( aux ) );
            }
        }

    }

    public void removeRow( int row ) {
        if ( row >= 0 && row < rowsCount ) {
            for ( List<String> col : table.values() ) {
                col.remove( row );
            }
            rowsCount--;
        } else {
            throw new ArrayIndexOutOfBoundsException( row );
        }
    }

    public boolean removeRowById( String rowId ) {
        int idx = table.get( ID_COLUMN_NAME ).indexOf( rowId );
        if ( idx != -1 ) {
            for ( List<String> col : table.values() ) {
                col.remove( idx );
            }
            rowsCount--;
        }
        return idx != -1;
    }

    public void setValue( String varName,
                          int rowIndex,
                          String newValue ) {
        getTable().get( varName ).set( rowIndex,
                                       newValue );
    }

    //Needed for Errai-RPC marshalling
    public void setIdCol( final int idCol ) {
        this.idCol = idCol;
    }

}
