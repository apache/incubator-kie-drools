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

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities
 */
public class DataUtilities {

    /**
     * Convert a two-dimensional array of Strings to a List of Lists, with
     * type-safe individual entries
     * @param oldData
     * @return New data
     */
    public static List<List<DTCellValue52>> makeDataLists( Object[][] oldData ) {
        List<List<DTCellValue52>> newData = new ArrayList<List<DTCellValue52>>();
        for ( int iRow = 0; iRow < oldData.length; iRow++ ) {
            Object[] oldRow = oldData[ iRow ];
            List<DTCellValue52> newRow = makeDataRowList( oldRow );
            newData.add( newRow );
        }
        return newData;
    }

    /**
     * Convert a single dimension array of Strings to a List with type-safe
     * entries. The first entry is converted into a numerical row number
     * @param oldRow
     * @return New row
     */
    public static List<DTCellValue52> makeDataRowList( Object[] oldRow ) {
        List<DTCellValue52> row = new ArrayList<DTCellValue52>();

        //Row numbers are numerical
        if ( oldRow[ 0 ] instanceof String ) {
            DTCellValue52 rowDcv = new DTCellValue52( new Integer( (String) oldRow[ 0 ] ) );
            row.add( rowDcv );
        } else if ( oldRow[ 0 ] instanceof Number ) {
            DTCellValue52 rowDcv = new DTCellValue52( ( (Number) oldRow[ 0 ] ).intValue() );
            row.add( rowDcv );
        } else {
            DTCellValue52 rowDcv = new DTCellValue52( oldRow[ 0 ] );
            row.add( rowDcv );
        }

        for ( int iCol = 1; iCol < oldRow.length; iCol++ ) {

            //The original model was purely String based. Conversion to typed fields
            //occurs when the Model is re-saved in Guvnor. Ideally the conversion 
            //should occur here but that requires reference to a SuggestionCompletionEngine
            //which requires RepositoryServices. I did not want to make a dependency between
            //common IDE classes and the Repository
            DTCellValue52 dcv = new DTCellValue52( oldRow[ iCol ] );
            row.add( dcv );
        }
        return row;
    }

}
