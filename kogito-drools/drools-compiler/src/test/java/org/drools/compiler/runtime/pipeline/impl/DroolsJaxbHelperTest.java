/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.runtime.pipeline.impl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.io.impl.BaseResource;
import org.drools.core.io.impl.InputStreamResource;
import org.drools.core.rule.ConsequenceMetaData.Field;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import com.sun.tools.xjc.Options;

public class DroolsJaxbHelperTest {

    private static final String simpleXsdRelativePath = "simple.xsd";

    @Test
    public void testXsdModelInRule() {
        // DRL rule that references object created in XSD
        final String s1 = "package test; " +
                          "global java.util.List list; " +

                          "rule Init when " +
                          "then " +
                          "  insert( new Sub() ); " +
                          "  insert( new Message() ); " +
                          "  insert( new Test() ); " +
                          "  insert( new Left() ); " +
                          "end\n" +

                          "rule CheckSub when " +
                          " $s : Sub() " +
                          "then " +
                          "  list.add( \"Sub\" );  " +
                          "end\n" +

                          "rule CheckMsg when " +
                          " $s : Message() " +
                          "then " +
                          "  list.add( \"Message\" );  " +
                          "end\n ";

        KieHelper kh = new KieHelper();
        kh.addContent( s1, ResourceType.DRL );

        // XSD that defines "Sub" class
        InputStream simpleXsdStream = getClass().getResourceAsStream(simpleXsdRelativePath);
        assertNotNull( "Could not find resource: " + simpleXsdRelativePath,  simpleXsdStream );
        BaseResource xsdResource = new InputStreamResource(simpleXsdStream);

        Options xjcOptions = new Options();
        xsdResource.setConfiguration(new JaxbConfigurationImpl(xjcOptions, "test-system-id"));
        kh.addResource( xsdResource, ResourceType.XSD );

        // Verify that build succeeded
        assertEquals( 0, kh.verify().getMessages( Message.Level.ERROR ).size() );
        assertEquals( 0, kh.verify().getMessages( Message.Level.WARNING ).size() );

        // Run rules
        KieSession ks = kh.build().newKieSession();
        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        // Verify results
        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList("Sub", "Message") ) );

        Collection<FactHandle> fhs = ks.getFactHandles();

        Iterator<FactHandle> iter = fhs.iterator();
        DefaultFactHandle subFh = null, msgFh = null, leftFh = null, testFh = null;
        while( iter.hasNext() ) {
            DefaultFactHandle dfh = (DefaultFactHandle) iter.next();
            if( dfh.getObjectClassName().equals("test.Sub") ) {
                subFh = dfh;
            } else if( dfh.getObjectClassName().equals("test.Message") ) {
                msgFh = dfh;
            } else if( dfh.getObjectClassName().equals("test.Left") ) {
                leftFh = dfh;
            } else if( dfh.getObjectClassName().equals("test.Test") ) {
                testFh = dfh;
            } else {
                fail( "Unexpected FH class: " + dfh.getObjectClassName() );
            }
        }
        assertNotNull( "No FactHandle for Sub found!", subFh );
        assertNotNull( "No FactHandle for Message found!", msgFh );

        Object xsdObj = subFh.getObject();

        Class xsdClass = xsdObj.getClass();
        try {
            Method m2 = xsdClass.getMethod( "getFld" );
            assertNotNull( m2 );
            assertEquals( String.class, m2.getReturnType() );

            assertEquals( 0, xsdClass.getFields().length );
            java.lang.reflect.Field[] declaredFields = xsdClass.getDeclaredFields();
            assertEquals( 1, declaredFields.length );
            assertEquals( "fld", declaredFields[0].getName() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        // "Message" has attribute 'mixed="true"' which means only one field "content"
        xsdObj = msgFh.getObject();

        xsdClass = xsdObj.getClass();
        try {
            Method m2 = xsdClass.getMethod( "getContent" );
            assertNotNull( m2 );
            assertEquals( List.class, m2.getReturnType() );

            assertEquals( 0, xsdClass.getFields().length );
            java.lang.reflect.Field[] declaredFields = xsdClass.getDeclaredFields();
            assertEquals( 1, declaredFields.length );
            assertEquals( "content", declaredFields[0].getName() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

}