/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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


package org.drools.core.base;

import org.drools.core.spi.FieldValue;


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
