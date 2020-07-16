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

package org.optaplanner.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.quarkus.testdata.chained.constraints.TestdataChainedQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.chained.domain.TestdataChainedQuarkusAnchor;
import org.optaplanner.quarkus.testdata.chained.domain.TestdataChainedQuarkusEntity;
import org.optaplanner.quarkus.testdata.chained.domain.TestdataChainedQuarkusObject;
import org.optaplanner.quarkus.testdata.chained.domain.TestdataChainedQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

public class OptaPlannerProcessorChainedXMLNoneTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(
                            TestdataChainedQuarkusObject.class,
                            TestdataChainedQuarkusAnchor.class,
                            TestdataChainedQuarkusEntity.class,
                            TestdataChainedQuarkusSolution.class,
                            TestdataChainedQuarkusConstraintProvider.class));

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataChainedQuarkusSolution> solverFactory;

    @Test
    public void solverConfigXml_default() {
        assertThat(solverConfig).isNotNull();
        assertThat(solverConfig.getSolutionClass()).isEqualTo(TestdataChainedQuarkusSolution.class);
        assertThat(solverConfig.getEntityClassList()).containsExactlyInAnyOrder(
                TestdataChainedQuarkusObject.class,
                TestdataChainedQuarkusEntity.class);
        assertThat(solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass())
                .isEqualTo(TestdataChainedQuarkusConstraintProvider.class);
        // No termination defined (solverConfig.xml isn't included)
        assertThat(solverConfig.getTerminationConfig().getSecondsSpentLimit()).isNull();
        assertThat(solverFactory).isNotNull();
        assertThat(solverFactory.buildSolver()).isNotNull();
    }

}
