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
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;

import org.drools.IntegrationException;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.jsr94.rules.Constants;
import org.drools.rule.Package;

/**
 * The Drools implementation of the <code>LocalRuleExecutionSetProvider</code>
 * interface which defines <code>RuleExecutionSet</code> creation methods for
 * defining <code>RuleExecutionSet</code>s from local (non-serializable)
 * resources.
 *
 * @see LocalRuleExecutionSetProvider
 */
public class LocalRuleExecutionSetProviderImpl
    implements
    LocalRuleExecutionSetProvider {
    /** Default constructor. */
    public LocalRuleExecutionSetProviderImpl() {
        super();
    }

    /**
     * Creates a <code>RuleExecutionSet</code> implementation using a supplied
     * input stream and additional Drools-specific properties. A Drools-specific
     * rule execution set is read from the supplied InputStream. The method
     * <code>createRuleExecutionSet</code> taking a Reader instance should be
     * used if the source is a character stream and encoding conversion should
     * be performed.
     *
     * @param ruleExecutionSetStream
     *            an input stream used to read the rule execution set.
     * @param properties
     *            additional properties used to create the
     *            <code>RuleExecutionSet</code> implementation. May be
     *            <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException
     *             on rule execution set creation error.
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSet createRuleExecutionSet(final InputStream ruleExecutionSetStream,
                                                   final Map properties) throws RuleExecutionSetCreateException {
        if ( properties != null ) {
            String source = ( String ) properties.get( Constants.RES_SOURCE );
            if ( source == null ) {
                // support legacy name
                source = ( String ) properties.get( "source" );
            }
            if ( source != null && source.equals( Constants.RES_SOURCE_TYPE_DECISION_TABLE ) ) {
                final SpreadsheetCompiler converter = new SpreadsheetCompiler();
                final String drl = converter.compile( ruleExecutionSetStream,
                                                      InputType.XLS );
                return createRuleExecutionSet( new StringReader( drl ), properties );
            } else {
                return createRuleExecutionSet( new InputStreamReader( ruleExecutionSetStream ), properties);
            }
        } else         
            return createRuleExecutionSet( new InputStreamReader( ruleExecutionSetStream ), properties);
    }
    


    /**
     * Creates a <code>RuleExecutionSet</code> implementation using a supplied
     * character stream Reader and additional Drools-specific properties. A
     * Drools-specific rule execution set is read from the supplied Reader.
     *
     * @param ruleExecutionSetReader
     *            a Reader used to read the rule execution set.
     * @param properties
     *            additional properties used to create the
     *            <code>RuleExecutionSet</code> implementation. May be
     *            <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException
     *             on rule execution set creation error.
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSet createRuleExecutionSet(final Reader ruleExecutionSetReader,
                                                   final Map properties) throws RuleExecutionSetCreateException {
        try {
            PackageBuilderConfiguration config= null;
            
            if ( properties != null ) {
                config = (PackageBuilderConfiguration) properties.get( Constants.RES_PACKAGEBUILDER_CONFIG );
            }
            
            PackageBuilder builder = null;
            if ( config != null ) {
                builder = new PackageBuilder(config);
            } else {
                builder = new PackageBuilder();
            }
            
            Object dsrl = null;
            String source = null;
            
            if ( properties != null ) {
                dsrl = properties.get( Constants.RES_DSL );
                if ( dsrl ==  null ) {
                    // check for old legacy name ending
                    dsrl = properties.get( "dsl" );
                }
                source = ( String ) properties.get( Constants.RES_SOURCE );
                if ( source == null ) {
                    // check for old legacy name ending
                    source = ( String ) properties.get( "source" );
                }
            }
            
            if ( source == null ) {
                source = "drl";
            }
            
            if ( dsrl == null ) {
                if ( source.equals( Constants.RES_SOURCE_TYPE_XML ) || source.equals( "xml" ) ) {
                    builder.addPackageFromXml( ruleExecutionSetReader );
                } else {
                    builder.addPackageFromDrl( ruleExecutionSetReader );
                }
            } else {
                if ( source.equals( Constants.RES_SOURCE_TYPE_XML ) || source.equals( "xml" ) ) {
                    // xml cannot specify a dsl
                    builder.addPackageFromXml( ruleExecutionSetReader );
                } else {
                    if  ( dsrl instanceof Reader ) {
                        builder.addPackageFromDrl( ruleExecutionSetReader,
                                                   (Reader) dsrl );
                    } else {
                        builder.addPackageFromDrl( ruleExecutionSetReader,
                                                   new StringReader( (String) dsrl ) );
                    }
                }
            }
            
            final Package pkg = builder.getPackage();
            return createRuleExecutionSet( pkg,
                                           properties );
        } catch ( final IOException e ) {
            throw new RuleExecutionSetCreateException( "cannot create rule execution set",
                                                       e );
        } catch ( final DroolsParserException e ) {
            throw new RuleExecutionSetCreateException( "cannot create rule execution set",
                                                       e );
        }
    }
    
    
    /**
     * Creates a <code>RuleExecutionSet</code> implementation from a
     * Drools-specific AST representation and Drools-specific properties.
     *
     * @param ruleExecutionSetAst
     *            the vendor representation of a rule execution set
     * @param properties
     *            additional properties used to create the
     *            <code>RuleExecutionSet</code> implementation. May be
     *            <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException
     *             on rule execution set creation error.
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSet createRuleExecutionSet(final Object ruleExecutionSetAst,
                                                   final Map properties) throws RuleExecutionSetCreateException {
        if ( ruleExecutionSetAst instanceof Package ) {
            final Package pkg = (Package) ruleExecutionSetAst;
            return this.createRuleExecutionSet( pkg,
                                                properties );
        }
        throw new RuleExecutionSetCreateException( " Incoming AST object must be an org.drools.rule.Package.  Was " + ruleExecutionSetAst.getClass() );
    }

    /**
     * Creates a <code>RuleExecutionSet</code> implementation from a
     * <code>RuleSet</code> and Drools-specific properties.
     *
     * @param pkg
     *            a Drools <code>org.drools.rule.Package</code> representation
     *            of a rule execution set.
     * @param properties
     *            additional properties used to create the RuleExecutionSet
     *            implementation. May be <code>null</code>.
     *
     * @throws RuleExecutionSetCreateException
     *             on rule execution set creation error.
     *
     * @return The created <code>RuleExecutionSet</code>.
     */
    private RuleExecutionSet createRuleExecutionSet(final Package pkg,
                                                    final Map properties) throws RuleExecutionSetCreateException {
        try {
            return new RuleExecutionSetImpl( pkg,
                                             properties );
        } catch ( final IntegrationException e ) {
            throw new RuleExecutionSetCreateException( "Failed to create RuleExecutionSet",
                                                       e );
        }
    }
}
