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

package org.drools.core.spi;

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
