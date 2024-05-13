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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.NumberEvalHelper;

public class NNModeFunction
        extends BaseFEELFunction {
    public static final NNModeFunction INSTANCE = new NNModeFunction();

    NNModeFunction() {
        super("nn mode");
    }

    public FEELFnResult<List> invoke(@ParameterName("list") List<?> list) {
        if (list == null || list.isEmpty()) {
            return FEELFnResult.ofResult( null );
        }

        Map<BigDecimal, Long> collect = new HashMap<>();
        long maxFreq = 0;
        for( int i = 0; i < list.size(); i++ ) {
            Object original = list.get( i );
            BigDecimal value = NumberEvalHelper.getBigDecimalOrNull(original );
            if( original != null && value == null ) {
                // conversion error
                return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "list", "contains items that are not numbers"));
            } else if( value != null ) {
                Long previous = collect.get( value );
                long newCount = previous != null ? previous + 1 : 1;

                collect.put( value, newCount );
                if( maxFreq < newCount ) {
                    maxFreq = newCount;
                }
            }
        }

        if( collect.isEmpty() ) {
            return FEELFnResult.ofResult( null );
        }

        final long maxF = maxFreq;
        List<BigDecimal> mostFrequents = collect.entrySet().stream()
                                       .filter(kv -> kv.getValue() == maxF)
                                       .map(Map.Entry::getKey)
                                       .collect(Collectors.toList());

        return FEELFnResult.ofResult(mostFrequents.stream().sorted().collect(Collectors.toList()));
    }

    public FEELFnResult<List> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) {
            return FEELFnResult.ofResult( null );
        }
        return invoke( Arrays.asList( list ) );
    }
}
