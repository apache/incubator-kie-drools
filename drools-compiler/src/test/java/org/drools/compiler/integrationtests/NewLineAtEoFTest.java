/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

/**
 * Tests an error appearing when DLR does not contain a new line at the end of
 * the file.
 */
public class NewLineAtEoFTest {

    private static final String drl =
            "package org.jboss.qa.brms.commentend\n"
                    + "rule simple\n"
                    + "    when\n"
                    + "    then\n"
                    + "        System.out.println(\"Hello world!\");\n"
                    + "end\n";

    @Test
    public void testNoNewlineAtTheEnd() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(drl + "//test")), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
    }

    @Test
    public void testNewlineAtTheEnd() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(drl)), ResourceType.DRL);

        assertFalse(kbuilder.hasErrors());
    }
}
