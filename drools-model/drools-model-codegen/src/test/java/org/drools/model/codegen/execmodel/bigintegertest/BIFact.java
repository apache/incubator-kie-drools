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
package org.drools.model.codegen.execmodel.bigintegertest;

import java.math.BigInteger;

public class BIFact {

    private BigInteger value1;
    private BigInteger value2;

    public BigInteger getValue1() {
        return value1;
    }

    public void setValue1(BigInteger value1) {
        this.value1 = value1;
    }

    public BigInteger getValue2() {
        return value2;
    }

    public void setValue2(BigInteger value2) {
        this.value2 = value2;
    }
}
