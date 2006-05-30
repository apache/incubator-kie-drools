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
import java.io.StringReader;
import java.util.Map;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;

import org.drools.IntegrationException;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/**
 * The Drools implementation of the <code>LocalRuleExecutionSetProvider</code>
 * interface which defines <code>RuleExecutionSet</code> creation methods for
 * defining <code>RuleExecutionSet</code>s from local (non-serializable)
 * resources.
 * 
 * @see LocalRuleExecutionSetProvider
 * 
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
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
        try {
            final PackageBuilder builder = new PackageBuilder();
            if ( properties != null && properties.containsKey( "dsl" ) ) {
                final Reader dsl = new StringReader( (String) properties.get( "dsl" ) );
                builder.addPackageFromDrl( new InputStreamReader( ruleExecutionSetStream ),
                                           dsl );
            } else {
                builder.addPackageFromDrl( new InputStreamReader( ruleExecutionSetStream ) );
            }
            final Package pkg = builder.getPackage();
            return this.createRuleExecutionSet( pkg,
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
            final PackageBuilder builder = new PackageBuilder();
            if ( properties != null && properties.containsKey( "dsl" ) ) {
                final Reader dsl = new StringReader( (String) properties.get( "dsl" ) );
                builder.addPackageFromDrl( ruleExecutionSetReader,
                                           dsl );
            } else {
                builder.addPackageFromDrl( ruleExecutionSetReader );
            }

            final Package pkg = builder.getPackage();
            return this.createRuleExecutionSet( pkg,
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
