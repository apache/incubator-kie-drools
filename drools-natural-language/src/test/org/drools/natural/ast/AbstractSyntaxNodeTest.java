package org.drools.natural.ast;

import java.util.List;

import org.drools.natural.NaturalLanguageException;

import junit.framework.TestCase;

/**
 * This tests building up syntax trees from the bottom up.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class AbstractSyntaxNodeTest extends TestCase
{
    
    /** The simplest thing that could possibly work */
    public void testSingleLiteral() {
        String val = "this is something";
        LiteralNode node1 = new LiteralNode(val);
        
        assertTrue(node1.isAllSatisfied(LiteralNode.class));
        node1.processLeftToRight(LiteralNode.class);
        assertTrue(node1.isAllSatisfied(LiteralNode.class));
        assertEquals(val, node1.originalValue);        
    }
    
    public void testTwoLiteralNodes() {
        LiteralNode node1 = new LiteralNode("lit1");
        LiteralNode node2 = new LiteralNode("lit2");
        
        node1.next = node2;
        node2.prev = node1;
        
        assertTrue(node1.isAllSatisfied(LiteralNode.class));
        node1.processLeftToRight(LiteralNode.class);
        assertEquals(node1.next, node2);
        assertEquals(null, node1.prev);
        assertEquals(null, node2.next);
        
    }
    
    
    /**
     * In this case we are trying:
     * lit1 method lit2
     *
     */
    public void testRightOnlyNode() {
        LiteralNode node1 = new LiteralNode("lit1");
        LiteralNode node2 = new LiteralNode("lit2");
        RightOnlyNode r1 = new RightOnlyNode("method", "function(${right})", 1);
        
        node1.next = r1;
        r1.prev = node1;
        
        r1.next = node2;
        node2.prev = r1;
        
        assertFalse(node1.isAllSatisfied(RightOnlyNode.class));
        assertTrue(node1.isAllSatisfied(LiteralNode.class));
        
        node1.processLeftToRight(RightOnlyNode.class);
        assertTrue(node1.isAllSatisfied(RightOnlyNode.class));
        
        assertEquals(r1, node1.next);
        assertEquals(node1, r1.prev);
        assertEquals(null, r1.next);
        
        List args = r1.getArguments();
        assertEquals(1, args.size());
        assertEquals(node2, args.get(0));
        
        
    }
    
    /**
     * In this will will try:
     * 
     * "[age of] bob is 21"
     */
    public void testRightOnlyBobsAge() {
        RightOnlyNode r1 = new RightOnlyNode("age of", "ageOf(${right})", 1);
        LiteralNode lit1 = new LiteralNode("bob");
        LiteralNode lit2 = new LiteralNode("is");
        LiteralNode lit3 = new LiteralNode("21");
        
        r1.next = lit1;
        lit1.prev = r1;
        lit1.next = lit2;
        lit2.prev = lit1;
        lit2.next = lit3;
        lit3.prev = lit1;
        
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        r1.processLeftToRight(RightOnlyNode.class);
        assertTrue(r1.isAllSatisfied(RightOnlyNode.class));
        assertEquals(lit2, r1.next);
        assertEquals(lit1, r1.getArguments().get(0));
        assertEquals(lit3, lit2.next);
        assertEquals(r1, lit2.prev);
        
    }
    
    /**
     * In this case we are trying:
     * "method arg1"
     */
    public void testRightOnlyNodeBasic() {
        LiteralNode node1 = new LiteralNode("arg1");        
        RightOnlyNode r1 = new RightOnlyNode("method", "function(${right})", 1);
        
        
        r1.next = node1;
        
        node1.prev = r1;        
        
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        assertTrue(node1.isAllSatisfied(LiteralNode.class));
        
        r1.processLeftToRight(RightOnlyNode.class);
        assertTrue(node1.isAllSatisfied(RightOnlyNode.class));
        
        assertEquals(null, r1.next);
                
        
        List args = r1.getArguments();
        assertEquals(1, args.size());
        assertEquals(node1, args.get(0));
        
        
    }
    
    
    public void testRightOnlyMultiArgs() {
        RightOnlyNode r1 = new RightOnlyNode("method", "f(${1}, ${2}", 2);
        LiteralNode node1 = new LiteralNode("arg1");
        LiteralNode node2 = new LiteralNode("arg2");

        r1.next = node1;
        node1.prev = r1;
        node1.next = node2;
        node2.prev = node1;
        
        
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        assertTrue(r1.isAllSatisfied(LiteralNode.class));
        
        r1.processLeftToRight(RightOnlyNode.class);
        assertTrue(r1.isAllSatisfied(RightOnlyNode.class));
        
        assertEquals(null, r1.next);
        assertEquals(null, r1.prev);
        
        
        List args = r1.getArguments();
        assertEquals(2, args.size());
        assertEquals(node1, args.get(0));
        assertEquals(node2, args.get(1));
    }
    
    public void testRightOnlyNotEnoughArgs() {
        RightOnlyNode r1 = new RightOnlyNode("method", "${right}", 1);        
        try {
            r1.processLeftToRight(RightOnlyNode.class);
            fail();
        } catch (NaturalLanguageException e) {
            assertNotNull(e.getMessage());
        }
    }
    
    
    /** this should show that the inner one gets done first, and then the outer one.
     *   We will try:
     *   
     *   [some method] [another method] arg1 arg2
     *   
     *   Which should build AST:
     *   
     *   somemethod 
     *       \     \
     *        \     arg2
     *         another method
     *                         \ arg1       
     *                         
     */
    public void testRightOnlyNested() {
  
        RightOnlyNode r1 = new RightOnlyNode("some method", "f( ${1}, ${2})", 2);
        RightOnlyNode r2 = new RightOnlyNode("another method", "method(${right})", 1);
        
        LiteralNode lit1 = new LiteralNode("arg1");
        SubstitutionNode lit2 = new SubstitutionNode("arg2", "ARG_2");        
        
        r1.next = r2;
        r2.prev = r1;
        
        r2.next = lit1;
        lit1.prev = r2;
        lit1.next = lit2;
        lit2.prev = lit1;
        
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        r1.processLeftToRight(RightOnlyNode.class);
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        r1.processLeftToRight(RightOnlyNode.class);
        assertTrue(r1.isAllSatisfied(RightOnlyNode.class));
        
        //holy crap... that worked. Better inspect the result a little closer...
        
        assertEquals(null, r1.next);
        List r1Args = r1.getArguments();
        assertEquals(2, r1Args.size());
        assertEquals(r2, r1Args.get(0));
        assertEquals(lit2, r1Args.get(1));
        List r2Args = r2.getArguments();
        assertEquals(1, r2Args.size());
        assertEquals(lit1, r2Args.get(0));
        
    }
    
    /** this should show that the inner one gets done first, and then the outer one.
     *   We will try:
     *   
     *   [some method] arg1 [another method] arg2
     *   
     *   Which should build AST:
     *   
     *   somemethod (r1)
     *       \     \
     *        \     \    
     *        arg1   another method (r2)
     *                              \
     *                               \
     *                                arg2       
     *                         
     */
    public void testRightOnlyMultiNested() {
  
        RightOnlyNode r1 = new RightOnlyNode("some method", "f( ${1}, ${2})", 2);
        RightOnlyNode r2 = new RightOnlyNode("another method", "method(${right})", 1);
        
        LiteralNode lit1 = new LiteralNode("arg1");
        LiteralNode lit2 = new LiteralNode("arg2");        
        
        r1.next = lit1;
        
        lit1.prev = r1;        
        lit1.next = r2;
        
        r2.prev = lit1;
        r2.next = lit2;
        
        lit2.prev = r2;
        
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        r1.processLeftToRight(RightOnlyNode.class);
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        assertTrue(r2.isSatisfied());        
        assertFalse(r1.isSatisfied());
                        
        //r2 is now next to r1
        assertEquals(r2, r1.next);
        
        //check that r1 has one param
        List r1Args = r1.getArguments();
        assertEquals(1, r1Args.size());
        assertEquals(lit1, r1Args.get(0));
        
        //check that r2 has one param 
        assertEquals(1, r2.getArguments().size());
        assertEquals(lit2, r2.getArguments().get(0));

        r1.processLeftToRight(RightOnlyNode.class);
        assertTrue(r1.isAllSatisfied(RightOnlyNode.class));
        
        assertEquals(r1, lit1.findStartNode());
        
    }    
    
    
    /**
     * Now testing the very interesting case of:
     * 
     * "arg1 before arg2"
     * 
     * which should be:
     * 
     *           before
     *          /      \
     *         arg1    arg2
     */
    public void testLeftRightNode() {
        LiteralNode lit1 = new LiteralNode("arg1");
        LiteralNode lit2 = new LiteralNode("arg2");        
        LeftRightNode lr = new LeftRightNode("before", "${left}.before(${right})", 1, 1);
        
        lit1.next = lr;
        lr.prev = lit1;
        lr.next = lit2;
        lit2.prev = lr;
        
        assertFalse(lit1.isAllSatisfied(LeftRightNode.class));
        lit1.processLeftToRight(LeftRightNode.class);
        assertTrue(lit1.isAllSatisfied(LeftRightNode.class));
        
        //well bugger me.... it looks like it actually worked... 
        assertEquals(null, lr.next);
        assertEquals(null, lr.prev);
        assertEquals(null, lit1.next);
        assertEquals(null, lit2.prev);
        
        assertEquals(1, lr.getArgumentsLeft().size());
        assertEquals(1, lr.getArgumentsRight().size());
        
        assertEquals(lit1, lr.getArgumentsLeft().get(0));
        assertEquals(lit2, lr.getArgumentsRight().get(0));
    }

    /**
     * This will look like
     *  
     * arg1 arg2 before
     * 
     * which should fill the left argument list with [arg2,arg1] IN THAT ORDER.
     *
     */
    public void testLeftRightLeftOnly() {
        LiteralNode lit1 = new LiteralNode("arg1");
        LiteralNode lit2 = new LiteralNode("arg2");        
        LeftRightNode lr = new LeftRightNode("before", "${-2}.XXX(${-1})", 2, 0);
        
        lit1.next = lit2;
        lit2.prev = lit1;
        
        lit2.next = lr;
        lr.prev = lit2;
        
        lr.processLeftToRight(LeftRightNode.class);
        assertEquals(null, lr.prev);
        assertEquals(2, lr.getArgumentsLeft().size());
        
        //IMPORTANT to note that arguments start from the closes to the left, and then move out.
        //as would be intuitive... when you think about it. 
        //remember that a user will specify things like ${-1}, ${-2} and so on...
        assertEquals(lit2, lr.getArgumentsLeft().get(0));
        assertEquals(lit1, lr.getArgumentsLeft().get(1));
    }
    
    
    /**
     * This is slightly more complex, but realistic scenario.
     *
     * The example:
     *  "Today <i>is</i> before [date of] 10-Jul-2006 [something irrelevant]"
     * Where before is a leftAndRight node, and [date of] is a RightOnlyNode thingy.
     * 
     * This should result in:
     * 
     *      before---------something irrelevant (peer relationship)
     *      /     \
     *    Today    \
     *            date of
     *              \
     *              10-Jul-2006
     */
    public void testCombineRightAndLeft() {
        SubstitutionNode today = new SubstitutionNode("Today", "new Date()");
        LeftRightNode before = new LeftRightNode("before", "${left}.before(${right})", 1, 1);
        RightOnlyNode dateOf = new RightOnlyNode("date of", "parseDate(${right}", 1);
        LiteralNode myBirthday = new LiteralNode("10-Jul-1974");
        LiteralNode irrelevant = new LiteralNode("something irrelevant");
        
        //chain it up...
        today.next = before;
        before.prev = today;
        before.next = dateOf;
        dateOf.prev = before;
        dateOf.next = myBirthday;
        myBirthday.prev = dateOf;
        myBirthday.next = irrelevant;
        irrelevant.prev = myBirthday;
        
        
        assertFalse(today.isAllSatisfied(RightOnlyNode.class));
        assertFalse(today.isAllSatisfied(LeftRightNode.class));
        
        //now process, in correct order of precedence
        today.processLeftToRight(RightOnlyNode.class);
        today.processLeftToRight(LeftRightNode.class);
        
        assertTrue(today.isAllSatisfied(RightOnlyNode.class));
        assertTrue(today.isAllSatisfied(LeftRightNode.class));
        
        //check its unchained... built into a tree...
        assertEquals(null, today.next);
        assertEquals(irrelevant, before.next);
        
        assertEquals(today, before.getArgumentsLeft().get(0));
        assertEquals(dateOf, before.getArgumentsRight().get(0));
        assertEquals(myBirthday, dateOf.getArguments().get(0));
        
        //and that my friend, is how you do it.
        
        //now check the parent relationships, that we can get to first node.         
        assertEquals(before, myBirthday.findStartNode());
        assertEquals(before, irrelevant.findStartNode());
        assertEquals(before, today.findStartNode());
        
    }
    
    /**
     * I just can't get enough of tests, and it is important to get it right. So here is one more. 
     * 
     * arg1 left1 leftRight2 arg2 
     * 
     * Which should yield:
     *  
     *      leftRight
     *      /       \
     *    left1     arg2
     *    /
     *   arg1   
     *
     */
    public void testLeftRightNesting() 
    {
        LiteralNode arg1 = new LiteralNode("arg1");
        LeftRightNode left1 = new LeftRightNode("left1", "left1", 1, 0);
        LeftRightNode leftRight = new LeftRightNode("leftRight", "leftRight", 1, 1);
        LiteralNode arg2 = new LiteralNode("arg2");
        
        arg1.next = left1;
        left1.prev = arg1;
        left1.next = leftRight;
        leftRight.prev = left1;
        leftRight.next = arg2;
        arg2.prev = leftRight;
        
        assertFalse(arg1.isAllSatisfied(LeftRightNode.class));
        
        //this should work in one pass, as it is right to left.
        arg1.processLeftToRight(LeftRightNode.class);
        
        assertTrue(arg1.isAllSatisfied(LeftRightNode.class));
        
        assertEquals(null, arg1.next);
        
        assertEquals(arg1, left1.getArgumentsLeft().get(0));
        assertEquals(left1, leftRight.getArgumentsLeft().get(0));
        assertEquals(arg2, leftRight.getArgumentsRight().get(0));
        
        assertEquals(leftRight, arg1.findStartNode());
        assertEquals(null, leftRight.parent);
    }
    
    /**
     * Testing the LeftOnlyNode with 2 literal args.
     * 
     * arg1 left1 arg2 left2
     */
    public void testLeftOnly() 
    {
        LiteralNode lit1 = new LiteralNode("arg1");
        LeftOnlyNode left1 = new LeftOnlyNode("left1", "left1(${left}", 1);
        LiteralNode lit2 = new LiteralNode("arg2");
        LeftOnlyNode left2 = new LeftOnlyNode("left2", "left2${-1}", 1);
        
        lit1.next = left1;
        left1.prev = lit1;
        left1.next = lit2;
        lit2.prev = left1;
        lit2.next = left2;
        left2.prev = lit2;
        
        assertFalse(lit1.isAllSatisfied(LeftOnlyNode.class));
        lit1.processLeftToRight(LeftOnlyNode.class);
        assertTrue(lit1.isAllSatisfied(LeftOnlyNode.class));
        
        assertEquals(left2, left1.next);
        assertEquals(left1, left2.prev);
        assertEquals(lit1, left1.getArguments().get(0));
        assertEquals(lit2, left2.getArguments().get(0));
    }
    
    /**
     * arg1 left1 left2
     * 
     * Also check the parent relationship.
     *
     */
    public void testLeftOnlyNestedAndParent() 
    {
        LiteralNode lit1 = new LiteralNode("arg1");
        LeftOnlyNode left1 = new LeftOnlyNode("left1", "left1(${left}", 1);
        LeftOnlyNode left2 = new LeftOnlyNode("left2", "left2${-1}", 1);
        
        lit1.next = left1;
        left1.prev = lit1;
        left1.next = left2;
        left2.prev = left1;
        
        lit1.processLeftToRight(LeftOnlyNode.class);
        assertTrue(left1.isAllSatisfied(LeftOnlyNode.class));
        assertEquals(left1, left2.getArguments().get(0)); 
        
        assertEquals(left1, lit1.parent);
        assertEquals(left2, left1.parent);
        assertEquals(null, left2.parent);
        
        assertEquals(left2, lit1.findStartNode());
        assertEquals(left2, left1.findStartNode());
    }
    
    public void testRuleParameterNode() {
        LiteralNode p1 = new LiteralNode("bob");
        RuleParamFieldNode field = new RuleParamFieldNode ("likes cheese", "${left}.likesCheese()");
        p1.next = field;
        field.prev = p1;
        
        p1.processLeftToRight(RuleParamFieldNode.class);
        assertEquals(p1, field.getArguments().get(0));
    }
    
    public void testBuildTree() {
        RightOnlyNode r1 = new RightOnlyNode("some method", "f( ${1}, ${2})", 2);
        RightOnlyNode r2 = new RightOnlyNode("another method", "method(${right})", 1);
        
        LiteralNode lit1 = new LiteralNode("arg1");
        SubstitutionNode lit2 = new SubstitutionNode("arg2", "ARG_2");        
        
        r1.next = r2;
        r2.prev = r1;
        
        r2.next = lit1;
        lit1.prev = r2;
        lit1.next = lit2;
        lit2.prev = lit1;
        
        assertFalse(r1.isAllSatisfied(RightOnlyNode.class));
        r1.buildSyntaxTree(RightOnlyNode.class);
        assertTrue(r1.isAllSatisfied(RightOnlyNode.class));
        
    }
    
    
    
    
}
