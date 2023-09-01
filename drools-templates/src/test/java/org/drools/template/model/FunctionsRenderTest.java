package org.drools.template.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionsRenderTest {

    @Test
    public void testFunctionRender() {
        final Functions func = new Functions();

        DRLOutput out = new DRLOutput();
        func.renderDRL(out);

        assertThat(out.toString()).isEqualTo("");

        func.setFunctionsListing("function myFunction() {}");
        out = new DRLOutput();
        func.renderDRL(out);
        final String s = out.toString();
        assertThat(s).isEqualTo("function myFunction() {}\n");
    }

}
