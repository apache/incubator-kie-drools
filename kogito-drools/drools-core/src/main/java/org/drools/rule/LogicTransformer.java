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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

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
    private final Map               duplicateTransformations = new HashMap();
    private final Map               orTransformations        = new HashMap();

    private static LogicTransformer INSTANCE                 = null;

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
        // these pairs will have their duplciates removed
        addTransformationPair( And.class,
                               And.class );
        addTransformationPair( Or.class,
                               Or.class );
        addTransformationPair( Exists.class,
                               Exists.class );

        // these pairs will be transformed
        addTransformationPair( Not.class,
                               Or.class,
                               new NotOrTransformation() );
        addTransformationPair( Exists.class,
                               Or.class,
                               new ExistOrTransformation() );
        addTransformationPair( And.class,
                               Or.class,
                               new AndOrTransformation() );
    }

    private void addTransformationPair(Class parent,
                                       Class child) {
        Map map = this.duplicateTransformations;
        Set childSet = (Set) map.get( child );
        if ( childSet == null ) {
            childSet = new HashSet();
            map.put( parent,
                     childSet );
        }
        childSet.add( child );
    }

    private void addTransformationPair(Class parent,
                                       Class child,
                                       Object method) {
        Map map = this.orTransformations;
        Map childMap = (Map) map.get( parent );
        if ( childMap == null ) {
            childMap = new HashMap();
            map.put( parent,
                     childMap );
        }
        childMap.put( child,
                      method );
    }

    And[] transform(And and) throws InvalidPatternException {
        And cloned = (And) and.clone();

        processTree( cloned );

        // Scan for any Child Ors, if found we need apply the
        // AndOrTransformation
        // And assign the result to the null declared or
        Or or = null;
        for ( Iterator it = cloned.getChildren().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof Or ) {
                or = (Or) applyOrTransformation( cloned,
                                                 (GroupElement) object );
                break;
            }
        }

        And[] ands = null;
        // Or will be null if there are no Ors in our tree
        if ( or == null ) {
            // No or so just assign
            ands = new And[]{cloned};
        } else {
            ands = new And[or.getChildren().size()];
            int i = 0;
            for ( Iterator it = or.getChildren().iterator(); it.hasNext(); ) {
                Object object = it.next();
                if ( object.getClass() == And.class ) {
                    ands[i] = (And) object;    
                } else {
                    And newAnd = new And();
                    newAnd.addChild( and );
                    ands[i] = newAnd; 
                }
                
                checkForAndRemoveDuplicates( ands[i] );
                
                i++;
            }

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
    void processTree(GroupElement ce) throws InvalidPatternException {

        for ( ListIterator it = ce.getChildren().listIterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof GroupElement ) {
                GroupElement parent = (GroupElement) object;

                processTree( parent );

                checkForAndRemoveDuplicates( parent );

                // Scan for any Child Ors, if found we need to move the Or
                // upwards
                for ( Iterator orIter = parent.getChildren().iterator(); orIter.hasNext(); ) {
                    Object object2 = orIter.next();
                    if ( object2 instanceof Or ) {
                        it.remove();
                        it.add( applyOrTransformation( parent,
                                                       (GroupElement) object2 ) );                        
                        break;
                    }
                }

            }
        }
    }

    /**
     * Given a parent and child checks if they are duplicates and that they set
     * to have duplicates removed
     * 
     * @param parent
     * @param child
     * @return
     */
    boolean removeDuplicate(GroupElement parent,
                            GroupElement child) {
        if ( this.duplicateTransformations.get( parent.getClass() ) != null ) {
            return ((HashSet) this.duplicateTransformations.get( parent.getClass() )).contains( child.getClass() );
        }

        return false;
    }

    /**
     * Removes duplicates, children of the duplicate added to the parent and the
     * duplicate child is removed by the parent method.
     * 
     */
    void checkForAndRemoveDuplicates(GroupElement parent) {
        for ( ListIterator it = parent.getChildren().listIterator(); it.hasNext(); ) {
            Object object = it.next();
            // Remove the duplicate if the classes are the same and
            // removeDuplicate method returns true
            if ( parent.getClass().isInstance( object ) && removeDuplicate( parent,
                                                                           (GroupElement) object ) ) {
                List newList = new ArrayList(); 
                GroupElement child = (GroupElement) object;
                for ( Iterator childIter = child.getChildren().iterator(); childIter.hasNext(); ) {
                     newList.add( childIter.next() );
                }
                it.remove();
                for ( Iterator childIter = newList.iterator(); childIter.hasNext(); ) {
                    it.add( childIter.next() );   
                }
            }
        }                
    }

    GroupElement applyOrTransformation(GroupElement parent,
                                       GroupElement child) throws InvalidPatternException {
        Transformation transformation = null;
        Map map = (HashMap) this.orTransformations.get( parent.getClass() );
        if ( map != null ) {
            transformation = (Transformation) map.get( child.getClass() );
        }

        if ( transformation == null ) {
            throw new RuntimeException( "applyOrTransformation could not find transformation for parent '" + parent.getClass().getName() + "' and child '" + child.getClass().getName() + "'" );
        }

        return transformation.transform( parent );
    }

    interface Transformation {
        GroupElement transform(GroupElement element) throws InvalidPatternException;
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

        public GroupElement transform(GroupElement and) throws InvalidPatternException {
            Or or = new Or();
            determinePermutations( 0,
                                   (And) and,
                                   null,
                                   or );
            return or;
        }

        /**
         * Recursive method that determins all unique combinations of children
         * for the given parent and.
         * 
         * @param currentLevel
         * @param and
         * @param combination
         * @param or
         */
        private void determinePermutations(int currentLevel,
                                           And and,
                                           And combination,
                                           Or or) {
            Object entry = and.getChildren().get( currentLevel );
            if ( entry instanceof Or ) {
                // Only OR nodes need to be iterated over
                Or childOr = (Or) entry;
                for ( Iterator it = childOr.getChildren().iterator(); it.hasNext(); ) {
                    // Make a temp copy of combinations+new entry which will be
                    // sent forward
                    And temp = new And();
                    if ( currentLevel == 0 ) {
                        // Always start with a clean combination
                        combination = new And();
                    } else {
                        temp.getChildren().addAll( combination.getChildren() );
                    }

                    // now check for and remove duplicates
                    Object object = it.next();
                    if ( object instanceof And ) {
                        // Can't have duplicate Ands so move up the children
                        And childAnd = (And) object;
                        for ( Iterator childIter = childAnd.getChildren().iterator(); childIter.hasNext(); ) {
                            temp.addChild( childIter.next() );
                        }
                    } else {
                        // no duplicates so just add
                        temp.addChild( object );
                    }

                    if ( currentLevel < and.getChildren().size() - 1 ) {
                        // keep recursing to build up the combination until we
                        // are at the end where it will be added to or
                        determinePermutations( currentLevel + 1,
                                               and,
                                               temp,
                                               or );
                    } else {
                        // we are at the end so just attach the combination to
                        // the or node
                        or.addChild( temp );
                    }
                }
            } else {
                // Make a temp copy of combinations+new entry which will be sent
                // forward
                And temp = new And();
                if ( currentLevel == 0 ) {
                    // Always start with a clean combination
                    combination = new And();
                } else {
                    temp.getChildren().addAll( combination.getChildren() );
                }
                temp.addChild( entry );

                if ( currentLevel < and.getChildren().size() - 1 ) {
                    // keep recursing to build up the combination until we are
                    // at the end where it will be added to or
                    determinePermutations( currentLevel + 1,
                                           and,
                                           temp,
                                           or );
                } else {
                    // we are at the end so just attach the combination to the
                    // or node
                    or.addChild( temp );
                }
            }
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
     *        Exist   
     *        /   \
     *       Not  Not
     *       |     |
     *       a     b
     * </pre>
     */
    class ExistOrTransformation
        implements
        Transformation {

        public GroupElement transform(GroupElement exist) throws InvalidPatternException {
            throw new InvalidPatternException( "You cannot nest an OR within an Exists" );
//            if ( !(exist.getChildren().get( 0 ) instanceof Or) ) {
//                throw new RuntimeException( "ExistOrTransformation expected '" + Or.class.getName() + "' but instead found '" + exist.getChildren().get( 0 ).getClass().getName() + "'" );
//            }
//
//            /*
//             * we know a Not only ever has one child, and the previous algorithm
//             * has confirmed the child is an OR
//             */
//            Or or = (Or) exist.getChildren().get( 0 );
//            And and = new And();
//            for ( Iterator it = or.getChildren().iterator(); it.hasNext(); ) {
//                Exists newExist = new Exists();
//                newExist.addChild( it.next() );
//                and.addChild( newExist );
//            }
//            return and;
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

        public GroupElement transform(GroupElement not) throws InvalidPatternException {

            throw new InvalidPatternException( "You cannot nest an OR within an Not" );
            // @todo for 3.1
//            if ( !(not.getChildren().get( 0 ) instanceof Or) ) {
//                throw new RuntimeException( "NotOrTransformation expected '" + Or.class.getName() + "' but instead found '" + not.getChildren().get( 0 ).getClass().getName() + "'" );
//            }
//
//            /*
//             * we know a Not only ever has one child, and the previous algorithm
//             * has confirmed the child is an OR
//             */
//            Or or = (Or) not.getChildren().get( 0 );
//            And and = new And();
//            for ( Iterator it = or.getChildren().iterator(); it.hasNext(); ) {
//                Not newNot = new Not();
//                newNot.addChild( it.next() );
//                and.addChild( newNot );
//            }
//            return and;
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