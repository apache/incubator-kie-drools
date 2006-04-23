package org.drools.leaps.util;

import junit.framework.TestCase;
import org.drools.leaps.*;
public class TokenStackTest extends TestCase {

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.empty()'
     */
    public void testEmpty() {
        TokenStack stack = new TokenStack();
        assertTrue(stack.empty());
        Token token = new Token(null, new FactHandleImpl(3, null), null);
        stack.push(token);
        assertFalse(stack.empty());
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.peek()'
     */
    public void testPeek() {
        TokenStack stack = new TokenStack();
        assertTrue(stack.empty());
        Token token1 = new Token(null, new FactHandleImpl(1, null), null);
        stack.push(token1);
        Token token2 = new Token(null, new FactHandleImpl(2, null), null);
        stack.push(token2);
        Token token10 = new Token(null, new FactHandleImpl(10, null), null);
        stack.push(token10);
        Token token8 = new Token(null, new FactHandleImpl(8, null), null);
        stack.push(token8);
        Token token6 = new Token(null, new FactHandleImpl(6, null), null);
        stack.push(token6);
        Token token3 = new Token(null, new FactHandleImpl(3, null), null);
        stack.push(token3);
        Token token4 = new Token(null, new FactHandleImpl(4, null), null);
        stack.push(token4);

        assertEquals(token4, stack.peek());
        stack.pop();
        assertEquals(token3, stack.peek());
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.pop()'
     */
    public void testPop() {
        TokenStack stack = new TokenStack();
        assertTrue(stack.empty());
        Token token1 = new Token(null, new FactHandleImpl(1, null), null);
        stack.push(token1);
        Token token2 = new Token(null, new FactHandleImpl(2, null), null);
        stack.push(token2);
        Token token10 = new Token(null, new FactHandleImpl(10, null), null);
        stack.push(token10);
        Token token8 = new Token(null, new FactHandleImpl(8, null), null);
        stack.push(token8);
        Token token6 = new Token(null, new FactHandleImpl(6, null), null);
        stack.push(token6);
        Token token3 = new Token(null, new FactHandleImpl(3, null), null);
        stack.push(token3);
        Token token4 = new Token(null, new FactHandleImpl(4, null), null);
        stack.push(token4);

        assertEquals(token4, stack.peek());
        stack.pop();
        assertEquals(token3, stack.peek());
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        assertEquals(token1, stack.peek());
        stack.pop();

        assertTrue(stack.empty());
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.remove(long)'
     */
    public void testRemoveBottom() {
        TokenStack stack = new TokenStack();
        assertTrue(stack.empty());
        Token token1 = new Token(null, new FactHandleImpl(1, null), null);
        stack.push(token1);
        Token token2 = new Token(null, new FactHandleImpl(2, null), null);
        stack.push(token2);
        Token token10 = new Token(null, new FactHandleImpl(10, null), null);
        stack.push(token10);
        Token token8 = new Token(null, new FactHandleImpl(8, null), null);
        stack.push(token8);
        Token token6 = new Token(null, new FactHandleImpl(6, null), null);
        stack.push(token6);
        Token token3 = new Token(null, new FactHandleImpl(3, null), null);
        stack.push(token3);
        Token token4 = new Token(null, new FactHandleImpl(4, null), null);
        stack.push(token4);

        stack.remove(1);
        assertEquals(token4, stack.peek());
        stack.pop();
        assertEquals(token3, stack.peek());
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();

        assertTrue(stack.empty());
    }


    /*
     * Test method for 'org.drools.leaps.util.TokenStack.remove(long)'
     */
    public void testRemoveTop() {
        TokenStack stack = new TokenStack();
        assertTrue(stack.empty());
        Token token1 = new Token(null, new FactHandleImpl(1, null), null);
        stack.push(token1);
        Token token2 = new Token(null, new FactHandleImpl(2, null), null);
        stack.push(token2);
        Token token10 = new Token(null, new FactHandleImpl(10, null), null);
        stack.push(token10);
        Token token8 = new Token(null, new FactHandleImpl(8, null), null);
        stack.push(token8);
        Token token6 = new Token(null, new FactHandleImpl(6, null), null);
        stack.push(token6);
        Token token3 = new Token(null, new FactHandleImpl(3, null), null);
        stack.push(token3);
        Token token4 = new Token(null, new FactHandleImpl(4, null), null);
        stack.push(token4);

        stack.remove(4);
        assertEquals(token3, stack.pop());
        assertEquals(token6, stack.pop());
        assertEquals(token8, stack.pop());
        assertEquals(token10, stack.pop());
        assertEquals(token2, stack.pop());
        assertEquals(token1, stack.pop());

        assertTrue(stack.empty());
    }


    /*
     * Test method for 'org.drools.leaps.util.TokenStack.remove(long)'
     */
    public void testRemoveMiddle() {
        TokenStack stack = new TokenStack();
        assertTrue(stack.empty());
        Token token1 = new Token(null, new FactHandleImpl(1, null), null);
        stack.push(token1);
        Token token2 = new Token(null, new FactHandleImpl(2, null), null);
        stack.push(token2);
        Token token10 = new Token(null, new FactHandleImpl(10, null), null);
        stack.push(token10);
        Token token8 = new Token(null, new FactHandleImpl(8, null), null);
        stack.push(token8);
        Token token6 = new Token(null, new FactHandleImpl(6, null), null);
        stack.push(token6);
        Token token3 = new Token(null, new FactHandleImpl(3, null), null);
        stack.push(token3);
        Token token4 = new Token(null, new FactHandleImpl(4, null), null);
        stack.push(token4);


        stack.remove(10);
        assertEquals(token4, stack.pop());
        assertEquals(token3, stack.pop());
        assertEquals(token6, stack.pop());
        assertEquals(token8, stack.pop());
        assertEquals(token2, stack.pop());
        assertEquals(token1, stack.pop());

        assertTrue(stack.empty());
    }

}
