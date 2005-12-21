package org.drools.natural.ast;

import java.util.Iterator;
import java.util.List;

import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.lexer.RawTokens;

/** 
 * This holds the context for the expression being build up. Is essentially a linked list of 
 * expression nodes, which may or maynot be complex AST structures themselves.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class ExpressionContext {

	private AbstractSyntaxNode firstNode;    
    private AbstractSyntaxNode prevNode;
    
	private NaturalGrammar grammar;
	
	public ExpressionContext(NaturalGrammar grammar) {		
		this.grammar = grammar;		
	}
	
	
    /**
     * This will take the raw tokens, from the lexer, and look them up in the dictionary.
     * Based on the type of stuff found in the dictionary (if anything found at all)
     * the astNodeList is built with the basic Node types - totally flat. 
     * 
     * It is the job of the nodes to build themselves into ASTs and string them together.
     * @param tokens
     */
	public void addTokens(RawTokens tokens) {
	    SyntaxNodeFactory factory = new SyntaxNodeFactory(grammar);        
        for ( Iterator iter = tokens.getTokens().iterator( ); iter.hasNext( ); )
        {
            String token = (String) iter.next( );
            AbstractSyntaxNode node = factory.getNodeForSnippet(token);
            addNode(node);
        }
	}
    
    public String render() {
        buildTree();
        return firstNode.findStartNode().renderAll();
    }
    


    /** build based on order of operations/precedence */
    private void buildTree()
    {        
        //firstNode.buildSyntaxTree(RuleParamFieldNode.class);
        
        firstNode.findStartNode().buildSyntaxTree(RightOnlyNode.class);
        firstNode.findStartNode().buildSyntaxTree(LeftOnlyNode.class);
        firstNode.findStartNode().buildSyntaxTree(LeftRightNode.class);
    }


    /**
     * This will build up the double linked list of nodes.
     * @param thisNode
     */
    private void addNode(AbstractSyntaxNode thisNode)
    {        
        if (firstNode == null) {
            firstNode = thisNode;            
        } else {
            prevNode.next = thisNode;
            thisNode.prev = prevNode;            
        }   
        prevNode = thisNode;
    }
	


    

    

	
}
