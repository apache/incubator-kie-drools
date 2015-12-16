/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.shared.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DataType.DataTypes;

/**
 * Holder for cell value and other attributes. This is serialised by GWT RPC and
 * therefore does not contain a single property of type Serializable (that would
 * have been ideal). Instead the concrete data types are included separately.
 */
public class DTCellValue52 {

    private static final long serialVersionUID = -3547167997433925031L;

    //Type safe value of cell
    private Boolean valueBoolean;
    private Date valueDate;
    private Number valueNumeric;
    private String valueString = "";
    private DataTypes dataType = DataTypes.STRING;

    //Does this cell represent "all other values" to those explicitly defined for the column
    private boolean isOtherwise;

    public DTCellValue52() {
    }

    public DTCellValue52( final DTCellValue52 sourceCell ) {
        if ( sourceCell == null ) {
            return;
        }
        switch ( sourceCell.getDataType() ) {
            case BOOLEAN:
                setBooleanValue( sourceCell.getBooleanValue() );
                this.dataType = DataTypes.BOOLEAN;
                break;
            case DATE:
                setDateValue( sourceCell.getDateValue() );
                this.dataType = DataTypes.DATE;
                break;
            case NUMERIC:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC;
                break;
            case NUMERIC_BIGDECIMAL:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_BIGDECIMAL;
                break;
            case NUMERIC_BIGINTEGER:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_BIGINTEGER;
                break;
            case NUMERIC_BYTE:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_BYTE;
                break;
            case NUMERIC_DOUBLE:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_DOUBLE;
                break;
            case NUMERIC_FLOAT:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_FLOAT;
                break;
            case NUMERIC_INTEGER:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_INTEGER;
                break;
            case NUMERIC_LONG:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_LONG;
                break;
            case NUMERIC_SHORT:
                setNumericValue( sourceCell.getNumericValue() );
                this.dataType = DataTypes.NUMERIC_SHORT;
                break;
            default:
                setStringValue( sourceCell.getStringValue() );
                this.dataType = DataTypes.STRING;
        }
    }

    public DTCellValue52( final DataTypes type,
                          final boolean emptyValue ) {
        switch ( type ) {
            case BOOLEAN:
                if ( !emptyValue ) {
                    setBooleanValue( false );
                }
                this.dataType = DataType.DataTypes.BOOLEAN;
                break;
            case DATE:
                if ( !emptyValue ) {
                    setDateValue( new Date() );
                }
                this.dataType = DataType.DataTypes.DATE;
                break;
            case NUMERIC:
                if ( !emptyValue ) {
                    setNumericValue( new BigDecimal( "0" ) );
                }
                this.dataType = DataType.DataTypes.NUMERIC;
                break;
            case NUMERIC_BIGDECIMAL:
                if ( !emptyValue ) {
                    setNumericValue( new BigDecimal( "0" ) );
                }
                this.dataType = DataType.DataTypes.NUMERIC_BIGDECIMAL;
                break;
            case NUMERIC_BIGINTEGER:
                if ( !emptyValue ) {
                    setNumericValue( new BigInteger( "0" ) );
                }
                this.dataType = DataType.DataTypes.NUMERIC_BIGINTEGER;
                break;
            case NUMERIC_BYTE:
                if ( !emptyValue ) {
                    setNumericValue( new Byte( "0" ) );
                }
                this.dataType = DataType.DataTypes.NUMERIC_BYTE;
                break;
            case NUMERIC_DOUBLE:
                if ( !emptyValue ) {
                    setNumericValue( 0.0d );
                }
                this.dataType = DataType.DataTypes.NUMERIC_DOUBLE;
                break;
            case NUMERIC_FLOAT:
                if ( !emptyValue ) {
                    setNumericValue( 0.0f );
                }
                this.dataType = DataType.DataTypes.NUMERIC_FLOAT;
                break;
            case NUMERIC_INTEGER:
                if ( !emptyValue ) {
                    setNumericValue( new Integer( "0" ) );
                }
                this.dataType = DataType.DataTypes.NUMERIC_INTEGER;
                break;
            case NUMERIC_LONG:
                if ( !emptyValue ) {
                    setNumericValue( 0l );
                }
                this.dataType = DataType.DataTypes.NUMERIC_LONG;
                break;
            case NUMERIC_SHORT:
                if ( !emptyValue ) {
                    setNumericValue( new Short( "0" ) );
                }
                this.dataType = DataType.DataTypes.NUMERIC_SHORT;
                break;
            default:
                if ( !emptyValue ) {
                    setStringValue( "" );
                }
                this.dataType = DataType.DataTypes.STRING;
        }
    }

    public DTCellValue52( final Object value ) {
        if ( value instanceof String ) {
            setStringValue( (String) value );
            this.dataType = DataType.DataTypes.STRING;
            return;
        }
        if ( value instanceof Boolean ) {
            setBooleanValue( (Boolean) value );
            this.dataType = DataType.DataTypes.BOOLEAN;
            return;
        }
        if ( value instanceof Date ) {
            setDateValue( (Date) value );
            this.dataType = DataType.DataTypes.DATE;
            return;
        }
        if ( value instanceof BigDecimal ) {
            setNumericValue( (BigDecimal) value );
            this.dataType = DataType.DataTypes.NUMERIC_BIGDECIMAL;
            return;
        }
        if ( value instanceof BigInteger ) {
            setNumericValue( (BigInteger) value );
            this.dataType = DataType.DataTypes.NUMERIC_BIGINTEGER;
            return;
        }
        if ( value instanceof Byte ) {
            setNumericValue( (Byte) value );
            this.dataType = DataType.DataTypes.NUMERIC_BYTE;
            return;
        }
        if ( value instanceof Double ) {
            setNumericValue( (Double) value );
            this.dataType = DataType.DataTypes.NUMERIC_DOUBLE;
            return;
        }
        if ( value instanceof Float ) {
            setNumericValue( (Float) value );
            this.dataType = DataType.DataTypes.NUMERIC_FLOAT;
            return;
        }
        if ( value instanceof Integer ) {
            setNumericValue( (Integer) value );
            this.dataType = DataType.DataTypes.NUMERIC_INTEGER;
            return;
        }
        if ( value instanceof Long ) {
            setNumericValue( (Long) value );
            this.dataType = DataType.DataTypes.NUMERIC_LONG;
            return;
        }
        if ( value instanceof Short ) {
            setNumericValue( (Short) value );
            this.dataType = DataType.DataTypes.NUMERIC_SHORT;
            return;
        }
        setStringValue( null );
    }

