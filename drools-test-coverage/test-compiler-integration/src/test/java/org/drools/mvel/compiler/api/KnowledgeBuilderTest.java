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
package org.drools.mvel.compiler.api;

import java.util.Collection;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;


public class KnowledgeBuilderTest {

    @Test
    public void testKnowledgeProvider() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        assertThat(builder).isNotNull();
    }

    @Test
    public void testKnowledgeProviderWithRules() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        String str = "";
        str += "package org.drools.mvel.compiler.test1\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule2\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        str = "package org.drools.mvel.compiler.test2\n";
        str += "rule rule3\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule4\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        Collection<KiePackage> pkgs = builder.getKnowledgePackages();
        assertThat(pkgs).isNotNull();
        assertThat(pkgs.size()).isEqualTo(2);

        KiePackage test1 = getKnowledgePackage(pkgs, "org.drools.mvel.compiler.test1" );
        Collection<Rule> rules = test1.getRules();
        assertThat(rules.size()).isEqualTo(2);
        Rule rule = getRule( rules, "rule1" );
        assertThat(rule.getName()).isEqualTo("rule1");
        rule = getRule( rules, "rule2" );
        assertThat(rule.getName()).isEqualTo("rule2");

        KiePackage test2 = getKnowledgePackage(pkgs, "org.drools.mvel.compiler.test2" );
        rules = test2.getRules();
        assertThat(rules.size()).isEqualTo(2);
        rule = getRule( rules, "rule3" );
        assertThat(rule.getName()).isEqualTo("rule3");
        rule = getRule( rules, "rule4" );
        assertThat(rule.getName()).isEqualTo("rule4");
    }

    public Rule getRule(Collection<Rule> rules, String name) {
        for ( Rule rule : rules ) {
            if ( rule.getName().equals( name ) ) {
                return rule;
            }
        }
        return null;
    }

    public KiePackage getKnowledgePackage(Collection<KiePackage> pkgs, String name) {
        for ( KiePackage pkg : pkgs ) {
            if ( pkg.getName().equals( name ) ) {
                return pkg;
            }
        }
        return null;
    }
    
    @Test
    public void testMalformedDrl() throws Exception {
        // DROOLS-928
        byte[] content = new byte[]{0x04,0x44,0x00,0x00,0x60,0x00,0x00,0x00};
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", new String(content) );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertThat(results.getMessages().size() > 0).isTrue();
    }
}
