package org.kie.dmn.model.api.dmndi;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class AlignmentKindTest {

    @Test
    public void testFromValue() {
        assertThat(AlignmentKind.fromValue("start")).isEqualTo(AlignmentKind.START).hasFieldOrPropertyWithValue("value", "start");
        assertThat(AlignmentKind.fromValue("end")).isEqualTo(AlignmentKind.END);
        assertThat(AlignmentKind.fromValue("center")).isEqualTo(AlignmentKind.CENTER);
        assertThatIllegalArgumentException().isThrownBy(() -> AlignmentKind.fromValue("asd"));
    }

}
