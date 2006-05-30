package org.drools.rule;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.DroolsTestCase;

public class LogicTransformerTest extends DroolsTestCase {
    /**
     * (a||b)&&c
     * 
     * <pre>
     *               and
     *               / \
     *              or  c 
     *             /  \
     *            a    b
     * </pre>
     * 
     * Should become (a&&c)||(b&&c)
     * 
     * <pre>
     *                 
     *               or
     *              /  \  
     *             /    \ 
     *            /      \ 
     *             and      and     
     *          / \      / \
     *         a   c    b   c
     * </pre>
     */
    public void testSingleOrAndOrTransformation() throws InvalidPatternException {
        final String a = "a";
        final String b = "b";
        final String c = "c";

        final And and = new And();
        and.addChild( c );
        final Or or = new Or();
        or.addChild( a );
        or.addChild( b );
        and.addChild( or );

        final Or newOr = (Or) LogicTransformer.getInstance().applyOrTransformation( and,
                                                                                    or );

        assertLength( 2,
                      newOr.getChildren() );
        assertEquals( And.class,
                      newOr.getChildren().get( 0 ).getClass() );
        assertEquals( And.class,
                      newOr.getChildren().get( 1 ).getClass() );

        final And and1 = (And) newOr.getChildren().get( 0 );
        assertContains( c,
                        and1.getChildren() );
        assertContains( a,
                        and1.getChildren() );

        final And and2 = (And) newOr.getChildren().get( 1 );
        assertContains( c,
                        and2.getChildren() );
        assertContains( b,
                        and2.getChildren() );

    }

    /**
     * (a||b)&&c
     * 
     * <pre>
     *                   And
     *                  /|\ \__
     *                _/ | \_  \_
     *               /   |   \   \  
     *              or   |   or   not
     *             /   \ |  / \    |
     *            a    b c d   e   f
     * </pre>
     * 
     * Should become (a&&c)||(b&&c)
     * 
     * <pre>
     *                           /\
     *                         _/  \_
     *                        /      \
     *                      _/|       |\_
     *                   __/  |       |  \__
     *                __/     |       |     \__
     *               /        |       |        \
     *              and      and     and      and
     *             /||\     /||\     /||\     /||\
     *            a cd Not a ce Not b cd Not b ce Not
     *                  |        |        |        |
     *                  f        f        f        f
     * </pre>
     */
    public void testMultipleOrAndOrTransformation() throws InvalidPatternException {
        final String a = "a";
        final String b = "b";
        final String c = "c";
        final String d = "d";
        final String e = "e";
        final String f = "f";

        final And and = new And();
        final Or or = new Or();
        or.addChild( a );
        or.addChild( b );
        and.addChild( or );
        and.addChild( c );

        final Or or2 = new Or();

        or2.addChild( d );
        or2.addChild( e );
        and.addChild( or2 );

        final Not not = new Not();
        not.addChild( f );
        and.addChild( not );

        final Or newOr = (Or) LogicTransformer.getInstance().applyOrTransformation( and,
                                                                                    or );

        assertLength( 4,
                      newOr.getChildren() );
        assertEquals( And.class,
                      newOr.getChildren().get( 0 ).getClass() );
        assertEquals( And.class,
                      newOr.getChildren().get( 1 ).getClass() );
        assertEquals( And.class,
                      newOr.getChildren().get( 2 ).getClass() );
        assertEquals( And.class,
                      newOr.getChildren().get( 3 ).getClass() );

        And and1 = (And) newOr.getChildren().get( 0 );
        assertLength( 4,
                      and1.getChildren() );
        assertContains( a,
                        and1.getChildren() );
        assertContains( c,
                        and1.getChildren() );
        assertContains( d,
                        and1.getChildren() );
        assertContains( not,
                        and1.getChildren() );

        and1 = (And) newOr.getChildren().get( 1 );
        assertLength( 4,
                      and1.getChildren() );
        assertContains( a,
                        and1.getChildren() );
        assertContains( c,
                        and1.getChildren() );
        assertContains( e,
                        and1.getChildren() );
        assertContains( not,
                        and1.getChildren() );

        and1 = (And) newOr.getChildren().get( 2 );
        assertLength( 4,
                      and1.getChildren() );
        assertContains( b,
                        and1.getChildren() );
        assertContains( c,
                        and1.getChildren() );
        assertContains( d,
                        and1.getChildren() );
        assertContains( not,
                        and1.getChildren() );

        and1 = (And) newOr.getChildren().get( 3 );
        assertLength( 4,
                      and1.getChildren() );
        assertContains( b,
                        and1.getChildren() );
        assertContains( c,
                        and1.getChildren() );
        assertContains( e,
                        and1.getChildren() );
        assertContains( not,
                        and1.getChildren() );
    }

