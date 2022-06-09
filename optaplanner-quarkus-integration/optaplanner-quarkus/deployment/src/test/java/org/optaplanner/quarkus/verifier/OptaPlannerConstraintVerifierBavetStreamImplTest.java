package org.optaplanner.quarkus.verifier;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.optaplanner.test.impl.score.stream.DefaultConstraintVerifier;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerConstraintVerifierBavetStreamImplTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("org/optaplanner/quarkus/verifier/bavetSolverConfig.xml", "solverConfig.xml"));

    @Inject
    ConstraintVerifier<TestdataQuarkusConstraintProvider, TestdataQuarkusSolution> constraintVerifier;

    @Test
    void constraintVerifierBavetStreamImpl() {
        Assertions.assertEquals(ConstraintStreamImplType.BAVET,
                ((DefaultConstraintVerifier<?, ?, ?>) constraintVerifier)
                        .getConstraintStreamImplType());
        TestdataQuarkusSolution solution = new TestdataQuarkusSolution();
        TestdataQuarkusEntity entityA = new TestdataQuarkusEntity();
        TestdataQuarkusEntity entityB = new TestdataQuarkusEntity();
        entityA.setValue("A");
        entityB.setValue("A");

        solution.setEntityList(Arrays.asList(
                entityA, entityB));
        solution.setValueList(Arrays.asList("A", "B"));
        constraintVerifier.verifyThat().givenSolution(solution).scores(SimpleScore.of(-2));

        entityB.setValue("B");
        constraintVerifier.verifyThat().givenSolution(solution).scores(SimpleScore.ZERO);
    }
}
