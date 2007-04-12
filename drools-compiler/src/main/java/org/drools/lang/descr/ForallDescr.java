/*
 * Copyright 2006 JBoss Inc
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

package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author etirelli
 *
 */
public class ForallDescr extends BaseDescr
    implements
    ConditionalElementDescr {

    private static final long serialVersionUID = 1742161904829304893L;

    private List              columns;

    public ForallDescr() {
        this.columns = new ArrayList( 2 );
    }

    /* (non-Javadoc)
     * @see org.drools.lang.descr.ConditionalElementDescr#addDescr(org.drools.lang.descr.BaseDescr)
     */
    public void addDescr(final BaseDescr baseDescr) {
        // cast to make sure we are adding a column descriptor
        this.columns.add( baseDescr );
    }

    /* (non-Javadoc)
     * @see org.drools.lang.descr.ConditionalElementDescr#getDescrs()
     */
    public List getDescrs() {
        return this.columns;
    }

    /**
     * Returns the base column from the forall CE
     * @return
     */
    public ColumnDescr getBaseColumn() {
        return (this.columns.size() > 0) ? (ColumnDescr) this.columns.get( 0 ) : null;
    }

    /**
     * Returns the remaining columns from the forall CE
     * @return
     */
    public List getRemainingColumns() {
        return (this.columns.size() > 1) ? this.columns.subList( 1,
                                                                 this.columns.size() ) : Collections.EMPTY_LIST;
    }

}
