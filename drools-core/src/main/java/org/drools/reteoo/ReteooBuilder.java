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
import org.drools.RuntimeDroolsException;
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
import org.drools.common.InstanceNotEqualsConstraint;
import org.drools.common.QuadroupleBetaConstraints;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.reteoo.builder.ReteooRuleBuilder;
import org.drools.rule.Accumulate;
import org.drools.rule.Collect;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
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
public class ReteooBuilder
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

    private Map                             rules;

    private final boolean                   removeIdentities;
    
    private transient ReteooRuleBuilder     ruleBuilder;
    
    private IdGenerator                     idGenerator;

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
        this.idGenerator = new IdGenerator(1);
        this.removeIdentities = this.ruleBase.getConfiguration().isRemoveIdentities();
        this.ruleBuilder = new ReteooRuleBuilder();
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
        List terminals = this.ruleBuilder.addRule( rule, this.ruleBase, this.attachedNodes, this.idGenerator );
        

        this.rules.put( rule,
                        terminals.toArray( new BaseNode[terminals.size()] ) );
    }

    public BaseNode[] getTerminalNodes(final Rule rule) {
        return (BaseNode[]) this.rules.remove( rule );
    }

//    private void attachAccumulate(final TupleSource tupleSource,
//                                  final GroupElement parent,
//                                  final Accumulate accumulate) {
//        // If a tupleSource does not exist then we need to adapt an
//        // InitialFact into a a TupleSource using LeftInputAdapterNode
//        if ( this.tupleSource == null ) {
//            // adjusting offset as all tuples will now contain initial-fact at index 0
//            this.currentOffsetAdjustment = 1;
//
//            final ObjectSource auxObjectSource = attachNode( new ObjectTypeNode( this.id++,
//                                                                                 new ClassObjectType( InitialFact.class ),
//                                                                                 this.rete,
//                                                                                 this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold()) );
//
//            this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
//                                                                     auxObjectSource ) );
//        }
//
//        final Column sourceColumn = accumulate.getSourceColumn();
//        final BetaConstraints sourceBinder = attachColumn( sourceColumn,
//                                                           parent,
//                                                           true );
//
//        final Column column = accumulate.getResultColumn();
//        // Adjusting offset in case a previous Initial-Fact was added to the network
//        column.adjustOffset( this.currentOffsetAdjustment );
//
//        final List constraints = column.getConstraints();
//
//        // Check if the Column is bound
//        if ( column.getDeclaration() != null ) {
//            final Declaration declaration = column.getDeclaration();
//            // Add the declaration the map of previously bound declarations
//            this.declarations.put( declaration.getIdentifier(),
//                                   declaration );
//        }
//
//        final List betaConstraints = new ArrayList();
//        final List alphaConstraints = new ArrayList();
//
//        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
//            final Object object = it.next();
//            // Check if its a declaration
//            if ( object instanceof Declaration ) {
//                final Declaration declaration = (Declaration) object;
//                // Add the declaration the map of previously bound declarations
//                this.declarations.put( declaration.getIdentifier(),
//                                       declaration );
//                continue;
//            }
//
//            final AlphaNodeFieldConstraint fieldConstraint = (AlphaNodeFieldConstraint) object;
//            if ( fieldConstraint instanceof LiteralConstraint ) {
//                alphaConstraints.add( fieldConstraint );
//            } else {
//                checkUnboundDeclarations( fieldConstraint.getRequiredDeclarations() );
//                betaConstraints.add( fieldConstraint );
//            }
//        }
//
//        final BetaConstraints resultsBinder = createBetaNodeConstraint( betaConstraints );
//
//        this.tupleSource = attachNode( new AccumulateNode( this.id++,
//                                                           this.tupleSource,
//                                                           this.objectSource,
//                                                           (AlphaNodeFieldConstraint[]) alphaConstraints.toArray( new AlphaNodeFieldConstraint[alphaConstraints.size()] ),
//                                                           sourceBinder,
//                                                           resultsBinder,
//                                                           accumulate ) );
//    }
//

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
    
    public static class IdGenerator implements Serializable {

        private static final long serialVersionUID = -5909710713463187779L;

        private int nextId;
        
        public IdGenerator( int firstId ) {
            this.nextId = firstId;
        }
        
        public int getNextId() {
            return this.nextId++;
        }
        
        public void releaseLastId() {
            this.nextId--;
        }
        
    }


}
