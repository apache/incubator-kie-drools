package org.drools.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
class LogicTransformer
{
    private final Map               duplicateTransformations = new HashMap( );
    private final Map               orTransformations        = new HashMap( );

    private static LogicTransformer INSTANCE                 = null;

    static LogicTransformer getInstance()
    {
        if ( INSTANCE == null )
        {
            INSTANCE = new LogicTransformer( );
        }

        return INSTANCE;
    }

    LogicTransformer()
    {
        initialize( );
    }

    /**
     * sets up the parent->child transformations map
     * 
     */
    private void initialize()
    {
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
                               new NotOrTransformation( ) );
        addTransformationPair( Exists.class,
                               Or.class,
                               new ExistOrTransformation( ) );
        addTransformationPair( And.class,
                               Or.class,
                               new AndOrTransformation( ) );
    }

    private void addTransformationPair(Class parent,
                                       Class child)
    {
        Map map = duplicateTransformations;
        Set childSet = (Set) map.get( child );
        if ( childSet == null )
        {
            childSet = new HashSet( );
            map.put( parent,
                     childSet );
        }
        childSet.add( child );
    }

    private void addTransformationPair(Class parent,
                                       Class child,
                                       Object method)
    {
        Map map = orTransformations;
        Map childMap = (Map) map.get( parent );
        if ( childMap == null )
        {
            childMap = new HashMap( );
            map.put( parent,
                     childMap );
        }
        childMap.put( child,
                      method );
    }

     And[] transform(And and) throws InvalidPatternException
     {
         And cloned = (And) and.clone();
         
         processTree( cloned );                            
                
         // Scan for any Child Ors, if found we need apply the AndOrTransformation
         // And assign the result to the null declared or
         Or or = null;
         for ( Iterator it = cloned.getChildren( ).iterator( ); it.hasNext(); )
         {
             Object object = it.next( );
             if ( object instanceof Or )
             {
                or =  (Or) applyOrTransformation( cloned,
                                                  (ConditionalElement) object );
                 break;
             }
         }
         
         And[] ands = null;
         // Or will be null if there are no Ors in our tree
         if ( or == null )
         {
             // No or so just assign
             ands =  new And[] { cloned };
         }
         else
         {
             ands = new And[ or.getChildren().size() ];
             int i = 0;
             for (Iterator it = or.getChildren().iterator(); it.hasNext(); )
             {
                 ands[i] = (And) it.next();
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
    void processTree(ConditionalElement ce) throws InvalidPatternException
    {
        List newChildren = new ArrayList( );

        for ( Iterator it = ce.getChildren( ).iterator( ); it.hasNext( ); )
        {
            Object object = it.next( );
            if ( object instanceof ConditionalElement )
            {
                ConditionalElement parent = (ConditionalElement) object;

                processTree( parent );

                checkForAndRemoveDuplicates( parent );

                // Scan for any Child Ors, if found we need to move the Or
                // upwards
                for ( Iterator orIter = parent.getChildren( ).iterator( ); orIter.hasNext( ); )
                {
                    Object object2 = orIter.next( );
                    if ( object2 instanceof Or )
                    {
                        newChildren.add( applyOrTransformation( parent,
                                                                (ConditionalElement) object2 ) );
                        it.remove( );
                        break;
                    }
                }

            }
        }

        // Add all the transformed children
        ce.getChildren( ).addAll( newChildren );
    }

    /**
     * Given a parent and child checks if they are duplicates and that they set
     * to have duplicates removed
     * 
     * @param parent
     * @param child
     * @return
     */
    boolean removeDuplicate(ConditionalElement parent,
                            ConditionalElement child)
    {
        if ( this.duplicateTransformations.get( parent.getClass( ) ) != null )
        {
            return ((HashSet) this.duplicateTransformations.get( parent.getClass( ) )).contains( child.getClass( ) );
        }

        return false;
    }

    /**
     * Removes duplicates, children of the duplicate added to the parent and the
     * duplicate child is removed by the parent method.
     * 
     */
    void checkForAndRemoveDuplicates(ConditionalElement parent)
    {
        List newChildren = new ArrayList( );

        for ( Iterator it = parent.getChildren( ).iterator( ); it.hasNext( ); )
        {
            Object object = it.next( );
            // Remove the duplicate if the classes are the same and
            // removeDuplicate method returns true
            if ( parent.getClass( ).isInstance( object ) && removeDuplicate( parent,
                                                                             (ConditionalElement) object ) )
            {
                ConditionalElement child = (ConditionalElement) object;
                for ( Iterator childIter = child.getChildren( ).iterator( ); childIter.hasNext( ); )
                {
                    newChildren.add( childIter.next( ) );
                }
                it.remove( );
            }
        }
        parent.getChildren( ).addAll( newChildren );
    }

    ConditionalElement applyOrTransformation(ConditionalElement parent,
                                             ConditionalElement child) throws InvalidPatternException
    {
        OrTransformation transformation = null;
        Map map = (HashMap) this.orTransformations.get( parent.getClass( ) );
        if ( map != null )
        {
            transformation = (OrTransformation) map.get( child.getClass( ) );
        }

        if ( transformation == null )
        {
            throw new RuntimeException( "applyOrTransformation could not find transformation for parent '" + parent.getClass( ).getName( ) + "' and child '" + child.getClass( ).getName( ) + "'" );
        }

        return transformation.transform( parent );
    }

    interface OrTransformation 
    {
        ConditionalElement transform(ConditionalElement element)  throws InvalidPatternException;
    }

    /**
     * Takes any And that has an Or as a child and rewrites it to move the Or
     * upwards
     * 
     * (a||b)&&c
     * 
     * <pre>
     *         and
     *         / \
     *        or  c 
     *       /  \
     *      a    b
     * </pre>
     * 
     * Should become (a&&c)||(b&&c)
     * 
     * <pre>
     *           
     *         or
     *        /  \  
     *       /    \ 
     *      /      \ 
     *    and      and     
     *    / \      / \
     *   a   c    b   c
     * </pre>
     */
    class AndOrTransformation
        implements
        OrTransformation
    {

        public ConditionalElement transform(ConditionalElement and) throws InvalidPatternException
        {
            Or or = new Or( );
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
                                          Or or)
        {
            Object entry = and.getChildren( ).get( currentLevel );
            if ( entry instanceof Or )
            {
                // Only OR nodes need to be iterated over
                Or childOr = (Or) entry;
                for ( Iterator it = childOr.getChildren( ).iterator( ); it.hasNext( ); )
                {
                    //Make a temp copy of combinations+new entry which will be sent forward
                    And temp = new And( );
                    if ( currentLevel == 0 )
                    {
                        //Always start with a clean combination
                        combination = new And( );
                    }
                    else
                    {
                        temp.getChildren( ).addAll( combination.getChildren( ) );
                    }

                    //now check for and remove duplicates
                    Object object = it.next( );
                    if ( object instanceof And )
                    {
                        // Can't have duplicate Ands so move up the children
                        And childAnd = (And) object;
                        for ( Iterator childIter = childAnd.getChildren( ).iterator( ); childIter.hasNext( ); )
                        {
                            temp.addChild( childIter.next( ) );
                        }
                    }
                    else
                    {
                        //no duplicates so just add
                        temp.addChild( object );
                    }
                    
                    if ( currentLevel < and.getChildren( ).size( ) - 1 )
                    {
                        //keep recursing to build up the combination until we are at the end where it will be added to or
                        determinePermutations( currentLevel + 1,
                                              and,
                                              temp,
                                              or );
                    }
                    else
                    {
                        //we are at the end so just attach the combination to the or node
                        or.addChild( temp );
                    }
                }
            }
            else
            {
                //Make a temp copy of combinations+new entry which will be sent forward                
                And temp = new And( );
                if ( currentLevel == 0 )
                {
                    //Always start with a clean combination                    
                    combination = new And( );
                }
                else
                {
                    temp.getChildren( ).addAll( combination.getChildren( ) );
                }
                temp.addChild( entry );

                if ( currentLevel < and.getChildren( ).size( ) - 1 )
                {
                    //keep recursing to build up the combination until we are at the end where it will be added to or                    
                    determinePermutations( currentLevel + 1,
                                          and,
                                          temp,
                                          or );
                }
                else
                {
                    //we are at the end so just attach the combination to the or node
                    or.addChild( temp );
                }
            }
        }
    }

    /**
     * This data structure is not valid
     * (Exists (OR (A B)
     * <pre>
     *         Exists
     *          | 
     *         or   
     *        /  \
     *       a    b
     * </pre>
     * 
     */
    class ExistOrTransformation
        implements
        OrTransformation
    {

        public ConditionalElement transform(ConditionalElement exist) throws InvalidPatternException
        {
        	  throw new InvalidPatternException("You cannot nest an OR within an Exists");
        }
    }
        

    /**
     * This data structure is now valid
     * 
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
     */
    class NotOrTransformation
        implements
        OrTransformation
    {

        public ConditionalElement transform(ConditionalElement not) throws InvalidPatternException
        {
        	throw new InvalidPatternException("You cannot nest an OR within an Not");
        }
    }


    
}
