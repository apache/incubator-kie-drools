package org.kie.dmn.core.compiler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeModeOptionTest {

    @Test
    void getRuntimeModeOption() {
        RuntimeModeOption runtimeModeOption = new RuntimeModeOption("strict");
        assertThat(runtimeModeOption).isNotNull();
        assertThat(runtimeModeOption.getRuntimeMode()).isEqualTo(RuntimeModeOption.MODE.STRICT);
        runtimeModeOption = new RuntimeModeOption("lenient");
        assertThat(runtimeModeOption).isNotNull();
        assertThat(runtimeModeOption.getRuntimeMode()).isEqualTo(RuntimeModeOption.MODE.LENIENT);
        runtimeModeOption = new RuntimeModeOption("test");
        assertThat(runtimeModeOption).isNotNull();
        assertThat(runtimeModeOption.getRuntimeMode()).isEqualTo(RuntimeModeOption.MODE.LENIENT);

    }

    @Test
    void getModeFromString() {
        String modeName = "strict";
        assertThat(RuntimeModeOption.MODE.getModeFromString(modeName)).isEqualTo(RuntimeModeOption.MODE.STRICT);
        modeName = "lenient";
        assertThat(RuntimeModeOption.MODE.getModeFromString(modeName)).isEqualTo(RuntimeModeOption.MODE.LENIENT);
        modeName = "test";
        assertThat(RuntimeModeOption.MODE.getModeFromString(modeName)).isEqualTo(RuntimeModeOption.MODE.LENIENT);

    }
}