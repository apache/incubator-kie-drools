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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.optaplanner.benchmark.impl.io.PlannerBenchmarkConfigIO;
import org.optaplanner.core.impl.io.OptaPlannerXmlSerializationException;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

class PlannerBenchmarkConfigTest {

    private static final String TEST_PLANNER_BENCHMARK_CONFIG_WITH_NAMESPACE = "testBenchmarkConfigWithNamespace.xml";
    private static final String TEST_PLANNER_BENCHMARK_CONFIG_WITHOUT_NAMESPACE = "testBenchmarkConfigWithoutNamespace.xml";

    @Test
    void validNameWithUnderscoreAndSpace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid_name with space_and_underscore");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test
    void validNameWithJapanese() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid name (有効名 in Japanese)");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test
    void invalidNameWithSlash() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("slash/name");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        assertThatIllegalStateException().isThrownBy(config::validate);
    }

    @Test
    void invalidNameWithSuffixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Suffixed with space ");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        assertThatIllegalStateException().isThrownBy(config::validate);
    }

    @Test
    void invalidNameWithPrefixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName(" prefixed with space");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        assertThatIllegalStateException().isThrownBy(config::validate);
    }

    @Test
    void noSolverConfigs() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setSolverBenchmarkConfigList(null);
        config.setSolverBenchmarkBluePrintConfigList(null);
        assertThatIllegalArgumentException().isThrownBy(config::validate);
    }

    @Test
    void nonUniqueSolverConfigName() {
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
    void uniqueNamesGenerated() {
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
    void resolveParallelBenchmarkCountAutomatically() {
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
    void parallelBenchmarkDisabledByDefault() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        assertThat(config.resolveParallelBenchmarkCount()).isEqualTo(1);
    }

    @Test
    void resolvedParallelBenchmarkCountNegative() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setParallelBenchmarkCount("-1");
        assertThatIllegalArgumentException().isThrownBy(config::resolveParallelBenchmarkCount);
    }

    @Test
    void calculateWarmUpTimeMillisSpentLimit() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setWarmUpHoursSpentLimit(1L);
        config.setWarmUpMinutesSpentLimit(2L);
        config.setWarmUpSecondsSpentLimit(5L);
        config.setWarmUpMillisecondsSpentLimit(753L);
        assertThat(config.calculateWarmUpTimeMillisSpentLimit()).isEqualTo(3_725_753L);
    }

    @ParameterizedTest
    @ValueSource(strings = { TEST_PLANNER_BENCHMARK_CONFIG_WITHOUT_NAMESPACE, TEST_PLANNER_BENCHMARK_CONFIG_WITH_NAMESPACE })
    void xmlConfigFileRemainsSameAfterReadWrite(String xmlBenchmarkConfigResource) throws IOException {
        PlannerBenchmarkConfigIO xmlIO = new PlannerBenchmarkConfigIO();
        PlannerBenchmarkConfig jaxbBenchmarkConfig;

        try (Reader reader = new InputStreamReader(
                PlannerBenchmarkConfigTest.class.getResourceAsStream(xmlBenchmarkConfigResource))) {
            jaxbBenchmarkConfig = xmlIO.read(reader);
        }

        assertThat(jaxbBenchmarkConfig).isNotNull();

        Writer stringWriter = new StringWriter();
        xmlIO.write(jaxbBenchmarkConfig, stringWriter);
        String jaxbString = stringWriter.toString();

        String originalXml = IOUtils.toString(PlannerBenchmarkConfigTest.class.getResourceAsStream(xmlBenchmarkConfigResource),
                StandardCharsets.UTF_8);

        // During writing the benchmark config, the benchmark element's namespace is removed.
        String benchmarkElementWithNamespace =
                PlannerBenchmarkConfig.XML_ELEMENT_NAME + " xmlns=\"" + PlannerBenchmarkConfig.XML_NAMESPACE + "\"";
        if (originalXml.contains(benchmarkElementWithNamespace)) {
            originalXml = originalXml.replace(benchmarkElementWithNamespace, PlannerBenchmarkConfig.XML_ELEMENT_NAME);
        }
        assertThat(jaxbString).isXmlEqualTo(originalXml);
    }

    @Test
    void readAndValidateInvalidBenchmarkConfig_failsIndicatingTheIssue() {
        PlannerBenchmarkConfigIO xmlIO = new PlannerBenchmarkConfigIO();
        String benchmarkConfigXml = "<plannerBenchmark xmlns=\"https://www.optaplanner.org/xsd/benchmark\">\n"
                + "  <benchmarkDirectory>data</benchmarkDirectory>\n"
                + "  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>\n"
                + "  <solverBenchmark>\n"
                + "    <name>Entity Tabu Search</name>\n"
                + "    <solver>\n"
                // Intentionally wrong to simulate a typo.
                + "      <solutionKlazz>org.optaplanner.core.impl.testdata.domain.TestdataSolution</solutionKlazz>\n"
                + "      <entityClass>org.optaplanner.core.impl.testdata.domain.TestdataEntity</entityClass>\n"
                + "    </solver>\n"
                + "    <problemBenchmarks>\n"
                + "      <solutionFileIOClass>org.optaplanner.benchmark.config.PlannerBenchmarkConfigTest$TestdataSolutionFileIO</solutionFileIOClass>\n"
                + "      <inputSolutionFile>nonExistingDataset1.xml</inputSolutionFile>\n"
                + "    </problemBenchmarks>\n"
                + "  </solverBenchmark>\n"
                + "</plannerBenchmark>\n";

        StringReader stringReader = new StringReader(benchmarkConfigXml);
        assertThatExceptionOfType(OptaPlannerXmlSerializationException.class)
                .isThrownBy(() -> xmlIO.read(stringReader))
                .withMessageContaining("Invalid content was found")
                .withMessageContaining("solutionKlazz");
    }

    // Used by the testBenchmarkConfig.xml
    private static class TestdataSolutionFileIO extends XStreamSolutionFileIO<TestdataSolution> {
        private TestdataSolutionFileIO() {
            super(TestdataSolution.class);
        }
    }
}
