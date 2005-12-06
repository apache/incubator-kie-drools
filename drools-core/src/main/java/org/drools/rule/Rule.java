package org.drools.rule;

/*
 * $Id: Rule.java,v 1.4 2005/12/04 05:07:52 mproctor Exp $
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.spi.Condition;
import org.drools.spi.Consequence;
import org.drools.spi.Duration;
import org.drools.spi.Extractor;
import org.drools.spi.Importer;
import org.drools.spi.Module;
import org.drools.spi.ObjectType;

/**
 * A <code>Rule</code> contains a set of <code>Test</code>s and a
 * <code>Consequence</code>.
 * <p>
 * The <code>Test</code>s describe the circumstances that
 * representrepresent a match for this rule. The <code>Consequence</code> gets
 * fired when the Conditions match.
 *
 * @see Condition
 * @see Consequence
 * @author <a href="mailto:bob@eng.werken.com"> bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au"> Simon Harris </a>
 * @author <a href="mailto:mproctor@codehaus.org"> mark pro </a>
 */
public class Rule
    implements
    Serializable
{
    /**   */
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    /** The parent ruleSet */
    private RuleSet      ruleSet;

    /** Name of the rule. */
    private final String name;

    /** Documentation. */
    private String       documentation;

    /** Salience value. */
    private int          salience;

    /** Columns */
    private final List   columns      = new ArrayList();

    private final Map    declarations = new HashMap();

    private final And    headPattern  = new And();    

    private final String module;

    /** Consequence. */
    private Consequence  consequence;

    /** Truthness duration. */
    private Duration     duration;

    /** Load order in RuleSet */
    private long         loadOrder;

    /** Is recursion of this rule allowed */
    private boolean      noLoop;

    /** makes the rule's much the current focus */
    private boolean      autoFocus;

    /** A map valid Application names and types */
    private Map          applicationData;

    /** The Importer to use, as specified by the RuleSet */
    private Importer     importer;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified ruleSet parent
     *
     * @param name
     *            The name of this rule.
     */
    public Rule(String name,
                RuleSet ruleSet,
                String module)
    {
        this.name = name;
        this.ruleSet = ruleSet;
        this.module = module;
        this.applicationData = Collections.EMPTY_MAP;
    }

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified ruleSet parent
     *
     * @param name
     *            The name of this rule.
     */
    public Rule(String name,
                String module)
    {
        this.name = name;
        this.ruleSet = null;
        this.module = module;
        this.applicationData = Collections.EMPTY_MAP;
    }

    public Rule(String name)
    {
        this.name = name;
        this.ruleSet = null;
        this.module = Module.MAIN;
        this.applicationData = Collections.EMPTY_MAP;
    }

    /**
     * Set the documentation.
     *
     * @param documentation -
     *        The documentation.
     */
    public void setDocumentation(String documentation)
    {
        this.documentation = documentation;
    }

    /**
     * Retrieve the documentation.
     *
     * @return The documentation or <code>null</code> if none.
     */
    public String getDocumentation()
    {
        return this.documentation;
    }

    /**
     * Set the truthness duration. This causes a delay before the firing of the
     * <code>Consequence</code> if the rule is still true at the end of the
     * duration.
     *
     * <p>
     * This is merely a convenience method for calling
     * {@link #setDuration(Duration)}with a <code>FixedDuration</code>.
     * </p>
     *
     * @see #setDuration(Duration)
     * @see FixedDuration
     *
     * @param seconds -
     *        The number of seconds the rule must hold true in order to fire.
     */
    public void setDuration(long seconds)
    {
        this.duration = new FixedDuration( seconds );
    }

    /**
     * Set the truthness duration object. This causes a delay before the firing of the
     * <code>Consequence</code> if the rule is still true at the end of the
     * duration.
     *
     * @param duration
     *        The truth duration object.
     */
    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }

    /**
     * Retrieve the truthness duration object.
     *
     * @return The truthness duration object.
     */
    public Duration getDuration()
    {
        return this.duration;
    }

    /**
     * Determine if this rule is internally consistent and valid.
     *
     * No exception is thrown.
     * <p>
     * A <code>Rule</code> must include at least one parameter declaration and
     * one condition.
     * </p>
     *
     * @return <code>true</code> if this rule is valid, else
     *         <code>false</code>.
     */
    public boolean isValid()
    {
        if ( this.columns.size() == 0 )
        {
            return false;
        }

        if ( this.consequence == null )
        {
            return false;
        }

        return true;
    }

    /**
     * Check the validity of this rule, and throw exceptions if it fails
     * validity tests.
     *
     * <p>
     * Possibly exceptions include:
     * </p>
     *
     * <pre>
     * NoParameterDeclarationException
     * NoConsequenceException
     * </pre>
     *
     * <p>
     * A <code>Rule</code> must include at least one parameter declaration and
     * one condition.
     * </p>
     *
     * @throws InvalidRuleException
     *         if this rule is in any way invalid.
     */
    public void checkValidity() throws InvalidRuleException
    {
        if ( this.columns.isEmpty() )
        {
            throw new NoColumnsException( this );
        }

        if ( this.consequence == null )
        {
            throw new NoConsequenceException( this );
        }
    }

    public RuleSet getRuleSet()
    {
        return this.ruleSet;
    }

    /**
     * Retrieve the name of this rule.
     *
     * @return The name of this rule.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Retrieve the <code>Rule</code> salience.
     *
     * @return The salience.
     */
    public int getSalience()
    {
        return this.salience;
    }

    /**
     * Set the <code>Rule<code> salience.
     *
     *  @param salience The salience.
     */
    public void setSalience(int salience)
    {
        this.salience = salience;
    }

    public boolean getNoLoop()
    {
        return this.noLoop;
    }

    public void setNoLoop(boolean noLoop)
    {
        this.noLoop = noLoop;
    }

    public boolean getAutoFocus()
    {
        return this.autoFocus;
    }

    public void setAutoFocus(boolean autoFocus)
    {
        this.autoFocus = autoFocus;
    }

    /**
     * Add a <i>root fact object </i> parameter <code>Declaration</code> for
     * this <code>Rule</code>.
     *
     * @param identifier
     *        The identifier.
     * @param objectType
     *        The type.
     * @return The declaration.
     */
    public Declaration addDeclaration(String identifier,
                                      ObjectType objectType,
                                      int column,
                                      Extractor extractor) throws InvalidRuleException
    {
        if ( getDeclaration( identifier ) != null )
        {
            throw new InvalidRuleException( this );
        }

        Declaration declaration = new Declaration( this.declarations.size(),
                                                   identifier,
                                                   objectType,
                                                   extractor,
                                                   column );

        this.declarations.put( identifier,
                               declaration );

        return declaration;
    }

    /**
     * Retrieve a parameter <code>Declaration</code> by identifier.
     *
     * @param identifier
     *        The identifier.
     *
     * @return The declaration or <code>null</code> if no declaration matches
     *         the <code>identifier</code>.
     */
    public Declaration getDeclaration(String identifier)
    {
        return (Declaration) this.declarations;
    }

    /**
     * Retrieve the set of all <i>root fact object </i> parameter
     * <code>Declarations</code>.
     *
     * @return The Set of <code>Declarations</code> in order which specify the
     *         <i>root fact objects</i>.
     */
    public Collection getDeclarations()
    {
        return Collections.unmodifiableCollection( this.declarations.values() );
    }

    /**
     * Add a <code>Test</code> to this rule.
     *
     * @param condition
     *        The <code>Test</code> to add.
     */
    public void addPattern(ConditionalElement ce)
    {
        this.headPattern.addChild( ce );
    }

    /**
     * Retrieve the <code>List</code> of <code>Conditions</code> for this
     * rule.
     *
     * @return The <code>List</code> of <code>Conditions</code>.
     */
    public And getPatterns()
    {
        return this.headPattern;
    }
    
    /**
     * Uses the LogicTransformer to process the Rule patters - if no ORs are used this will return an
     * array of a single AND element. If there are Ors it will return an And element for each possible 
     * logic branch. The processing uses as a clone of the Rule's patterns, so they are not changed.
     * 
     * @return
     */
    public And[] getProcessPatterns()
    {
        return LogicTransformer.getInstance( ).transform( this.headPattern );
    }

    public int getPatternSize()
    {
        //return this.conditions.size();
        return determinePatternSize( this.headPattern );
    }
    
    private int determinePatternSize(ConditionalElement ce)
    {
        Object object = null;
        Iterator it = ce.getChildren().iterator();
        int size = 0;
        while ( it.hasNext() )
        {
            object = it.next();
            if ( object instanceof ConditionalElement )
            {
                size++;
                size += determinePatternSize( ( ConditionalElement ) object );                
            }
        }
        return size;
    }

    /**
     * Set the <code>Consequence</code> that is associated with the successful
     * match of this rule.
     *
     * @param consequence
     *        The <code>Consequence</code> to attach to this <code>Rule</code>.
     */
    public void setConsequence(Consequence consequence)
    {
        this.consequence = consequence;
    }

    /**
     * Retrieve the <code>Consequence</code> associated with this
     * <code>Rule</code>.
     *
     * @return The <code>Consequence</code>.
     */
    public Consequence getConsequence()
    {
        return this.consequence;
    }

    public long getLoadOrder()
    {
        return loadOrder;
    }

    void setLoadOrder(long loadOrder)
    {
        this.loadOrder = loadOrder;
    }

    public Importer getImporter()
    {
        return this.importer;
    }

    public void setImporter(Importer importer)
    {
        this.importer = importer;
    }

    public void setApplicationData(Map applicationData)
    {
        this.applicationData = applicationData;
    }

    public Map getApplicationData()
    {
        return this.applicationData;
    }

    public String getModule()
    {
        return this.module;
    }

}
