/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.io.ResourceFactory;

public class ConsequenceOffsetTest {
    
    @Test
    public void testConsequenceOffset() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" )), ResourceType.DRL);

        assertFalse(kbuilder.hasErrors());

        int offset = -1;
        assertEquals(false, kbuilder.hasErrors());
        for (RuleDescr rule : ((KnowledgeBuilderImpl)kbuilder).getPackageDescrs("com.sample").get(0).getRules()) {
            if (rule.getName().equals("test")) {
                offset = rule.getConsequenceOffset();
            }
        }

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset2.drl" )), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" )), ResourceType.DRL);

        assertFalse(kbuilder.hasErrors());
        for (RuleDescr rule : ((KnowledgeBuilderImpl)kbuilder).getPackageDescrs("com.sample").get(0).getRules()) {
            if (rule.getName().equals("test")) {
                assertEquals(offset, rule.getConsequenceOffset());
                return;
            }
        }
        fail();
    }
    
    @Test
    public void testLargeSetOfImports() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffsetImports.drl" )), ResourceType.DRL);

        assertFalse(kbuilder.hasErrors());
    }
    
}
