/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;


/**
 * This represents a literal node in the rule language. This is
 * a constraint on a single field of a pattern. 
 * The "text" contains the content, which may also be an enumeration. 
 */
public class FieldConstraintDescr extends BaseDescr {

    private static final long serialVersionUID = 510l;
    private String            fieldName;
    private RestrictionConnectiveDescr restriction     = new RestrictionConnectiveDescr( RestrictionConnectiveDescr.AND );

    public FieldConstraintDescr() { }

    public FieldConstraintDescr(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
        super.readExternal( in );
        this.fieldName = (String) in.readObject();
        this.restriction = (RestrictionConnectiveDescr) in.readObject();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject(fieldName);
        out.writeObject( restriction );
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void addRestriction(final RestrictionDescr restriction) {
        this.restriction.addRestriction( restriction );
    }

    public List getRestrictions() {
        return this.restriction.getRestrictions();
    }
    
    public RestrictionConnectiveDescr getRestriction() {
        return this.restriction;
    }

    @Override
    public void setResource(org.kie.api.io.Resource resource) {
        super.setResource(resource);
        this.restriction.setResource(resource);
    };

    @Override
    public String toString() {
        return fieldName + " " + restriction;
    }

}
