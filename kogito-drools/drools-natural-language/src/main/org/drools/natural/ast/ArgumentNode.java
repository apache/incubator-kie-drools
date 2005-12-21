package org.drools.natural.ast;

import java.util.List;
import java.util.Properties;

import org.drools.natural.NaturalLanguageException;
import org.drools.natural.lexer.StringInterpolator;

/**
 * This is the super class of the argument driven expression node types.
 * Really just a place for common functionality. 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 */
public abstract class ArgumentNode extends AbstractSyntaxNode
{
        
    public static final String RIGHT = "right";
    public static final String LEFT  = "left";    
    
    public String expressionFromDictionary;
    private StringInterpolator interpolator;

    void addToArguments(AbstractSyntaxNode startArg,
                        int numberOfArgsToGet, int numberOfArgsTotal,
                        List listOfArgsToAddTo, Direction direction)
    {
        AbstractSyntaxNode currentArg = startArg;
        for ( int i = 1; i <= numberOfArgsToGet; i++ )
        {
            checkValidArg( currentArg, numberOfArgsTotal, direction );
            if ( !currentArg.isSatisfied( ) )
            {
                // bail... we have done enough for this pass.
                return;
            }
            else
            {

                if (direction == Direction.RIGHT) {
                    reattachNodesForward( currentArg );
                } else {
                    reattachNodesBackward( currentArg );
                }
                
                resetLinks( currentArg );
                listOfArgsToAddTo.add( currentArg );                
            }
            if (direction == Direction.RIGHT) {
                currentArg = this.next;
            } else {
                currentArg = this.prev;
            }

        }
    }

    /** reset the peer links, and set the parent to this instance */
    private void resetLinks(AbstractSyntaxNode currentArg)
    {
        currentArg.next = null;
        currentArg.prev = null;
        currentArg.parent = this;
    }

    private void reattachNodesBackward(AbstractSyntaxNode currentArg)
    {
        // detach argument node, and reattach to the one before
        // this node now skips the arg node (as it has moved down the tree).
        this.prev = currentArg.prev;

        // now make prior node point back to currentNode
        if ( this.prev != null )
        {
            this.prev.next = this;
        }        
    }
    
    /**
     * @return The list of variables from the dictionary expression.
     */
    List getVariableNameList() {
        return getInterpolator( ).extractVariableNames();        
    }
    
    

    private StringInterpolator getInterpolator()
    {
        if (interpolator == null) {
            interpolator = new StringInterpolator(this.expressionFromDictionary);
        } 
        return interpolator;
    }
    
    /**
     * this will insert a space before, unless there is a "<<" at the start
     * or there is no previous node.
     */
    String generateScript(Properties values) 
    {
        if (this.expressionFromDictionary.startsWith("<<")) {
            return getInterpolator().interpolate(values).substring(2);
        } else {
            if (prev == null) {
                return getInterpolator().interpolate(values);
            } else 
            {
                return SPACE + getInterpolator().interpolate(values);
            }
        }
    }

    private void reattachNodesForward(AbstractSyntaxNode currentArg)
    {
        // detach argument node, and reattach to the one after
        // this node now skips argNode
        this.next = currentArg.next;

        // now make next node point back to currentNode
        if ( this.next != null )
        {
            this.next.prev = this;
        }
    }

    void checkValidArg(AbstractSyntaxNode currentNode, int numberOfArgs, Direction direction)
    {
        if ( currentNode == null )
        {
            if (direction == Direction.RIGHT) {
                throw new NaturalLanguageException( "The token [" + originalValue + "] " 
                                                    + "requires " + numberOfArgs + 
                                                    " argument(s) to the right." );
            } else {
                throw new NaturalLanguageException( "The token [" + originalValue + "] " 
                                                    + "requires " + numberOfArgs + 
                                                    " argument(s) to the left." );
            }
        }
    }
    
    protected String getArgForLeftPositionVal(String varName,
                                               List arguments)
    {
        //note the subtle use of the minus
        int argNum = -Integer.parseInt(varName);     
        AbstractSyntaxNode node = (AbstractSyntaxNode) arguments.get(--argNum); 
        return node.render();        
    }
    
    protected String getArgForRightPositionVal(String varName,
                                              List arguments)
   {       
       int argNum = Integer.parseInt(varName);        
       AbstractSyntaxNode node = (AbstractSyntaxNode) arguments.get(--argNum);
       return node.render();        
   }    

    /**
     * 
     * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
     * Need this as arguments can be to the left or to the right of operation expressions.
     * 
     * When going to the right, the numbers start from left to right, when going to the left,
     * the argument number starts from right to left (which means that the lowest argument 
     * number is closest to the expression).
     * 
     */
    static class Direction {
        static Direction RIGHT = new Direction();
        static Direction LEFT = new Direction();
        private Direction() {            
        }
    }

}
