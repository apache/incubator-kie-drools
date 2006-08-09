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

	private BaseSyntaxNode firstNode;    
    private BaseSyntaxNode prevNode;
    
	NaturalGrammar grammar;
	private boolean ignoreUnknownTokens = false;
    
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
            BaseSyntaxNode node = factory.getNodeForSnippet(token);
            addNode(node);
        }
	}
    
    public String render() {
        buildTreeByOrderOfPrecedence();
        return firstNode.findStartNode().renderAll().trim();
    }
    


    /** build based on order of operations/precedence */
    private void buildTreeByOrderOfPrecedence()
    {        
        firstNode.findStartNode().buildSyntaxTree(RightInfix.class);
        firstNode.findStartNode().buildSyntaxTree(LeftInfix.class);
        firstNode.findStartNode().buildSyntaxTree(LeftRightInfix.class);
    }


    /**
     * This will build up the double linked list of nodes.
     * @param thisNode
     */
    private void addNode(BaseSyntaxNode thisNode)
    {        
        thisNode.setContext(this);
        if (firstNode == null) {
            firstNode = thisNode;            
        } else {
            prevNode.next = thisNode;
            thisNode.prev = prevNode;            
        }   
        prevNode = thisNode;
    }
	
    

    

    

	
}
