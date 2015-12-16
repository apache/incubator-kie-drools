/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.backend.util;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.commons.backend.IUpgradeHelper;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionInsertFactCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionRetractFactCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionSetFieldCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.AttributeCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ConditionCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.GuidedDecisionTable;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.MetadataCol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class to upgrade model used for Guided Decision Table. This
 * implementation converts legacy GuidedDecisionTable objects to
 * GuidedDecisionTable52 objects used from Guvnor 5.2 onwards.
 */
@SuppressWarnings("deprecation")
public class GuidedDecisionTableUpgradeHelper1
        implements
        IUpgradeHelper<GuidedDecisionTable52, GuidedDecisionTable> {

    /**
     * Convert the legacy Decision Table model to the new
     * @param legacyDTModel
     * @return The new DTModel
     */
    public GuidedDecisionTable52 upgrade( GuidedDecisionTable legacyDTModel ) {

        assertConditionColumnPatternGrouping( legacyDTModel );

        GuidedDecisionTable52 newDTModel = new GuidedDecisionTable52();

        newDTModel.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );

        newDTModel.setTableName( legacyDTModel.tableName );
        newDTModel.setParentName( legacyDTModel.parentName );

        newDTModel.setRowNumberCol( new RowNumberCol52() );
        newDTModel.setDescriptionCol( new DescriptionCol52() );

        //Metadata columns' data-type is implicitly defined in the metadata value. For example
        //a String metadata attribute is: "value", a numerical: 1. No conversion action required
        if ( legacyDTModel.metadataCols != null ) {
            for ( MetadataCol c : legacyDTModel.metadataCols ) {
                newDTModel.getMetadataCols().add( makeNewColumn( c ) );
            }
        }

        //Attribute columns' data-type is based upon the attribute name
        if ( legacyDTModel.attributeCols != null ) {
            for ( AttributeCol c : legacyDTModel.attributeCols ) {
                newDTModel.getAttributeCols().add( makeNewColumn( c ) );
            }
        }

        //Legacy decision tables did not have Condition field data-types. Set all Condition 
        //fields to a *sensible* default of String (as this matches legacy behaviour).
        List<Pattern52> patterns = new ArrayList<Pattern52>();
        Map<String, Pattern52> uniquePatterns = new HashMap<String, Pattern52>();
        if ( legacyDTModel.conditionCols != null ) {
            for ( int i = 0; i < legacyDTModel.conditionCols.size(); i++ ) {
                ConditionCol c = legacyDTModel.conditionCols.get( i );
                String boundName = c.boundName;
                Pattern52 p = uniquePatterns.get( boundName );
                if ( p == null ) {
                    p = new Pattern52();
                    p.setBoundName( boundName );
                    p.setFactType( c.factType );
                    patterns.add( p );
                    uniquePatterns.put( boundName,
                                        p );
                }
                if ( p.getFactType() != null && !p.getFactType().equals( c.factType ) ) {
                    throw new IllegalArgumentException( "Inconsistent FactTypes for ConditionCols bound to '" + boundName + "' detected." );
                }
                p.getChildColumns().add( makeNewColumn( c ) );
            }
            for ( Pattern52 p : patterns ) {
                newDTModel.getConditions().add( p );
            }
        }

        //Action columns have a discrete data-type. No conversion action required.
        if ( legacyDTModel.actionCols != null ) {
            for ( ActionCol c : legacyDTModel.actionCols ) {
                newDTModel.getActionCols().add( makeNewColumn( c ) );
            }
        }

        //Copy across data
        newDTModel.setData( DataUtilities.makeDataLists( legacyDTModel.data ) );

        //Copy the boundName for ActionRetractFactCol into the data of the new Guided Decision Table model
        if ( legacyDTModel.actionCols != null ) {
            final int metaDataColCount = ( legacyDTModel.metadataCols == null ? 0 : legacyDTModel.metadataCols.size() );
            final int attributeColCount = ( legacyDTModel.attributeCols == null ? 0 : legacyDTModel.attributeCols.size() );
            final int conditionColCount = ( legacyDTModel.conditionCols == null ? 0 : legacyDTModel.conditionCols.size() );
            final int DATA_COLUMN_OFFSET = metaDataColCount + attributeColCount + conditionColCount + GuidedDecisionTable.INTERNAL_ELEMENTS;
            for ( int iCol = 0; iCol < legacyDTModel.actionCols.size(); iCol++ ) {
                ActionCol lc = legacyDTModel.actionCols.get( iCol );
                if ( lc instanceof ActionRetractFactCol) {
                    String boundName = ( (ActionRetractFactCol) lc ).boundName;
                    for ( List<DTCellValue52> row : newDTModel.getData() ) {
                        row.get( DATA_COLUMN_OFFSET + iCol ).setStringValue( boundName );
                    }
                }
            }
        }

        return newDTModel;
    }

    // Ensure Condition columns are grouped by pattern (as we merge equal
    // patterns in the UI). This operates on the original Model data and
    // therefore should be called before the Decision Table's internal data
    // representation (i.e. DynamicData, DynamicDataRow and CellValue) is
    // populated
    private void assertConditionColumnPatternGrouping( GuidedDecisionTable model ) {

        class ConditionColData {

            ConditionCol col;
            String[] data;
        }

        // Offset into Model's data array
        final int metaDataColCount = ( model.metadataCols == null ? 0 : model.metadataCols.size() );
        final int attributeColCount = ( model.attributeCols == null ? 0 : model.attributeCols.size() );
        final int DATA_COLUMN_OFFSET = metaDataColCount + attributeColCount + GuidedDecisionTable.INTERNAL_ELEMENTS;
        Map<String, List<ConditionColData>> uniqueGroups = new TreeMap<String, List<ConditionColData>>();
        List<List<ConditionColData>> groups = new ArrayList<List<ConditionColData>>();
        final int DATA_ROWS = model.data.length;

        // Copy conditions and related data into temporary groups
        for ( int iCol = 0; iCol < model.conditionCols.size(); iCol++ ) {

            ConditionCol col = model.conditionCols.get( iCol );
            String pattern = col.boundName + "";
            List<ConditionColData> groupCols = uniqueGroups.get( pattern );
            if ( groupCols == null ) {
                groupCols = new ArrayList<ConditionColData>();
                groups.add( groupCols );
                uniqueGroups.put( pattern,
                                  groupCols );
            }

            // Make a ConditionColData object
            ConditionColData ccd = new ConditionColData();
            int colIndex = DATA_COLUMN_OFFSET + iCol;
            ccd.data = new String[ DATA_ROWS ];
            for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                String[] row = model.data[ iRow ];
                ccd.data[ iRow ] = row[ colIndex ];
            }
            ccd.col = col;
            groupCols.add( ccd );
        }

        // Copy temporary groups back into the model
        int iCol = 0;
        model.conditionCols.clear();
        for ( List<ConditionColData> me : groups ) {
            for ( ConditionColData ccd : me ) {
                model.conditionCols.add( ccd.col );
                int colIndex = DATA_COLUMN_OFFSET + iCol;
                for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                    String[] row = model.data[ iRow ];
                    row[ colIndex ] = ccd.data[ iRow ];
                }
                iCol++;
            }
        }

    }

    private AttributeCol52 makeNewColumn( AttributeCol c ) {
        AttributeCol52 nc = new AttributeCol52();
        nc.setAttribute( c.attr );
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setHideColumn( c.hideColumn );
        nc.setReverseOrder( c.reverseOrder );
        nc.setUseRowNumber( c.useRowNumber );
        nc.setWidth( c.width );
        return nc;
    }

    private MetadataCol52 makeNewColumn( MetadataCol c ) {
        MetadataCol52 nc = new MetadataCol52();
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setHideColumn( c.hideColumn );
        nc.setMetadata( c.attr );
        nc.setWidth( c.width );
        return nc;
    }

    private ConditionCol52 makeNewColumn( ConditionCol c ) {
        ConditionCol52 nc = new ConditionCol52();
        nc.setConstraintValueType( c.constraintValueType );
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setFactField( c.factField );
        nc.setFieldType( DataType.TYPE_STRING );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setOperator( c.operator );
        nc.setValueList( c.valueList );
        nc.setWidth( c.width );
        return nc;
    }

    private ActionCol52 makeNewColumn( ActionCol c ) {
        if ( c instanceof ActionInsertFactCol) {
            return makeNewColumn( (ActionInsertFactCol) c );
        } else if ( c instanceof ActionRetractFactCol ) {
            return makeNewColumn( (ActionRetractFactCol) c );
        } else if ( c instanceof ActionSetFieldCol) {
            return makeNewColumn( (ActionSetFieldCol) c );
        }
        ActionCol52 nc = new ActionCol52();
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setWidth( c.width );
        return nc;
    }

    private ActionInsertFactCol52 makeNewColumn( ActionInsertFactCol c ) {
        ActionInsertFactCol52 nc = new ActionInsertFactCol52();
        nc.setBoundName( c.boundName );
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setFactField( c.factField );
        nc.setFactType( c.factType );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setType( c.type );
        nc.setValueList( c.valueList );
        nc.setWidth( c.width );
        return nc;
    }

    private ActionRetractFactCol52 makeNewColumn( ActionRetractFactCol c ) {
        ActionRetractFactCol52 nc = new ActionRetractFactCol52();
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setWidth( c.width );
        return nc;

    }

    private ActionSetFieldCol52 makeNewColumn( ActionSetFieldCol c ) {
        ActionSetFieldCol52 nc = new ActionSetFieldCol52();
        nc.setBoundName( c.boundName );
        nc.setDefaultValue( new DTCellValue52( c.defaultValue ) );
        nc.setFactField( c.factField );
        nc.setHeader( c.header );
        nc.setHideColumn( c.hideColumn );
        nc.setType( c.type );
        nc.setUpdate( c.update );
        nc.setValueList( c.valueList );
        nc.setWidth( c.width );
        return nc;
    }

}
