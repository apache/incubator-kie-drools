package org.drools.base.rule.accessor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface FieldValue
    extends
    Serializable {

    Object getValue();

    char getCharValue();

    BigDecimal getBigDecimalValue();

    BigInteger getBigIntegerValue();

    int getIntValue();

    byte getByteValue();

    short getShortValue();

    long getLongValue();

    float getFloatValue();

    double getDoubleValue();

    boolean getBooleanValue();

    boolean isNull();

    boolean isBooleanField();

    boolean isIntegerNumberField();

    boolean isFloatNumberField();

    boolean isObjectField();

    /**
     * Returns true if the given field value implements the Collection interface
     * @return
     */
    boolean isCollectionField();

    boolean isStringField();
}
