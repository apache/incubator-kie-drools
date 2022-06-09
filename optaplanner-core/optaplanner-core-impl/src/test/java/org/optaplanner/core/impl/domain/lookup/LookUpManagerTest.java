package org.optaplanner.core.impl.domain.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectIntegerId;

class LookUpManagerTest {

    private LookUpManager lookUpManager;

    @BeforeEach
    void setUpLookUpManager() {
        lookUpManager = new LookUpManager(
                new LookUpStrategyResolver(DomainAccessType.REFLECTION, LookUpStrategyType.PLANNING_ID_OR_NONE));
    }

    @Test
    void lookUpNull() {
        assertThat(lookUpManager.<Object> lookUpWorkingObject(null)).isNull();
    }

    @Test
    void resetWorkingObjects() {
        TestdataObjectIntegerId o = new TestdataObjectIntegerId(0);
        TestdataObjectIntegerId p = new TestdataObjectIntegerId(1);
        // The objects should be added during the reset
        lookUpManager.reset();
        for (Object fact : Arrays.asList(o, p)) {
            lookUpManager.addWorkingObject(fact);
        }
        // So it's possible to look up and remove them
        assertThat(lookUpManager.lookUpWorkingObject(new TestdataObjectIntegerId(0))).isSameAs(o);
        assertThat(lookUpManager.lookUpWorkingObject(new TestdataObjectIntegerId(1))).isSameAs(p);
        lookUpManager.removeWorkingObject(o);
        lookUpManager.removeWorkingObject(p);
    }

}
