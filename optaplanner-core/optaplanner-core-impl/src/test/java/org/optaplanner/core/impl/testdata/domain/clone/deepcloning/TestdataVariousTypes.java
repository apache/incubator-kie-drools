/*
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

package org.optaplanner.core.impl.testdata.domain.clone.deepcloning;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;

public class TestdataVariousTypes {

    // Primitives.
    public boolean booleanValue = true;
    public byte byteValue = 1;
    public char charValue = 2;
    public short shortValue = 3;
    public int intValue = 4;
    public long longValue = 5;
    public float floatValue = 6;
    public double doubleValue = 7;

    // Popular known immutables.
    public Boolean booleanRef = true;
    public Byte byteRef = 8;
    public Character charRef = 9;
    public Short shortRef = 10;
    public Integer intRef = 11;
    public Long longRef = 12L;
    public Float floatRef = 13f;
    public Double doubleRef = 14d;
    public BigInteger bigInteger = BigInteger.valueOf(15);
    public BigDecimal bigDecimal = BigDecimal.valueOf(16);
    public UUID uuidRef = UUID.randomUUID();
    public String stringRef = uuidRef.toString();

    // And something mutable.
    public List<String> shallowClonedListRef = Collections.singletonList(stringRef);
    @DeepPlanningClone
    public List<String> deepClonedListRef = Collections.singletonList(stringRef);

}
