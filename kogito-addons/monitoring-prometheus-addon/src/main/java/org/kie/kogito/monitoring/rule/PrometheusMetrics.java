/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.rule;

import java.util.stream.IntStream;

import io.prometheus.client.Histogram;


public class PrometheusMetrics {

    private static final long NANOSECONDS_PER_MICROSECOND = 1_000_000;

    private static long toMicro(long second) {
        return second * NANOSECONDS_PER_MICROSECOND;
    }

    private static double[] rangeMicro(int start, int end) {
        return IntStream.range(start, end).mapToDouble(l -> toMicro((long) l)).toArray();
    }

    protected static double millisToSeconds(long millis) {
        return millis / 1000.0;
    }

    private static final double[] RULE_TIME_BUCKETS;

    static {
        RULE_TIME_BUCKETS = rangeMicro(1, 10);
    }

    private static final Histogram droolsEvaluationTimeHistogram = Histogram.build()
            .name("drl_match_fired_nanosecond")
            .help("Drools Firing Time")
            .labelNames("identifier", "rule_name")
            .buckets(RULE_TIME_BUCKETS)
            .register();

    public static Histogram getDroolsEvaluationTimeHistogram() {
        return droolsEvaluationTimeHistogram;
    }
}
