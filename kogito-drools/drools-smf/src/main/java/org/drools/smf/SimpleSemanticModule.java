package org.drools.smf;

/*
 * $Id: SimpleSemanticModule.java,v 1.10 2005/02/04 02:13:38 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a Semantic Module.
 *
 * @see org.drools.spi
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public class SimpleSemanticModule implements SemanticModule
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** URI of this module. */
    private String uri;

    /** Rule factories. */
    private Map    ruleFactories;

    /** Object type factories. */
    private Map    objectTypeFactories;

    /** Condition factories. */
    private Map    conditionFactories;

    /** Consequence factories. */
    private Map    consequenceFactories;

    private Map    durationFactories;

    /** Imports factories */
    private Map    importEntryFactories;

    /** ApplicationData factories */
    private Map    applicationDataFactories;   
    
    /** functions factories */
    private Map    functionFactories;    
    
    private Map    types;


    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /**
     * Construct with a URI.
     *
     * @param uri The URI the identifies this semantic module.
     */
    public SimpleSemanticModule(String uri)
    {
        this.uri = uri;

        this.ruleFactories = new HashMap( );
        this.objectTypeFactories = new HashMap( );
        this.conditionFactories = new HashMap( );
        this.consequenceFactories = new HashMap( );
        this.durationFactories = new HashMap( );
        this.importEntryFactories = new HashMap( );
        this.applicationDataFactories = new HashMap( ); 
        this.functionFactories = new HashMap( );    
        this.types = new HashMap( );
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /**
     * @see SemanticModule
     */
    public String getUri()
    {
        return this.uri;
    }
    
    public String getType(String name)
    {
        return (String) this.types.get(name);
    }

    public void addRuleFactory(String name, RuleFactory factory)
    {
        this.ruleFactories.put( name, factory );
        this.types.put(name, "Rule");
    }

    /**
     * @see SemanticModule
     */
    public RuleFactory getRuleFactory(String name)
    {
        return ( RuleFactory ) this.ruleFactories.get( name );
    }

    public Set getRuleFactoryNames()
    {
        return this.ruleFactories.keySet( );
    }

    /**
     * Add a semantic <code>ObjectTypeFactory</code>.
     *
     * @param name The object type name.
     * @param factory The object type factory.
     *
     */
    public void addObjectTypeFactory(String name, ObjectTypeFactory factory)
    {
        this.objectTypeFactories.put( name, factory );
        this.types.put(name, "ObjectType");
    }

    /**
     * @see SemanticModule
     */
    public ObjectTypeFactory getObjectTypeFactory(String name)
    {
        return ( ObjectTypeFactory ) this.objectTypeFactories.get( name );
    }

    /**
     * @see SemanticModule
     */
    public Set getObjectTypeFactoryNames()
    {
        return this.objectTypeFactories.keySet( );
    }

    /**
     * Add a semantic <code>ConditionFactory</code>.
     *
     * @param name The condition name.
     * @param factory The condition factory.
     */
    public void addConditionFactory(String name, ConditionFactory factory)
    {
        this.conditionFactories.put( name, factory );
        this.types.put(name, "Condition");
    }

    /**
     * @see SemanticModule
     */
    public ConditionFactory getConditionFactory(String name)
    {
        return ( ConditionFactory ) this.conditionFactories.get( name );
    }

    /**
     * @see SemanticModule
     */
    public Set getConditionFactoryNames()
    {
        return this.conditionFactories.keySet( );
    }

    /**
     * Add a semantic <code>ConsequenceFactory</code>.
     *
     * @param name The consequence name.
     * @param factory The consequence factory.
     */
    public void addConsequenceFactory(String name, ConsequenceFactory factory)
    {
        this.consequenceFactories.put( name, factory );
        this.types.put(name, "Consequence");
    }

    /**
     * @see SemanticModule
     */
    public ConsequenceFactory getConsequenceFactory(String name)
    {
        return ( ConsequenceFactory ) this.consequenceFactories.get( name );
    }

    /**
     * @see SemanticModule
     */
    public Set getConsequenceFactoryNames()
    {
        return this.consequenceFactories.keySet( );
    }

    public void addDurationFactory(String name, DurationFactory factory)
    {
        this.durationFactories.put( name, factory );
        this.types.put(name, "Duration");
    }

    public DurationFactory getDurationFactory(String name)
    {
        return ( DurationFactory ) this.durationFactories.get( name );
    }

    public Set getDurationFactoryNames()
    {
        return this.durationFactories.keySet( );
    }

    public void addImportEntryFactory(String name, ImportEntryFactory factory)
    {
        this.importEntryFactories.put( name, factory );
        this.types.put(name, "ImportEntry");
    }

    public ImportEntryFactory getImportEntryFactory(String name)
    {
        return ( ImportEntryFactory ) this.importEntryFactories.get( name );
    }

    public Set getImportEntryFactoryNames()
    {
        return this.importEntryFactories.keySet( );
    }    

    public void addApplicationDataFactory(String name, ApplicationDataFactory factory)
    {
        this.applicationDataFactories.put( name, factory );
        this.types.put(name, "ApplicationData");
    }

    public ApplicationDataFactory getApplicationDataFactory(String name)
    {
        return ( ApplicationDataFactory ) this.applicationDataFactories.get( name );
    }

    public Set getApplicationDataFactoryNames()
    {
        return this.applicationDataFactories.keySet( );
    }   
    
    public void addFunctionsFactory(String name, FunctionsFactory factory)
    {
        this.functionFactories.put( name, factory );
        this.types.put(name, "Functions");
    }    

    public FunctionsFactory getFunctionsFactory(String name)
    {
        return ( FunctionsFactory ) this.functionFactories.get( name );
    }

    public Set getFunctionsFactoryNames()
    {
        return this.functionFactories.keySet();
    }    
}