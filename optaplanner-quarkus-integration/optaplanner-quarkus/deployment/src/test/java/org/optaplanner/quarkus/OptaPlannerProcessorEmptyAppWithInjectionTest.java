package org.optaplanner.quarkus;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.solver.SolverManager;

import io.quarkus.arc.Arc;
import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorEmptyAppWithInjectionTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses())
            .overrideConfigKey("quarkus.arc.unremovable-types", "org.optaplanner.core.api.solver.SolverManager");

    @Test
    void emptyAppInjectingSolverManagerCrashes() {
        assertThatIllegalStateException().isThrownBy(() -> Arc.container().instance(SolverManager.class).get())
                .withMessageContaining("The " + SolverManager.class.getName() + " is not available as there are no");
    }

}
