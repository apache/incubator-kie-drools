package org.drools.rule;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.spi.AgendaGroup;
import org.drools.spi.Consequence;
import org.drools.spi.Duration;

/**
 * A <code>Rule</code> contains a set of <code>Test</code>s and a
 * <code>Consequence</code>.
 * <p>
 * The <code>Test</code>s describe the circumstances that representrepresent
 * a match for this rule. The <code>Consequence</code> gets fired when the
 * Conditions match.
 * 
 * @see Eval
 * @see Consequence
 * @author <a href="mailto:bob@eng.werken.com"> bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au"> Simon Harris </a>
 * @author <a href="mailto:mproctor@codehaus.org"> mark pro </a>
 */
public class Rule
    implements
    Serializable {
    /**   */
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    /** The parent pkg */
    private String        pkg;

    /** Name of the rule. */
    private final String  name;

    /** Salience value. */
    private int           salience;

    private final Map     declarations      = new HashMap();

    private Declaration[] declarationArray;

    private final And     lhsRoot           = new And();

    private String        agendaGroup;

    /** Consequence. */
    private Consequence   consequence;

    /** Truthness duration. */
    private Duration      duration;

    /** Load order in Package */
    private long          loadOrder;

    /** Is recursion of this rule allowed */
    private boolean       noLoop;

    /** makes the rule's much the current focus */
    private boolean       autoFocus;
    
    private String        xorGroup;

    /** indicates that the rule is semantically correct. */
    private boolean       semanticallyValid = true;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified pkg parent
     *
     * @param name
     *            The name of this rule.
     */
    public Rule(String name,
                String pkg,
                String agendaGroup) {
        this.name = name;
        this.pkg = pkg;
        this.agendaGroup = agendaGroup;
    }

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified pkg parent
     *
     * @param name
     *            The name of this rule.
     */
    public Rule(String name,
                String agendaGroup) {
        this.name = name;
        this.pkg = null;
        this.agendaGroup = agendaGroup;
    }

    public Rule(String name) {
        this.name = name;
        this.pkg = null;
        this.agendaGroup = AgendaGroup.MAIN;
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
     *            The number of seconds the rule must hold true in order to
     *            fire.
     */
    public void setDuration(long ms) {
        this.duration = new FixedDuration( ms );
    }

    /**
     * Set the truthness duration object. This causes a delay before the firing
     * of the <code>Consequence</code> if the rule is still true at the end of
     * the duration.
     * 
     * @param duration
     *            The truth duration object.
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Retrieve the truthness duration object.
     * 
     * @return The truthness duration object.
     */
    public Duration getDuration() {
        return this.duration;
    }

    /**
     * Determine if this rule is internally consistent and valid.
     * This will include checks to make sure the rules semantic components (actions and predicates)
     * are valid.
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
    public boolean isValid() {
        //if ( this.columns.size() == 0 ) {
        //    return false;
        //}

        if ( this.consequence == null || !isSemanticallyValid() ) {
            return false;
        }

        return true;
    }

    public String getPackage() {
        return this.pkg;
    }

    /**
     * Retrieve the name of this rule.
     * 
     * @return The name of this rule.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieve the <code>Rule</code> salience.
     * 
     * @return The salience.
     */
    public int getSalience() {
        return this.salience;
    }

    /**
     * Set the <code>Rule<code> salience.
     *
     *  @param salience The salience.
     */
    public void setSalience(int salience) {
        this.salience = salience;
    }

    public String getAgendaGroup() {
        return this.agendaGroup;
    }

    public void setAgendaGroup(String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    public boolean getNoLoop() {
        return this.noLoop;
    }

    public void setNoLoop(boolean noLoop) {
        this.noLoop = noLoop;
    }

    public boolean getAutoFocus() {
        return this.autoFocus;
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }    

    public String getXorGroup() {
        return this.xorGroup;
    }

    public void setXorGroup(String xorGroup) {
        this.xorGroup = xorGroup;
    }    

    /**
     * Retrieve a parameter <code>Declaration</code> by identifier.
     * 
     * @param identifier
     *            The identifier.
     * 
     * @return The declaration or <code>null</code> if no declaration matches
     *         the <code>identifier</code>.
     */
    public Declaration getDeclaration(String identifier) {
        return (Declaration) this.declarations.get( identifier );
    }

    /**
     * Retrieve the set of all <i>root fact object </i> parameter
     * <code>Declarations</code>.
     * 
     * @return The Set of <code>Declarations</code> in order which specify the
     *         <i>root fact objects</i>.
     */
    public Declaration[] getDeclarations() {
        if ( this.declarationArray == null ) {
            this.declarationArray = (Declaration[]) this.declarations.values().toArray( new Declaration[this.declarations.values().size()] );
        }
        return this.declarationArray;
    }

    /**
     * Add a pattern to the rule. All patterns are searched for bindings which are then added to the rule
     * as declarations 
     * 
     * @param condition
     *            The <code>Test</code> to add.
     * @throws InvalidRuleException 
     */
    public void addPattern(ConditionalElement ce) {
        if ( ce instanceof GroupElement ) {
            addDeclarations( (GroupElement) ce );
        }
        this.lhsRoot.addChild( ce );
    }

    public void addPattern(Column column) {
        addDeclarations( column );
        this.lhsRoot.addChild( column );
    }

    private void addDeclarations(Column column) {
        // Check if the column is bound and if so add it as a declaration
        if ( column.isBound() ) {
            Declaration declaration = column.getDeclaration();
            this.declarations.put( declaration.getIdentifier(),
                                   declaration );
        }

        // Check if there are any bound fields and if so add it as a declaration
        for ( Iterator it = column.getConstraints().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof Declaration ) {
                Declaration declaration = (Declaration) object;
                this.declarations.put( declaration.getIdentifier(),
                                       declaration );
            }
        }
    }

    private void addDeclarations(GroupElement ce) {
        for ( Iterator it = ce.getChildren().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof Column ) {
                addDeclarations( (Column) object );
            } else if ( object instanceof GroupElement ) {
                addDeclarations( (GroupElement) object );
            }
        }
    }

    /**
     * Retrieve the <code>List</code> of <code>Conditions</code> for this
     * rule.
     * 
     * @return The <code>List</code> of <code>Conditions</code>.
     */
    public And getLhs() {
        return this.lhsRoot;
    }

    /**
     * Uses the LogicTransformer to process the Rule patters - if no ORs are
     * used this will return an array of a single AND element. If there are Ors
     * it will return an And element for each possible logic branch. The
     * processing uses as a clone of the Rule's patterns, so they are not
     * changed.
     * 
     * @return
     * @throws InvalidPatternException
     */
    public And[] getTransformedLhs() throws InvalidPatternException {
        return LogicTransformer.getInstance().transform( this.lhsRoot );
    }

    public int getSpecifity() {
        return getSpecifity( this.lhsRoot );
    }

    private int getSpecifity(GroupElement ce) {
        int specificity = 0;
        for ( Iterator it = ce.getChildren().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof Column ) {
                specificity += getSpecifity( (Column) object );
            } else if ( object instanceof GroupElement ) {
                specificity += getSpecifity( (GroupElement) object );
            }
        }
        return specificity;
    }

    private int getSpecifity(Column column) {
        int specificity = 0;
        for ( Iterator it = column.getConstraints().iterator(); it.hasNext(); ) {
            if ( !(it.next() instanceof Declaration) ) {
                specificity++;
            }
        }

        return specificity;
    }

    /**
     * Set the <code>Consequence</code> that is associated with the successful
     * match of this rule.
     * 
     * @param consequence
     *            The <code>Consequence</code> to attach to this
     *            <code>Rule</code>.
     */
    public void setConsequence(Consequence consequence) {
        this.consequence = consequence;
    }

    /**
     * Retrieve the <code>Consequence</code> associated with this
     * <code>Rule</code>.
     * 
     * @return The <code>Consequence</code>.
     */
    public Consequence getConsequence() {
        return this.consequence;
    }

    public long getLoadOrder() {
        return this.loadOrder;
    }

    void setLoadOrder(long loadOrder) {
        this.loadOrder = loadOrder;
    }

    public String toString() {
        return "[Rule name=" + this.name + ", agendaGroup=" + this.agendaGroup + ", salience=" + this.salience + ", no-loop=" + this.noLoop + "]";
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || object.getClass() != Rule.class ) {
            return false;
        }

        Rule other = (Rule) object;

        return (this.name.equals( other.name ) && this.agendaGroup.equals( other.agendaGroup ) && this.xorGroup.equals( other.xorGroup )&& this.salience == other.salience && this.noLoop == other.noLoop);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public void setSemanticallyValid(boolean valid) {
        this.semanticallyValid = valid;
    }

    /** 
     * This will return if the semantic actions or predicates in the rules
     * are valid.
     * This is provided so that lists of rules can be provided even if their semantic actions
     * do not "compile" etc.
     */
    public boolean isSemanticallyValid() {
        return semanticallyValid;
    }
}
