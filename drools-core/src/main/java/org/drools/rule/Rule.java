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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.drools.base.SalienceInteger;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Consequence;
import org.drools.spi.Duration;
import org.drools.spi.Salience;

/**
 * A <code>Rule</code> contains a set of <code>Test</code>s and a
 * <code>Consequence</code>.
 * <p>
 * The <code>Test</code>s describe the circumstances that representrepresent
 * a match for this rule. The <code>Consequence</code> gets fired when the
 * Conditions match.
 *
 * @author <a href="mailto:bob@eng.werken.com"> bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au"> Simon Harris </a>
 * @author <a href="mailto:mproctor@codehaus.org"> mark proctor </a>
 */
public class Rule
    implements
    Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 400L;

    /**   */
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    /** The parent pkg */
    private String            pkg;

    /** Name of the rule. */
    private final String      name;

    /** Salience value. */
    private Salience               salience;

    /** The Rule is dirty after patterns have been added */
    private boolean           dirty;
    private Map               declarations;
    private Declaration[]     declarationArray;

    private GroupElement      lhsRoot;

    private String            dialect;

    private String            agendaGroup;

    /** Consequence. */
    private Consequence       consequence;

    /** Truthness duration. */
    private Duration          duration;

    /** Load order in Package */
    private long              loadOrder;

    /** Is recursion of this rule allowed */
    private boolean           noLoop;

    /** makes the rule's much the current focus */
    private boolean           autoFocus;

    private String            activationGroup;

    private String            ruleFlowGroup;

    private boolean           lockOnActive;

    private boolean           hasLogicalDependency;

    /** indicates that the rule is semantically correct. */
    private boolean           semanticallyValid;

    private Calendar          dateEffective;

    private Calendar          dateExpires;

    private boolean           enabled;

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
    public Rule(final String name,
                final String pkg,
                final String agendaGroup) {
        this.name = name;
        this.pkg = pkg;
        this.agendaGroup = agendaGroup;
        this.lhsRoot = GroupElementFactory.newAndInstance();
        this.semanticallyValid = true;
        this.enabled = true;
        this.salience = SalienceInteger.DEFAULT_SALIENCE;
    }

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified pkg parent
     *
     * @param name
     *            The name of this rule.
     */
    public Rule(final String name,
                final String agendaGroup) {
        this( name,
              null,
              agendaGroup );
    }

    public Rule(final String name) {
        this( name,
              null,
              AgendaGroup.MAIN );
    }



    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
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
    public void setDuration(final long ms) {
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
    public void setDuration(final Duration duration) {
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
        //if ( this.patterns.size() == 0 ) {
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
    public Salience getSalience() {
        return this.salience;
    }

    /**
     * Set the <code>Rule<code> salience.
     *
     *  @param salience The salience.
     */
    public void setSalience(final Salience salience) {
        this.salience = salience;
    }

    public String getAgendaGroup() {
        return this.agendaGroup;
    }

    public void setAgendaGroup(final String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    public boolean isNoLoop() {
        return this.noLoop;
    }

    /**
     * This returns true is the rule is effective.
     * If the rule is not effective, it cannot activate.
     *
     * This uses the dateEffective, dateExpires and enabled flag to decide this.
     */
    public boolean isEffective(TimeMachine tm) {
        if ( !this.enabled ) {
            return false;
        }
        if ( this.dateEffective == null && this.dateExpires == null ) {
            return true;
        } else {
            final Calendar now = tm.getNow();

            if ( this.dateEffective != null && this.dateExpires != null ) {
                return (now.after( this.dateEffective ) && now.before( this.dateExpires ));
            } else if ( this.dateEffective != null ) {
                return (now.after( this.dateEffective ));
            } else {
                return (now.before( this.dateExpires ));
            }

        }
    }

    public void setNoLoop(final boolean noLoop) {
        this.noLoop = noLoop;
    }

    public boolean getAutoFocus() {
        return this.autoFocus;
    }

    public void setAutoFocus(final boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public String getActivationGroup() {
        return this.activationGroup;
    }

    public void setActivationGroup(final String activationGroup) {
        this.activationGroup = activationGroup;
    }

    public String getRuleFlowGroup() {
        return this.ruleFlowGroup;
    }

    public void setRuleFlowGroup(final String ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
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
    public Declaration getDeclaration(final String identifier) {
        if ( this.dirty || (this.declarations == null) ) {
            this.declarations = this.lhsRoot.getOuterDeclarations();
            this.declarationArray = (Declaration[]) this.declarations.values().toArray( new Declaration[this.declarations.values().size()] );
            this.dirty = false;
        }
        return (Declaration) this.declarations.get( identifier );
    }

    /**
     * This field is updated at runtime, when the first logical assertion is done. I'm currently not too happy about having this determine at runtime
     * but its currently easier than trying to do this at compile time, although eventually this should be changed
     * @return
     */
    public boolean hasLogicalDependency() {
        return this.hasLogicalDependency;
    }

    public void setHasLogicalDependency(boolean hasLogicalDependency) {
        this.hasLogicalDependency = hasLogicalDependency;
    }

    public boolean isLockOnActive() {
        return this.lockOnActive;
    }

    public void setLockOnActive(final boolean lockOnActive) {
        this.lockOnActive = lockOnActive;
    }

    /**
     * Retrieve the set of all <i>root fact object </i> parameter
     * <code>Declarations</code>.
     *
     * @return The Set of <code>Declarations</code> in order which specify the
     *         <i>root fact objects</i>.
     */
    public Declaration[] getDeclarations() {
        if ( this.dirty || (this.declarationArray == null) ) {
            this.declarations = this.lhsRoot.getOuterDeclarations();
            this.declarationArray = (Declaration[]) this.declarations.values().toArray( new Declaration[this.declarations.values().size()] );
            this.dirty = false;
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
    public void addPattern(final RuleConditionElement element) {
        this.dirty = true;
        this.lhsRoot.addChild( element );
    }

    /**
     * Retrieve the <code>List</code> of <code>Conditions</code> for this
     * rule.
     *
     * @return The <code>List</code> of <code>Conditions</code>.
     */
    public GroupElement getLhs() {
        return this.lhsRoot;
    }

    public void setLhs(final GroupElement lhsRoot) {
        this.dirty = true;
        this.lhsRoot = lhsRoot;
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
    public GroupElement[] getTransformedLhs() throws InvalidPatternException {
        return LogicTransformer.getInstance().transform( this.lhsRoot );
    }

    public int getSpecifity() {
        return getSpecifity( this.lhsRoot );
    }

    private int getSpecifity(final GroupElement ce) {
        int specificity = 0;
        for ( final Iterator it = ce.getChildren().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof Pattern ) {
                specificity += getSpecifity( (Pattern) object );
            } else if ( object instanceof GroupElement ) {
                specificity += getSpecifity( (GroupElement) object );
            }
        }
        return specificity;
    }

    private int getSpecifity(final Pattern pattern) {
        int specificity = 0;
        for ( final Iterator it = pattern.getConstraints().iterator(); it.hasNext(); ) {
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
    public void setConsequence(final Consequence consequence) {
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

    public void setLoadOrder(final long loadOrder) {
        this.loadOrder = loadOrder;
    }

    public String toString() {
        return "[Rule name=" + this.name + ", agendaGroup=" + this.agendaGroup + ", salience=" + this.salience + ", no-loop=" + this.noLoop + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + ((pkg == null) ? 0 : pkg.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Rule other = (Rule) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( pkg == null ) {
            if ( other.pkg != null ) return false;
        } else if ( !pkg.equals( other.pkg ) ) return false;
        return true;
    }

    public void setSemanticallyValid(final boolean valid) {
        this.semanticallyValid = valid;
    }

    /**
     * This will return if the semantic actions or predicates in the rules
     * are valid.
     * This is provided so that lists of rules can be provided even if their semantic actions
     * do not "compile" etc.
     */
    public boolean isSemanticallyValid() {
        return this.semanticallyValid;
    }

    /**
     * Sets the date from which this rule takes effect (can include time to the millisecond).
     * @param effectiveDate
     */
    public void setDateEffective(final Calendar effectiveDate) {
        this.dateEffective = effectiveDate;
    }

    /**
     * Sets the date after which the rule will no longer apply (can include time to the millisecond).
     * @param expiresDate
     */
    public void setDateExpires(final Calendar expiresDate) {
        this.dateExpires = expiresDate;
    }


    public Calendar getDateEffective() {
        return this.dateEffective;
    }

    public Calendar getDateExpires() {
        return this.dateExpires;
    }

    /**
     * A rule is enabled by default. This can explicitly disable it in which case it will never activate.
     */
    public void setEnabled(final boolean b) {
        this.enabled = b;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
