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

public class ColumnDescr extends PatternDescr {
    private String objectType;
    private String identifier;
    private List   descrs = Collections.EMPTY_LIST;

    public ColumnDescr(String objectType) {
        this(objectType, null);
    }    
    
    public ColumnDescr(String objectType,
                       String identifier) {
        this.objectType = objectType;
        this.identifier = identifier;
    }

    public void addDescr(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList( 1 );
        }
        this.descrs.add(  patternDescr );
    }

    public void setIdentifier(String identifier) {
    		this.identifier = identifier;
    }
    
    public String getObjectType() {
        return objectType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List getDescrs() {
        return this.descrs;
    }
    
    public String toString() {
    		return "[Column: id=" + identifier + "; objectType=" + objectType + "]";
    }
}