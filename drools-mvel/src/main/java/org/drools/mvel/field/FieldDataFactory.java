package org.drools.mvel.field;

import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.FieldValue;


public interface FieldDataFactory {

    FieldValue getFieldValue( Object value, ValueType valueType );

    FieldValue getFieldValue(final Object value);

    FieldValue getFieldValue(final byte value);

    FieldValue getFieldValue(final short value);

    FieldValue getFieldValue(final char value);

    FieldValue getFieldValue(final int value);

    FieldValue getFieldValue(final long value);

    FieldValue getFieldValue(final boolean value);

    FieldValue getFieldValue(final float value);

    FieldValue getFieldValue(final double value);

    FieldValue getFieldValue(final Class value);
}
