/*
 * Copyright 2011 JBoss Inc
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

package org.drools.integrationtests;

import static junit.framework.Assert.assertEquals;

import java.io.StringReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.conf.MBeansOption;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationsTest {

    @Test
    public void testRuleAnnotation() {
        String drl = "package org.drools\n" +
                     "rule X\n" +
                     "    @author(\"John Doe\")\n" +
                     "    @output(Hello World!)\n" + // backward compatibility
                     "    @value( 10 + 10 )\n" +
                     "    @alt( \"Hello \"+\"World!\" )\n" +
                     "when\n" +
                     "    Person()\n" +
                     "then\n" +
                     "end";
        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        conf.setOption( MBeansOption.ENABLED );

        KnowledgeBase kbase = loadKnowledgeBase( "kb1",
                                                 drl,
                                                 conf );

        Rule rule = kbase.getRule( "org.drools",
                                   "X" );

        Assert.assertEquals( "John Doe",
                             rule.getMetaData().get( "author" ) );
        Assert.assertEquals( "Hello World!",
                             rule.getMetaData().get( "output" ) );
        Assert.assertEquals( 20,
                             ((Number)rule.getMetaData().get( "value" )).intValue() );
        Assert.assertEquals( "Hello World!",
                             rule.getMetaData().get( "alt" ) );

    }

    private KnowledgeBase loadKnowledgeBase( String id,
                                             String drl,
                                             KnowledgeBaseConfiguration conf ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        Assert.assertFalse( kbuilder.getErrors().toString(),
                            kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( id,
                                                                     conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

}
