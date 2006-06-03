package org.drools.leaps.util;

import junit.framework.TestCase;

import org.drools.leaps.LeapsFactHandle;
import org.drools.leaps.Token;

public class TokenStackTest extends TestCase {

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.empty()'
     */
    public void testEmpty() {
        final TokenStack stack = new TokenStack();
        assertTrue( stack.empty() );
        final Token token = new Token( null,
                                       new LeapsFactHandle( 3,
                                                           new Object() ),
                                       null );
        stack.push( token );
        assertFalse( stack.empty() );
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.peek()'
     */
    public void testPeek() {
        final TokenStack stack = new TokenStack();
        assertTrue( stack.empty() );
        final Object object = new Object();
        final Token token1 = new Token( null,
                                        new LeapsFactHandle( 1,
                                                            object ),
                                        null );
        stack.push( token1 );
        final Token token2 = new Token( null,
                                        new LeapsFactHandle( 2,
                                                            object ),
                                        null );
        stack.push( token2 );
        final Token token10 = new Token( null,
                                         new LeapsFactHandle( 10,
                                                             object ),
                                         null );
        stack.push( token10 );
        final Token token8 = new Token( null,
                                        new LeapsFactHandle( 8,
                                                            object ),
                                        null );
        stack.push( token8 );
        final Token token6 = new Token( null,
                                        new LeapsFactHandle( 6,
                                                            object ),
                                        null );
        stack.push( token6 );
        final Token token3 = new Token( null,
                                        new LeapsFactHandle( 3,
                                                            object ),
                                        null );
        stack.push( token3 );
        final Token token4 = new Token( null,
                                        new LeapsFactHandle( 4,
                                                            object ),
                                        null );
        stack.push( token4 );

        assertEquals( token4,
                      stack.peek() );
        stack.pop();
        assertEquals( token3,
                      stack.peek() );
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.pop()'
     */
    public void testPop() {
        final TokenStack stack = new TokenStack();
        assertTrue( stack.empty() );
        final Object object = new Object();
        final Token token1 = new Token( null,
                                        new LeapsFactHandle( 1,
                                                            object ),
                                        null );
        stack.push( token1 );
        final Token token2 = new Token( null,
                                        new LeapsFactHandle( 2,
                                                            object ),
                                        null );
        stack.push( token2 );
        final Token token10 = new Token( null,
                                         new LeapsFactHandle( 10,
                                                             object ),
                                         null );
        stack.push( token10 );
        final Token token8 = new Token( null,
                                        new LeapsFactHandle( 8,
                                                            object ),
                                        null );
        stack.push( token8 );
        final Token token6 = new Token( null,
                                        new LeapsFactHandle( 6,
                                                            object ),
                                        null );
        stack.push( token6 );
        final Token token3 = new Token( null,
                                        new LeapsFactHandle( 3,
                                                            object ),
                                        null );
        stack.push( token3 );
        final Token token4 = new Token( null,
                                        new LeapsFactHandle( 4,
                                                            object ),
                                        null );
        stack.push( token4 );

        assertEquals( token4,
                      stack.peek() );
        stack.pop();
        assertEquals( token3,
                      stack.peek() );
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        assertEquals( token1,
                      stack.peek() );
        stack.pop();

        assertTrue( stack.empty() );
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.remove(long)'
     */
    public void testRemoveBottom() {
        final TokenStack stack = new TokenStack();
        assertTrue( stack.empty() );
        final Object object = new Object();
        final Token token1 = new Token( null,
                                        new LeapsFactHandle( 1,
                                                            object ),
                                        null );
        stack.push( token1 );
        final Token token2 = new Token( null,
                                        new LeapsFactHandle( 2,
                                                            object ),
                                        null );
        stack.push( token2 );
        final Token token10 = new Token( null,
                                         new LeapsFactHandle( 10,
                                                             object ),
                                         null );
        stack.push( token10 );
        final Token token8 = new Token( null,
                                        new LeapsFactHandle( 8,
                                                            object ),
                                        null );
        stack.push( token8 );
        final Token token6 = new Token( null,
                                        new LeapsFactHandle( 6,
                                                            object ),
                                        null );
        stack.push( token6 );
        final Token token3 = new Token( null,
                                        new LeapsFactHandle( 3,
                                                            object ),
                                        null );
        stack.push( token3 );
        final Token token4 = new Token( null,
                                        new LeapsFactHandle( 4,
                                                            object ),
                                        null );
        stack.push( token4 );

        stack.remove( 1 );
        assertEquals( token4,
                      stack.peek() );
        stack.pop();
        assertEquals( token3,
                      stack.peek() );
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();

        assertTrue( stack.empty() );
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.remove(long)'
     */
    public void testRemoveTop() {
        final TokenStack stack = new TokenStack();
        assertTrue( stack.empty() );
        final Object object = new Object();
        final Token token1 = new Token( null,
                                        new LeapsFactHandle( 1,
                                                            object ),
                                        null );
        stack.push( token1 );
        final Token token2 = new Token( null,
                                        new LeapsFactHandle( 2,
                                                            object ),
                                        null );
        stack.push( token2 );
        final Token token10 = new Token( null,
                                         new LeapsFactHandle( 10,
                                                             object ),
                                         null );
        stack.push( token10 );
        final Token token8 = new Token( null,
                                        new LeapsFactHandle( 8,
                                                            object ),
                                        null );
        stack.push( token8 );
        final Token token6 = new Token( null,
                                        new LeapsFactHandle( 6,
                                                            object ),
                                        null );
        stack.push( token6 );
        final Token token3 = new Token( null,
                                        new LeapsFactHandle( 3,
                                                            object ),
                                        null );
        stack.push( token3 );
        final Token token4 = new Token( null,
                                        new LeapsFactHandle( 4,
                                                            object ),
                                        null );
        stack.push( token4 );

        stack.remove( 4 );
        assertEquals( token3,
                      stack.pop() );
        assertEquals( token6,
                      stack.pop() );
        assertEquals( token8,
                      stack.pop() );
        assertEquals( token10,
                      stack.pop() );
        assertEquals( token2,
                      stack.pop() );
        assertEquals( token1,
                      stack.pop() );

        assertTrue( stack.empty() );
    }

    /*
     * Test method for 'org.drools.leaps.util.TokenStack.remove(long)'
     */
    public void testRemoveMiddle() {
        final TokenStack stack = new TokenStack();
        assertTrue( stack.empty() );
        final Object object = new Object();
        final Token token1 = new Token( null,
                                        new LeapsFactHandle( 1,
                                                            object ),
                                        null );
        stack.push( token1 );
        final Token token2 = new Token( null,
                                        new LeapsFactHandle( 2,
                                                            object ),
                                        null );
        stack.push( token2 );
        final Token token10 = new Token( null,
                                         new LeapsFactHandle( 10,
                                                             object ),
                                         null );
        stack.push( token10 );
        final Token token8 = new Token( null,
                                        new LeapsFactHandle( 8,
                                                            object ),
                                        null );
        stack.push( token8 );
        final Token token6 = new Token( null,
                                        new LeapsFactHandle( 6,
                                                            object ),
                                        null );
        stack.push( token6 );
        final Token token3 = new Token( null,
                                        new LeapsFactHandle( 3,
                                                            object ),
                                        null );
        stack.push( token3 );
        final Token token4 = new Token( null,
                                        new LeapsFactHandle( 4,
                                                            object ),
                                        null );
        stack.push( token4 );

        stack.remove( 10 );
        assertEquals( token4,
                      stack.pop() );
        assertEquals( token3,
                      stack.pop() );
        assertEquals( token6,
                      stack.pop() );
        assertEquals( token8,
                      stack.pop() );
        assertEquals( token2,
                      stack.pop() );
        assertEquals( token1,
                      stack.pop() );

        assertTrue( stack.empty() );
    }

}
