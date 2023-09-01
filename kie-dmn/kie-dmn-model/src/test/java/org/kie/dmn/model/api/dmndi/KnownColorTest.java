package org.kie.dmn.model.api.dmndi;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class KnownColorTest {

    @Test
    public void testFromValue() {
        assertThat(KnownColor.fromValue("maroon")).isEqualTo(KnownColor.MAROON).hasFieldOrPropertyWithValue("value", "maroon");
        assertThat(KnownColor.fromValue("red")).isEqualTo(KnownColor.RED);
        assertThat(KnownColor.fromValue("orange")).isEqualTo(KnownColor.ORANGE);
        assertThat(KnownColor.fromValue("yellow")).isEqualTo(KnownColor.YELLOW);
        assertThat(KnownColor.fromValue("olive")).isEqualTo(KnownColor.OLIVE);
        assertThat(KnownColor.fromValue("purple")).isEqualTo(KnownColor.PURPLE);
        assertThat(KnownColor.fromValue("fuchsia")).isEqualTo(KnownColor.FUCHSIA);
        assertThat(KnownColor.fromValue("white")).isEqualTo(KnownColor.WHITE);
        assertThat(KnownColor.fromValue("lime")).isEqualTo(KnownColor.LIME);
        assertThat(KnownColor.fromValue("green")).isEqualTo(KnownColor.GREEN);
        assertThat(KnownColor.fromValue("navy")).isEqualTo(KnownColor.NAVY);
        assertThat(KnownColor.fromValue("blue")).isEqualTo(KnownColor.BLUE);
        assertThat(KnownColor.fromValue("aqua")).isEqualTo(KnownColor.AQUA);
        assertThat(KnownColor.fromValue("teal")).isEqualTo(KnownColor.TEAL);
        assertThat(KnownColor.fromValue("black")).isEqualTo(KnownColor.BLACK);
        assertThat(KnownColor.fromValue("silver")).isEqualTo(KnownColor.SILVER);
        assertThat(KnownColor.fromValue("gray")).isEqualTo(KnownColor.GRAY);
        assertThatIllegalArgumentException().isThrownBy(() -> KnownColor.fromValue("asd"));
    }
}
