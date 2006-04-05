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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.InitialFact;
import org.drools.RuleIntegrationException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.EvaluatorFactory;
import org.drools.base.FieldFactory;
import org.drools.common.BetaNodeBinder;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.GroupElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldValue;
import org.drools.spi.ObjectTypeResolver;

/**
 * Builds the Rete-OO network for a <code>Package</code>.
 * 
 * @see org.drools.rule.Package
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 * 
 */
class ReteooBuilder implements Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The RuleBase */
    private final RuleBaseImpl       ruleBase;

    private final ObjectTypeResolver resolver;

    private WorkingMemoryImpl[]      workingMemories;

    /** Rete network to build against. */
    private final Rete               rete;

    /** Rule-sets added. */
    private final List               pkgs;

    /** Nodes that have been attached. */
    private final Map                attachedNodes;

    private TupleSource              tupleSource;

    private ObjectSource             objectSource;
    
    private Map                      declarations;

    private int                      id;

    private Map                      rules;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    ReteooBuilder(RuleBaseImpl ruleBase,
                  ObjectTypeResolver resolver) {
        this.ruleBase = ruleBase;
        this.rete = this.ruleBase.getRete();
        this.resolver = resolver;
        this.pkgs = new ArrayList();
        this.attachedNodes = new HashMap();
        this.rules = new HashMap();

        //Set to 1 as Rete node is set to 0
        this.id = 1;
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
    void addRule(Rule rule) throws InvalidPatternException {
        // reset working memories for potential propagation
        this.workingMemories = (WorkingMemoryImpl[]) this.ruleBase.getWorkingMemories().toArray( new WorkingMemoryImpl[this.ruleBase.getWorkingMemories().size()] );

        List nodes = new ArrayList();
        And[] and = rule.getTransformedLhs();
        for ( int i = 0; i < and.length; i++ ) {
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
                node.attach( workingMemories );
            }            
        }
        
        this.rules.put( rule,
                        ( BaseNode[] )nodes.toArray( new BaseNode[ nodes.size() ] ) );
    }

    private void addRule(And and,
                         Rule rule) throws InvalidPatternException {
        this.objectSource = null;
        this.tupleSource = null;
        this.declarations = new HashMap();

        if ( rule instanceof Query ) {
            attachQuery( rule.getName() );
        }

        for ( Iterator it = and.getChildren().iterator(); it.hasNext(); ) {
            Object object = it.next();
            
            if ( object instanceof EvalCondition ) {
                EvalCondition eval = (EvalCondition) object;
                checkUnboundDeclarations(  eval.getRequiredDeclarations() );
                this.tupleSource = attachNode( new EvalConditionNode( this.id++,
                                                                      this.tupleSource,
                                                                      eval ) );
                continue;
            }
        

            BetaNodeBinder binder;
            Column column;

            if ( object instanceof Column ) {
                column = (Column) object;

                binder = attachColumn( (Column) object,
                                       and );

                // If a tupleSource does not exist then we need to adapt this
                // into
                // a TupleSource using LeftInputAdapterNode
                if ( this.tupleSource == null ) {
                    this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                             this.objectSource,
                                                                             binder) );

                    // objectSource is created by the attachColumn method, if we
                    // adapt this to
                    // a TupleSource then we need to null the objectSource
                    // reference.
                    this.objectSource = null;
                }
            } else {
                // If its not a Column or EvalCondition then it can either be a Not or an Exists
                GroupElement ce = (GroupElement) object;
                while ( !(ce.getChildren().get( 0 ) instanceof Column) ) {
                    ce = (GroupElement) ce.getChildren().get( 0 );
                }
                column = (Column) ce.getChildren().get( 0 );
                binder = attachColumn( column,
                                       and );

                // If a tupleSource does not exist then we need to adapt an
                // InitialFact into a a TupleSource using LeftInputAdapterNode
                if ( this.tupleSource == null ) {
                    ObjectSource objectSource = attachNode( new ObjectTypeNode( this.id++,
                                                                                new ClassObjectType( InitialFact.class ),
                                                                                this.rete ) );

                    this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
                                                                             objectSource ) );

                }
            }

            if ( object instanceof Not ) {
                attachNot( this.tupleSource,
                           (Not) object,
                           this.objectSource,
                           binder,
                           column );
            } else if ( object instanceof Exists ) {
                attachExists( this.tupleSource,
                              (Exists) object,
                              this.objectSource,
                              binder,
                              column );
            } else if ( this.objectSource != null ) {
                this.tupleSource = attachNode( new JoinNode( this.id++,
                                                             this.tupleSource,
                                                             this.objectSource,
                                                             binder ) );
            }
        }
    }

    public BaseNode[] getTerminalNodes(Rule rule) {
        return (BaseNode[]) this.rules.remove( rule );
    }

    private void attachQuery(String queryName) {
        ClassObjectType queryObjectType = new ClassObjectType( DroolsQuery.class );
        ObjectTypeNode queryObjectTypeNode = new ObjectTypeNode( this.id++,
                                                                 queryObjectType,
                                                                 rete );
        queryObjectTypeNode.attach();

        ClassFieldExtractor extractor = new ClassFieldExtractor( DroolsQuery.class,
                                                                 "name" );

        FieldValue field = FieldFactory.getFieldValue( queryName,
                                                       Evaluator.STRING_TYPE );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( Evaluator.STRING_TYPE,
                                                             Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        AlphaNode alphaNode = new AlphaNode( this.id++,
                                             constraint,
                                             queryObjectTypeNode );
        alphaNode.attach();

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( this.id++,
                                                                 alphaNode );
        liaNode.attach();

        this.tupleSource = liaNode;
    }

    private BetaNodeBinder attachColumn(Column column,
                                        GroupElement parent) throws InvalidPatternException {
        // Check if the Column is bound
        if ( column.getDeclaration() != null ) {
            Declaration declaration = column.getDeclaration();
            // Add the declaration the map of previously bound declarations
            this.declarations.put(  declaration.getIdentifier(), declaration );
        }

        List predicates = attachAlphaNodes( column );

        BetaNodeBinder binder;

        if ( !predicates.isEmpty() ) {
            binder = new BetaNodeBinder( (FieldConstraint[]) predicates.toArray( new FieldConstraint[predicates.size()] ) );
        } else {
            binder = new BetaNodeBinder();
        }

        return binder;
    }

    public List attachAlphaNodes(Column column) throws InvalidPatternException {
        List constraints = column.getConstraints();

        this.objectSource = attachNode( new ObjectTypeNode( this.id++,
                                                            column.getObjectType(),
                                                            this.rete ) );

        List predicateConstraints = new ArrayList();

        for ( Iterator it = constraints.iterator(); it.hasNext(); ) {
            Object object = it.next();
            // Check if its a declaration
            if (object instanceof Declaration) {
                Declaration declaration = ( Declaration ) object;
                // Add the declaration the map of previously bound declarations
                this.declarations.put( declaration.getIdentifier(), declaration );
                continue;
            }

            FieldConstraint fieldConstraint = (FieldConstraint) object;
            if ( fieldConstraint instanceof LiteralConstraint ) {
                this.objectSource = attachNode( new AlphaNode( this.id++,
                                                               fieldConstraint,
                                                               objectSource ) );
            } else {
                checkUnboundDeclarations(  fieldConstraint.getRequiredDeclarations() );
                predicateConstraints.add( fieldConstraint );
            }
        }

        return predicateConstraints;
    }

    private void attachNot(TupleSource tupleSource,
                           Not not,
                           ObjectSource ObjectSource,
                           BetaNodeBinder binder,
                           Column column) {
        NotNode notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                             tupleSource,
                                                             ObjectSource,
                                                             binder ) );
        if ( not.getChild() instanceof Not ) {

            RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                           column.getIndex(),
                                                                                                           notNode ) );
            attachNot( tupleSource,
                       (Not) not.getChild(),
                       adapter,
                       new BetaNodeBinder(),
                       column );
        } else if ( not.getChild() instanceof Exists ) {
            RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                           column.getIndex(),
                                                                                                           notNode ) );
            attachExists( tupleSource,
                          (Exists) not.getChild(),
                          adapter,
                          new BetaNodeBinder(),
                          column );
        } else {
            this.tupleSource = notNode;
        }
    }

    private void attachExists(TupleSource tupleSource,
                              Exists exists,
                              ObjectSource ObjectSource,
                              BetaNodeBinder binder,
                              Column column) {
        NotNode notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                             tupleSource,
                                                             ObjectSource,
                                                             binder ) );
        RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                       column.getIndex(),
                                                                                                       notNode ) );
        notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                     tupleSource,
                                                     adapter,
                                                     new BetaNodeBinder() ) );

        if ( exists.getChild() instanceof Not ) {
            adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                     column.getIndex(),
                                                                                     notNode ) );
            attachNot( tupleSource,
                       (Not) exists.getChild(),
                       adapter,
                       new BetaNodeBinder(),
                       column );
        } else if ( exists.getChild() instanceof Exists ) {
            adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                     column.getIndex(),
                                                                                     notNode ) );
            attachExists( tupleSource,
                          (Exists) exists.getChild(),
                          adapter,
                          new BetaNodeBinder(),
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
    private TupleSource attachNode(TupleSource candidate) {
        TupleSource node = (TupleSource) this.attachedNodes.get( candidate );

        if ( node == null ) {
            if ( this.workingMemories.length == 0 ) {
                candidate.attach();
            } else {
                candidate.attach( workingMemories );
            }

            this.attachedNodes.put( candidate,
                                    candidate );

            node = candidate;
        } else {
            node.addShare();
            this.id--;
        }

        return node;
    }

    private ObjectSource attachNode(ObjectSource candidate) {
        ObjectSource node = (ObjectSource) this.attachedNodes.get( candidate );

        if ( node == null ) {
            if ( this.workingMemories.length == 0 ) {
                candidate.attach();
            } else {
                candidate.attach( workingMemories );
            }

            this.attachedNodes.put( candidate,
                                    candidate );

            node = candidate;
        } else {
            this.id--;
        }

        return node;
    }
    
    public void removeRule(Rule rule) {
        // reset working memories for potential propagation
        this.workingMemories = (WorkingMemoryImpl[]) this.ruleBase.getWorkingMemories().toArray( new WorkingMemoryImpl[this.ruleBase.getWorkingMemories().size()] );
        
        Object object = this.rules.get( rule );
        
        BaseNode[] nodes =  ( BaseNode[] ) object;
        for ( int i = 0, length = nodes.length; i < length; i++ ) {
            BaseNode node = nodes[i];
            node.remove( null, this.workingMemories );
        }
    }
    
    /**
     * Make sure the required declarations are previously bound
     * 
     * @param declarations
     * @throws InvalidPatternException
     */
    private void checkUnboundDeclarations(Declaration[] declarations) throws InvalidPatternException {
        List list = new ArrayList();
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
          if ( this.declarations.get( declarations[i].getIdentifier() ) == null ) {
              list.add( declarations[i].getIdentifier() );
          }
        }
        
        // Make sure the required declarations        
        if ( list.size() != 0 ) {
            StringBuffer buffer = new StringBuffer();
            buffer.append( list.get( 0 ) );
            for ( int i = 1, size = list.size(); i < size; i++ ) {
                buffer.append( ", " + list.get( i )  );
            }
            
            throw new InvalidPatternException("Required Declarations not bound: '" + buffer );
        }

    }

}
