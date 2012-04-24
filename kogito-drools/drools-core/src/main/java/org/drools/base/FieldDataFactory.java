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


package org.drools.base;

import org.drools.spi.FieldValue;
import org.drools.type.DateFormats;


public interface FieldDataFactory {

    public FieldValue getFieldValue( String value, ValueType valueType, DateFormats dateFormats );

    public FieldValue getFieldValue( Object value, ValueType valueType, DateFormats dateFormats );

    public FieldValue getFieldValue(final Object value);

    public FieldValue getFieldValue(final byte value);

    public FieldValue getFieldValue(final short value);

    public FieldValue getFieldValue(final char value);

    public FieldValue getFieldValue(final int value);

    public FieldValue getFieldValue(final long value);

    public FieldValue getFieldValue(final boolean value);

    public FieldValue getFieldValue(final float value);

    public FieldValue getFieldValue(final double value);

    public FieldValue getFieldValue(final Class value);


}
