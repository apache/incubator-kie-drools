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
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;
import javax.rules.admin.RuleExecutionSetProvider;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.xml.XmlPackageReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

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
    	  try
          {
//    		 Prepare the DOM source
    	      Source source = new DOMSource( ruleExecutionSetElement );

    		  XmlPackageReader xmlPackageReader = new XmlPackageReader( );
              // Prepare the result
              SAXResult result = new SAXResult( xmlPackageReader );

              // Create a transformer
              Transformer xformer =
                  TransformerFactory.newInstance( ).newTransformer( );

              // Traverse the DOM tree
              xformer.transform( source, result );

              PackageDescr packageDescr = xmlPackageReader.getPackageDescr();

              //          pre build the package
              PackageBuilder builder = new PackageBuilder();
              builder.addPackage( packageDescr );
              Package pkg = builder.getPackage();

              LocalRuleExecutionSetProviderImpl localRuleExecutionSetProvider = new LocalRuleExecutionSetProviderImpl();
              return localRuleExecutionSetProvider.createRuleExecutionSet( pkg,
                                                                           properties );
          }
          catch ( TransformerException e )
          {
              throw new RuleExecutionSetCreateException(
                  "could not create RuleExecutionSet: " + e );
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
