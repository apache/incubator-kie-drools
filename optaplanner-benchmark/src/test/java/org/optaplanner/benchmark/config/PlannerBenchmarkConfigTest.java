/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.io.jaxb.JaxbIO;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class PlannerBenchmarkConfigTest {

    private static final String TEST_PLANNER_BENCHMARK_CONFIG = "testBenchmarkConfig.xml";

    @Test
    public void validNameWithUnderscoreAndSpace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid_name with space_and_underscore");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test
    public void validNameWithJapanese() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid name (有効名 in Japanese)");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test
    public void invalidNameWithSlash() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("slash/name");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        assertThatIllegalStateException().isThrownBy(config::validate);
    }

    @Test
    public void invalidNameWithSuffixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Suffixed with space ");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        assertThatIllegalStateException().isThrownBy(config::validate);
    }

    @Test
    public void invalidNameWithPrefixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName(" prefixed with space");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        assertThatIllegalStateException().isThrownBy(config::validate);
    }

    @Test
    public void noSolverConfigs() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setSolverBenchmarkConfigList(null);
        config.setSolverBenchmarkBluePrintConfigList(null);
        assertThatIllegalArgumentException().isThrownBy(config::validate);
    }

    @Test
    public void nonUniqueSolverConfigName() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        final String sbcName = "x";
        SolverBenchmarkConfig sbc1 = new SolverBenchmarkConfig();
        sbc1.setName(sbcName);
        SolverBenchmarkConfig sbc2 = new SolverBenchmarkConfig();
        sbc2.setName(sbcName);
        config.setSolverBenchmarkConfigList(Arrays.asList(sbc1, sbc2));
        assertThatIllegalStateException().isThrownBy(config::generateSolverBenchmarkConfigNames);
    }

    @Test
    public void uniqueNamesGenerated() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        SolverBenchmarkConfig sbc1 = new SolverBenchmarkConfig();
        SolverBenchmarkConfig sbc2 = new SolverBenchmarkConfig();
        SolverBenchmarkConfig sbc3 = new SolverBenchmarkConfig();
        sbc3.setName("Config_1");
        List<SolverBenchmarkConfig> configs = Arrays.asList(sbc1, sbc2, sbc3);
        config.setSolverBenchmarkConfigList(configs);
        config.generateSolverBenchmarkConfigNames();
        assertThat(sbc3.getName()).isEqualTo("Config_1");
        TreeSet<String> names = new TreeSet<>();
        for (SolverBenchmarkConfig sc : configs) {
            names.add(sc.getName());
        }
        for (int i = 0; i < configs.size(); i++) {
            assertThat(names).contains("Config_" + i);
        }
    }

    @Test
    public void resolveParallelBenchmarkCountAutomatically() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        assertThat(config.resolveParallelBenchmarkCountAutomatically(-1)).isEqualTo(1);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(0)).isEqualTo(1);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(1)).isEqualTo(1);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(2)).isEqualTo(1);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(3)).isEqualTo(2);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(4)).isEqualTo(2);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(5)).isEqualTo(3);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(6)).isEqualTo(4);
        assertThat(config.resolveParallelBenchmarkCountAutomatically(17)).isEqualTo(9);
    }

    @Test
    public void resolveParallelBenchmarkCountFromFormula() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setParallelBenchmarkCount(ConfigUtils.AVAILABLE_PROCESSOR_COUNT + "+1");
        // resolved benchmark count cannot be higher than available processors
        assertThat(config.resolveParallelBenchmarkCount()).isEqualTo(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void parallelBenchmarkDisabledByDefault() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        assertThat(config.resolveParallelBenchmarkCount()).isEqualTo(1);
    }

    @Test
    public void resolvedParallelBenchmarkCountNegative() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setParallelBenchmarkCount("-1");
        assertThatIllegalArgumentException().isThrownBy(config::resolveParallelBenchmarkCount);
    }

    @Test
    public void calculateWarmUpTimeMillisSpentLimit() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setWarmUpHoursSpentLimit(1L);
        config.setWarmUpMinutesSpentLimit(2L);
        config.setWarmUpSecondsSpentLimit(5L);
        config.setWarmUpMillisecondsSpentLimit(753L);
        assertThat(config.calculateWarmUpTimeMillisSpentLimit()).isEqualTo(3_725_753L);
    }

    @Test
    public void xmlConfigFileRemainsSameAfterReadWrite() throws IOException {
        JaxbIO<PlannerBenchmarkConfig> xmlIO = new JaxbIO<>(PlannerBenchmarkConfig.class);
        PlannerBenchmarkConfig jaxbBenchmarkConfig;

        try (Reader reader =
                new InputStreamReader(PlannerBenchmarkConfigTest.class.getResourceAsStream(TEST_PLANNER_BENCHMARK_CONFIG))) {
            jaxbBenchmarkConfig = xmlIO.read(reader);
        }

        Writer stringWriter = new StringWriter();
        xmlIO.write(jaxbBenchmarkConfig, stringWriter);
        String jaxbString = stringWriter.toString();

        String originalXml = IOUtils.toString(
                PlannerBenchmarkConfigTest.class.getResourceAsStream(TEST_PLANNER_BENCHMARK_CONFIG), StandardCharsets.UTF_8);

        assertThat(jaxbString.trim()).isEqualToNormalizingNewlines(originalXml.trim());
    }

    // Used by the testBenchmarkConfig.xml
    private static class TestdataSolutionFileIO extends XStreamSolutionFileIO<TestdataSolution> {
        private TestdataSolutionFileIO() {
            super(TestdataSolution.class);
        }
    }
}
