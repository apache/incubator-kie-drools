package org.kie.dmn.validation.dtanalysis.model;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class NullBoundImplTest {

    public static final Logger LOG = LoggerFactory.getLogger(NullBoundImplTest.class);

    /**
     * assert the requirement over NullBoundImpl.NULL to always throw exception if attempting to use it.
     */
    @Test
    public void test() {
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.compareTo(new Bound<>(BigDecimal.ONE, Range.RangeBoundary.CLOSED, null)));
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getValue());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getBoundaryType());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.getParent());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.isLowerBound());
        assertThatIllegalStateException().isThrownBy(() -> NullBoundImpl.NULL.isUpperBound());
    }

    @Test
    public void testToStringInLogger() {
        LOG.info("{}", NullBoundImpl.NULL); // this could sometimes happen in debug mode
    }
}
