package org.drools.jsr94.rules.admin;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetProvider;

//import org.apache.xerces.parsers.DOMParser;
import org.drools.compiler.PackageBuilder;
import org.drools.jsr94.rules.RuleEngineTestBase;
import org.drools.rule.Package;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;

/**
 * Test the RuleExecutionSetProvider implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
 */
public class RuleExecutionSetProviderTestCase extends RuleEngineTestBase
{
    private RuleAdministrator ruleAdministrator;

    private RuleExecutionSetProvider ruleSetProvider;

    private Package pkg;

    /**
     * Setup the test case.
     */
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        ruleAdministrator = ruleServiceProvider.getRuleAdministrator( );
        ruleSetProvider = ruleAdministrator.getRuleExecutionSetProvider( null );

        initPackage( );
    }

    private void initPackage( )
    {
        InputStream resourceAsStream = null;
        try
        {
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( RuleEngineTestBase.class.getResourceAsStream( bindUri ) ) );
            Package pkg = builder.getPackage();
           
           this.pkg = pkg;
        }
        catch ( IOException e )
        {
            throw new ExceptionInInitializerError(
                "setUp() could not init the " +
                "RuleSet due to an IOException in the InputStream: " + e );
        }
        catch ( Exception e )
        {
            throw new ExceptionInInitializerError(
                "setUp() could not init the RuleSet, " + e );
        }
        finally
        {
            if ( resourceAsStream != null )
            {
                try
                {
                    resourceAsStream.close( );
                }
                catch ( IOException e )
                {
                    e.printStackTrace( );
                }
            }
        }
    }

    protected void tearDown( )
    {
       pkg = null;
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
    public void testCreateFromSerializable( ) throws Exception
    {
        RuleExecutionSet ruleExecutionSet =
            ruleSetProvider.createRuleExecutionSet( this.pkg, null );
        assertEquals( "rule set name", "SistersRules",
                      ruleExecutionSet.getName( ) );
        assertEquals(
            "number of rules", 1, ruleExecutionSet.getRules( ).size( ) );
    }

    /**
     * Test createRuleExecutionSet from URI.
     */
    public void testCreateFromURI( ) throws Exception
    {
        String rulesUri =
            RuleEngineTestBase.class.getResource( bindUri ).toExternalForm( );
        RuleExecutionSet testRuleSet =
            ruleSetProvider.createRuleExecutionSet( rulesUri, null );
        assertEquals(
            "rule set name", "SistersRules", testRuleSet.getName( ) );
        assertEquals( "number of rules", 1, testRuleSet.getRules( ).size( ) );
    }

    public void testIncompatibleSerializableCreation( ) throws Exception
    {
        try
        {
            RuleExecutionSet testRuleSet =
                ruleSetProvider.createRuleExecutionSet( new ArrayList( ), null );
            fail( "Should have thrown an IllegalArgumentException. ArrayList " +
                  "objects are not valid AST representations. " + testRuleSet );
        }
        catch ( IllegalArgumentException e )
        {
            /*
             * this is supposed to happen if you pass in a serializable object
             * that isn't a supported AST representation.
             */
        }
    }
}
