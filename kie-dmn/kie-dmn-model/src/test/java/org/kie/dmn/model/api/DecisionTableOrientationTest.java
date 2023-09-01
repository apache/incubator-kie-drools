package org.kie.dmn.model.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class DecisionTableOrientationTest {

    @Test
    public void testFromValue() {
        assertThat(DecisionTableOrientation.fromValue("Rule-as-Row")).isEqualTo(DecisionTableOrientation.RULE_AS_ROW);
        assertThat(DecisionTableOrientation.fromValue("Rule-as-Column")).isEqualTo(DecisionTableOrientation.RULE_AS_COLUMN);
        assertThat(DecisionTableOrientation.fromValue("CrossTable")).isEqualTo(DecisionTableOrientation.CROSS_TABLE);
        assertThatIllegalArgumentException().isThrownBy(() -> DecisionTableOrientation.fromValue("asd"));
    }

}
