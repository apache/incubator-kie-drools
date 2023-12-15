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
package org.drools.core.base.accumulators;

import org.drools.core.base.accumulators.VarianceAccumulateFunction.VarianceData;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class VarianceNaNTest {
    
    @Test
    public void shouldNotProduceNaNAfterBackout(){
        VarianceAccumulateFunction varianceAccumulateFunction = new VarianceAccumulateFunction();
        VarianceData data = varianceAccumulateFunction.createContext();
        varianceAccumulateFunction.init(data);

        //Before being initialized result is NaN.
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(Double.NaN, within(0.0));

        Double value = 1.5;
        
        //With single value variance should be 0
        varianceAccumulateFunction.accumulate(data, value);
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(0.0d, within(.001d));
        
        //should be back to NaN after backout
        varianceAccumulateFunction.reverse(data, value);
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(Double.NaN, within(0.0));
        
        //should be zero after adding number back
        varianceAccumulateFunction.accumulate(data, value);
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(0.0d, within(.001d));
    }
}
