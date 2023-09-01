package org.drools.template.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueriesRenderTest {

    @Test
    public void testQueriesRender() {
        final Queries queries = new Queries();

        DRLOutput out = new DRLOutput();
        queries.renderDRL(out);

        assertThat(out.toString()).isEqualTo("");

        queries.setQueriesListing("query myQuery(String value) Person() end");
        out = new DRLOutput();
        queries.renderDRL(out);
        final String s = out.toString();
        assertThat(s).isEqualTo("query myQuery(String value) Person() end\n");
    }

}