    /**
     * This data structure is now valid
     * 
     * (Not (OR (A B)
     * 
     * <pre>
     *             Not
     *              | 
     *             or   
     *            /  \
     *           a    b
     * </pre>
     * 
     */
    public void xxxtestNotOrTransformation() throws InvalidPatternException {
        final String a = "a";
        final String b = "b";

        final Not not = new Not();
        final Or or = new Or();
        not.addChild( or );

        or.addChild( a );
        or.addChild( b );

        try {
            final And newAnd = (And) LogicTransformer.getInstance().applyOrTransformation( not,
                                                                                           or );
            fail( "This should fail as you cannot nest Ors under Nots" );
        } catch ( final InvalidPatternException e ) {
            //
        }
    }

    /**
     * This data structure is not valid (Exists (OR (A B)
     * 
     * <pre>
     *             Exists
     *              | 
     *             or   
     *            /  \
     *           a    b
     * </pre>
     * 
     */
    public void xxxtestExistOrTransformation() throws InvalidPatternException {
        final String a = "a";
        final String b = "b";

        final Exists exist = new Exists();
        final Or or = new Or();
        exist.addChild( or );

        or.addChild( a );
        or.addChild( b );

        try {
            final And newAnd = (And) LogicTransformer.getInstance().applyOrTransformation( exist,
                                                                                           or );

            fail( "This should fail as you cannot nest Ors under Existss" );
        } catch ( final InvalidPatternException e ) {
            //
        }

    }

    public void testDuplicatTransformation() throws InvalidRuleException {
        final String a = "a";
        final String b = "b";
        final String c = "c";
        final String d = "d";

        final And and1 = new And();
        and1.addChild( a );
        and1.addChild( b );

        final And and2 = new And();
        and2.addChild( c );
        and2.addChild( d );

        and1.addChild( and2 );

        final Or or = new Or();
        and1.addChild( or );

        LogicTransformer.getInstance().checkForAndRemoveDuplicates( and1 );

        assertLength( 5,
                      and1.getChildren() );
        assertContains( a,
                        and1.getChildren() );
        assertContains( b,
                        and1.getChildren() );
        assertContains( c,
                        and1.getChildren() );
        assertContains( d,
                        and1.getChildren() );
        assertContains( or,
                        and1.getChildren() );

    }

    /**
     * <pre>
     *                         _/|\_
     *                      __/  |  \__
     *                     /     |     \ 
     *                  __/      |      \__
     *                 /         |         \
     *                And       and        Not
     *               / | \      / \         |
     *             a  And d    e  Or        h
     *                / \        /  \      
     *               b  Not     f  Exists    
     *                   |           |      
     *                  Not          g   
     *                   |           
     *                   c         
     * </pre>
     * <pre>
     *                           _/|\__
     *                        __/  |   \___
     *                       /     |       \__
     *                    __/      |          \__
     *                   /         |             \__
     *                  /          |                \__
     *                 |           |                   \
     *                And          Or                 Not
     *              / | | \       /  \                 |  
     *            a   b d Not   And   And              i
     *                     |    / \  / |            
     *                    Not  e  f e Exists       
     *                     |           |        
     *                     c           g        
     * </pre>
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     * 
     * 
     */
    public void xTestProcessTree() throws IOException,
                                  ClassNotFoundException,
                                  InvalidPatternException {
        final String a = "a";
        final String b = "b";
        final String c = "c";
        final String d = "d";
        final String e = "e";
        final String f = "f";
        final String g = "g";
        final String h = "h";
        final String i = "i";
        final String j = "j";
        final String k = "notAssertObject";

        final And and1 = new And();
        final And and2 = new And();
        and1.addChild( a );
        and1.addChild( and2 );
        and2.addChild( b );
        final Not not1 = new Not();
        final Not not2 = new Not();
        not1.addChild( not2 );
        not2.addChild( c );
        and2.addChild( not1 );
        and1.addChild( d );

        final And and3 = new And();
        and3.addChild( e );
        final Or or1 = new Or();
        and3.addChild( or1 );
        final Exists exist1 = new Exists();
        exist1.addChild( g );
        or1.addChild( exist1 );
        or1.addChild( h );

        final Not not3 = new Not();
        not3.addChild( i );

        final And root = new And();
        root.addChild( and1 );
        root.addChild( and3 );
        root.addChild( not3 );

        LogicTransformer.getInstance().processTree( root );

        // --------------------------------------
        // Test that the treesEqual method works
        // --------------------------------------

        // Check against itself
        assertEquals( root,
                      root );

        // Test against a known false tree
        final And testAnd1 = new And();
        testAnd1.addChild( a );
        testAnd1.addChild( b );
        final Or testOr2 = new Or();
        testOr2.addChild( c );
        testOr2.addChild( d );
        testAnd1.addChild( testOr2 );
        assertFalse( root.equals( testAnd1 ) );

        // ----------------------------------------------------------------------------------
        // Now construct the result tree so we can test root against what it
        // should look like
        // ----------------------------------------------------------------------------------

        // Get known correct tree
        // The binary stream was created from a handchecked correct output

        // Uncomment this when you need to output a new known correct tree
        // result
        // writeTree(root, "correct_processTree1.dat");
        final ObjectInputStream ois = new ObjectInputStream( this.getClass().getResourceAsStream( "/correct_processTree1.dat" ) );

        final And correctResultRoot = (And) ois.readObject();

        // Make sure they are equal
        assertEquals( correctResultRoot,
                      root );
    }

