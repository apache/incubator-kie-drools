package org.kie.dmn.core.internal.utils;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.core.impl.DMNRuntimeImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNRuntimeBuilderTest {

    private DMNRuntimeBuilder dmnRuntimeBuilder;

    @Before
    public void setup() {
        dmnRuntimeBuilder = DMNRuntimeBuilder.fromDefaults();
        assertThat(dmnRuntimeBuilder).isNotNull();
    }

    @Test
    public void buildFromConfiguration() {
        final DMNRuntimeImpl retrieved = (DMNRuntimeImpl) dmnRuntimeBuilder
                .buildConfiguration()
                .fromResources(Collections.emptyList()).getOrElseThrow(RuntimeException::new);
        assertThat(retrieved).isNotNull();
    }
}