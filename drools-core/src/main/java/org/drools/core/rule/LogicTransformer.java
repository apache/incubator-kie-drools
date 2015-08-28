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

package org.drools.core.rule;

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.DeclarationScopeResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * LogicTransformation is reponsible for removing redundant nodes and move Or
 * nodes upwards.
 * 
 * This class does not turn Exists into two Nots at this stage, that role is
 * delegated to the Builder.
 */
public class LogicTransformer {
    private final Map<GroupElement.Type, Transformation> orTransformations = new HashMap<GroupElement.Type, Transformation>();

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

    public GroupElement[] transform( final GroupElement cloned, Map<String, Class<?>> globals ) throws InvalidPatternException {
        //moved cloned to up
        //final GroupElement cloned = (GroupElement) and.clone();

        boolean hasNamedConsequenceAndIsStream = processTree( cloned );
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

        for ( GroupElement and : ands ) {
            // fix the cloned declarations
            this.fixClonedDeclarations( and, globals );
            and.setRoot( true );
        }

        return hasNamedConsequenceAndIsStream ? processNamedConsequences(ands) : ands;
    }

    private GroupElement[] processNamedConsequences(GroupElement[] ands) {
        List<GroupElement> result = new ArrayList<GroupElement>();

        for (GroupElement and : ands) {
            List<RuleConditionElement> children = and.getChildren();
            for (int i = 0; i < children.size(); i++) {
                RuleConditionElement child = children.get(i);
                if (child instanceof NamedConsequence) {
                    GroupElement clonedAnd = GroupElementFactory.newAndInstance();
                    for (int j = 0; j < i; j++) {
                        clonedAnd.getChildren().add(children.get(j).clone());
                    }
                    ((NamedConsequence) child).setTerminal(true);
                    clonedAnd.getChildren().add(child);
                    children.remove(i--);
                    result.add(clonedAnd);
                }
            }
            result.add(and);
        }

        return result.toArray(new GroupElement[result.size()]);
    }