    public void testCloneable() {
        final String a = "a";
        final String b = "b";
        final String c = "c";
        final String d = "d";
        final String e = "e";
        final String f = "f";
        final String g = "g";
        final String h = "h";

        // Test against a known false tree
        final And and = new And();
        and.addChild( a );
        and.addChild( b );

        final Or or = new Or();
        or.addChild( c );
        or.addChild( d );
        and.addChild( or );
        final And and2 = new And();
        and2.addChild( e );
        and2.addChild( f );
        or.addChild( and2 );

        final Not not = new Not();
        and.addChild( not );
        final Or or2 = new Or();
        not.addChild( or2 );
        or2.addChild( g );
        or2.addChild( h );

        final GroupElement cloned = (GroupElement) and.clone();

        assertEquals( and,
                      cloned );

    }

    /**
     * 
     * 
     * /**
     * 
     * <pre>
     *                         _/|\_
     *                      __/  |  \__
     *                     /     |     \ 
     *                  __/      |      \__
     *                 /         |         \
     *                And       or         And
     *               /  \       / \        /  \
     *             a    Or     d   e      Not OR
     *                 / \                |  / | 
     *               b    c               f g Not
     *                                         |
     *                                         h
     *                  
     *                   
     *                  
     * </pre>
     * 
     * Each And is a Rete sub rule
     * 
     * <pre>
     *     
     *    
     *       And___     And___      And___      And___        And__    And___       And___    And___     
     *      ||| |  \   ||| |  \     ||| |  \   ||| |  \     ||| |  \  ||| |  \     ||| |  \  ||| |  \ 
     *      dab Not g  dab Not Not  dac Not g  dac Not Not  eab Not g eab Not Not  eac Not g eac Not Not
     *           |          |   |        |          |   |   |    |        |    |       |          |   |   
     *           f          f   h        f          f   h        f        f    h       f          f   h
     *                        
     *                        
     * </pre>
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     * 
     * 
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     */
    public void xTestTransform() throws IOException,
                                ClassNotFoundException,
                                InvalidPatternException {
        final String a = "a";
        final String b = "b";
        final String c = "c";
        final String d = "d";
        final String e = "e";
        final String f = "f";
        final String g = "g";
        final String h = "h";
        final String i = "i";

        final And and = new And();

        final And and1 = new And();
        and1.addChild( a );
        final Or or1 = new Or();
        or1.addChild( b );
        or1.addChild( c );
        and1.addChild( or1 );
        and.addChild( and1 );

        final Or or2 = new Or();
        or2.addChild( d );
        or2.addChild( e );
        and.addChild( or2 );

        final And and2 = new And();
        final Not not1 = new Not();
        not1.addChild( f );
        final Or or3 = new Or();
        or3.addChild( g );

        final Not not2 = new Not();
        not2.addChild( h );
        or3.addChild( not2 );

        // ---------------------------------------
        // Check a simple case no just one branch
        // ---------------------------------------
        And[] ands = LogicTransformer.getInstance().transform( and1 );
        assertLength( 2,
                      ands );
        assertTrue( ands[0] instanceof And );
        assertLength( 2,
                      ands[0].getChildren() );

        assertLength( 2,
                      ands[0].getChildren() );
        assertEquals( And.class,
                      ands[0].getClass() );
        assertEquals( And.class,
                      ands[0].getClass() );

        And newAnd = ands[0];
        assertContains( a,
                        newAnd.getChildren() );
        assertContains( b,
                        newAnd.getChildren() );

        newAnd = ands[1];
        assertContains( a,
                        newAnd.getChildren() );
        assertContains( c,
                        newAnd.getChildren() );

        ands = LogicTransformer.getInstance().transform( and );

        // Uncomment this when you need to output a new known correct tree
        // result
        // writeTree(ands, "correct_transform1.dat");

        // Now check the main tree

        // Get known correct tree
        // The binary stream was created from a handchecked correct output
        final ObjectInputStream ois = new ObjectInputStream( this.getClass().getResourceAsStream( "/correct_transform1.dat" ) );
        final And[] correctResultAnds = (And[]) ois.readObject();

        for ( int j = 0; j < ands.length; j++ ) {
            assertEquals( correctResultAnds[j],
                          ands[j] );
        }
    }

    private void writeTree(final Object object,
                           final String fileName) throws IOException {
        final String className = this.getClass().getName();

        File file = new File( this.getClass().getResource( className.substring( className.lastIndexOf( '.' ) + 1 ) + ".class" ).getFile() );

        file = new File( file.getParent(),
                         fileName );

        new ObjectOutputStream( new FileOutputStream( file ) ).writeObject( object );
    }

}