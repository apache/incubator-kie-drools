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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * LogicTransformation is reponsible for removing redundant nodes and move Or
 * nodes upwards.
 * 
 * This class does not turn Exists into two Nots at this stage, that role is
 * delegated to the Builder.
 * 
 * @author mproctor
 * 
 */
class LogicTransformer {
    private final Map               orTransformations = new HashMap();

    private static LogicTransformer INSTANCE          = null;

    static LogicTransformer getInstance() {
        if ( LogicTransformer.INSTANCE == null ) {
            LogicTransformer.INSTANCE = new LogicTransformer();
        }

        return LogicTransformer.INSTANCE;
    }

    LogicTransformer() {
        initialize();
    }

    /**
     * sets up the parent->child transformations map
     * 
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

    public GroupElement[] transform(final GroupElement and) throws InvalidPatternException {
        GroupElement cloned = (GroupElement) and.clone();
        
        processTree( cloned );
        cloned.pack();

        GroupElement[] ands = null;
        // is top element an AND?
        if ( cloned.isAnd() ) {
            // Yes, so just return it
            ands = new GroupElement[]{cloned};
        } else if( cloned.isOr() ){
            // it is an OR, so each child is an AND branch
            ands = new GroupElement[cloned.getChildren().size()];
            int i = 0;
            for( Iterator it = cloned.getChildren().iterator(); it.hasNext(); ) {
                Object branch = it.next();
                if( ( branch instanceof GroupElement ) && (((GroupElement)branch).isAnd()) ){
                    ands[i++] = (GroupElement) branch;
                } else {
                    ands[i] = GroupElementFactory.newAndInstance();
                    ands[i].addChild( branch );
                    i++;
                }
            }
        } else {
            // no, so just wrap into an AND
            GroupElement wrapper = GroupElementFactory.newAndInstance();
            wrapper.addChild( cloned );
            ands = new GroupElement[]{wrapper};
        }
        return ands;
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
    void processTree(final GroupElement ce) throws InvalidPatternException {

        boolean hasChildOr = false;
        
        // first we eliminicate any redundancy
        ce.pack();
        
        for ( final ListIterator it = ce.getChildren().listIterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof GroupElement ) {
                final GroupElement child = (GroupElement) object;

                processTree( child );

                if ( child.isOr() ) {
                    hasChildOr = true;
                }
            }
        }
        if ( hasChildOr ) {
            applyOrTransformation( ce );
        }
    }

    void applyOrTransformation(final GroupElement parent) throws InvalidPatternException {
        Transformation transformation = (Transformation) this.orTransformations.get( parent.getType() );

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
            List orsList = new ArrayList();
            // must keep order, so, using array
            Object[] others = new Object[parent.getChildren().size()];

            // first we split children as OR or not OR
            int permutations = 1;
            int index = 0;
            for ( Iterator it = parent.getChildren().iterator(); it.hasNext(); ) {
                Object child = it.next();
                if ( (child instanceof GroupElement) && ((GroupElement) child).isOr() ) {
                    permutations *= ((GroupElement) child).getChildren().size();
                    orsList.add( child );
                } else {
                    others[index] = child ;
                }
                index++;
            }

            // transform parent into an OR
            parent.setType( GroupElement.OR );
            parent.getChildren().clear();

            // prepare arrays and indexes to calculate permutation
            GroupElement[] ors = (GroupElement[]) orsList.toArray( new GroupElement[orsList.size()] );
            int[] indexes = new int[ors.length];

            // now we know how many permutations we will have, so create it
            for ( int i = 1; i <= permutations; i++ ) {
                GroupElement and = GroupElementFactory.newAndInstance();

                // create the actual permutations
                int mod = 1;
                for ( int j = ors.length - 1; j >= 0; j-- ) {
                    // we must insert at the beggining to keep the order
                    and.getChildren().add( 0, ors[j].getChildren().get( indexes[j] ) );
                    if ( (i % mod) == 0 ) {
                        indexes[j] = (indexes[j] + 1) % ors[j].getChildren().size();
                    }
                    mod *= ors[j].getChildren().size();
                }

                // elements originally outside OR will be in every permutation, so add them
                // in their original position
                for( int j = 0; j < others.length; j++ ) {
                    if( others[j] != null ) {
                        and.getChildren().add( j, others[j] );
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
            if ( (!(parent.getChildren().get( 0 ) instanceof GroupElement)) && (((GroupElement) parent.getChildren().get( 0 )).isExists()) ) {
                throw new RuntimeException( "ExistOrTransformation expected 'OR' but instead found '" + parent.getChildren().get( 0 ).getClass().getName() + "'" );
            }

            /*
             * we know an Exists only ever has one child, and the previous algorithm
             * has confirmed the child is an OR
             */
            GroupElement or = (GroupElement) parent.getChildren().get( 0 );
            parent.setType( GroupElement.OR );
            parent.getChildren().clear();
            for ( Iterator it = or.getChildren().iterator(); it.hasNext(); ) {
                GroupElement newExists = GroupElementFactory.newExistsInstance();
                newExists.addChild( it.next() );
                parent.addChild( newExists );
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

            if ( (!(parent.getChildren().get( 0 ) instanceof GroupElement)) && (((GroupElement) parent.getChildren().get( 0 )).isOr()) ) {
                throw new RuntimeException( "NotOrTransformation expected 'OR' but instead found '" + parent.getChildren().get( 0 ).getClass().getName() + "'" );
            }

            /*
             * we know a Not only ever has one child, and the previous algorithm
             * has confirmed the child is an OR
             */
            GroupElement or = (GroupElement) parent.getChildren().get( 0 );
            parent.setType( GroupElement.AND );
            parent.getChildren().clear();
            for ( Iterator it = or.getChildren().iterator(); it.hasNext(); ) {
                GroupElement newNot = GroupElementFactory.newNotInstance();
                newNot.addChild( it.next() );
                parent.addChild( newNot );
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