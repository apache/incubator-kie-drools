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

package org.drools.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.drools.base.ClassObjectType;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.util.ArrayUtils;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.DataProvider;
import org.drools.spi.DeclarationScopeResolver;

/**
 * LogicTransformation is reponsible for removing redundant nodes and move Or
 * nodes upwards.
 * 
 * This class does not turn Exists into two Nots at this stage, that role is
 * delegated to the Builder.
 */
public class LogicTransformer {
    private final Map               orTransformations = new HashMap();

    private static LogicTransformer INSTANCE          = new LogicTransformer();

    public static LogicTransformer getInstance() {
        return LogicTransformer.INSTANCE;
    }

    protected LogicTransformer() {
        initialize();
    }

    /**
     * sets up the parent->child transformations map
     */
    private void initialize() {
        // these pairs will be transformed
        addTransformationPair( GroupElement.NOT,
                               new NotOrTransformation() );
        addTransformationPair( GroupElement.EXISTS,
                               new ExistOrTransformation() );
        addTransformationPair( GroupElement.AND,
                               new AndOrTransformation() );
    }

    private void addTransformationPair(final GroupElement.Type parent,
                                       final Transformation method) {
        this.orTransformations.put( parent,
                                    method );
    }

    public GroupElement[] transform(final GroupElement cloned) throws InvalidPatternException {
        //moved cloned to up
        //final GroupElement cloned = (GroupElement) and.clone();

        processTree( cloned );
        cloned.pack();

        GroupElement[] ands;
        // is top element an AND?
        if ( cloned.isAnd() ) {
            // Yes, so just return it
            ands = new GroupElement[]{cloned};
        } else if ( cloned.isOr() ) {
            // it is an OR, so each child is an AND branch
            ands = splitOr( cloned );
        } else {
            // no, so just wrap into an AND
            final GroupElement wrapper = GroupElementFactory.newAndInstance();
            wrapper.addChild( cloned );
            ands = new GroupElement[]{wrapper};
        }

        for ( int i = 0; i < ands.length; i++ ) {
            // fix the cloned declarations
            this.fixClonedDeclarations( ands[i] );
            ands[i].setRoot( true );
        }

        return ands;
    }

    protected GroupElement[] splitOr( final GroupElement cloned ) {
        GroupElement[] ands = new GroupElement[cloned.getChildren().size()];
        int i = 0;
        for ( final Iterator it = cloned.getChildren().iterator(); it.hasNext(); ) {
            final RuleConditionElement branch = (RuleConditionElement) it.next();
            if ( (branch instanceof GroupElement) && (((GroupElement) branch).isAnd()) ) {
                ands[i++] = (GroupElement) branch;
            } else {
                ands[i] = GroupElementFactory.newAndInstance();
                ands[i].addChild( branch );
                i++;
            }
        }
        return ands;
    }

    /**
     * During the logic transformation, we eventually clone CEs, 
     * specially patterns and corresponding declarations. So now
     * we need to fix any references to cloned declarations.
     * @param and
     */
    protected void fixClonedDeclarations(GroupElement and) {
        Stack contextStack = new Stack();
        DeclarationScopeResolver resolver = new DeclarationScopeResolver( Collections.<String, Class<?>>emptyMap(),
                                                                          contextStack );

        contextStack.push( and );
        processElement( resolver,
                        contextStack,
                        and );
        contextStack.pop();
    }

