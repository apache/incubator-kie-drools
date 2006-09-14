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

public class AndDescr extends BaseDescr
    implements
    ConditionalElementDescr {
    /**
     * 
     */
    private static final long serialVersionUID = 8225023304408452585L;
    private List descrs       = Collections.EMPTY_LIST;
    private final Map  boundColumns = new HashMap();

    public AndDescr() {
    }

    public void addDescr(final BaseDescr baseDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList( 1 );
        }

        if ( baseDescr instanceof ColumnDescr ) {
            addColumn( (ColumnDescr) baseDescr );
        } else {
            this.descrs.add( baseDescr );
        }
    }

    /**
     * NOTE: to be used possibly in a future version... wire in above...
     * This will check the column binding, and add the patterns 
     * to a previously bound column
     * if one exists. 
     * If its not a bound column, or it is a unique bound variable column,
     * it will just add it to the list of children descrs.
     */
    private void addColumn(final ColumnDescr col) {
        final String identifier = col.getIdentifier();
        if ( identifier == null || "".equals( identifier ) ) {
            this.descrs.add( col );
        } else {
            if ( this.boundColumns.containsKey( identifier ) ) {
                final ColumnDescr existingCol = (ColumnDescr) this.boundColumns.get( identifier );
                if ( existingCol.getObjectType().equals( col.getObjectType() ) ) {
                    combinePatterns( existingCol,
                                     col.getDescrs() );
                } else {
                    this.descrs.add( col );
                }
            } else {
                this.boundColumns.put( identifier,
                                  col );
                this.descrs.add( col );
            }
        }
    }

    private void combinePatterns(final ColumnDescr existingCol,
                                 final List newColPatterns) {
        for ( final Iterator iter = newColPatterns.iterator(); iter.hasNext(); ) {
            existingCol.addDescr( (BaseDescr) iter.next() );
        }

    }

    public List getDescrs() {
        return this.descrs;
    }
}