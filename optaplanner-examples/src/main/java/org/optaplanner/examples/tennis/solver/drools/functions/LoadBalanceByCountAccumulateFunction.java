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

package org.optaplanner.examples.tennis.solver.drools.functions;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.rule.AccumulateFunction;

public class LoadBalanceByCountAccumulateFunction implements AccumulateFunction {

    protected static class LoadBalanceData implements Serializable {

        private Map<Object, Long> groupWeightMap;
        // the sum of squared deviation from zero
        private long squaredDeviation;

    }

    @Override
    public Serializable createContext() {
        return new LoadBalanceData();
    }

    @Override
    public void init(Serializable context) {
        LoadBalanceData data = (LoadBalanceData) context;
        data.groupWeightMap = new HashMap<>();
        data.squaredDeviation = 0L;
    }

    @Override
    public void accumulate(Serializable context, Object groupBy) {
        LoadBalanceData data = (LoadBalanceData) context;
        long count = data.groupWeightMap.compute(groupBy,
                (key, value) -> (value == null) ? 1L : value + 1L);
        // squaredDeviation = squaredDeviation - (count - 1)² + count²
        // <=> squaredDeviation = squaredDeviation + (2 * count - 1)
        data.squaredDeviation += (2 * count - 1);
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public void reverse(Serializable context, Object groupBy) {
        LoadBalanceData data = (LoadBalanceData) context;
        Long count = data.groupWeightMap.compute(groupBy,
                (key, value) -> (value.longValue() == 1L) ? null : value - 1L);
        data.squaredDeviation -= (count == null) ? 1L : (2 * count + 1);
    }

    @Override
    public Class<LoadBalanceResult> getResultType() {
        return LoadBalanceResult.class;
    }

    @Override
    public LoadBalanceResult getResult(Serializable context) {
        LoadBalanceData data = (LoadBalanceData) context;
        return new LoadBalanceResult(data.squaredDeviation);
    }

    @Override
    public void writeExternal(ObjectOutput out) {
    }

    @Override
    public void readExternal(ObjectInput in) {
    }

    public static class LoadBalanceResult implements Serializable {

        private final long squaredDeviation;

        public LoadBalanceResult(long squaredDeviation) {
            this.squaredDeviation = squaredDeviation;
        }

        public long getSquaredDeviation() {
            return squaredDeviation;
        }

        public long getRootSquaredDeviationMillis() {
            return getRootSquaredDeviation(1_000.0);
        }

        public long getRootSquaredDeviationMicros() {
            return getRootSquaredDeviation(1_000_000.0);
        }

        public long getRootSquaredDeviation(double scaleMultiplier) {
            return (long) (Math.sqrt((double) squaredDeviation) * scaleMultiplier);
        }
    }

}
