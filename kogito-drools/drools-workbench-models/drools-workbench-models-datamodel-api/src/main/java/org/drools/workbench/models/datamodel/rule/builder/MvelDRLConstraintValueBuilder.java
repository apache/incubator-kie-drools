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
package org.drools.workbench.models.datamodel.rule.builder;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;

/**
 * Specific implementation for MVEL
 */
public class MvelDRLConstraintValueBuilder extends DRLConstraintValueBuilder {

    /**
     * Concatenate a String to the provided buffer suitable for the fieldValue
     * and fieldType. Strings and Dates are escaped with double-quotes, whilst
     * Numerics, Booleans, (Java 1.5+) enums and all other fieldTypes are not
     * escaped at all. Guvnor-type enums are really a pick list of Strings and
     * in these cases the underlying fieldType is a String.
     * @param buf
     * @param constraintType
     * @param fieldType
     * @param fieldValue
     */
    public void buildLHSFieldValue( StringBuilder buf,
                                    int constraintType,
                                    String fieldType,
                                    String fieldValue ) {

        final boolean isDelimitedString = isDelimitedString( fieldValue );

        if ( fieldType == null || fieldType.length() == 0 ) {
            //This should ideally be an error however we show leniency to legacy code
            if ( fieldValue == null ) {
                return;
            }
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
            buf.append( fieldValue );
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
            return;
        }

        if ( fieldType.equals( DataType.TYPE_BOOLEAN ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            buf.append( "\"" );
            buf.append( fieldValue );
            buf.append( "\"" );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            buf.append( fieldValue + "B" );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            buf.append( fieldValue + "I" );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
            buf.append( fieldValue );
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            buf.append( fieldValue );
        } else {
            if ( !isDelimitedString ) {
                addQuote( constraintType,
                          buf );
            }
            buf.append( fieldValue );
            if ( !isDelimitedString ) {
                addQuote( constraintType,
                          buf );
            }
        }

    }

    /**
     * Concatenate a String to the provided buffer suitable for the fieldType
     * and fieldValue. Strings are escaped with double-quotes, Dates are wrapped
     * with a call to a pre-constructed SimpleDateFormatter, whilst Numerics,
     * Booleans, (Java 1.5+) enums and all other fieldTypes are not escaped at
     * all. Guvnor-type enums are really a pick list of Strings and in these
     * cases the underlying fieldType is a String.
     * @param buf
     * @param fieldType
     * @param fieldValue
     */
    public void buildRHSFieldValue( StringBuilder buf,
                                    String fieldType,
                                    String fieldValue ) {

        final boolean isDelimitedString = isDelimitedString( fieldValue );

        if ( fieldType == null || fieldType.length() == 0 ) {
            //This should ideally be an error however we show leniency to legacy code
            if ( fieldValue == null ) {
                return;
            }
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
            buf.append( fieldValue );
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
            return;
        }

        if ( fieldType.equals( DataType.TYPE_BOOLEAN ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            buf.append( "sdf.parse(\"" );
            buf.append( fieldValue );
            buf.append( "\")" );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            buf.append( fieldValue + "B" );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            buf.append( fieldValue + "I" );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            buf.append( fieldValue );
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
            buf.append( fieldValue );
            if ( !isDelimitedString ) {
                buf.append( "\"" );
            }
        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            buf.append( fieldValue );
        } else {
            buf.append( fieldValue );
        }

    }

    //Add a quote to literal values, if applicable
    private static void addQuote( int constraintType,
                                  StringBuilder buf ) {
        if ( constraintType == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            buf.append( "\"" );
        }
    }

    protected boolean isDelimitedString( final String content ) {
        return content.startsWith( "\"" ) && content.endsWith( "\"" );
    }

}
