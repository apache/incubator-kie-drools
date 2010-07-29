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

package org.drools.lang.descr;

public class FieldBindingDescr extends BaseDescr {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;
    private String               fieldName;
    private String               identifier;
    private FieldConstraintDescr fieldConstraint = null;

    public FieldBindingDescr() {
        this( null,
              null );
    }

    public FieldBindingDescr(final String fieldName,
                             final String identifier) {
        this.fieldName = fieldName;
        this.identifier = identifier;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getIdentifier() {
        return this.identifier;
    }

	public FieldConstraintDescr getFieldConstraint() {
		return fieldConstraint;
	}

	public void setFieldConstraint(FieldConstraintDescr fieldConstraint) {
		this.fieldConstraint = fieldConstraint;
	}

    public String toString() {
        return "[FieldBinding: field=" + this.fieldName + "; identifier=" + this.identifier + "]";
    }
}
