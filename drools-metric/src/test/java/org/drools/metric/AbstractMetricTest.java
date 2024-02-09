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
package org.drools.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.search.Search;
import org.drools.metric.util.MetricLogUtils;
import org.drools.metric.util.MicrometerUtils;
import org.drools.mvel.CommonTestMethodBase;
import org.junit.After;
import org.junit.Before;

abstract class AbstractMetricTest extends CommonTestMethodBase {

    protected MeterRegistry registry;

    @Before
    public void setup() {
        System.setProperty(MetricLogUtils.METRIC_LOGGER_ENABLED, "true");
        System.setProperty(MetricLogUtils.METRIC_LOGGER_THRESHOLD, "-1");
        this.registry = Metrics.globalRegistry;
    }

    @After
    public void clearMeters() { // Remove meters we inserted without affecting those that may have already been there.
        Search.in(registry)
                .name(name -> name.startsWith("org.drools.metric"))
                .meters()
                .forEach(registry::remove);
        MicrometerUtils.INSTANCE.clear();
        registry = null;
    }


}
