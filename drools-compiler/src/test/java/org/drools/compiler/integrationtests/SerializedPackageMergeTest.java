/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.Message;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import static org.junit.Assert.*;

public class SerializedPackageMergeTest {
    private static final DateFormat DF   = new SimpleDateFormat( "dd-MMM-yyyy", Locale.UK );
    private static final String[]   DRLs = {"HelloWorld.drl","test_Serialization1.drl"};

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

    private void testRuleExecution(StatelessKnowledgeSession session) throws Exception {
        List<Object> list = new ArrayList<Object>();
        session.setGlobal( "list",
                           list );

        session.execute( getObject() );

        assertEquals( 2,
                      list.size() );
    }

    private Message getObject() throws ParseException {
        Message message = new Message();

        message.setMessage( "hola" );
        message.setNumber( 50 );
        message.getList().add( "hello" );
        message.setBirthday( DF.parse( "10-Jul-1976" ) );
        return message;
    }

    private StatelessKnowledgeSession getSession(boolean serialize) throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        for ( String drl : DRLs ) {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newInputStreamResource(getClass().getResourceAsStream(drl)),
                          ResourceType.DRL );
            
            assertFalse( kbuilder.getErrors().toString(),
                         kbuilder.hasErrors() );
            
            Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();

            Collection<KnowledgePackage> newCollection = null;
            if ( serialize ) {
                newCollection = new ArrayList<KnowledgePackage>();
                for( KnowledgePackage kpkg : kpkgs) {
                    kpkg = SerializationHelper.serializeObject(kpkg);
                    newCollection.add( kpkg );
                }
            } else {
                newCollection = kpkgs;
            }
            kbase.addKnowledgePackages( newCollection );
        }
        return kbase.newStatelessKnowledgeSession();
    }

    @Test
    public void testBuildAndSerializePackagesWithSamePackageName() throws IOException, ClassNotFoundException    {
        // RHBRMS-2773
        String str1 =
                "package com.sample\n" +
                "import org.drools.compiler.Person\n" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "  $p : Person( name == \"John\" )\n" +
                "then\n" +
                "  list.add($p);" +
                "end\n";

        String str2 =
                "package com.sample\n" +
                "import org.drools.compiler.Person\n" +
                "global java.util.List list\n" +
                "rule R2 when\n" +
                "  $p : Person( name == \"Paul\" )\n" +
                "then\n" +
                "  list.add($p);" +
                "end\n";

        // Create 2 knowledgePackages separately (but these rules have the same package name)
        KnowledgeBuilder builder1 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder1.add( ResourceFactory.newByteArrayResource( str1.getBytes() ), ResourceType.DRL );
        Collection<KnowledgePackage> knowledgePackages1 = builder1.getKnowledgePackages();

        KnowledgeBuilder builder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
        Collection<KnowledgePackage> knowledgePackages2 = builder2.getKnowledgePackages();

        // Combine the knowledgePackages
        KnowledgeBase knowledgeBase1 = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase1.addKnowledgePackages( knowledgePackages1 );
        knowledgeBase1.addKnowledgePackages( knowledgePackages2 );
        Collection<KnowledgePackage> knowledgePackagesCombined = knowledgeBase1.getKnowledgePackages();

        // serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream( baos );
        out.writeObject( knowledgePackagesCombined );
        out.flush();
        out.close();

        // deserialize
        ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
        Collection<KnowledgePackage> deserializedPackages = (Collection<KnowledgePackage>) in.readObject();

        // Use the deserialized knowledgePackages
        KnowledgeBase knowledgeBase2 = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase2.addKnowledgePackages(deserializedPackages);

        KieSession ksession = knowledgeBase2.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert(new org.drools.compiler.Person("John"));
        ksession.insert(new org.drools.compiler.Person("Paul"));
        ksession.fireAllRules();

        assertEquals(2, list.size());

        ksession.dispose();
    }
}
