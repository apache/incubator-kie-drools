package org.drools.natural.ast;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/** 
 * Test rendering related stuff in isolation.
 */
public class NodeRenderTest extends TestCase
{
    
    public void testLeftOnlyNode() {
        LeftInfix node = new LeftInfix("something here", "somethingHere(${-1},${-2})", 2);
        node.getArguments().add(new LiteralNode("arg1"));
        node.getArguments().add(new LiteralNode("arg2"));
        assertEquals("somethingHere(arg1,arg2)", node.render());
    }
    
    /**
     * Also tests for no space insertion.
     */
    public void testLeftRightNode() {
        LeftRightInfix node = new LeftRightInfix("something here", "<<somethingHere(${left},${right})", 1, 1);
        node.getArgumentsLeft().add(new LiteralNode("arg1"));
        node.getArgumentsRight().add(new LiteralNode("arg2"));
        assertEquals("somethingHere(arg1,arg2)", node.render());
    }
    
    public void testRightNodeSingleArg() {
        RightInfix node = new RightInfix("something here", "<<${right}.somethingHere()", 1);
        node.getArguments().add(new SubstitutionNode("arg 1", "arg1"));        
        assertEquals("arg1.somethingHere()", node.render());
    }    
    
    public void testRightOnlyNode() {
        RightInfix node = new RightInfix("something here", "somethingHere(${1},${2})", 2);
        node.getArguments().add(new LiteralNode("arg1"));
        node.getArguments().add(new LiteralNode("arg2"));
        assertEquals("somethingHere(arg1,arg2)", node.render());        
    }
    
    
    /** 
     * Literals and Subs should have spaces in some cases.
     * 
     */
    public void testSpaceInsertBehaviour() {
       LiteralNode lit1 = new LiteralNode("aaa"); 
       LiteralNode lit2 = new LiteralNode("bbb");
       SubstitutionNode sub = new SubstitutionNode("BOOO boo", "ccc");
       LiteralNode lit3 = new LiteralNode("ddd");
       
       lit1.next = lit2;
       lit2.prev = lit1;
       lit2.next = sub;
       sub.prev = lit2;
       sub.next = lit3;
       lit3.prev = sub;
       
       assertEquals("aaa", lit1.render());
       assertEquals(" bbb", lit2.render());
       assertEquals(" ccc", sub.render());
       assertEquals(" ddd", lit3.render());       
    }
    
    /**
     * SHOULD render a space, but only if it has a previous node.
     */
    public void testSpaceInsertRightOnlyNode() {
        LiteralNode lit1 = new LiteralNode("bbb");
        RightInfix right = new RightInfix("blah", "blah(${1})", 1);
        LiteralNode lit2 = new LiteralNode("ccc");
        lit1.next = right;
        right.prev = lit1;
        right.next = lit2;
        lit2.prev = right;
        
        lit1.processLeftToRight(RightInfix.class);
        assertEquals(" blah(ccc)", right.render());
        right.prev = null;
        assertEquals("blah(ccc)", right.render());
        
    }
    
    public void testSubstitionNodeNoSpace() {
        SubstitutionNode sub = new SubstitutionNode("yeah", "<<sub");
        LiteralNode lit = new LiteralNode("boo");
        LiteralNode lit2 = new LiteralNode("boo");
        
        lit.next = sub;
        sub.prev = lit;
        sub.next = lit2;
        lit2.prev = sub;
        
        sub.processLeftToRight(SubstitutionNode.class);
        assertEquals("sub", sub.render());
    }
    
    public void testSubstitionNodeInsertSpace() {
        SubstitutionNode sub = new SubstitutionNode("yeah", "sub");
        LiteralNode lit = new LiteralNode("boo");
        
        lit.next = sub;
        sub.prev = lit;
        
        sub.processLeftToRight(SubstitutionNode.class);
        assertEquals(" sub", sub.render());
    }
    
    
    
    
    
    public void testGetLeftArgs() {        
        LeftInfix node = new LeftInfix("", "", 1);
        List list = new ArrayList();
        list.add(new LiteralNode("pos1"));
        assertEquals("pos1", node.getArgForLeftPositionVal("-1", list));
        list.add(new LiteralNode("pos2"));
        list.add(new LiteralNode("pos3"));
        assertEquals("pos3", node.getArgForLeftPositionVal("-3", list));
        assertEquals("pos2", node.getArgForLeftPositionVal("-2", list));
    }
    
    public void testGetRightArgs() {        
        LeftInfix node = new LeftInfix("", "", 1);
        List list = new ArrayList();
        list.add(new LiteralNode("pos1"));        
        list.add(new LiteralNode("pos2"));
        list.add(new LiteralNode("pos3"));
        assertEquals("pos1", node.getArgForRightPositionVal("1", list));
        assertEquals("pos2", node.getArgForRightPositionVal("2", list));
        assertEquals("pos3", node.getArgForRightPositionVal("3", list));
    }    

}
