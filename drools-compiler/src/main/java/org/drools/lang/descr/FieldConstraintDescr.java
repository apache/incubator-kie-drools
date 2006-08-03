package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

/**
 * This represents a literal node in the rule language. This is
 * a constraint on a single field of a column. 
 * The "text" contains the content, which may also be an enumeration. 
 */
public class FieldConstraintDescr extends PatternDescr {

	private static final long serialVersionUID = 320;
    private String  fieldName;
    private List    restrictions = Collections.EMPTY_LIST;

    public FieldConstraintDescr(final String fieldName) {
        this.fieldName = fieldName;        
    }

    public String getFieldName() {
        return this.fieldName;
    }
    
    public void addRestriction(RestrictionDescr restriction) {
        if ( this.restrictions == Collections.EMPTY_LIST ) {
            this.restrictions = new ArrayList();
        }
        this.restrictions.add( restriction );
    }
    
    public List getRestrictions() {
        return this.restrictions;
    }

    
}