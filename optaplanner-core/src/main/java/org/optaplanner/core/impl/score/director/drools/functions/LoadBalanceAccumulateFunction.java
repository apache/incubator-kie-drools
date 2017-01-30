/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.director.drools.functions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.rule.AccumulateFunction;

public class LoadBalanceAccumulateFunction implements AccumulateFunction {

    protected static class LoadBalanceData implements Serializable {

        private Map<Object, Long> groupWeightMap;
        private long variance;

    }

    @Override
    public Serializable createContext() {
        return new LoadBalanceData();
    }

    @Override
    public void init(Serializable context) throws Exception {
        LoadBalanceData data = (LoadBalanceData) context;
        data.groupWeightMap = new HashMap<>();
        data.variance = 0L;
    }

    @Override
    public void accumulate(Serializable context, Object groupBy) {
        LoadBalanceData data = (LoadBalanceData) context;
        long count = data.groupWeightMap.compute(groupBy,
                (key, value) -> (value == null) ? 1L : value + 1L);
        // variance = variance - (count - 1)² + count²
        // <=> variance = variance + (2 * count - 1)
        data.variance += (2 * count - 1);
    }

    @Override
    public void reverse(Serializable context, Object groupBy) throws Exception {
        LoadBalanceData data = (LoadBalanceData) context;
        Long count = data.groupWeightMap.compute(groupBy,
                (key, value) -> (value.longValue() == 1L) ? null : value - 1L);
        data.variance -= (count == null) ? 1L : (2 * count + 1);
    }

    @Override
    public Double getResult(Serializable context) throws Exception {
        LoadBalanceData data = (LoadBalanceData) context;
        return Math.sqrt((double) data.variance);
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Class<Double> getResultType() {
        return Double.class;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

}
