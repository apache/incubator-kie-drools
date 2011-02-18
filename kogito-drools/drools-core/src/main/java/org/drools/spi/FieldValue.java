/**
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

package org.drools.spi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface FieldValue
    extends
    Serializable {

    public Object getValue();

    public char getCharValue();

    public BigDecimal getBigDecimalValue();

    public BigInteger getBigIntegerValue();

    public int getIntValue();

    public byte getByteValue();

    public short getShortValue();

    public long getLongValue();

    public float getFloatValue();

    public double getDoubleValue();

    public boolean getBooleanValue();

    public boolean isNull();

    public boolean isBooleanField();

    public boolean isIntegerNumberField();

    public boolean isFloatNumberField();

    public boolean isObjectField();

    /**
     * Returns true if the given field value implements the Collection interface
     * @return
     */
    public boolean isCollectionField();

    public boolean isStringField();

}
