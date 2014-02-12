/*
 * Copyright 2010 JBoss Inc
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

package org.drools.jsr94.rules.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetProvider;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.jsr94.rules.RuleEngineTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the RuleExecutionSetProvider implementation.
 */
public class RuleExecutionSetProviderTest extends RuleEngineTestBase {
    private RuleAdministrator        ruleAdministrator;

    private RuleExecutionSetProvider ruleSetProvider;

    private InternalKnowledgePackage pkg;

    /**
     * Setup the test case.
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();
        this.ruleSetProvider = this.ruleAdministrator.getRuleExecutionSetProvider( null );

        initPackage();
    }

    private void initPackage() {
        final InputStream resourceAsStream = null;
        try {
            final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
            builder.addPackageFromDrl( new InputStreamReader( RuleEngineTestBase.class.getResourceAsStream( this.bindUri ) ) );
            InternalKnowledgePackage pkg = builder.getPackage();

            this.pkg = pkg;
        } catch ( final IOException e ) {
            throw new ExceptionInInitializerError( "setUp() could not init the " + "RuleSet due to an IOException in the InputStream: " + e );
        } catch ( final Exception e ) {
            throw new ExceptionInInitializerError( "setUp() could not init the RuleSet, " + e );
        } finally {
            if ( resourceAsStream != null ) {
                try {
                    resourceAsStream.close();
                } catch ( final IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    @After
    public void tearDown() {
        this.pkg = null;
    }

    //     Rule files are no XML
    //    /**
    //     * Test createRuleExecutionSet from DOM.
    //     */
    //    public void testCreateFromElement( ) throws Exception
    //    {
    //        DOMParser parser = new DOMParser( );
    //        Document doc = null;
    //        try
    //        {
    //            parser.parse( new InputSource(
    //                RuleEngineTestBase.class.getResourceAsStream( bindUri ) ) );
    //            doc = parser.getDocument( );
    //        }
    //        catch ( SAXException e )
    //        {
    //            fail( "could not parse incoming data stream: " + e );
    //        }
    //        catch ( IOException e )
    //        {
    //            fail( "could not open incoming data stream: " + e );
    //        }
    //        Element element = null;
    //        NodeList children = doc.getChildNodes( );
    //        if ( children != null )
    //        {
    //            for ( int i = 0; i < children.getLength( ); i++ )
    //            {
    //                Node child = children.item( i );
    //                if ( Node.ELEMENT_NODE == child.getNodeType( ) )
    //                {
    //                    element = ( Element ) child;
    //                }
    //            }
    //        }
    //
    //        if ( element != null )
    //        {
    //            RuleExecutionSet testRuleSet =
    //                ruleSetProvider.createRuleExecutionSet( element, null );
    //            assertEquals(
    //                "rule set name", "Sisters Rules", testRuleSet.getName( ) );
    //            assertEquals(
    //                "number of rules", 1, testRuleSet.getRules( ).size( ) );
    //        }
    //        else
    //        {
    //            fail( "could not build an org.w3c.dom.Element" );
    //        }
    //    }

    /**
     * Test createRuleExecutionSet from Serializable.
     */
    @Test
    public void testCreateFromSerializable() throws Exception {
        final RuleExecutionSet ruleExecutionSet = this.ruleSetProvider.createRuleExecutionSet( this.pkg,
                                                                                               null );
        assertEquals( "rule set name",
                      "SistersRules",
                      ruleExecutionSet.getName() );
        assertEquals( "number of rules",
                      1,
                      ruleExecutionSet.getRules().size() );
    }

    /**
     * Test createRuleExecutionSet from URI.
     */
    @Test
    public void testCreateFromURI() throws Exception {
        final String rulesUri = RuleEngineTestBase.class.getResource( this.bindUri ).toExternalForm();
        final RuleExecutionSet testRuleSet = this.ruleSetProvider.createRuleExecutionSet( rulesUri,
                                                                                          null );
        assertEquals( "rule set name",
                      "SistersRules",
                      testRuleSet.getName() );
        assertEquals( "number of rules",
                      1,
                      testRuleSet.getRules().size() );
    }

    @Test
    public void testIncompatibleSerializableCreation() throws Exception {
        try {
            final RuleExecutionSet testRuleSet = this.ruleSetProvider.createRuleExecutionSet( new ArrayList(),
                                                                                              null );
            fail( "Should have thrown an IllegalArgumentException. ArrayList " + "objects are not valid AST representations. " + testRuleSet );
        } catch ( final IllegalArgumentException e ) {
            /*
             * this is supposed to happen if you pass in a serializable object
             * that isn't a supported AST representation.
             */
        }
    }
}
