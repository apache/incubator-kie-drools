/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.template.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueriesRenderTest {

    @Test
    public void testQueriesRender() {
        final Queries queries = new Queries();

        DRLOutput out = new DRLOutput();
        queries.renderDRL(out);

        assertEquals("",
                     out.toString());

        queries.setQueriesListing("query myQuery(String value) Person() end");
        out = new DRLOutput();
        queries.renderDRL(out);
        final String s = out.toString();
        assertEquals("query myQuery(String value) Person() end\n",
                     s);
    }

}
