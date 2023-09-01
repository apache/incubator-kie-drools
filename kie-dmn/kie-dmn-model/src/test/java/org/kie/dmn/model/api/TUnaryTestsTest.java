package org.kie.dmn.model.api;

import org.junit.Test;
import org.kie.dmn.model.v1_2.TDMNElement;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TUnaryTestsTest {

    @Test
    public void smokeTest() {
        UnaryTests ut = new STUnaryTests();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.getTypeRef());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.setTypeRef(null));
    }

    /**
     * Up to DMNv1.2.
     */
    private static class STUnaryTests extends TDMNElement implements UnaryTests {

        @Override
        public String getText() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getExpressionLanguage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExpressionLanguage(String value) {
            throw new UnsupportedOperationException();
        }

    }
}
