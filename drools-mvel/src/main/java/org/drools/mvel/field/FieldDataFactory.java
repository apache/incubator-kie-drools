/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
