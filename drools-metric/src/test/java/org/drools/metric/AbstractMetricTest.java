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
