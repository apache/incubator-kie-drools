package org.drools.reteoo;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.InitialFact;
import org.drools.RuleIntegrationException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DoubleBetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InstanceEqualsConstraint;
import org.drools.common.InstanceNotEqualsConstraint;
import org.drools.common.QuadroupleBetaConstraints;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.rule.Accumulate;
import org.drools.rule.And;
import org.drools.rule.Collect;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.FieldValue;

/**
 * Builds the Rete-OO network for a <code>Package</code>.
 * 
 * @see org.drools.rule.Package
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 * 
 */
class ReteooBuilder
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long               serialVersionUID = 1737643968218792944L;

    /** The RuleBase */
    private transient ReteooRuleBase        ruleBase;

    /** Rete network to build against. */
    private transient Rete                  rete;

    private transient ReteooWorkingMemory[] workingMemories;

    /** Nodes that have been attached. */
    private final Map                       attachedNodes;

    private TupleSource                     tupleSource;

    private ObjectSource                    objectSource;

    private Map                             declarations;

    private int                             id;

    private Map                             rules;

    private Map                             objectType;

    private int                             currentOffsetAdjustment;
    
    private final boolean                   removeIdentities;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    ReteooBuilder(final ReteooRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.rete = this.ruleBase.getRete();
        this.attachedNodes = new HashMap();
        this.rules = new HashMap();

        //Set to 1 as Rete node is set to 0
        this.id = 1;
        this.removeIdentities = this.ruleBase.getConfiguration().isRemoveIdentities();
    }

    /**
     * Allow this to be settable, otherwise we get infinite recursion on serialisation
     * @param ruleBase
     */
    void setRuleBase(final ReteooRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    /**
     * Allow this to be settable, otherwise we get infinite recursion on serialisation
     * @param ruleBase
     */
    void setRete(final Rete rete) {

    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Add a <code>Rule</code> to the network.
     * 
     * @param rule
     *            The rule to add.
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws InvalidPatternException
     */
    void addRule(final Rule rule) throws InvalidPatternException {
        // reset working memories for potential propagation
        this.workingMemories = (ReteooWorkingMemory[]) this.ruleBase.getWorkingMemories().toArray( new ReteooWorkingMemory[this.ruleBase.getWorkingMemories().size()] );
        this.currentOffsetAdjustment = 0;

        final List nodes = new ArrayList();
        final And[] and = rule.getTransformedLhs();

        for ( int i = 0; i < and.length; i++ ) {
            if ( !hasColumns( and[i] ) ) {
                addInitialFactMatch( and[i] );
            }

            addRule( and[i],
                     rule );
            BaseNode node = null;
            if ( !(rule instanceof Query) ) {
                // Check a consequence is set
                if ( rule.getConsequence() == null ) {
                    throw new InvalidPatternException( "Rule '" + rule.getName() + "' has no Consequence" );
                }
                node = new TerminalNode( this.id++,
                                         this.tupleSource,
                                         rule );
            } else {
                // Check there is no consequence
                if ( rule.getConsequence() != null ) {
                    throw new InvalidPatternException( "Query '" + rule.getName() + "' should have no Consequence" );
                }
                node = new QueryTerminalNode( this.id++,
                                              this.tupleSource,
                                              rule );
            }

            nodes.add( node );

            if ( this.workingMemories.length == 0 ) {
                node.attach();
            } else {
                node.attach( this.workingMemories );
            }
        }

        this.rules.put( rule,
                        nodes.toArray( new BaseNode[nodes.size()] ) );
    }

    private boolean hasColumns(final GroupElement ge) {
        for ( final Iterator it = ge.getChildren().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof Column || (object instanceof GroupElement && hasColumns( (GroupElement) object )) ) {
                return true;
            }
        }
        return false;
    }

    private void addInitialFactMatch(final And and) {
        And temp = null;

        // If we have children we know there are no columns but we need to make sure that InitialFact is first
        if ( !and.getChildren().isEmpty() ) {
            temp = (And) and.clone();
            and.getChildren().clear();
        }
        final Column column = new Column( 0,
                                          new ClassObjectType( InitialFact.class ) );
        and.addChild( column );

        // now we know InitialFact is first add all the previous constrains
        if ( temp != null ) {
            and.getChildren().addAll( temp.getChildren() );
        }
        this.currentOffsetAdjustment = 1;
    }

    private void addRule(final And and,
                         final Rule rule) throws InvalidPatternException {
        this.objectSource = null;
        this.tupleSource = null;
        this.declarations = new HashMap();
        this.objectType = new LinkedHashMap();

        if ( rule instanceof Query ) {
            attachQuery( rule.getName() );
        }

        for ( final Iterator it = and.getChildren().iterator(); it.hasNext(); ) {
            final Object object = it.next();

            if ( object instanceof EvalCondition ) {
                final EvalCondition eval = (EvalCondition) object;
                checkUnboundDeclarations( eval.getRequiredDeclarations() );
                this.tupleSource = attachNode( new EvalConditionNode( this.id++,
                                                                      this.tupleSource,
                                                                      eval ) );
                continue;
            }

            BetaConstraints binder = null;
            Column column = null;

            if ( object instanceof Column ) {
                column = (Column) object;

                binder = attachColumn( (Column) object,
                                       and,
                                       this.removeIdentities );

                // If a tupleSource does not exist then we need to adapt this
                // into
                // a TupleSource using LeftInputAdapterNode
                if ( this.tupleSource == null ) {
                    this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                             this.objectSource ) );

                    // objectSource is created by the attachColumn method, if we
                    // adapt this to
                    // a TupleSource then we need to null the objectSource
                    // reference.
                    this.objectSource = null;
                }
            } else if ( object instanceof GroupElement ) {
                // If its not a Column or EvalCondition then it can either be a Not or an Exists
                GroupElement ce = (GroupElement) object;
                while ( !(ce.getChildren().get( 0 ) instanceof Column) ) {
                    ce = (GroupElement) ce.getChildren().get( 0 );
                }
                column = (Column) ce.getChildren().get( 0 );

                // If a tupleSource does not exist then we need to adapt an
                // InitialFact into a a TupleSource using LeftInputAdapterNode
                if ( this.tupleSource == null ) {
                    // adjusting offset as all tuples will now contain initial-fact at index 0
                    this.currentOffsetAdjustment = 1;

                    final ObjectSource objectSource = attachNode( new ObjectTypeNode( this.id++,
                                                                                      new ClassObjectType( InitialFact.class ),
                                                                                      this.rete,
                                                                                      this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

                    this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                             objectSource ) );
                }

                binder = attachColumn( column,
                                       and,
                                       false );
            }

            if ( object.getClass() == Not.class ) {
                attachNot( this.tupleSource,
                           (Not) object,
                           this.objectSource,
                           binder,
                           column );
            } else if ( object.getClass() == Exists.class ) {
                attachExists( this.tupleSource,
                              (Exists) object,
                              this.objectSource,
                              binder,
                              column );
            } else if ( object.getClass() == From.class ) {
                attachFrom( this.tupleSource,
                            (From) object );
            } else if ( object.getClass() == Accumulate.class ) {
                attachAccumulate( this.tupleSource,
                                  and,
                                  (Accumulate) object );
            } else if ( object.getClass() == Collect.class ) {
                attachCollect( this.tupleSource,
                               and,
                               (Collect) object );
            } else if ( this.objectSource != null ) {
                this.tupleSource = attachNode( new JoinNode( this.id++,
                                                             this.tupleSource,
                                                             this.objectSource,
                                                             binder ) );
            }
        }
    }

    public BaseNode[] getTerminalNodes(final Rule rule) {
        return (BaseNode[]) this.rules.remove( rule );
    }

    private void attachQuery(final String queryName) {
        // incrementing offset adjustment, since we are adding a new ObjectNodeType as our
        // first column
        this.currentOffsetAdjustment += 1;

        final ObjectSource objectTypeSource = attachNode( new ObjectTypeNode( this.id++,
                                                                              new ClassObjectType( DroolsQuery.class ),
                                                                              this.rete,
                                                                              this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

        final ClassFieldExtractor extractor = new ClassFieldExtractor( DroolsQuery.class,
                                                                       "name" );

        final FieldValue field = FieldFactory.getFieldValue( queryName,
                                                             ValueType.STRING_TYPE );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL ),
                                                                    field );

        final ObjectSource alphaNodeSource = attachNode( new AlphaNode( this.id++,
                                                                        constraint,
                                                                        objectTypeSource,
                                                                        this.ruleBase.getConfiguration().isAlphaMemory(),
                                                                        this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

        this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                 alphaNodeSource ) );
    }

    private BetaConstraints attachColumn(final Column column,
                                         final GroupElement parent,
                                         boolean removeIdentities) throws InvalidPatternException {
        // Adjusting offset in case a previous Initial-Fact was added to the network
        column.adjustOffset( this.currentOffsetAdjustment );

        // Check if the Column is bound
        if ( column.getDeclaration() != null ) {
            final Declaration declaration = column.getDeclaration();
            // Add the declaration the map of previously bound declarations
            this.declarations.put( declaration.getIdentifier(),
                                   declaration );
        }

        final List predicates = attachAlphaNodes( column,
                                                  removeIdentities );

        final BetaConstraints binder = createBetaNodeConstraint( predicates );

        return binder;
    }

    public List attachAlphaNodes(final Column column,
                                 final boolean removeIdentities) throws InvalidPatternException {
        final List constraints = column.getConstraints();

        this.objectSource = attachNode( new ObjectTypeNode( this.id++,
                                                            column.getObjectType(),
                                                            this.rete,
                                                            this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

        final List betaConstraints = new ArrayList();

        if ( removeIdentities && column.getObjectType().getClass() == ClassObjectType.class ) {
            // Check if this object type exists before
            // If it does we need stop instance equals cross product
            final Class thisClass = ((ClassObjectType) column.getObjectType()).getClassType();
            for ( final Iterator it = this.objectType.entrySet().iterator(); it.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) it.next();
                final Class previousClass = ((ClassObjectType) entry.getKey()).getClassType();
                if ( thisClass.isAssignableFrom( previousClass ) ) {
                    betaConstraints.add( new InstanceNotEqualsConstraint( (Column) entry.getValue() ) );
                }
            }

            // Must be added after the checking, otherwise it matches against itself
            this.objectType.put( column.getObjectType(),
                                 column );
        }

        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                final Declaration declaration = (Declaration) object;
                // Add the declaration the map of previously bound declarations
                this.declarations.put( declaration.getIdentifier(),
                                       declaration );
                continue;
            }

            final Constraint constraint = (Constraint) object;
            if ( constraint.getRequiredDeclarations().length == 0 ) {
                this.objectSource = attachNode( new AlphaNode( this.id++,
                                                               (AlphaNodeFieldConstraint) constraint,
                                                               this.objectSource ) );
            } else {
                checkUnboundDeclarations( constraint.getRequiredDeclarations() );
                betaConstraints.add( constraint );
            }
        }

        return betaConstraints;
    }

    private void attachNot(final TupleSource tupleSource,
                           final Not not,
                           final ObjectSource ObjectSource,
                           final BetaConstraints binder,
                           final Column column) {
        final NotNode notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                                   tupleSource,
                                                                   ObjectSource,
                                                                   binder ) );
        if ( not.getChild() instanceof Not ) {

            final RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                                 column,
                                                                                                                 notNode ) );
            attachNot( tupleSource,
                       (Not) not.getChild(),
                       adapter,
                       EmptyBetaConstraints.getInstance(),
                       column );
        } else if ( not.getChild() instanceof Exists ) {
            final RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                                 column,
                                                                                                                 notNode ) );
            attachExists( tupleSource,
                          (Exists) not.getChild(),
                          adapter,
                          EmptyBetaConstraints.getInstance(),
                          column );
        } else {
            this.tupleSource = notNode;
        }
    }

    private void attachExists(final TupleSource tupleSource,
                              final Exists exists,
                              final ObjectSource ObjectSource,
                              final BetaConstraints binder,
                              final Column column) {
        NotNode notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                             tupleSource,
                                                             ObjectSource,
                                                             binder ) );
        RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                       column,
                                                                                                       notNode ) );

        final BetaConstraints identityBinder = new SingleBetaConstraints( new InstanceEqualsConstraint( column ), this.ruleBase.getConfiguration() );
        notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                     tupleSource,
                                                     adapter,
                                                     identityBinder ) );

        if ( exists.getChild() instanceof Not ) {
            adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                     column,
                                                                                     notNode ) );
            attachNot( tupleSource,
                       (Not) exists.getChild(),
                       adapter,
                       EmptyBetaConstraints.getInstance(),
                       column );
        } else if ( exists.getChild() instanceof Exists ) {
            adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                     column,
                                                                                     notNode ) );
            attachExists( tupleSource,
                          (Exists) exists.getChild(),
                          adapter,
                          EmptyBetaConstraints.getInstance(),
                          column );
        } else {
            this.tupleSource = notNode;
        }
    }

    /**
     * Attaches a node into the network. If a node already exists that could
     * substitute, it is used instead.
     * 
     * @param candidate
     *            The node to attach.
     * @param leafNodes
     *            The list to which the newly added node will be added.
     */
    private TupleSource attachNode(final TupleSource candidate) {
        TupleSource node = (TupleSource) this.attachedNodes.get( candidate );

        if ( this.ruleBase.getConfiguration().isShareBetaNodes() && node !=  null ) {
            if ( !node.isInUse() ) {
                if ( this.workingMemories.length == 0 ) {
                    node.attach();
                } else {
                    node.attach( this.workingMemories );
                }
            }
            node.addShare();
            this.id--;
        } else {
            if ( this.workingMemories.length == 0 ) {
                candidate.attach();
            } else {
                candidate.attach( this.workingMemories );
            }

            this.attachedNodes.put( candidate,
                                    candidate );

            node = candidate;            
        }
        
        return node;
    }

    private void attachFrom(final TupleSource tupleSource,
                            final From from) {
        final Column column = from.getColumn();

        // If a tupleSource does not exist then we need to adapt an
        // InitialFact into a a TupleSource using LeftInputAdapterNode
        if ( this.tupleSource == null ) {
            // adjusting offset as all tuples will now contain initial-fact at index 0
            this.currentOffsetAdjustment = 1;

            final ObjectSource objectSource = attachNode( new ObjectTypeNode( this.id++,
                                                                              new ClassObjectType( InitialFact.class ),
                                                                              this.rete,
                                                                              this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

            this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                     objectSource ) );
        }

        // Adjusting offset in case a previous Initial-Fact was added to the network
        column.adjustOffset( this.currentOffsetAdjustment );

        final List constraints = column.getConstraints();

        // Check if the Column is bound
        if ( column.getDeclaration() != null ) {
            final Declaration declaration = column.getDeclaration();
            // Add the declaration the map of previously bound declarations
            this.declarations.put( declaration.getIdentifier(),
                                   declaration );
        }

        final List betaConstraints = new ArrayList();
        final List alphaConstraints = new ArrayList();

        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                final Declaration declaration = (Declaration) object;
                // Add the declaration the map of previously bound declarations
                this.declarations.put( declaration.getIdentifier(),
                                       declaration );
                continue;
            }

            final AlphaNodeFieldConstraint fieldConstraint = (AlphaNodeFieldConstraint) object;
            if ( fieldConstraint instanceof LiteralConstraint ) {
                alphaConstraints.add( fieldConstraint );
            } else {
                checkUnboundDeclarations( fieldConstraint.getRequiredDeclarations() );
                betaConstraints.add( fieldConstraint );
            }
        }

        final BetaConstraints binder = createBetaNodeConstraint( betaConstraints );

        this.tupleSource = attachNode( new FromNode( this.id++,
                                                     from.getDataProvider(),
                                                     this.tupleSource,
                                                     (AlphaNodeFieldConstraint[]) alphaConstraints.toArray( new AlphaNodeFieldConstraint[alphaConstraints.size()] ),
                                                     binder ) );

    }

    private void attachAccumulate(final TupleSource tupleSource,
                                  final And parent,
                                  final Accumulate accumulate) {
        // If a tupleSource does not exist then we need to adapt an
        // InitialFact into a a TupleSource using LeftInputAdapterNode
        if ( this.tupleSource == null ) {
            // adjusting offset as all tuples will now contain initial-fact at index 0
            this.currentOffsetAdjustment = 1;

            final ObjectSource auxObjectSource = attachNode( new ObjectTypeNode( this.id++,
                                                                                 new ClassObjectType( InitialFact.class ),
                                                                                 this.rete,
                                                                                 this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

            this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                     auxObjectSource ) );
        }

        final Column sourceColumn = accumulate.getSourceColumn();
        final BetaConstraints sourceBinder = attachColumn( sourceColumn,
                                                           parent,
                                                           true );

        final Column column = accumulate.getResultColumn();
        // Adjusting offset in case a previous Initial-Fact was added to the network
        column.adjustOffset( this.currentOffsetAdjustment );

        final List constraints = column.getConstraints();

        // Check if the Column is bound
        if ( column.getDeclaration() != null ) {
            final Declaration declaration = column.getDeclaration();
            // Add the declaration the map of previously bound declarations
            this.declarations.put( declaration.getIdentifier(),
                                   declaration );
        }

        final List betaConstraints = new ArrayList();
        final List alphaConstraints = new ArrayList();

        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                final Declaration declaration = (Declaration) object;
                // Add the declaration the map of previously bound declarations
                this.declarations.put( declaration.getIdentifier(),
                                       declaration );
                continue;
            }

            final AlphaNodeFieldConstraint fieldConstraint = (AlphaNodeFieldConstraint) object;
            if ( fieldConstraint instanceof LiteralConstraint ) {
                alphaConstraints.add( fieldConstraint );
            } else {
                checkUnboundDeclarations( fieldConstraint.getRequiredDeclarations() );
                betaConstraints.add( fieldConstraint );
            }
        }

        final BetaConstraints resultsBinder = createBetaNodeConstraint( betaConstraints );

        this.tupleSource = attachNode( new AccumulateNode( this.id++,
                                                           this.tupleSource,
                                                           this.objectSource,
                                                           (AlphaNodeFieldConstraint[]) alphaConstraints.toArray( new AlphaNodeFieldConstraint[alphaConstraints.size()] ),
                                                           sourceBinder,
                                                           resultsBinder,
                                                           accumulate ) );
    }

    private void attachCollect(final TupleSource tupleSource,
                               final And parent,
                               final Collect collect) {
        // If a tupleSource does not exist then we need to adapt an
        // InitialFact into a a TupleSource using LeftInputAdapterNode
        if ( this.tupleSource == null ) {
            // adjusting offset as all tuples will now contain initial-fact at index 0
            this.currentOffsetAdjustment = 1;

            final ObjectSource auxObjectSource = attachNode( new ObjectTypeNode( this.id++,
                                                                                 new ClassObjectType( InitialFact.class ),
                                                                                 this.rete,
                                                                                 this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );

            this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                     auxObjectSource ) );
        }

        final Column sourceColumn = collect.getSourceColumn();
        final BetaConstraints sourceBinder = attachColumn( sourceColumn,
                                                           parent,
                                                           true );

        final Column column = collect.getResultColumn();
        // Adjusting offset in case a previous Initial-Fact was added to the network
        column.adjustOffset( this.currentOffsetAdjustment );

        final List constraints = column.getConstraints();

        // Check if the Column is bound
        if ( column.getDeclaration() != null ) {
            final Declaration declaration = column.getDeclaration();
            // Add the declaration the map of previously bound declarations
            this.declarations.put( declaration.getIdentifier(),
                                   declaration );
        }

        final List betaConstraints = new ArrayList();
        final List alphaConstraints = new ArrayList();

        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                final Declaration declaration = (Declaration) object;
                // Add the declaration the map of previously bound declarations
                this.declarations.put( declaration.getIdentifier(),
                                       declaration );
                continue;
            }

            final AlphaNodeFieldConstraint fieldConstraint = (AlphaNodeFieldConstraint) object;
            if ( fieldConstraint instanceof LiteralConstraint ) {
                alphaConstraints.add( fieldConstraint );
            } else {
                checkUnboundDeclarations( fieldConstraint.getRequiredDeclarations() );
                betaConstraints.add( fieldConstraint );
            }
        }

        final BetaConstraints resultsBinder = createBetaNodeConstraint( betaConstraints );

        this.tupleSource = attachNode( new CollectNode( this.id++,
                                                        this.tupleSource,
                                                        this.objectSource,
                                                        (AlphaNodeFieldConstraint[]) alphaConstraints.toArray( new AlphaNodeFieldConstraint[alphaConstraints.size()] ),
                                                        sourceBinder,
                                                        resultsBinder,
                                                        collect ) );
    }

    private ObjectSource attachNode(final ObjectSource candidate) {
        ObjectSource node = (ObjectSource) this.attachedNodes.get( candidate );

        if ( this.ruleBase.getConfiguration().isShareAlphaNodes() && node != null) {
            if ( !node.isInUse() ) {
                if ( this.workingMemories.length == 0 ) {
                    node.attach();
                } else {
                    node.attach( this.workingMemories );
                }
            }
            node.addShare();
            this.id--;
        } else {
            if ( this.workingMemories.length == 0 ) {
                candidate.attach();
            } else {
                candidate.attach( this.workingMemories );
            }

            this.attachedNodes.put( candidate,
                                    candidate );

            node = candidate;
        }

        return node;
    }

    public void removeRule(final Rule rule) {
        // reset working memories for potential propagation
        this.workingMemories = (ReteooWorkingMemory[]) this.ruleBase.getWorkingMemories().toArray( new ReteooWorkingMemory[this.ruleBase.getWorkingMemories().size()] );

        final Object object = this.rules.get( rule );

        final BaseNode[] nodes = (BaseNode[]) object;
        for ( int i = 0, length = nodes.length; i < length; i++ ) {
            final BaseNode node = nodes[i];
            node.remove( null,
                         this.workingMemories );
        }
    }

    /**
     * Make sure the required declarations are previously bound
     * 
     * @param declarations
     * @throws InvalidPatternException
     */
    private void checkUnboundDeclarations(final Declaration[] declarations) throws InvalidPatternException {
        final List list = new ArrayList();
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            if ( this.declarations.get( declarations[i].getIdentifier() ) == null ) {
                list.add( declarations[i].getIdentifier() );
            }
        }

        // Make sure the required declarations        
        if ( list.size() != 0 ) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append( list.get( 0 ) );
            for ( int i = 1, size = list.size(); i < size; i++ ) {
                buffer.append( ", " + list.get( i ) );
            }

            throw new InvalidPatternException( "Required Declarations not bound: '" + buffer );
        }
    }

    public BetaConstraints createBetaNodeConstraint(final List list) {
        BetaConstraints constraints;
        switch ( list.size() ) {
            case 0 :
                constraints = EmptyBetaConstraints.getInstance();
                break;
            case 1 :
                constraints = new SingleBetaConstraints( (BetaNodeFieldConstraint) list.get( 0 ), this.ruleBase.getConfiguration() );
                break;
            case 2 :
                constraints = new DoubleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), this.ruleBase.getConfiguration() );
                break;
            case 3 :
                constraints = new TripleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), this.ruleBase.getConfiguration() );
                break;
            case 4 :
                constraints = new QuadroupleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), this.ruleBase.getConfiguration() );
                break;                
            default :
                constraints = new DefaultBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), this.ruleBase.getConfiguration() );
        }
        return constraints;
    }

}
