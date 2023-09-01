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
package org.drools.mvel.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.compiler.Message;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SerializedPackageMergeTest {

    // kpackage serialization is not supported. But leave it for standard-drl.

    private static final DateFormat DF   = new SimpleDateFormat( "dd-MMM-yyyy", Locale.UK );
    private static final String[]   DRLs = {"drl/HelloWorld.drl","test_Serialization1.drl"};

    @Test
    public void testRuleExecutionWithoutSerialization() {
        try {
            // without serialization, it works.
            testRuleExecution( getSession( false ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not have raised any exception. Message: " + e.getMessage() );
        }
    }

    @Test
    public void testRuleExecutionWithSerialization() throws Exception {
        try {
            // with serialized packages, NullPointerException
            testRuleExecution( getSession( true ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not have raised any exception. Message: " + e.getMessage() );
        }
    }

    private void testRuleExecution(StatelessKieSession session) throws Exception {
        List<Object> list = new ArrayList<Object>();
        session.setGlobal( "list",
                           list );

        session.execute( getObject() );

        assertThat(list.size()).isEqualTo(2);
    }

    private Message getObject() throws ParseException {
        Message message = new Message();

        message.setMessage( "hola" );
        message.setNumber( 50 );
        message.getList().add( "hello" );
        message.setBirthday( DF.parse( "10-Jul-1976" ) );
        return message;
    }

    private StatelessKieSession getSession(boolean serialize) throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        for ( String drl : DRLs ) {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newInputStreamResource(getClass().getResourceAsStream(drl)),
                          ResourceType.DRL );

            assertThat(kbuilder.hasErrors()).as(kbuilder.getErrors().toString()).isFalse();
            
            Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();

            Collection<KiePackage> newCollection = null;
            if ( serialize ) {
                newCollection = new ArrayList<KiePackage>();
                for( KiePackage kpkg : kpkgs) {
                    kpkg = SerializationHelper.serializeObject(kpkg);
                    newCollection.add( kpkg );
                }
            } else {
                newCollection = kpkgs;
            }
            kbase.addPackages( newCollection );
        }
        return kbase.newStatelessKieSession();
    }

    @Test
    public void testBuildAndSerializePackagesWithSamePackageName() throws IOException, ClassNotFoundException    {
        // RHBRMS-2773
        String str1 =
                "package com.sample\n" +
                "import org.drools.mvel.compiler.Person\n" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "  $p : Person( name == \"John\" )\n" +
                "then\n" +
                "  list.add($p);" +
                "end\n";

        String str2 =
                "package com.sample\n" +
                "import org.drools.mvel.compiler.Person\n" +
                "global java.util.List list\n" +
                "rule R2 when\n" +
                "  $p : Person( name == \"Paul\" )\n" +
                "then\n" +
                "  list.add($p);" +
                "end\n";

        // Create 2 knowledgePackages separately (but these rules have the same package name)
        KnowledgeBuilder builder1 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder1.add( ResourceFactory.newByteArrayResource( str1.getBytes() ), ResourceType.DRL );
        Collection<KiePackage> knowledgePackages1 = builder1.getKnowledgePackages();

        KnowledgeBuilder builder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
        Collection<KiePackage> knowledgePackages2 = builder2.getKnowledgePackages();

        // Combine the knowledgePackages
        InternalKnowledgeBase knowledgeBase1 = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase1.addPackages( knowledgePackages1 );
        knowledgeBase1.addPackages( knowledgePackages2 );
        Collection<KiePackage> knowledgePackagesCombined = knowledgeBase1.getKiePackages();

        // serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream( baos );
        out.writeObject( knowledgePackagesCombined );
        out.flush();
        out.close();

        // deserialize
        ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
        Collection<KiePackage> deserializedPackages = (Collection<KiePackage>) in.readObject();

        // Use the deserialized knowledgePackages
        InternalKnowledgeBase knowledgeBase2 = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase2.addPackages(deserializedPackages);

        KieSession ksession = knowledgeBase2.newKieSession();
        try {
            List<String> list = new ArrayList<String>();
            ksession.setGlobal( "list", list );
            ksession.insert(new org.drools.mvel.compiler.Person("John"));
            ksession.insert(new org.drools.mvel.compiler.Person("Paul"));
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }
}
