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
package org.drools.workbench.models.guided.dtable.backend.util;

import org.drools.core.util.DateUtils;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Utilities to support Guided Decision Table operations
 */
public class GuidedDTDRLUtilities {

    /**
     * Utility method to convert DTCellValues to their String representation
     * @param dcv
     * @return
     */
    public static String convertDTCellValueToString( DTCellValue52 dcv ) {
        switch ( dcv.getDataType() ) {
            case BOOLEAN:
                Boolean booleanValue = dcv.getBooleanValue();
                return ( booleanValue == null ? null : booleanValue.toString() );
            case DATE:
                Date dateValue = dcv.getDateValue();
                return ( dateValue == null ? null : DateUtils.format( dcv.getDateValue() ) );
            case NUMERIC:
                BigDecimal numericValue = (BigDecimal) dcv.getNumericValue();
                return ( numericValue == null ? null : numericValue.toPlainString() );
            case NUMERIC_BIGDECIMAL:
                BigDecimal bigDecimalValue = (BigDecimal) dcv.getNumericValue();
                return ( bigDecimalValue == null ? null : bigDecimalValue.toPlainString() );
            case NUMERIC_BIGINTEGER:
                BigInteger bigIntegerValue = (BigInteger) dcv.getNumericValue();
                return ( bigIntegerValue == null ? null : bigIntegerValue.toString() );
            case NUMERIC_BYTE:
                Byte byteValue = (Byte) dcv.getNumericValue();
                return ( byteValue == null ? null : byteValue.toString() );
            case NUMERIC_DOUBLE:
                Double doubleValue = (Double) dcv.getNumericValue();
                return ( doubleValue == null ? null : doubleValue.toString() );
            case NUMERIC_FLOAT:
                Float floatValue = (Float) dcv.getNumericValue();
                return ( floatValue == null ? null : floatValue.toString() );
            case NUMERIC_INTEGER:
                Integer integerValue = (Integer) dcv.getNumericValue();
                return ( integerValue == null ? null : integerValue.toString() );
            case NUMERIC_LONG:
                Long longValue = (Long) dcv.getNumericValue();
                return ( longValue == null ? null : longValue.toString() );
            case NUMERIC_SHORT:
                Short shortValue = (Short) dcv.getNumericValue();
                return ( shortValue == null ? null : shortValue.toString() );
            default:
                return dcv.getStringValue();
        }
    }

}
