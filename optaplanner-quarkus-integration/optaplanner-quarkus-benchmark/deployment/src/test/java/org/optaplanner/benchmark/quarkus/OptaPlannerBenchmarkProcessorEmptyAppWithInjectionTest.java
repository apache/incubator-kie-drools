package org.optaplanner.benchmark.quarkus;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;

import io.quarkus.arc.Arc;
import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerBenchmarkProcessorEmptyAppWithInjectionTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.benchmark.solver.termination.best-score-limit", "0")
            .overrideConfigKey("quarkus.arc.unremovable-types", PlannerBenchmarkFactory.class.getName())
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses());

    @Test
    void emptyAppDoesNotCrash() {
        assertThatIllegalStateException().isThrownBy(() -> Arc.container().instance(PlannerBenchmarkFactory.class).get())
                .withMessageContaining("The " + PlannerBenchmarkFactory.class.getName() + " is not available as there are no");
    }

}
