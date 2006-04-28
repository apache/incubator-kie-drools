package org.drools.lang.descr;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AndDescr extends PatternDescr
    implements
    ConditionalElementDescr {
    private List descrs       = Collections.EMPTY_LIST;
    private Map  boundColumns = new HashMap();

    public AndDescr() {
    }

    public void addDescr(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList( 1 );
        }
//MN: No pattern combining ??     
//        if ( patternDescr instanceof ColumnDescr ) {
//            addColumn( (ColumnDescr) patternDescr );
//        } else {
            this.descrs.add( patternDescr );
//        }
    }

    /**
     * NOTE: to be used possibly in a future version... wire in above...
     * This will check the column binding, and add the patterns 
     * to a previously bound column
     * if one exists. 
     * If its not a bound column, or it is a unique bound variable column,
     * it will just add it to the list of children descrs.
     */
    private void addColumn(ColumnDescr col) {
        String identifier = col.getIdentifier();
        if ( identifier == null || "".equals( identifier ) ) {
            this.descrs.add( col );
        } else {
            if (boundColumns.containsKey( identifier )) {
                ColumnDescr existingCol = (ColumnDescr) boundColumns.get( identifier );
                if (existingCol.getObjectType().equals( col.getObjectType() )) {
                    combinePatterns(existingCol, col.getDescrs());
                } else {
                    this.descrs.add( col );
                }
            } else {
                boundColumns.put( identifier, col );
                this.descrs.add( col );
            }
        }
    }

    private void combinePatterns(ColumnDescr existingCol,
                                 List newColPatterns) {
        for ( Iterator iter = newColPatterns.iterator(); iter.hasNext(); ) {
            existingCol.addDescr( (PatternDescr) iter.next() );
        }
        
    }

    public List getDescrs() {
        return this.descrs;
    }
}