    public DTCellValue52( final BigDecimal value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_BIGDECIMAL;
    }

    public DTCellValue52( final BigInteger value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_BIGINTEGER;
    }

    public DTCellValue52( final Byte value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_BYTE;
    }

    public DTCellValue52( final Double value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_DOUBLE;
    }

    public DTCellValue52( final Float value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_FLOAT;
    }

    public DTCellValue52( final Integer value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_INTEGER;
    }

    public DTCellValue52( final Long value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_LONG;
    }

    public DTCellValue52( final Short value ) {
        setNumericValue( value );
        this.dataType = DataType.DataTypes.NUMERIC_SHORT;
    }

    public DTCellValue52( final Boolean value ) {
        setBooleanValue( value );
        this.dataType = DataType.DataTypes.BOOLEAN;
    }

    public DTCellValue52( final Date value ) {
        setDateValue( value );
        this.dataType = DataType.DataTypes.DATE;
    }

    public DTCellValue52( final String value ) {
        setStringValue( value );
        this.dataType = DataType.DataTypes.STRING;
    }

    public DataType.DataTypes getDataType() {
        return this.dataType;
    }

    public Boolean getBooleanValue() {
        return valueBoolean;
    }

    public Date getDateValue() {
        return valueDate;
    }

    public Number getNumericValue() {
        return valueNumeric;
    }

    public String getStringValue() {
        return valueString;
    }

    public boolean isOtherwise() {
        return isOtherwise;
    }

    public void setBooleanValue( final Boolean value ) {
        clearValues();
        this.valueBoolean = value;
        this.dataType = DataType.DataTypes.BOOLEAN;
    }

    public void setDateValue( final Date value ) {
        clearValues();
        this.valueDate = value;
        this.dataType = DataType.DataTypes.DATE;
    }

    public void setNumericValue( final Number value ) {
        clearValues();
        this.valueNumeric = value;
    }

    public void setNumericValue( final BigDecimal value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_BIGDECIMAL;
    }

    public void setNumericValue( final BigInteger value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_BIGINTEGER;
    }

    public void setNumericValue( final Byte value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_BYTE;
    }

    public void setNumericValue( final Double value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_DOUBLE;
    }

    public void setNumericValue( final Float value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_FLOAT;
    }

    public void setNumericValue( final Integer value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_INTEGER;
    }

    public void setNumericValue( final Long value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_LONG;
    }

    public void setNumericValue( final Short value ) {
        clearValues();
        this.valueNumeric = value;
        this.dataType = DataType.DataTypes.NUMERIC_SHORT;
    }

    public void setOtherwise( final boolean isOtherwise ) {
        this.isOtherwise = isOtherwise;
    }

    public void setStringValue( final String value ) {
        clearValues();
        this.valueString = value;
        this.dataType = DataType.DataTypes.STRING;
    }

    public void clearValues() {
        this.valueBoolean = null;
        this.valueDate = null;
        this.valueNumeric = null;
        this.valueString = null;
        this.isOtherwise = false;
    }

    public boolean hasValue() {
        return valueBoolean != null
                || valueDate != null
                || valueNumeric != null
                || (valueString != null && !valueString.isEmpty())
                || isOtherwise;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * ( valueBoolean == null ? 0 : valueBoolean.hashCode() );
        hash = hash + 31 * ( valueDate == null ? 0 : valueDate.hashCode() );
        hash = hash + 31 * ( valueNumeric == null ? 0 : valueNumeric.hashCode() );
        hash = hash + 31 * dataType.hashCode();
        return hash;
    }

    @Override
    //A clone of this class is used while editing a column. Overriding this method
    //allows us to easily compare the clone and the original to check if a change 
    //has been made
    public boolean equals( Object obj ) {
        if ( !( obj instanceof DTCellValue52 ) ) {
            return false;
        }
        DTCellValue52 that = (DTCellValue52) obj;
        if ( valueBoolean != null ? !valueBoolean.equals( that.valueBoolean ) : that.valueBoolean != null ) {
            return false;
        }
        if ( valueDate != null ? !valueDate.equals( that.valueDate ) : that.valueDate != null ) {
            return false;
        }
        if ( valueNumeric != null ? !valueNumeric.equals( that.valueNumeric ) : that.valueNumeric != null ) {
            return false;
        }
        if ( valueString != null ? !valueString.equals( that.valueString ) : that.valueString != null ) {
            return false;
        }
        if ( !dataType.equals( that.dataType ) ) {
            return false;
        }
        return true;
    }

    /**
     * Clones this default value instance.
     * @return The cloned instance.
     */
    public DTCellValue52 cloneDefaultValueCell() {
        DTCellValue52 cloned = new DTCellValue52();
        cloned.valueBoolean = valueBoolean;
        cloned.valueDate = valueDate;
        cloned.valueNumeric = valueNumeric;
        cloned.valueString = valueString;
        cloned.dataType = dataType;
        return cloned;
    }

}
