package org.kie.dmn.core.jsr223;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.jsr223.JSR223Utils.escapeIdentifierForBinding;

public class EscapeTest {

    @Test
    public void test() {
        assertThat(escapeIdentifierForBinding("123abc")).isEqualTo("_123abc");
        assertThat(escapeIdentifierForBinding("a+bc")).isEqualTo("a_bc");
        assertThat(escapeIdentifierForBinding("full name")).isEqualTo("full_name");
        assertThat(escapeIdentifierForBinding("previous incidents?")).isEqualTo("previous_incidents_");
    }
}
