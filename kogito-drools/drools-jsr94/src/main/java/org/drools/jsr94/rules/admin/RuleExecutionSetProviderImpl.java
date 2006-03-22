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
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;
import javax.rules.admin.RuleExecutionSetProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Drools implementation of the <code>RuleExecutionSetProvider</code>
 * interface which defines <code>RuleExecutionSet</code> creation methods for
 * defining <code>RuleExecutionSet</code>s from potentially serializable
 * resources.
 *
 * @see RuleExecutionSetProvider
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
 */
public class RuleExecutionSetProviderImpl
    implements
    RuleExecutionSetProvider {
    /**
     * Creates a <code>RuleExecutionSet</code> implementation from an XML
     * Document and additional Drools-specific properties. A Drools-specific
     * rule execution set is read from the supplied XML Document.
     *
     * @param ruleExecutionSetElement the XML element that is the source of the
     *        rule execution set
     * @param properties additional properties used to create the
     *        <code>RuleExecutionSet</code> implementation.
     *        May be <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException on rule execution set creation
     *         error.
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSet createRuleExecutionSet(Element ruleExecutionSetElement,
                                                   Map properties) throws RuleExecutionSetCreateException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        try {
            /*
             * This should unpack rules from a xml-file, which has the following structure:
             * <rule-execution-set>
                 <name>RuleExecutionSet2</name>
                     <description>Stateful RuleExecutionSet for the TCK for Drools</description>
                     <code>
                     <!-- enter rules code for Drools 3 here -->
                     </code>
                     </rule-execution-set>
             */
            docBuilder = factory.newDocumentBuilder();
            Document doc1 = docBuilder.newDocument();
            doc1.appendChild( ruleExecutionSetElement );
            Reader reader = new StringReader( ruleExecutionSetElement.getChildNodes().item( 2 ).getNodeValue() );

            DrlParser parser = new DrlParser();
            PackageDescr packageDescr;
            packageDescr = parser.parse( reader );

            //          pre build the package
            PackageBuilder builder = new PackageBuilder();
            builder.addPackage( packageDescr );
            Package pkg = builder.getPackage();

            LocalRuleExecutionSetProviderImpl localRuleExecutionSetProvider = new LocalRuleExecutionSetProviderImpl();
            return localRuleExecutionSetProvider.createRuleExecutionSet( pkg,
                                                                         properties );

        } catch ( ParserConfigurationException e ) {
            throw new RuleExecutionSetCreateException( "could not create RuleExecutionSet: " + e );
        } catch ( IOException e ) {
            throw new RuleExecutionSetCreateException( "could not create RuleExecutionSet: " + e );
        } catch ( DroolsParserException e ) {
            throw new RuleExecutionSetCreateException( "could not create RuleExecutionSet: " + e );
        }

    }

    /**
     * Creates a <code>RuleExecutionSet</code> implementation from a
     * Drools-specific Abstract Syntax Tree (AST) representation and
     * Drools-specific properties.
     * <p/>
     * This method accepts a <code>org.drools.RuleBase</code> object as its
     * vendor-specific AST representation.
     *
     * @param ruleExecutionSetAst the Drools representation of a
     *        rule execution set
     * @param properties additional properties used to create the
     *        <code>RuleExecutionSet</code> implementation.
     *        May be <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException on rule execution set creation
     *         error.
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSet createRuleExecutionSet(Serializable ruleExecutionSetAst,
                                                   Map properties) throws RuleExecutionSetCreateException {
        if ( ruleExecutionSetAst instanceof Package ) {
            LocalRuleExecutionSetProviderImpl localRuleExecutionSetProvider = new LocalRuleExecutionSetProviderImpl();
            return localRuleExecutionSetProvider.createRuleExecutionSet( ruleExecutionSetAst,
                                                                         properties );
        } else {
            throw new IllegalArgumentException( "Serializable object must be " + "an instance of org.drools.rule.RuleSet.  It was " + ruleExecutionSetAst.getClass().getName() );
        }
    }

    /**
     * Creates a <code>RuleExecutionSet</code> implementation from a URI.
     * The URI is opaque to the specification and may be used to refer to the
     * file system, a database, or Drools-specific datasource.
     *
     * @param ruleExecutionSetUri the URI to load the rule execution set from
     * @param properties additional properties used to create the
     *        <code>RuleExecutionSet</code> implementation.
     *        May be <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException on rule execution set creation
     *         error.
     * @throws IOException if an I/O error occurs while accessing the URI
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSet createRuleExecutionSet(String ruleExecutionSetUri,
                                                   Map properties) throws RuleExecutionSetCreateException,
                                                                  IOException {
        InputStream in = null;
        try {
            LocalRuleExecutionSetProviderImpl localRuleExecutionSetProvider = new LocalRuleExecutionSetProviderImpl();
            in = new URL( ruleExecutionSetUri ).openStream();
            Reader reader = new InputStreamReader( in );
            return localRuleExecutionSetProvider.createRuleExecutionSet( reader,
                                                                         properties );
        } catch ( IOException ex ) {
            throw ex;
        } finally {
            if ( in != null ) {
                in.close();
            }
        }
    }
}
