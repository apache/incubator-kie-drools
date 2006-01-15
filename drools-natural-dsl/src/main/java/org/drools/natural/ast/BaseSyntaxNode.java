package org.drools.natural.ast;

/**
 * All natural language syntax nodes extend this class.
 * 
 * Basically we start out with a list of tokens, each token is then classified into one of the syntax types,
 * and then they build themselvs up into a list of little ASTs.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public abstract class BaseSyntaxNode
{
    
    public static final String SPACE = " ";    
    
    public BaseSyntaxNode parent;
    public BaseSyntaxNode next;
    public BaseSyntaxNode prev;    
    public String originalValue;
    protected ExpressionContext context;
    
    /**
     * Node types return true when all their argument needs have been satisfied.
     */
    public abstract boolean isSatisfied();
    
    /** node types must implement this to actually take care of themselves. */
    protected abstract void process();
    
    /**
     * Node types must render themselves (the DO NOT worry about peers 
     * or parents, only themselves including their arguments if applicable.
     */
    protected abstract String render();    
     
    
    /** the iterative method that checks if all 
     * nodes of the appropriate type are satisfied */
    public boolean isAllSatisfied(Class nodeType)
    {        
        BaseSyntaxNode currentNode = this;
        
        boolean satisfied = true;
        while (currentNode != null && satisfied == true) {
            if (currentNode.isThisCorrectType( nodeType )) {
                satisfied = satisfied && currentNode.isSatisfied();
            }
            currentNode = currentNode.next;
        }
        return satisfied;
            
    }

    boolean isThisCorrectType(Class nodeType)
    {
        return this.getClass() == nodeType;
    }


   
    /** 
     *  The recursive method that moves from Left to Right processing ONLY the 
     *  appropriate type. This allows the Context/client to specify order of operations.
     *  
     *  Note that this only runs once through the list. 
     *  
     *  In some cases it will be needed to be called repeatedly to build the tree. 
     */
    void processLeftToRight(Class nodeTypeToProcess) {
        if (isThisCorrectType( nodeTypeToProcess )) {
            process();
        }
        
        if (next == null) {
            return;
        } else {            
            next.processLeftToRight( nodeTypeToProcess );
        }
        
    }
    
    /**
     * This will build up the AST from the linked list.
     * It do multiple passes across the list until this type is
     * satisfied. You only need to call this once per node type.
     * @param nodeTypeToProcess The type of node to process.
     */
    public void buildSyntaxTree(Class nodeTypeToProcess) {
        while (!isAllSatisfied(nodeTypeToProcess)) {
            processLeftToRight(nodeTypeToProcess);
        }
    }

    /**
     * This should be called on the top left of the tree. Will 
     * render the whole shebang.
     */
    public String renderAll() {
        String result = this.render();
        if (next != null) {
            result = result + next.renderAll();
        } 
        return result;
    }
    

    
    /**
     * This will walk up, and then across to the left to find the very top left of the tree.
     * 
     * It does this by going up to the highest level in the tree, and then moving to 
     * the left until there are no nodes left.
     * 
     * @return The node that represents the start.
     */
    public BaseSyntaxNode findStartNode() {
        if (parent != null) {
            return parent.findStartNode();
        } else if (prev != null) {
            return prev.findStartNode();
        } else {
            return this;
        }
    }

    public void setContext(ExpressionContext context) {
        this.context = context;
    }
    

}
