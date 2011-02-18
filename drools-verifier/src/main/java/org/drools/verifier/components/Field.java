/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.components;

import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class Field extends VerifierComponent
    implements
    Cause {

    public static final String BOOLEAN  = "java.lang.Boolean";
    public static final String STRING   = "java.lang.String";
    public static final String INT      = "java.lang.Integer";
    public static final String DOUBLE   = "java.lang.Double";
    public static final String DATE     = "java.util.Date";
    public static final String VARIABLE = "Variable";
    public static final String OBJECT   = "Object";
    public static final String ENUM     = "Enum";
    public static final String UNKNOWN  = "Unknown";

    private String             objectTypePath;
    protected String           objectTypeName;
    protected String           name;
    private String             fieldType;

    @Override
    public String getPath() {
        return String.format( "%s/field[@name='%s']",
                              getObjectTypePath(),
                              getName() );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FIELD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        // Only set fieldType to variable if there is no other fieldType found.
        if ( fieldType == VARIABLE && this.fieldType == null ) {
            this.fieldType = fieldType;
        } else {
            this.fieldType = fieldType;
        }
    }

    public String getObjectTypePath() {
        return objectTypePath;
    }

    public void setObjectTypePath(String objectTypePath) {
        this.objectTypePath = objectTypePath;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    @Override
    public String toString() {
        return "Field '" + name + "' from object type '" + objectTypeName + "'";
    }

}
