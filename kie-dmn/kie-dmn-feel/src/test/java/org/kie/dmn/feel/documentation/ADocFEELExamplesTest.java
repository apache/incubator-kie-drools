/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.documentation;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.Section;
import org.asciidoctor.ast.StructuralNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADocFEELExamplesTest {

    private static final Logger LOG = LoggerFactory.getLogger(ADocFEELExamplesTest.class);
    private static final List<FEELProfile> profiles = new ArrayList<>();
    {
        profiles.add(new KieExtendedFEELProfile());
    }
    private final FEEL feel = FEEL.newInstance(profiles);

    /**
     * Dev notes: the availability of the .adoc resource to this test and its refresh is governed by Maven.
     * You might want to execute this test from CLI if the IDE is not able handle the maven build refresh properly, for example as:
     * $ mvn test -Dtest=org.kie.dmn.feel.documentation.ADocFEELExamplesTest
     */
    @Test
    public void test() throws URISyntaxException {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        URL resource = this.getClass().getResource("/FEELbuiltinfunctions.adoc");
        URI uri = resource.toURI();
        LOG.debug("{}", uri);
        File src = new File(uri);
        Document loadFile = asciidoctor.loadFile(src, OptionsBuilder.options().asMap());
        processBlock(loadFile);
    }

    private void processBlock(StructuralNode block) {
        List<StructuralNode> blocks = block.getBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            final StructuralNode currentBlock = blocks.get(i);
            if (currentBlock instanceof StructuralNode) {
                if ("listing".equals(currentBlock.getContext())) {
                    Block b = (Block) currentBlock;
                    List<String> lines = b.getLines();
                    LOG.trace("{}", lines);
                    LOG.trace("{}", b.getAttributes());
                    ContentNode parent = b.getParent();
                    String sectionTitle = "";
                    if (parent instanceof Section) {
                        Section section = (Section) parent;
                        sectionTitle = section.getTitle();
                    }
                    if (b.getAttribute("language", "unknown").equals("FEEL")) {
                        for (String l : lines) {
                            String titled = sectionTitle + ": " + l;
                            LOG.info("checking DOC {}", titled);
                            Object FEELResult = feel.evaluate(l);
                            Assert.assertThat(titled, FEELResult, Matchers.is(true));
                        }
                    } else {
                        LOG.trace("This block is not FEEL true predicate snippets: {}", b);
                    }
                } else {
                    processBlock(currentBlock);
                }
            }
        }
    }


}
