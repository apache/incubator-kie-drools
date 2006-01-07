package org.drools.reteoo;

/*
 * $Id: Builder.java,v 1.72 2005/02/02 00:23:21 mproctor Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.InitialFact;
import org.drools.RuleIntegrationException;
import org.drools.rule.And;
import org.drools.rule.Binding;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.Exists;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeConstraint;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Constraint;
import org.drools.spi.ObjectTypeResolver;

/**
 * Builds the Rete-OO network for a <code>RuleSet</code>.
 * 
 * @see org.drools.rule.RuleSet
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * TODO Make joinForCondition actually be intelligent enough to build optimal
 * joins. Currently using forgy's original description of 2-input nodes, which I
 * feel (but don't know for sure, is sub-optimal.
 */
class Builder {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The RuleBase */
    private final RuleBaseImpl ruleBase;

    private final ObjectTypeResolver resolver;
    
    /** Rete network to build against. */
    private final Rete         rete;

    /** Rule-sets added. */
    private final List         ruleSets;

    /** Nodes that have been attached. */
    private final Map          attachedNodes;

    private final Map          applicationData;

    private TupleSource  tupleSource;

    private ObjectSource objectSource;

    private Map          declarations;

    private int          id;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    Builder(RuleBaseImpl ruleBase, ObjectTypeResolver resolver) {
        this.ruleBase = ruleBase;
        this.rete = new Rete();
        this.resolver = resolver;
        this.ruleSets = new ArrayList();
        this.attachedNodes = new HashMap();
        this.applicationData = new HashMap();
        this.declarations = new HashMap();
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
        And[] and = rule.getProcessPatterns();
        for ( int i = 0; i < and.length; i++ ) {
            addRule( and[i],
                     rule );
        }
        TerminalNode node = new TerminalNode( this.tupleSource,
                                              rule );
    }

    private void addRule(And and,
                         Rule rule) {
        for ( Iterator it = and.getChildren().iterator(); it.hasNext(); ) {
            Object object = it.next();

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
                                                                             column.getIndex(),
                                                                             this.objectSource ) );

                    // objectSource is created by the attachColumn method, if we
                    // adapt this to
                    // a TupleSource then we need to null the objectSource
                    // reference.
                    this.objectSource = null;
                }
            } else {
                // If its not a Column then it can either be a Not or an Exists
                ConditionalElement ce = (ConditionalElement) object;
                while ( !(ce.getChildren().get( 0 ) instanceof Column) ) {
                    ce = (ConditionalElement) ce.getChildren().get( 0 );
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
                                                                             column.getIndex(),
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
                                                             column.getIndex(),
                                                             binder ) );
            }
        }
    }

    private BetaNodeBinder attachColumn(Column column,
                                        ConditionalElement parent) {
        addDeclarations( column );

        List predicates = attachAlphaNodes( column );

        BetaNodeBinder binder;

        if ( !predicates.isEmpty() ) {
            binder = new BetaNodeBinder( (BetaNodeConstraint[]) predicates.toArray( new BetaNodeConstraint[predicates.size()] ) );
        } else {
            binder = new BetaNodeBinder();
        }

        return binder;
    }

    private void addDeclarations(Column column) {
        for ( Iterator it = column.getDeclarations().iterator(); it.hasNext(); ) {
            Declaration declaration = (Declaration) it.next();
            this.declarations.put( declaration.getIdentifier(),
                                   declaration );
        }

        if ( column.getBinding() != null ) {
            Binding binding = column.getBinding();
            this.declarations.put( binding.getIdentifier(),
                                   binding );
        }
    }

    public List attachAlphaNodes(Column column) {
        List constraints = column.getConstraints();

        ObjectSource objectSource = attachNode( new ObjectTypeNode( this.id++,
                                                                    column.getObjectType(),
                                                                    this.rete ) );

        List predicateConstraints = new ArrayList();

        for ( Iterator it = constraints.iterator(); it.hasNext(); ) {
            Constraint constraint = (Constraint) it.next();
            if ( constraint instanceof LiteralConstraint ) {
                this.objectSource = attachNode( new AlphaNode( this.id++,
                                                               (LiteralConstraint) constraint,
                                                               true,
                                                               objectSource ) );
            } else if (constraint instanceof BetaNodeConstraint ){
                predicateConstraints.add( constraint );
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
                                                             column.getIndex(),
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
                                                             column.getIndex(),
                                                             binder ) );
        RightInputAdapterNode adapter = (RightInputAdapterNode) attachNode( new RightInputAdapterNode( this.id++,
                                                                                                       column.getIndex(),
                                                                                                       notNode ) );
        notNode = (NotNode) attachNode( new NotNode( this.id++,
                                                     tupleSource,
                                                     adapter,
                                                     column.getIndex(),
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
            candidate.attach();

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
            candidate.attach();

            this.attachedNodes.put( candidate,
                                    candidate );

            node = candidate;
        } else {
            this.id--;
        }

        return node;
    }

}
