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
package org.kie.pmml.models.drools.executor;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class used inside drools. Rules are fired based on the value of status
 */
public class KiePMMLStatusHolder {

    private String status;

    private AtomicReference<Double> accumulator = new AtomicReference<>(0.0);

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAccumulator() {
        return accumulator.get();
    }

    public void accumulate(double toAccumulate) {
        accumulator.accumulateAndGet(toAccumulate, Double::sum);
    }

    @Override
    public String toString() {
        return "KiePMMLStatusHolder{" +
                "status='" + status + '\'' +
                "accumulator='" + accumulator + '\'' +
                '}';
    }
}
