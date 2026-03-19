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
package org.drools.metric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.drools.metric.profiling.MetricDashboardReporter;
import org.drools.metric.util.SessionMetricCollector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MetricDashboardReporter} HTML generation.
 */
public class MetricDashboardReporterTest {

    @Test
    public void testGenerateReportWithData() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("com.example.HighValueOrder", 5_000_000);
        collector.recordRuleFiring("com.example.HighValueOrder", 3_000_000);
        collector.recordRuleFiring("com.example.DiscountRule", 1_500_000);
        collector.recordRuleFiring("com.example.ValidationRule", 800_000);
        collector.incrementMatchesCreated();
        collector.incrementMatchesCreated();
        collector.incrementMatchesCreated();
        collector.incrementMatchesCancelled();
        collector.setFactCountSupplier(() -> 25L);

        String html = MetricDashboardReporter.generateReport(collector);

        // Basic HTML structure
        assertThat(html).contains("<!DOCTYPE html>");
        assertThat(html).contains("<title>Drools Performance Dashboard</title>");
        assertThat(html).contains("</html>");

        // Summary card values
        assertThat(html).contains(">4<"); // total rules fired
        assertThat(html).contains(">3<"); // distinct rules
        assertThat(html).contains(">3<"); // matches created
        assertThat(html).contains(">1<"); // matches cancelled
        assertThat(html).contains(">25<"); // fact count

        // Rule table
        assertThat(html).contains("HighValueOrder");
        assertThat(html).contains("DiscountRule");
        assertThat(html).contains("ValidationRule");

        // Hotspot chart
        assertThat(html).contains("chart-bar");
        assertThat(html).contains("Top 10 Hotspot");

        // CSS is inline
        assertThat(html).contains("<style>");
        assertThat(html).contains("background: #0f1117");
    }

    @Test
    public void testGenerateReportEmpty() {
        SessionMetricCollector collector = new SessionMetricCollector();
        String html = MetricDashboardReporter.generateReport(collector);

        assertThat(html).contains("<!DOCTYPE html>");
        assertThat(html).contains("No rules have been fired yet");
        // chart-row only appears if hotspot chart is rendered with actual data
        assertThat(html).doesNotContain("chart-row");
    }

    @Test
    public void testWriteReportToFile() throws IOException {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("test.Rule1", 2_000_000);
        collector.recordRuleFiring("test.Rule2", 1_000_000);
        collector.recordRuleFiring("test.Rule1", 3_000_000);

        Path outputPath = Path.of("/tmp/drools-metric-dashboard-test.html");
        MetricDashboardReporter.writeReport(collector, outputPath);

        assertThat(Files.exists(outputPath)).isTrue();
        String content = Files.readString(outputPath);
        assertThat(content).contains("Drools Performance Dashboard");
        assertThat(content).contains("Rule1");
        assertThat(content).contains("Rule2");

        // Cleanup
        Files.deleteIfExists(outputPath);
    }

    @Test
    public void testHtmlEscaping() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("pkg.<script>alert('xss')</script>", 1_000_000);

        String html = MetricDashboardReporter.generateReport(collector);

        assertThat(html).doesNotContain("<script>alert");
        assertThat(html).contains("&lt;script&gt;");
    }

    @Test
    public void testManyRulesGeneratesFullTable() {
        SessionMetricCollector collector = new SessionMetricCollector();
        for (int i = 0; i < 20; i++) {
            collector.recordRuleFiring("pkg.Rule" + i, (20 - i) * 1_000_000L);
        }

        String html = MetricDashboardReporter.generateReport(collector);

        // Table should contain all 20 rules
        for (int i = 0; i < 20; i++) {
            assertThat(html).contains("Rule" + i);
        }

        // Hotspot chart should only have top 10
        assertThat(html).contains("Top 10 Hotspot");
    }
}
