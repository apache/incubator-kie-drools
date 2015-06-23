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

package org.drools.compiler.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;


public class KnowledgeBuilderTest {

    @Test
    public void testKnowledgeProvider() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        assertNotNull( builder );
    }

    @Test
    public void testKnowledgeProviderWithRules() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        String str = "";
        str += "package org.drools.compiler.test1\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule2\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        str = "package org.drools.compiler.test2\n";
        str += "rule rule3\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule4\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
        assertNotNull( pkgs );
        assertEquals( 2, pkgs.size() );

        KnowledgePackage test1 = getKnowledgePackage(pkgs, "org.drools.compiler.test1" );
        Collection<Rule> rules = test1.getRules();
        assertEquals( 2, rules.size() );
        Rule rule = getRule( rules, "rule1" );
        assertEquals("rule1", rule.getName() );
        rule = getRule( rules, "rule2" );
        assertEquals( "rule2", rule.getName() );

        KnowledgePackage test2 = getKnowledgePackage(pkgs, "org.drools.compiler.test2" );
        rules = test2.getRules();
        assertEquals( 2, rules.size() );
        rule = getRule( rules, "rule3" );
        assertEquals("rule3", rule.getName() );
        rule = getRule( rules, "rule4" );
        assertEquals( "rule4", rule.getName() );
    }

    public Rule getRule(Collection<Rule> rules, String name) {
        for ( Rule rule : rules ) {
            if ( rule.getName().equals( name ) ) {
                return rule;
            }
        }
        return null;
    }

    public KnowledgePackage getKnowledgePackage(Collection<KnowledgePackage> pkgs, String name) {
        for ( KnowledgePackage pkg : pkgs ) {
            if ( pkg.getName().equals( name ) ) {
                return pkg;
            }
        }
        return null;
    }
    
    @Test
    public void testEmptyByteResource() throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            builder.add(ResourceFactory.newByteArrayResource(new byte[0]), ResourceType.DRL);
            fail();
        } catch ( IllegalArgumentException e ) {
            
        }
    }    

}
