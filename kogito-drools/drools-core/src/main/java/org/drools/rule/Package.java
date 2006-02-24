package org.drools.rule;

/*
 * $Id: RuleSet.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.spi.Functions;
import org.drools.spi.RuleBaseContext;
import org.drools.spi.TypeResolver;

/**
 * Collection of related <code>Rule</code>s.
 * 
 * @see Rule
 * 
 * @author <a href="mail:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: RuleSet.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 */
public class Package
    implements
    Serializable {
    // ------------------------------------------------------------
    // Constants`
    // ------------------------------------------------------------

    /** Empty <code>RuleSet</code> array. */
    public static final Package[] EMPTY_ARRAY = new Package[0];

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Name of the pkg. */
    private String                name;

    /** Set of all rule-names in this <code>RuleSet</code>. */
    private Set                   ruleNames;

    /** Ordered list of all <code>Rules</code> in this <code>RuleSet</code>. */
    private List                  rules;

    private List                  imports;

    private Map                   globals;

    private Map                   functions;
    
    private TypeResolver          typeResolver;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param name
     *            The name of this <code>RuleSet</code>.
     */
    public Package(String name) {
        this.name = name;
        this.imports = new ArrayList(1);
        this.ruleNames = new HashSet();
        this.rules = new ArrayList();
        this.globals = new HashMap();
        this.functions = new HashMap();
    }

    /**
     * Construct.
     * 
     * @param name
     *            The name of this <code>RuleSet</code>.
     * @param ruleBaseContext
     */
    public Package(String name,
                   RuleBaseContext ruleBaseContext) {
        this.name = name;
        this.ruleNames = new HashSet();
        this.rules = new ArrayList();
        this.globals = new HashMap();
        this.functions = new HashMap();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the name of this <code>RuleSet</code>.
     * 
     * @return The name of this <code>RuleSet</code>.
     */
    public String getName() {
        return this.name;
    }
    
    public void addImport(String importEntry) {
        this.imports.add( importEntry );
    }

    public List getImports() {
        return this.imports;
    }
    
    /**
     * Add a <code>Rule</code> to this <code>RuleSet</code>.
     * 
     * @param rule
     *            The rule to add.
     * 
     * @throws DuplicateRuleNameException
     *             If the <code>Rule</code> attempting to be added has the
     *             same name as another previously added <code>Rule</code>.
     * @throws InvalidRuleException
     *             If the <code>Rule</code> is not valid.
     */
    public void addRule(Rule rule) throws DuplicateRuleNameException,
                                  InvalidRuleException {
        rule.checkValidity();

        String name = rule.getName();

        if ( containsRule( name ) ) {
            throw new DuplicateRuleNameException( this,
                                                  getRule( name ),
                                                  rule );
        }

        this.ruleNames.add( name );
        rule.setLoadOrder( this.rules.size() );
        this.rules.add( rule );
    }

    /**
     * Retrieve a <code>Rule</code> by name.
     * 
     * @param name
     *            The name of the <code>Rule</code> to retrieve.
     * 
     * @return The named <code>Rule</code>, or <code>null</code> if not
     *         such <code>Rule</code> has been added to this
     *         <code>RuleSet</code>.
     */
    public Rule getRule(String name) {
        Rule[] rules = getRules();

        for ( int i = 0; i < rules.length; ++i ) {
            if ( rules[i].getName().equals( name ) ) {
                return rules[i];
            }
        }

        return null;
    }

    /**
     * Determine if this <code>RuleSet</code> contains a <code>Rule</code
     *  with the specified name.
     *
     *  @param name The name of the <code>Rule</code>.
     *
     *  @return <code>true</code> if this <code>RuleSet</code> contains a
     *          <code>Rule</code> with the specified name, else <code>false</code>.
     */
    public boolean containsRule(String name) {
        return this.ruleNames.contains( name );
    }

    /**
     * Retrieve all <code>Rules</code> in this <code>RuleSet</code>.
     * 
     * @return An array of all <code>Rules</code> in this <code>RuleSet</code>.
     */
    public Rule[] getRules() {
        return (Rule[]) this.rules.toArray( new Rule[this.rules.size()] );
    }

    public void addGlobalDeclaration(String identifier, Class clazz) {
        this.globals.put( identifier,
                          clazz );
    }

    public Map getGlobals() {
        return this.globals;
    }

    public void addFunctions(Functions functions) {
        this.functions.put( functions.getSemantic(),
                            functions );
    }

    public Functions getFunctions(String semantic) {
        return (Functions) this.functions.get( semantic );
    }
    
    public Map getFunctions()
    {        
        return  Collections.unmodifiableMap( this.functions );
    }
    
    public void setTypeSolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }
    
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public String toString() {
        return "[RuleSet name=" + this.name + "]";
    }
    
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        
        if (object == null||!(object instanceof Package)) {
            return false;
        }
        
        Package other = (Package) object;
        
        return (this.name.equals(other.name));
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }    
}
