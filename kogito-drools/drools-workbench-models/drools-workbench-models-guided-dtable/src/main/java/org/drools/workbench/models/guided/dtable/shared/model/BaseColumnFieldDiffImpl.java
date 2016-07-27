/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;
import java.util.Map;

public class BaseColumnFieldDiffImpl implements BaseColumnFieldDiff {

    private String fieldName;
    private Object oldValue;
    private Object newValue;

    /**
     * Default no-arg constructor for errai marshalling.
     */
    public BaseColumnFieldDiffImpl() {
    }

    public BaseColumnFieldDiffImpl( String fieldName,
                                    Object oldValue,
                                    Object newValue ) {
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue( Object oldValue ) {
        this.oldValue = oldValue;
    }

    public Object getValue() {
        return newValue;
    }

    public void setValue( Object newValue ) {
        this.newValue = newValue;
    }

    public static boolean hasChanged( String fieldName,
                                      List<BaseColumnFieldDiff> source ) {
        if ( fieldName == null ) {
            return false;
        }
        if ( source != null && !source.isEmpty() ) {
            for ( BaseColumnFieldDiff diffCol : source ) {
                if ( fieldName.equals( diffCol.getFieldName() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static BaseColumnFieldDiff getDiff( String fieldName,
                                               List<BaseColumnFieldDiff> source ) {
        if ( fieldName == null ) {
            return null;
        }
        if ( source != null && !source.isEmpty() ) {
            for ( BaseColumnFieldDiff diffCol : source ) {
                if ( fieldName.equals( diffCol.getFieldName() ) ) {
                    return diffCol;
                }
            }
        }
        return null;
    }

    /**
     * Check whether two Objects are equal or both null.
     * @param s1
     *         The object.
     * @param s2
     *         The other  object.
     * @return Whether two Objects are equal or both null
     */
    public static boolean isEqualOrNull( Object s1,
                                         Object s2 ) {
        if ( s1 == null
                && s2 == null ) {
            return true;
        } else if ( s1 != null
                && s2 != null
                && s1.equals( s2 ) ) {
            return true;
        }
        return false;
    }

    /**
     * Check whether two Objects are equal or both null.
     * @param dcv1
     *         The DTCellValue52.
     * @param dcv2
     *         The other DTCellValue52.
     * @return Whether two DTCellValue52s are equal or both null
     */
    public static boolean isEqualOrNull( final DTCellValue52 dcv1,
                                         final DTCellValue52 dcv2 ) {
        if ( dcv1 == null && dcv2 == null ) {
            return true;
        } else if ( dcv1 == null && dcv2 != null ) {
            return false;
        } else if ( dcv1 != null && dcv2 == null ) {
            return false;
        } else if ( !dcv1.getDataType().equals( dcv2.getDataType() ) ) {
            return false;
        } else {
            //Both DataTypes are equal here, so just check one DCV's type
            switch ( dcv1.getDataType() ) {
                case BOOLEAN:
                    if ( dcv1.getBooleanValue() == null && dcv2.getBooleanValue() == null ) {
                        return true;
                    } else if ( dcv1.getBooleanValue() == null && dcv2.getBooleanValue() != null ) {
                        return false;
                    } else if ( dcv1.getBooleanValue() != null && dcv2.getBooleanValue() == null ) {
                        return false;
                    } else {
                        return dcv1.getBooleanValue().equals( dcv2.getBooleanValue() );
                    }
                case DATE:
                    if ( dcv1.getDateValue() == null && dcv2.getDateValue() == null ) {
                        return true;
                    } else if ( dcv1.getDateValue() == null && dcv2.getDateValue() != null ) {
                        return false;
                    } else if ( dcv1.getDateValue() != null && dcv2.getDateValue() == null ) {
                        return false;
                    } else {
                        return dcv1.getDateValue().equals( dcv2.getDateValue() );
                    }
                case NUMERIC:
                case NUMERIC_BIGDECIMAL:
                case NUMERIC_BIGINTEGER:
                case NUMERIC_BYTE:
                case NUMERIC_DOUBLE:
                case NUMERIC_FLOAT:
                case NUMERIC_INTEGER:
                case NUMERIC_LONG:
                case NUMERIC_SHORT:
                    if ( dcv1.getNumericValue() == null && dcv2.getNumericValue() == null ) {
                        return true;
                    } else if ( dcv1.getNumericValue() == null && dcv2.getNumericValue() != null ) {
                        return false;
                    } else if ( dcv1.getNumericValue() != null && dcv2.getNumericValue() == null ) {
                        return false;
                    } else {
                        return dcv1.getNumericValue().equals( dcv2.getNumericValue() );
                    }
                default:
                    if ( dcv1.getStringValue() == null && dcv2.getStringValue() == null ) {
                        return true;
                    } else if ( dcv1.getStringValue() == null && dcv2.getStringValue() != null ) {
                        return false;
                    } else if ( dcv1.getStringValue() != null && dcv2.getStringValue() == null ) {
                        return false;
                    } else {
                        return dcv1.getStringValue().equals( dcv2.getStringValue() );
                    }
            }
        }
    }

    /**
     * Check whether two List are same size or both null.
     * @param s1
     *         The fist list..
     * @param s2
     *         The other  list.
     * @return Whether two List are same size or both null
     */
    public static boolean isEqualOrNull( List s1,
                                         List s2 ) {
        if ( s1 == null
                && s2 == null ) {
            return true;
        } else if ( s1 != null
                && s2 != null
                && s1.size() == s2.size() ) {
            return true;
        }
        return false;
    }

    /**
     * Check whether two Map are same size or both null.
     * @param s1
     *         The fist Map..
     * @param s2
     *         The other  Map.
     * @return Whether two Map are same size or both null
     */

    public static boolean isEqualOrNull( Map s1,
                                         Map s2 ) {
        if ( s1 == null
                && s2 == null ) {
            return true;
        } else if ( s1 != null
                && s2 != null
                && s1.size() == s2.size() ) {
            return true;
        }
        return false;
    }
}