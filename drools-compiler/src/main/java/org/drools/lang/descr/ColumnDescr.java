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
import java.util.List;

public class ColumnDescr extends BaseDescr {
    /**
     * 
     */
    private static final long serialVersionUID = 63959215220308107L;
    private String objectType;
    private String identifier;
    private List   descrs = Collections.EMPTY_LIST;

    public ColumnDescr() {
        this(null, null);
    }
    
    public ColumnDescr(final String objectType) {
        this( objectType,
              null );
    }

    public ColumnDescr(final String objectType,
                       final String identifier) {
        this.objectType = objectType;
        this.identifier = identifier;
    }

    public void addDescr(final BaseDescr baseDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList( 1 );
        }
        this.descrs.add( baseDescr );
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;    
    }
    
    public String getObjectType() {
        return this.objectType;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public List getDescrs() {
        return this.descrs;
    }

    public String toString() {
        return "[Column: id=" + this.identifier + "; objectType=" + this.objectType + "]";
    }
}