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

package org.drools.core.definitions.rule.impl;

import org.drools.core.WorkingMemory;
import org.drools.core.base.EnabledBoolean;
import org.drools.core.base.SalienceInteger;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.ConsequenceMetaData;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Dialectable;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.GroupElementFactory;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.LogicTransformer;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Enabled;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Salience;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.Wireable;
import org.drools.core.time.impl.Timer;
import org.drools.core.util.StringUtils;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.Resource;
import org.kie.internal.security.KiePolicyHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RuleImpl implements Externalizable,
                                 Wireable,
                                 Dialectable,
                                 Rule,
                                 Query {

    private static final int NO_LOOP_BIT =              1 << 0;
    private static final int AUTO_FOCUS_BIT =           1 << 1;
    private static final int LOCK_ON_ACTIVE_BIT =       1 << 2;
    private static final int LOGICAL_DEPENDENCY_BIT =   1 << 3;
    private static final int SEMANTICALLY_VALID_BIT =   1 << 4;
    private static final int EAGER_BIT =                1 << 5;
    private static final int DATA_DRIVEN_BIT =          1 << 6;
    private static final int ALL_MATCHES_BIT =          1 << 7;

    public static final String DEFAULT_CONSEQUENCE_NAME = "default";

    /** The parent pkg */
    private String                   pkg;

    /** Name of the rule. */
    private String                   name;

    /** Parent Rule Name, optional */
    private RuleImpl                 parent;

    /** Salience value. */
    private Salience salience;

    /** The Rule is dirty after patterns have been added */
    private boolean                  dirty;
    private Map<String, Declaration> declarations;
    private Map<String, String[]>    requiredDeclarations = new HashMap<String, String[]>();

    private GroupElement lhsRoot;

    private String                   dialect;

    private String                   agendaGroup;

    private Map<String, Object>      metaAttributes;

    /** Consequence. */
    private Consequence consequence;

    private Map<String, Consequence> namedConsequences;

    /** Timer semantics that controls the firing of a rule */
    private Timer timer;

    /** Load order in Package */
    private int                     loadOrder;

    private String                   activationGroup;

    private String                   ruleFlowGroup;

    private String[]                 calendars;

    private Calendar dateEffective;

    private Calendar                 dateExpires;

    private Enabled enabled;

    private Resource resource;

    protected String                 activationListener;

    private ConsequenceMetaData consequenceMetaData = new ConsequenceMetaData();

    private List<QueryImpl> usedQueries;

    private List<QueryImpl> dependingQueries;

    private int ruleFlags;

    public RuleImpl() {

    }

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified pkg parent
     *
     * @param name
     *            The name of this rule.
     */
    public RuleImpl(final String name,
                    final String pkg,
                    final String agendaGroup) {
        this.name = name;
        this.pkg = pkg;
        this.agendaGroup = agendaGroup == null ? AgendaGroup.MAIN : agendaGroup;
        this.lhsRoot = GroupElementFactory.newAndInstance();
        setSemanticallyValid(true);
        this.enabled = EnabledBoolean.ENABLED_TRUE;
        this.salience = SalienceInteger.DEFAULT_SALIENCE;
        this.metaAttributes = new HashMap<String, Object>();
        setActivationListener( "agenda" );
    }

    /**
     * Construct a
     * <code>Rule<code> with the given name for the specified pkg parent
     *
     * @param name
     *            The name of this rule.
     */
    public RuleImpl(final String name,
                    final String agendaGroup) {
        this( name,
              null,
              agendaGroup );
    }

    public RuleImpl(final String name) {
        this( name,
              null,
              AgendaGroup.MAIN );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( pkg );
        out.writeObject( name );
        out.writeObject( parent );
        out.writeObject( salience );
        out.writeBoolean( dirty );
        out.writeObject( declarations );
        out.writeObject( lhsRoot );
        out.writeObject( dialect );
        out.writeObject( agendaGroup );
        out.writeObject( metaAttributes );
        out.writeObject( requiredDeclarations );

        if ( this.consequence instanceof CompiledInvoker) {
            out.writeObject( null );
            out.writeObject( null );
        } else {
            out.writeObject( this.consequence );
            out.writeObject( this.namedConsequences);
        }
        out.writeObject(timer);
        out.writeInt(loadOrder);
        out.writeObject(activationGroup);
        out.writeObject( ruleFlowGroup );
        out.writeObject( calendars );
        out.writeObject( dateEffective );
        out.writeObject( dateExpires );
        out.writeObject( enabled );
        out.writeObject( resource );
        out.writeObject( activationListener );
        out.writeObject(consequenceMetaData);
        out.writeObject( usedQueries );
        out.writeInt(ruleFlags);
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        pkg = (String) in.readObject();
        name = (String) in.readObject();
        parent = (RuleImpl) in.readObject();
        salience = (Salience) in.readObject();

        dirty = in.readBoolean();
        declarations = (Map<String, Declaration>) in.readObject();
        lhsRoot = (GroupElement) in.readObject();
        dialect = (String) in.readObject();
        agendaGroup = (String) in.readObject();
        metaAttributes = (Map<String, Object>) in.readObject();
        requiredDeclarations = (Map<String, String[]>) in.readObject();

        consequence = (Consequence) in.readObject();
        namedConsequences = (Map<String, Consequence>) in.readObject();
        timer = (Timer) in.readObject();
        loadOrder = in.readInt();
        activationGroup = (String) in.readObject();
        ruleFlowGroup = (String) in.readObject();
        calendars =(String[]) in.readObject();
        dateEffective = (Calendar) in.readObject();
        dateExpires = (Calendar) in.readObject();
        enabled = (Enabled) in.readObject();
        resource = (Resource) in.readObject();
        activationListener = ( String ) in.readObject();
        consequenceMetaData = ( ConsequenceMetaData ) in.readObject();
        usedQueries = (List<QueryImpl>) in.readObject();
        ruleFlags = in.readInt();
    }

    public void addUsedQuery(QueryImpl query) {
        if (usedQueries == null) {
            usedQueries = new ArrayList<QueryImpl>();
        }
        usedQueries.add(query);
    }

    /**
     * Returns the lists of queries from which this rule (or query) depends on ordered
     * by their relative dependencies, e.g. if R1 -> A -> B -> C (where the letter are queries)
     * it will return [C, B, A]
     */
    public List<QueryImpl> getDependingQueries() {
        if (dependingQueries == null) {
            dependingQueries = usedQueries == null ? Collections.<QueryImpl>emptyList() : collectDependingQueries(new LinkedList<QueryImpl>());
        }
        return dependingQueries;
    }

    protected List<QueryImpl> collectDependingQueries(LinkedList<QueryImpl> accumulator) {
        if (usedQueries == null) {
            return accumulator;
        }
        for (QueryImpl query : usedQueries) {
            if (!accumulator.contains(query)) {
                accumulator.offerFirst(query);
                query.collectDependingQueries(accumulator);
            }
        }
        return accumulator;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    /**
     * Returns the Timer semantics for a rule. Timer based rules are not added directly to the Agenda
     * instead they are scheduled for Agenda addition, based on the timer.
     * @return
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Sets the timer semantics for a rule. Timer based rules are not added directly to the Agenda
     * instead they are scheduled for Agenda addition, based on the timer.
     * @param timer
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
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

        return !(this.consequence == null || !isSemanticallyValid());
    }

    public String getPackage() {
        return this.pkg;
    }

    public void setPackage(String pkg) {
        this.pkg = pkg;
    }

    public String getPackageName() {
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
        if ( salience.isDynamic() ) {
            setEager(true);
        }
    }

    public String getAgendaGroup() {
        if ( this.agendaGroup.equals( "" ) ) {
            return AgendaGroup.MAIN;
        }
        return this.agendaGroup;
    }

    public void setAgendaGroup(final String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    private void set(int flag, boolean b) {
        if (b) {
            ruleFlags |= flag;
        } else {
            ruleFlags &= (0xffffffff - flag);
        }
    }

    private boolean isSet(int flag) {
        return (ruleFlags & flag) == flag;
    }

    public boolean isNoLoop() {
        return isSet(NO_LOOP_BIT);
    }

    /**
     * This returns true is the rule is effective.
     * If the rule is not effective, it cannot activate.
     *
     * This uses the dateEffective, dateExpires and enabled flag to decide this.
     */
    public boolean isEffective(Tuple tuple,
                               RuleTerminalNode rtn,
                               WorkingMemory workingMemory) {
        if ( !this.enabled.getValue( tuple,
                                     rtn.getEnabledDeclarations(),
                                     this,
                                     workingMemory ) ) {
            return false;
        }
        if ( this.dateEffective == null && this.dateExpires == null ) {
            return true;
        } else {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis( workingMemory.getSessionClock().getCurrentTime() );

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
        set(NO_LOOP_BIT, noLoop);
    }

    public boolean getAutoFocus() {
        return isSet(AUTO_FOCUS_BIT);
    }

    public void setAutoFocus(final boolean autoFocus) {
        set(AUTO_FOCUS_BIT, autoFocus);
        setEager(autoFocus);
    }

    public String getActivationGroup() {
        return this.activationGroup;
    }

    public void setActivationGroup(final String activationGroup) {
        this.activationGroup = activationGroup;
        setEager(!StringUtils.isEmpty(activationGroup));
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
    @SuppressWarnings("unchecked")
    public Declaration getDeclaration(final String identifier) {
        if ( this.dirty || (this.declarations == null) ) {
            this.declarations = this.getExtendedLhs( this, null ).getOuterDeclarations();
            this.dirty = false;
        }
        return this.declarations.get( identifier );
    }

    public String[] getRequiredDeclarationsForConsequence(String consequenceName) {
        String[] declarations = requiredDeclarations.get(consequenceName);
        return declarations != null ? declarations : new String[0];
    }

    public void setRequiredDeclarationsForConsequence(String consequenceName, String[] requiredDeclarations) {
        this.requiredDeclarations.put(consequenceName, requiredDeclarations);
    }

    /**
     * This field is updated at runtime, when the first logical assertion is done. I'm currently not too happy about having this determine at runtime
     * but its currently easier than trying to do this at compile time, although eventually this should be changed
     * @return
     */
    public boolean hasLogicalDependency() {
        return isSet(LOGICAL_DEPENDENCY_BIT);
    }

    public void setHasLogicalDependency(boolean hasLogicalDependency) {
        set(LOGICAL_DEPENDENCY_BIT, hasLogicalDependency);
    }

    public boolean isLockOnActive() {
        return isSet(LOCK_ON_ACTIVE_BIT);
    }

    public void setLockOnActive(final boolean lockOnActive) {
        set(LOCK_ON_ACTIVE_BIT, lockOnActive);
    }

    /**
     * Retrieve the set of all <i>root fact object </i> parameter
     * <code>Declarations</code>.
     *
     * @return The Set of <code>Declarations</code> in order which specify the
     *         <i>root fact objects</i>.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Declaration> getDeclarations() {
        if ( this.dirty || (this.declarations == null) ) {
            this.declarations = this.getExtendedLhs( this, null ).getOuterDeclarations();
            this.dirty = false;
        }
        return this.declarations;
    }

    /**
     * Add a pattern to the rule. All patterns are searched for bindings which are then added to the rule
     * as declarations
     *
     * @param element
     *            The <code>Test</code> to add.
     * @throws org.drools.core.rule.InvalidRuleException
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

    private GroupElement getExtendedLhs(RuleImpl rule,
                                        GroupElement fromChild) {
        //combine rules LHS with Parent "Extends"
        final GroupElement lhs = rule.lhsRoot.cloneOnlyGroup();
        //use the children passed from prior child rules, and combine with current LHS (at the end)
        if ( null != fromChild ) {
            //Have GroupElement from a child rule, so combine it
            lhs.getChildren().addAll( fromChild.getChildren() );
        }
        //move recursively up the tree
        if ( rule.parent != null ) {
            return getExtendedLhs( rule.parent,
                                   lhs );
        }
        //at the top of the tree, return combined LHS
        //TODO Merge LHS for performace

        return lhs;
    }

    /**
     * Uses the LogicTransformer to process the Rule patters - if no ORs are
     * used this will return an array of a single AND element. If there are Ors
     * it will return an And element for each possible logic branch. The
     * processing uses as a clone of the Rule's patterns, so they are not
     * changed.
     *
     * @return
     * @throws org.drools.core.rule.InvalidPatternException
     */
    public GroupElement[] getTransformedLhs( LogicTransformer transformer, Map<String, Class<?>> globals ) throws InvalidPatternException {
        //Moved to getExtendedLhs --final GroupElement cloned = (GroupElement) this.lhsRoot.clone();
        return transformer.transform( getExtendedLhs( this,
                                                      null ),
                                      globals );
    }

    public int getSpecifity() {
        return getSpecifity( this.lhsRoot );
    }

    private int getSpecifity(final GroupElement ce) {
        int specificity = 0;
        for (final RuleConditionElement object : ce.getChildren()) {
            if (object instanceof Pattern) {
                specificity += getSpecifity((Pattern) object);
            } else if (object instanceof GroupElement) {
                specificity += getSpecifity((GroupElement) object);
            }
        }
        return specificity;
    }

    private int getSpecifity(final Pattern pattern) {
        int specificity = 0;
        for (Constraint constraint : pattern.getConstraints()) {
            if (!(constraint instanceof Declaration)) {
                specificity++;
            }
        }

        return specificity;
    }

    public void wire(Object object) {
        if ( object instanceof Consequence ) {
            Consequence c = KiePolicyHelper.isPolicyEnabled() ? new SafeConsequence((Consequence) object) : (Consequence) object;
            if ( DEFAULT_CONSEQUENCE_NAME.equals( c.getName() ) ) {
                setConsequence( c );
            } else {
                addNamedConsequence(c.getName(), c);
            }
        } else if ( object instanceof Salience ) {
            setSalience( KiePolicyHelper.isPolicyEnabled() ? new SafeSalience((Salience) object) : (Salience) object );
        } else if ( object instanceof Enabled ) {
            setEnabled( KiePolicyHelper.isPolicyEnabled() ? new SafeEnabled((Enabled) object) : (Enabled) object );
        }
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

    public boolean hasNamedConsequences() {
        return namedConsequences != null && !namedConsequences.isEmpty();
    }

    public Map<String, Consequence> getNamedConsequences() {
        return this.namedConsequences;
    }

    public Consequence getNamedConsequence(String consequenceName)  {
        Consequence consequence = namedConsequences != null ? namedConsequences.get(consequenceName) : null;
        return consequence == null && parent != null ? parent.getNamedConsequence(consequenceName) : consequence;
    }

    public void addNamedConsequence(String name, Consequence consequence) {
        if ( this.namedConsequences == null ) {
            this.namedConsequences = new HashMap<String, Consequence>();
        }
        this.namedConsequences.put(name, consequence);
    }

    public int getLoadOrder() {
        return this.loadOrder;
    }

    public void setLoadOrder(final int loadOrder) {
        this.loadOrder = loadOrder;
    }

    public boolean isEager() {
        return isSet(EAGER_BIT);
    }

    public void setEager(boolean eager) {
        set(EAGER_BIT, eager);
    }

    public boolean isDataDriven() {
        return isSet(DATA_DRIVEN_BIT);
    }

    public void setDataDriven(boolean dataDriven) {
        set(DATA_DRIVEN_BIT, dataDriven);
    }

    public boolean isAllMatches() {
        return isSet(ALL_MATCHES_BIT);
    }

    public void setAllMatches(boolean allMatches) {
        set(ALL_MATCHES_BIT, allMatches);
    }

    public String toString() {
        return "[Rule name=" + this.name + ", agendaGroup=" + this.agendaGroup + ", salience=" + this.salience + ", no-loop=" + isNoLoop() + "]";
    }

    public String toRuleNameAndPathString() {
        String path = getResource() != null ? getResource().getSourcePath() : null;
        return "Rule \"" + getName() + "\"" + (path != null ? " in " + path : "");
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
        if ( obj == null || getClass() != obj.getClass() ) return false;
        final RuleImpl other = (RuleImpl) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( pkg == null ) {
            if ( other.pkg != null ) return false;
        } else if ( !pkg.equals( other.pkg ) ) return false;
        return true;
    }

    public void setSemanticallyValid(final boolean valid) {
        set(SEMANTICALLY_VALID_BIT, valid);
    }

    /**
     * This will return if the semantic actions or predicates in the rules
     * are valid.
     * This is provided so that lists of rules can be provided even if their semantic actions
     * do not "compile" etc.
     */
    public boolean isSemanticallyValid() {
        return isSet(SEMANTICALLY_VALID_BIT);
    }

    public String[] getCalendars() {
        return calendars;
    }

    public void setCalendars(String[] calendars) {
        this.calendars = calendars;
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
    public void setEnabled(final Enabled b) {
        this.enabled = b;
    }

    public Enabled getEnabled() {
        return enabled;
    }


    public boolean isEnabled(Tuple tuple,
                             RuleTerminalNode rtn,
                             WorkingMemory workingMemory) {
        return this.enabled.getValue( tuple,
                                      rtn.getEnabledDeclarations(),
                                      this,
                                      workingMemory );
    }

    public void addMetaAttribute(String key,
                                 Object value) {
        this.metaAttributes.put( key,
                                 value );
    }

    public String getActivationListener() {
        return activationListener;
    }

    public void setActivationListener(String activationListener) {
        this.activationListener = activationListener;
    }

    public Map<String, Object> getMetaData() {
        return Collections.unmodifiableMap(metaAttributes);
    }

    @Deprecated
    public Map<String, Object> getMetaAttributes() {
        return Collections.unmodifiableMap( metaAttributes );
    }

    @Deprecated
    public String getMetaAttribute(final String identifier) {
        return this.metaAttributes.get( identifier ).toString();
    }

    @Deprecated
    public Collection<String> listMetaAttributes() {
        return this.metaAttributes.keySet();
    }

    public void setParent(RuleImpl parent) {
        this.parent = parent;
    }

    public RuleImpl getParent() {
        return parent;
    }

    public static java.util.List getMethodBytecode(Class cls, String ruleClassName, String packageName, String methodName, String resource) {
        org.drools.core.util.asm.MethodComparator.Tracer visit = new org.drools.core.util.asm.MethodComparator.Tracer(methodName);

        java.io.InputStream is = cls.getClassLoader().getResourceAsStream( resource );

        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        try {
            byte[] data = new byte[1024];
            int byteCount;
            while ( (byteCount = is.read( data,
                                          0,
                                          1024 )) > -1 )
            {
                bos.write(data, 0, byteCount);
            }
        } catch ( java.io.IOException e ) {
            throw new RuntimeException("Unable getResourceAsStream for Class '" + ruleClassName+ "' ");
        }

        org.mvel2.asm.ClassReader classReader = new org.mvel2.asm.ClassReader( bos.toByteArray() );
        classReader.accept( visit, org.mvel2.asm.ClassReader.SKIP_DEBUG  );
        return visit.getText();
    }

    public boolean isQuery() {
        return false;
    }

    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.RULE;
    }

    public String getNamespace() {
        return getPackage();
    }

    public String getId() {
        return getName();
    }

    public ConsequenceMetaData getConsequenceMetaData() {
        return consequenceMetaData;
    }

    public static class SafeConsequence implements Consequence, Serializable {
        private static final long serialVersionUID = -8109957972163261899L;
        private final Consequence delegate;
        public SafeConsequence( Consequence delegate ) {
            this.delegate = delegate;
        }

        @Override
        public String getName() {
            return this.delegate.getName();
        }

        @Override
        public void evaluate(final KnowledgeHelper knowledgeHelper, final WorkingMemory workingMemory) throws Exception {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    delegate.evaluate(knowledgeHelper, workingMemory);
                    return null;
                }
            }, KiePolicyHelper.getAccessContext());
        }
    }

    public static class SafeSalience implements Salience, Serializable {
        private static final long serialVersionUID = 1L;
        private final Salience delegate;
        public SafeSalience( Salience delegate ) {
            this.delegate = delegate;
        }

        @Override
        public int getValue(final KnowledgeHelper khelper,
                            final org.kie.api.definition.rule.Rule rule,
                            final WorkingMemory workingMemory) {
            return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    return delegate.getValue(khelper, rule, workingMemory);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public int getValue() {
            // no need to secure calls to static values
            return delegate.getValue();
        }

        @Override
        public boolean isDynamic() {
            // no need to secure calls to static values
            return delegate.isDynamic();
        }
    }

    public static class SafeEnabled implements Enabled, Serializable {
        private static final long serialVersionUID = -8361753962814039574L;
        private final Enabled delegate;
        public SafeEnabled( Enabled delegate ) {
            this.delegate = delegate;
        }

        @Override
        public boolean getValue(final Tuple tuple,
                                final Declaration[] declrs,
                                final RuleImpl rule,
                                final WorkingMemory workingMemory) {
            return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return delegate.getValue(tuple, declrs, rule, workingMemory);
                }
            }, KiePolicyHelper.getAccessContext());
        }

    }
}
