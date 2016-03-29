/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.datamodel.oracle;

/**
 * An Oracle for all things "data type" related
 */

public class DataType {

    /**
     * These are the explicit types supported
     */
    public static final String TYPE_COLLECTION = "Collection";
    public static final String TYPE_COMPARABLE = "Comparable";
    public static final String TYPE_STRING = "String";
    public static final String TYPE_NUMERIC = "Numeric";
    public static final String TYPE_NUMERIC_BIGDECIMAL = "BigDecimal";
    public static final String TYPE_NUMERIC_BIGINTEGER = "BigInteger";
    public static final String TYPE_NUMERIC_BYTE = "Byte";
    public static final String TYPE_NUMERIC_DOUBLE = "Double";
    public static final String TYPE_NUMERIC_FLOAT = "Float";
    public static final String TYPE_NUMERIC_INTEGER = "Integer";
    public static final String TYPE_NUMERIC_LONG = "Long";
    public static final String TYPE_NUMERIC_SHORT = "Short";
    public static final String TYPE_BOOLEAN = "Boolean";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_OBJECT = "Object";                                                                                                                                                      // for all other unknown
    public static final String TYPE_FINAL_OBJECT = "FinalObject";                                                                                                                                                 // for all other unknown
    public static final String TYPE_THIS = "this";
    public static final String TYPE_VOID = "void";

    public enum DataTypes {
        STRING,
        NUMERIC,
        NUMERIC_BIGDECIMAL,
        NUMERIC_BIGINTEGER,
        NUMERIC_BYTE,
        NUMERIC_DOUBLE,
        NUMERIC_FLOAT,
        NUMERIC_INTEGER,
        NUMERIC_LONG,
        NUMERIC_SHORT,
        DATE,
        BOOLEAN
    }

    public static boolean isNumeric( final String type ) {
        if ( type.equals( TYPE_NUMERIC ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_BIGDECIMAL ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_BIGINTEGER ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_BYTE ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_DOUBLE ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_FLOAT ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_INTEGER ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_LONG ) ) {
            return true;
        } else if ( type.equals( TYPE_NUMERIC_SHORT ) ) {
            return true;
        }
        return false;
    }

}
