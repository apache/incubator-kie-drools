package org.drools.natural.ast;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.drools.natural.NaturalLanguageException;
import org.drools.natural.grammar.SimpleGrammar;

import junit.framework.TestCase;

/**
 * Test the factory behaviour.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class SyntaxNodeFactoryTest extends TestCase
{
    
    //a reasonable test grammar
    private static SimpleGrammar grammar;
    static {
        grammar = new SimpleGrammar();
        grammar.addToDictionary("rightFunction", "function(${right})");
        grammar.addToDictionary("leftFunction", "function(${left})" );
        grammar.addToDictionary("sub", "substitution");
        grammar.addToDictionary("leftRight", "function(${left},${right})");
        grammar.addToDictionary("leftRightNumbered", "function(${-1},${1})");
        grammar.addToDictionary("rightNumbered", "function(${1},${2})");
        grammar.addToDictionary("leftNumbered", "function(${-1},${-2})");     
        grammar.addToDictionary("badGrammar", "function(${-1},${right})");           
    }
    
    
    public void testLiteral() {
        AbstractSyntaxNode node = getFactory().getNodeForSnippet("not in dictionary");
        assertTrue(node instanceof LiteralNode);
        LiteralNode lit = (LiteralNode) node;
        assertEquals("not in dictionary", lit.originalValue);
    }
    
    public void testRightOnly() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("rightFunction");
        assertTrue(node instanceof RightOnlyNode);
        RightOnlyNode rn = (RightOnlyNode) node;
        assertEquals("rightFunction", rn.originalValue);
        assertEquals("function(${right})", rn.expressionFromDictionary);
    }
    
    public void testSubstitutionNode() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("sub");
        assertTrue(node instanceof SubstitutionNode);
        SubstitutionNode sub = (SubstitutionNode) node;
        assertEquals("substitution", sub.expressionFromDictionary);        
    }
    
    public void testLeftOnly() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("leftFunction");
        assertTrue(node instanceof LeftOnlyNode);
        LeftOnlyNode ln = (LeftOnlyNode) node;
        assertEquals("leftFunction", ln.originalValue);
        assertEquals("function(${left})", ln.expressionFromDictionary);        
        
    }
    
    public void testLeftRightNode() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("leftRight");
        assertTrue(node instanceof LeftRightNode);
        LeftRightNode lr = (LeftRightNode) node;
        assertEquals("leftRight", lr.originalValue);
        assertEquals("function(${left},${right})", lr.expressionFromDictionary);        
    }
    
    public void testLeftRightNumbered() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("leftRightNumbered");
        assertTrue(node instanceof LeftRightNode);
        LeftRightNode lr = (LeftRightNode) node;
        assertEquals("leftRightNumbered", lr.originalValue);
        assertEquals("function(${-1},${1})", lr.expressionFromDictionary);                
    }
    
    public void testRightOnlyNumbered() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("rightNumbered");
        assertTrue(node instanceof RightOnlyNode);
        RightOnlyNode rn = (RightOnlyNode) node;
        assertEquals("rightNumbered", rn.originalValue);
        assertEquals("function(${1},${2})", rn.expressionFromDictionary);        
    }
    
    public void testLeftOnlyNumbered() {
        AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("leftNumbered");
        assertTrue(node instanceof LeftOnlyNode);
        LeftOnlyNode ln = (LeftOnlyNode) node;
        assertEquals("leftNumbered", ln.originalValue);
        assertEquals("function(${-1},${-2})", ln.expressionFromDictionary);        
    }
    

    public void tesHighestLowestNumbers() {
        List positive = new ArrayList();
        positive.add(new Integer(1));
        positive.add(new Integer(3));
        positive.add(new Integer(2));
        Collections.sort(positive);
        
        SyntaxNodeFactory factory = getFactory();
        assertEquals(3, factory.highestNumber(positive));
        assertEquals(1, factory.lowestNumber(positive));
        
        List negative = new ArrayList();
        negative.add(new Integer(-1));
        negative.add(new Integer(-3));
        negative.add(new Integer(-2));
        Collections.sort(negative);
        assertEquals(-1, factory.highestNumber(negative));
        assertEquals(-3, factory.lowestNumber(negative));        
    }
    
    /**
     * This is to test mixing of the styles of variable naming.
     */
    public void testMixedStyleError() {
        try {
            AbstractSyntaxNode node = getFactory( ).getNodeForSnippet("badGrammar");
            fail();
        } catch (NaturalLanguageException e) {
            assertNotNull(e.getMessage());
        }
        
    }
    
    
    
    private SyntaxNodeFactory getFactory()
    {
        SyntaxNodeFactory factory = new SyntaxNodeFactory(grammar);
        return factory;
    }    
    
    
}
