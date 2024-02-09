/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.ast.descr.RuleDescr;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConsequenceOffsetTest {
    
    @Test
    public void testConsequenceOffset() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" )), ResourceType.DRL);

        assertThat(kbuilder.hasErrors()).isFalse();

        int offset = -1;
        assertThat(kbuilder.hasErrors()).isFalse();
        for (final RuleDescr rule : ((KnowledgeBuilderImpl)kbuilder).getPackageDescrs("com.sample").get(0).getRules()) {
            if (rule.getName().equals("test")) {
                offset = rule.getConsequenceOffset();
            }
        }

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset2.drl" )), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" )), ResourceType.DRL);

        assertThat(kbuilder.hasErrors()).isFalse();
        for (final RuleDescr rule : ((KnowledgeBuilderImpl)kbuilder).getPackageDescrs("com.sample").get(0).getRules()) {
            if (rule.getName().equals("test")) {
                assertThat(rule.getConsequenceOffset()).isEqualTo(offset);
                return;
            }
        }
        fail("Unexpected point in test");
    }
    
    @Test
    public void testLargeSetOfImports() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffsetImports.drl" )), ResourceType.DRL);

        assertThat(kbuilder.hasErrors()).isFalse();
    }
    
}