    /**
     * recurse through the rule condition elements updating the declaration objecs
     * @param resolver
     * @param contextStack
     * @param element
     */
    private void processElement(final DeclarationScopeResolver resolver,
                                final Stack contextStack,
                                final RuleConditionElement element) {
        if ( element instanceof Pattern ) {
            Pattern pattern = (Pattern) element;
            for ( Iterator it = pattern.getNestedElements().iterator(); it.hasNext(); ) {
                processElement( resolver,
                                contextStack,
                                (RuleConditionElement) it.next() );
            }
            for (Constraint next : pattern.getConstraints()) {
                if (next instanceof Declaration) {
                    continue;
                }
                Constraint constraint = (Constraint) next;
                Declaration[] decl = constraint.getRequiredDeclarations();
                for (int i = 0; i < decl.length; i++) {
                    Declaration resolved = resolver.getDeclaration(null,
                            decl[i].getIdentifier());

                    if (constraint instanceof MvelConstraint && ((MvelConstraint) constraint).isUnification()) {
                        if (ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom(resolved.getPattern().getObjectType())) {
                            Declaration redeclaredDeclr = new Declaration(resolved.getIdentifier(), ((MvelConstraint) constraint).getFieldExtractor(), pattern, false);
                            pattern.addDeclaration(redeclaredDeclr);
                        } else {
                            ((MvelConstraint) constraint).unsetUnification();
                        }
                    }

                    if (resolved != null && resolved != decl[i] && resolved.getPattern() != pattern) {
                        constraint.replaceDeclaration(decl[i],
                                resolved);
                    } else if (resolved == null) {
                        // it is probably an implicit declaration, so find the corresponding pattern
                        Pattern old = decl[i].getPattern();
                        Pattern current = resolver.findPatternByIndex(old.getIndex());
                        if (current != null && old != current) {
                            resolved = new Declaration(decl[i].getIdentifier(),
                                    decl[i].getExtractor(),
                                    current);
                            constraint.replaceDeclaration(decl[i],
                                    resolved);
                        }
                    }
                }
            }
        } else if ( element instanceof EvalCondition ) {
            Declaration[] decl = ((EvalCondition) element).getRequiredDeclarations();
            for (Declaration aDecl : decl) {
                Declaration resolved = resolver.getDeclaration(null,
                        aDecl.getIdentifier());
                if (resolved != null && resolved != aDecl) {
                    ((EvalCondition) element).replaceDeclaration(aDecl,
                            resolved);
                }
            }
        } else if ( element instanceof Accumulate ) {
            for ( RuleConditionElement rce : element.getNestedElements() ) {
                processElement( resolver,
                                contextStack,
                                rce );
            }
            ((Accumulate)element).resetInnerDeclarationCache();
        } else if ( element instanceof From ) {
            DataProvider provider = ((From) element).getDataProvider();
            Declaration[] decl = provider.getRequiredDeclarations();
            for (Declaration aDecl : decl) {
                Declaration resolved = resolver.getDeclaration(null,
                        aDecl.getIdentifier());
                if (resolved != null && resolved != aDecl) {
                    provider.replaceDeclaration(aDecl,
                            resolved);
                } else if (resolved == null) {
                    // it is probably an implicit declaration, so find the corresponding pattern
                    Pattern old = aDecl.getPattern();
                    Pattern current = resolver.findPatternByIndex(old.getIndex());
                    if (current != null && old != current) {
                        resolved = new Declaration(aDecl.getIdentifier(),
                                aDecl.getExtractor(),
                                current);
                        provider.replaceDeclaration(aDecl,
                                resolved);
                    }
                }
            }
        } else if ( element instanceof QueryElement ) {
            QueryElement qe = ( QueryElement ) element;
            Pattern pattern = qe.getResultPattern();
            
            for ( Entry<String, Declaration> entry : pattern.getInnerDeclarations().entrySet() ) {
                Declaration resolved = resolver.getDeclaration( null,
                                                                entry.getValue().getIdentifier() );
                if ( resolved != null && resolved != entry.getValue() && resolved.getPattern() != pattern ) {
                    entry.setValue( resolved );
                }
            }

            
            List<Integer> varIndexes = ArrayUtils.asList( qe.getVariableIndexes() );
            for ( int i = 0; i < qe.getDeclIndexes().length; i++ ) {
                Declaration declr = (Declaration) qe.getArgTemplate()[qe.getDeclIndexes()[i]];
                Declaration resolved = resolver.getDeclaration( null,
                                                                    declr.getIdentifier() );
                if ( resolved != null && resolved != declr && resolved.getPattern() != pattern ) {
                    qe.getArgTemplate()[qe.getDeclIndexes()[i]] = resolved;
                }
                
                if( ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom( resolved.getPattern().getObjectType() ) ) {
                    // if the resolved still points to DroolsQuery, we know this is the first unification pattern, so redeclare it as the visible Declaration
                    declr = pattern.addDeclaration( declr.getIdentifier() );

                    // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                    ArrayElementReader reader = new ArrayElementReader( new SelfReferenceClassFieldReader(Object[].class, "this"),
                                                                        qe.getDeclIndexes()[i],
                                                                        resolved.getExtractor().getExtractToClass() );                    

                    declr.setReadAccessor( reader );  
                    
                    varIndexes.add( qe.getDeclIndexes()[i] );
                }                  
            }
            qe.setVariableIndexes( ArrayUtils.toIntArray( varIndexes ) );            
        } else {
            contextStack.push( element );
            for (RuleConditionElement ruleConditionElement : element.getNestedElements()) {
                processElement(resolver,
                        contextStack,
                        ruleConditionElement);
            }
            contextStack.pop();
        }
    }

    /**
     * Traverses a Tree, during the process it transforms Or nodes moving the
     * upwards and it removes duplicate logic statement, this does not include
     * Not nodes.
     * 
     * Traversal involves three levels the graph for each iteration. The first
     * level is the current node, this node will not be transformed, instead
     * what we are interested in are the children of the current node (called
     * the parent nodes) and the children of those parents (call the child
     * nodes).
     * 
     * @param ce
     */
    protected void processTree(final GroupElement ce) throws InvalidPatternException {

        boolean hasChildOr = false;

        // first we elimininate any redundancy
        ce.pack();

        Object[] children = (Object[]) ce.getChildren().toArray();
        for (Object aChildren : children) {
            if (aChildren instanceof GroupElement) {
                final GroupElement child = (GroupElement) aChildren;

                processTree(child);
                if ((child.isOr() || child.isAnd()) && child.getType() == ce.getType()) {
                    child.pack(ce);
                } else if (child.isOr()) {
                    hasChildOr = true;
                }
            }
        }

        if ( hasChildOr ) {
            applyOrTransformation( ce );
        }
    }

    void applyOrTransformation(final GroupElement parent) throws InvalidPatternException {
        final Transformation transformation = (Transformation) this.orTransformations.get( parent.getType() );

        if ( transformation == null ) {
            throw new RuntimeException( "applyOrTransformation could not find transformation for parent '" + parent.getType() + "' and child 'OR'" );
        }
        transformation.transform( parent );
    }

    interface Transformation {
        void transform(GroupElement element) throws InvalidPatternException;
    }

    /**
     * Takes any And that has an Or as a child and rewrites it to move the Or
     * upwards
     * 
     * (a||b)&&c
     * 
     * <pre>
     *             and
     *             / \
     *            or  c 
     *           /  \
     *          a    b
     * </pre>
     * 
     * Should become (a&&c)||(b&&c)
     * 
     * <pre>
     *               
     *             or
     *            /  \  
     *           /    \ 
     *          /      \ 
     *        and      and     
     *        / \      / \
     *       a   c    b   c
     * </pre>
     */
    class AndOrTransformation
        implements
        Transformation {

        public void transform(final GroupElement parent) throws InvalidPatternException {
            final List orsList = new ArrayList();
            // must keep order, so, using array
            final Object[] others = new Object[parent.getChildren().size()];

            // first we split children as OR or not OR
            int permutations = 1;
            int index = 0;
            for (final RuleConditionElement child : parent.getChildren()) {
                if ((child instanceof GroupElement) && ((GroupElement) child).isOr()) {
                    permutations *= ((GroupElement) child).getChildren().size();
                    orsList.add(child);
                } else {
                    others[index] = child;
                }
                index++;
            }

            // transform parent into an OR
            parent.setType( GroupElement.OR );
            parent.getChildren().clear();

            // prepare arrays and indexes to calculate permutation
            final GroupElement[] ors = (GroupElement[]) orsList.toArray( new GroupElement[orsList.size()] );
            final int[] indexes = new int[ors.length];

            // now we know how many permutations we will have, so create it
            for ( int i = 1; i <= permutations; i++ ) {
                final GroupElement and = GroupElementFactory.newAndInstance();

                // create the actual permutations
                int mod = 1;
                for ( int j = ors.length - 1; j >= 0; j-- ) {
                    // we must insert at the beginning to keep the order
                    and.getChildren().add(0,
                            ors[j].getChildren().get(indexes[j]).clone());
                    if ( (i % mod) == 0 ) {
                        indexes[j] = (indexes[j] + 1) % ors[j].getChildren().size();
                    }
                    mod *= ors[j].getChildren().size();
                }

                // elements originally outside OR will be in every permutation, so add them
                // in their original position
                for ( int j = 0; j < others.length; j++ ) {
                    if ( others[j] != null ) {
                        // always add clone of them to avoid offset conflicts in declarations

                        // HERE IS THE MESSY PROBLEM: need to change further references to the appropriate cloned ref
                        and.getChildren().add( j,
                                               ((RuleConditionElement) others[j]).clone() );
                    }
                }
                parent.addChild( and );
            }

            // remove duplications
            parent.pack();
        }
    }

    /**
     * (Exist (OR (A B)
     * 
     * <pre>
     *         Exist
     *          | 
     *         or   
     *        /  \
     *       a    b
     * </pre>
     * 
     * (Exist ( Not (a) Not (b)) )
     * 
     * <pre>
     *          Or   
     *        /   \
     *    Exists  Exists
     *      |       |
     *      a       b
     * </pre>
     */
    class ExistOrTransformation
        implements
        Transformation {

        public void transform(final GroupElement parent) throws InvalidPatternException {
            if ( (!(parent.getChildren().get( 0 ) instanceof GroupElement)) || (!((GroupElement) parent.getChildren().get( 0 )).isOr()) ) {
                throw new RuntimeException( "ExistOrTransformation expected 'OR' but instead found '" + parent.getChildren().get( 0 ).getClass().getName() + "'" );
            }

            /*
             * we know an Exists only ever has one child, and the previous algorithm
             * has confirmed the child is an OR
             */
            final GroupElement or = (GroupElement) parent.getChildren().get( 0 );
            parent.setType( GroupElement.OR );
            parent.getChildren().clear();
            for (RuleConditionElement ruleConditionElement : or.getChildren()) {
                final GroupElement newExists = GroupElementFactory.newExistsInstance();
                newExists.addChild(ruleConditionElement);
                parent.addChild(newExists);
            }
            parent.pack();
        }
    }

    /**
     * (Not (OR (A B)
     * 
     * <pre>
     *         Not
     *          | 
     *         or   
     *        /  \
     *       a    b
     * </pre>
     * 
     * (And ( Not (a) Exist (b)) )
     * 
     * <pre>
     *         And   
     *        /   \
     *       Not  Not
     *       |     |
     *       a     b
     * </pre>
     */
    public class NotOrTransformation
        implements
        Transformation {

        public void transform(final GroupElement parent) throws InvalidPatternException {

            if ( (!(parent.getChildren().get( 0 ) instanceof GroupElement)) || (!((GroupElement) parent.getChildren().get( 0 )).isOr()) ) {
                throw new RuntimeException( "NotOrTransformation expected 'OR' but instead found '" + parent.getChildren().get( 0 ).getClass().getName() + "'" );
            }

            /*
             * we know a Not only ever has one child, and the previous algorithm
             * has confirmed the child is an OR
             */
            final GroupElement or = (GroupElement) parent.getChildren().get( 0 );
            parent.setType( GroupElement.AND );
            parent.getChildren().clear();
            for (RuleConditionElement ruleConditionElement : or.getChildren()) {
                final GroupElement newNot = GroupElementFactory.newNotInstance();
                newNot.addChild(ruleConditionElement);
                parent.addChild(newNot);
            }
            parent.pack();
        }
    }

    //@todo for 3.1
    //    public class NotAndTransformation
    //        implements
    //        Transformation {
    //
    //        public GroupElement transform(GroupElement not) throws InvalidPatternException {
    //            if ( !(not.getChildren().get( 0 ) instanceof And) ) {
    //                throw new RuntimeException( "NotAndTransformation expected '" + And.class.getName() + "' but instead found '" + not.getChildren().get( 0 ).getClass().getName() + "'" );
    //            }
    //
    //            /*
    //             * we know a Not only ever has one child, and the previous algorithm
    //             * has confirmed the child is an And
    //             */
    //            And and = (And) not.getChildren().get( 0 );
    //            for ( Iterator it = and.getChildren().iterator(); it.hasNext(); ) {
    //                Object object1 = it.next();
    //
    //                for ( Iterator it2 = and.getChildren().iterator(); it.hasNext(); ) {
    //                    Object object2 = it.next();
    //                    if ( object2 != object1 ) {
    //
    //                    }
    //                }
    //
    //                Not newNot = new Not();
    //                newNot.addChild( it.next() );
    //                and.addChild( newNot );
    //            }
    //
    //            return and;
    //        }
    //    }
}
