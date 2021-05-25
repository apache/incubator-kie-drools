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

import static org.assertj.core.api.Assertions.assertThatCode;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.config.solver.SolverConfigTest;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedEntity;
import org.optaplanner.quarkus.gizmo.OptaPlannerGizmoBeanFactory;

import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.test.QuarkusUnitTest;

public class OptaPlannerProcessorGeneratedGizmoSupplierTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("org/optaplanner/core/config/solver/testSolverConfigWithoutNamespace.xml",
                            "solverConfig.xml")
                    .addClasses(
                            TestdataSolution.class,
                            TestdataEntity.class,
                            TestdataAnnotatedExtendedEntity.class,
                            SolverConfigTest.DummyChangeMoveFilter.class,
                            SolverConfigTest.DummyConstraintProvider.class,
                            SolverConfigTest.DummyEasyScoreCalculator.class,
                            SolverConfigTest.DummyEntityFilter.class,
                            SolverConfigTest.DummyIncrementalScoreCalculator.class,
                            SolverConfigTest.DummyMoveIteratorFactory.class,
                            SolverConfigTest.DummyMoveListFactory.class,
                            SolverConfigTest.DummySolutionPartitioner.class,
                            SolverConfigTest.DummyValueFilter.class))
            .addBuildChainCustomizer(buildChainBuilder -> buildChainBuilder.addBuildStep(context -> {
                context.produce(CapabilityBuildItem.class, new CapabilityBuildItem("kogito-rules"));
            }).produces(CapabilityBuildItem.class).build());

    @Inject
    OptaPlannerGizmoBeanFactory gizmoBeanFactory;

    private void assertFactoryContains(Class<?> clazz) {
        // All the classes are abstract, so they throw Instantiation Error,
        // yet you can still call new on them in bytecode
        assertThatCode(() -> gizmoBeanFactory.newInstance(clazz))
                .isInstanceOf(InstantiationError.class).hasMessage(clazz.getName());
    }

    @Test
    public void gizmoFactoryContainClassesReferencedInSolverConfig() {
        assertFactoryContains(SolverConfigTest.DummyChangeMoveFilter.class);
        assertFactoryContains(SolverConfigTest.DummyConstraintProvider.class);
        assertFactoryContains(SolverConfigTest.DummyEasyScoreCalculator.class);
        assertFactoryContains(SolverConfigTest.DummyEntityFilter.class);
        assertFactoryContains(SolverConfigTest.DummyIncrementalScoreCalculator.class);
        assertFactoryContains(SolverConfigTest.DummyMoveIteratorFactory.class);
        assertFactoryContains(SolverConfigTest.DummyMoveListFactory.class);
        assertFactoryContains(SolverConfigTest.DummySolutionPartitioner.class);
        assertFactoryContains(SolverConfigTest.DummyValueFilter.class);
    }

}