    protected GroupElement[] splitOr( final GroupElement cloned ) {
        GroupElement[] ands = new GroupElement[cloned.getChildren().size()];
        int i = 0;
        for ( final RuleConditionElement branch : cloned.getChildren() ) {
            if ( ( branch instanceof GroupElement ) && ( ( (GroupElement) branch ).isAnd() ) ) {
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
     */
    protected void fixClonedDeclarations( GroupElement and, Map<String, Class<?>> globals ) {
        Stack<RuleConditionElement> contextStack = new Stack<RuleConditionElement>();
        DeclarationScopeResolver resolver = new DeclarationScopeResolver( globals,
                                                                          contextStack );

        contextStack.push( and );
        processElement( resolver,
                        contextStack,
                        and );
        contextStack.pop();
    }

    /**
     * recurse through the rule condition elements updating the declaration objecs
     */
    private void processElement(final DeclarationScopeResolver resolver,
                                final Stack<RuleConditionElement> contextStack,
                                final RuleConditionElement element) {
        if ( element instanceof Pattern ) {
            Pattern pattern = (Pattern) element;
            for ( RuleConditionElement ruleConditionElement : pattern.getNestedElements() ) {
                processElement( resolver,
                                contextStack,
                                ruleConditionElement );
            }
            for (Constraint constraint : pattern.getConstraints()) {
                if (constraint instanceof Declaration) {
                    continue;
                }
                replaceDeclarations( resolver, pattern, constraint );
            }

        } else if ( element instanceof EvalCondition ) {
            processEvalCondition(resolver, (EvalCondition) element);

        } else if ( element instanceof Accumulate ) {
            for ( RuleConditionElement rce : element.getNestedElements() ) {
                processElement( resolver, contextStack, rce );
            }
            Accumulate accumulate = (Accumulate)element;
            replaceDeclarations( resolver, accumulate );
            accumulate.resetInnerDeclarationCache();

        } else if ( element instanceof From ) {
            DataProvider provider = ((From) element).getDataProvider();
            Declaration[] decl = provider.getRequiredDeclarations();
            for (Declaration aDecl : decl) {
                Declaration resolved = resolver.getDeclaration(null, aDecl.getIdentifier());
                if (resolved != null && resolved != aDecl) {
                    provider.replaceDeclaration(aDecl, resolved);
                } else if (resolved == null) {
                    // it is probably an implicit declaration, so find the corresponding pattern
                    Pattern old = aDecl.getPattern();
                    Pattern current = resolver.findPatternByIndex(old.getIndex());
                    if (current != null && old != current) {
                        resolved = new Declaration(aDecl.getIdentifier(), aDecl.getExtractor(), current);
                        provider.replaceDeclaration(aDecl, resolved);
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

            List<Integer> varIndexes = asList( qe.getVariableIndexes() );
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
                                                                        resolved.getDeclarationClass() );
                    declr.setReadAccessor( reader );
                    
                    varIndexes.add( qe.getDeclIndexes()[i] );
                }                  
            }
            qe.setVariableIndexes( toIntArray( varIndexes ) );

        }  else if ( element instanceof ConditionalBranch ) {
            processBranch( resolver, (ConditionalBranch) element );

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

    private void replaceDeclarations( DeclarationScopeResolver resolver, Pattern pattern, Constraint constraint ) {
        Declaration[] decl = constraint.getRequiredDeclarations();
        for ( Declaration aDecl : decl ) {
            Declaration resolved = resolver.getDeclaration( null,
                                                            aDecl.getIdentifier() );

            if ( constraint instanceof MvelConstraint && ( (MvelConstraint) constraint ).isUnification() ) {
                if ( ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom( resolved.getPattern().getObjectType() ) ) {
                    Declaration redeclaredDeclr = new Declaration( resolved.getIdentifier(), ( (MvelConstraint) constraint ).getFieldExtractor(), pattern, false );
                    pattern.addDeclaration( redeclaredDeclr );
                } else if ( resolved.getPattern() != pattern ) {
                    ( (MvelConstraint) constraint ).unsetUnification();
                }
            }

            if ( resolved != null && resolved != aDecl && resolved.getPattern() != pattern ) {
                constraint.replaceDeclaration( aDecl,
                                               resolved );
            } else if ( resolved == null ) {
                // it is probably an implicit declaration, so find the corresponding pattern
                Pattern old = aDecl.getPattern();
                Pattern current = resolver.findPatternByIndex( old.getIndex() );
                if ( current != null && old != current ) {
                    resolved = new Declaration( aDecl.getIdentifier(), aDecl.getExtractor(),
                                                current );
                    constraint.replaceDeclaration( aDecl, resolved );
                }
            }
        }
    }

    private void replaceDeclarations( DeclarationScopeResolver resolver, Accumulate accumulate ) {
        Declaration[] decl = accumulate.getRequiredDeclarations();
        for ( Declaration aDecl : decl ) {
            Declaration resolved = resolver.getDeclaration( null,
                                                            aDecl.getIdentifier() );

            if ( resolved != null && resolved != aDecl ) {
                accumulate.replaceDeclaration( aDecl,
                                               resolved );
            } else if ( resolved == null ) {
                // it is probably an implicit declaration, so find the corresponding pattern
                Pattern old = aDecl.getPattern();
                Pattern current = resolver.findPatternByIndex( old.getIndex() );
                if ( current != null && old != current ) {
                    resolved = new Declaration( aDecl.getIdentifier(), aDecl.getExtractor(),
                                                current );
                    accumulate.replaceDeclaration( aDecl, resolved );
                }
            }
        }
    }

    private static List<Integer> asList(int[] ints) {
        List<Integer> list = new ArrayList<Integer>(ints.length);
        for ( int i : ints ) {
            list.add( i );
        }
        return list;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] ints = new int[list.size()];
        for ( int i = 0; i < list.size(); i++ ) {
            ints[i] = list.get( i );
        }
        return ints;
    }

    private void processEvalCondition(DeclarationScopeResolver resolver, EvalCondition element) {
        Declaration[] decl = element.getRequiredDeclarations();
        for (Declaration aDecl : decl) {
            Declaration resolved = resolver.getDeclaration(null,
                    aDecl.getIdentifier());
            if (resolved != null && resolved != aDecl) {
                element.replaceDeclaration( aDecl, resolved );
            }
        }
    }

    private void processBranch(DeclarationScopeResolver resolver, ConditionalBranch branch) {
        processEvalCondition(resolver, branch.getEvalCondition());
        if ( branch.getElseBranch() != null ) {
            processBranch(resolver, branch.getElseBranch());
        }
    }


    protected boolean processTree(final GroupElement ce) throws InvalidPatternException {
        boolean[] hasNamedConsequenceAndIsStream = new boolean[2];
        processTree(ce, hasNamedConsequenceAndIsStream);
        return hasNamedConsequenceAndIsStream[0] && hasNamedConsequenceAndIsStream[1];
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
     */
    private void processTree(final GroupElement ce, boolean[] result) throws InvalidPatternException {
        boolean hasChildOr = false;

        // first we elimininate any redundancy
        ce.pack();

        for (Object child : ce.getChildren().toArray()) {
            if (child instanceof GroupElement) {
                final GroupElement group = (GroupElement) child;

                processTree(group, result);
                if ((group.isOr() || group.isAnd()) && group.getType() == ce.getType()) {
                    group.pack(ce);
                } else if (group.isOr()) {
                    hasChildOr = true;
                }
            } else if (child instanceof NamedConsequence) {
                result[0] = true;
            } else if (child instanceof Pattern && ((Pattern) child).getObjectType().isEvent()) {
                result[1] = true;
            }
        }

        if ( hasChildOr ) {
            applyOrTransformation( ce );
        }
    }

    void applyOrTransformation(final GroupElement parent) throws InvalidPatternException {
        final Transformation transformation = this.orTransformations.get( parent.getType() );

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
            final List<GroupElement> orsList = new ArrayList<GroupElement>();
            // must keep order, so, using array
            final RuleConditionElement[] others = new RuleConditionElement[parent.getChildren().size()];

            // first we split children as OR or not OR
            int permutations = 1;
            int index = 0;
            for (final RuleConditionElement child : parent.getChildren()) {
                if ((child instanceof GroupElement) && ((GroupElement) child).isOr()) {
                    permutations *= ((GroupElement) child).getChildren().size();
                    orsList.add((GroupElement)child);
                } else {
                    others[index] = child;
                }
                index++;
            }

            // transform parent into an OR
            parent.setType( GroupElement.OR );
            parent.getChildren().clear();

            // prepare arrays and indexes to calculate permutation
            final int[] indexes = new int[orsList.size()];

            // now we know how many permutations we will have, so create it
            for ( int i = 1; i <= permutations; i++ ) {
                final GroupElement and = GroupElementFactory.newAndInstance();

                // create the actual permutations
                int mod = 1;
                for ( int j = orsList.size() - 1; j >= 0; j-- ) {
                    GroupElement or = orsList.get(j);
                    // we must insert at the beginning to keep the order
                    and.getChildren().add(0,
                                          or.getChildren().get(indexes[j]).clone());
                    if ( (i % mod) == 0 ) {
                        indexes[j] = (indexes[j] + 1) % or.getChildren().size();
                    }
                    mod *= or.getChildren().size();
                }

                // elements originally outside OR will be in every permutation, so add them
                // in their original position
                for ( int j = 0; j < others.length; j++ ) {
                    if ( others[j] != null ) {
                        // always add clone of them to avoid offset conflicts in declarations

                        // HERE IS THE MESSY PROBLEM: need to change further references to the appropriate cloned ref
                        and.getChildren().add( j, others[j].clone() );
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
     * Exist
     * |
     * or
     * / \
     * a b
     * </pre>
     *
     * (Not Exist ( Not (a) And Not (b)) )
     *
     * <pre>
     * Not
     * |
     * And
     * / \
     * Not Not
     * | |
     * a b
     * </pre>
     */
    class ExistOrTransformation
            implements
            Transformation {

        public void transform(final GroupElement parent) throws InvalidPatternException {
            if ( (!(parent.getChildren().get( 0 ) instanceof GroupElement)) || (!((GroupElement) parent.getChildren().get( 0 )).isOr()) ) {
                throw new RuntimeException( "ExistOrTransformation expected 'OR' but instead found '" + parent.getChildren().get( 0 ).getClass().getName() + "'" );
            }

            // we know an Exists only ever has one child, and the previous algorithm
            // has confirmed the child is an OR

            final GroupElement or = (GroupElement) parent.getChildren().get( 0 );
            parent.setType( GroupElement.NOT );
            parent.getChildren().clear();
            final GroupElement and = GroupElementFactory.newAndInstance();
            for (RuleConditionElement ruleConditionElement : or.getChildren()) {
                final GroupElement newNot = GroupElementFactory.newNotInstance();
                newNot.addChild(ruleConditionElement);
                and.addChild(newNot);
            }
            parent.addChild( and );
            parent.pack();
        }
    }

    /**
     * (Not (OR (A B) )
     *
     * <pre>
     * Not
     * |
     * or
     * / \
     * a b
     * </pre>
     *
     * (And ( Not (a) Not (b)) )
     *
     * <pre>
     * And
     * / \
     * Not Not
     * | |
     * a b
     * </pre>
     */
    public class NotOrTransformation
            implements
            Transformation {

        public void transform(final GroupElement parent) throws InvalidPatternException {

            if ( (!(parent.getChildren().get( 0 ) instanceof GroupElement)) || (!((GroupElement) parent.getChildren().get( 0 )).isOr()) ) {
                throw new RuntimeException( "NotOrTransformation expected 'OR' but instead found '" + parent.getChildren().get( 0 ).getClass().getName() + "'" );
            }

            // we know a Not only ever has one child, and the previous algorithm
            // has confirmed the child is an OR

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
}
