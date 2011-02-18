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

package org.drools.rule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.drools.DroolsTestCase;
import org.drools.base.ClassObjectType;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.spi.ObjectType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
    @Test
    public void testSingleOrAndOrTransformation() throws InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );
        final Pattern c = new Pattern( 2,
                                     type,
                                     "c" );

        final GroupElement or = GroupElementFactory.newOrInstance();
        or.addChild( a );
        or.addChild( b );

        final GroupElement parent = GroupElementFactory.newAndInstance();
        parent.addChild( or );
        parent.addChild( c );

        LogicTransformer.getInstance().applyOrTransformation( parent );

        assertLength( 2,
                      parent.getChildren() );
        assertEquals( GroupElement.class,
                      parent.getChildren().get( 0 ).getClass() );
        assertEquals( GroupElement.class,
                      parent.getChildren().get( 1 ).getClass() );

        final GroupElement and1 = (GroupElement) parent.getChildren().get( 0 );
        assertTrue( and1.isAnd() );

        // transformation MUST keep the order
        assertEquals( a,
                      and1.getChildren().get( 0 ) );
        assertEquals( c,
                      and1.getChildren().get( 1 ) );

        final GroupElement and2 = (GroupElement) parent.getChildren().get( 1 );
        assertEquals( b,
                      and2.getChildren().get( 0 ) );
        assertEquals( c,
                      and2.getChildren().get( 1 ) );

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
    @Test
    public void testMultipleOrAndOrTransformation() throws InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );
        final Pattern c = new Pattern( 2,
                                     type,
                                     "c" );
        final Pattern d = new Pattern( 3,
                                     type,
                                     "d" );
        final Pattern e = new Pattern( 4,
                                     type,
                                     "e" );
        final Pattern f = new Pattern( 5,
                                     type,
                                     "f" );

        final GroupElement parent = GroupElementFactory.newAndInstance();
        final GroupElement or = GroupElementFactory.newOrInstance();
        or.addChild( a );
        or.addChild( b );
        parent.addChild( or );
        parent.addChild( c );

        final GroupElement or2 = GroupElementFactory.newOrInstance();

        or2.addChild( d );
        or2.addChild( e );
        parent.addChild( or2 );

        final GroupElement not = GroupElementFactory.newNotInstance();
        not.addChild( f );
        parent.addChild( not );

        LogicTransformer.getInstance().applyOrTransformation( parent );

        assertEquals( GroupElement.Type.OR,
                      parent.getType() );

        assertLength( 4,
                      parent.getChildren() );
        assertEquals( GroupElement.class,
                      parent.getChildren().get( 0 ).getClass() );
        assertEquals( GroupElement.class,
                      parent.getChildren().get( 1 ).getClass() );
        assertEquals( GroupElement.class,
                      parent.getChildren().get( 2 ).getClass() );
        assertEquals( GroupElement.class,
                      parent.getChildren().get( 3 ).getClass() );

        GroupElement and1 = (GroupElement) parent.getChildren().get( 0 );
        assertTrue( and1.isAnd() );
        assertLength( 4,
                      and1.getChildren() );
        assertEquals( a,
                      and1.getChildren().get( 0 ) );
        assertEquals( c,
                      and1.getChildren().get( 1 ) );
        assertEquals( d,
                      and1.getChildren().get( 2 ) );
        assertEquals( not,
                      and1.getChildren().get( 3 ) );

        and1 = (GroupElement) parent.getChildren().get( 1 );
        assertTrue( and1.isAnd() );
        assertLength( 4,
                      and1.getChildren() );
        assertEquals( a,
                      and1.getChildren().get( 0 ) );
        assertEquals( c,
                      and1.getChildren().get( 1 ) );
        assertEquals( e,
                      and1.getChildren().get( 2 ) );
        assertEquals( not,
                      and1.getChildren().get( 3 ) );

        and1 = (GroupElement) parent.getChildren().get( 2 );
        assertTrue( and1.isAnd() );
        assertLength( 4,
                      and1.getChildren() );
        assertEquals( b,
                      and1.getChildren().get( 0 ) );
        assertEquals( c,
                      and1.getChildren().get( 1 ) );
        assertEquals( d,
                      and1.getChildren().get( 2 ) );
        assertEquals( not,
                      and1.getChildren().get( 3 ) );

        and1 = (GroupElement) parent.getChildren().get( 3 );
        assertTrue( and1.isAnd() );
        assertLength( 4,
                      and1.getChildren() );
        assertEquals( b,
                      and1.getChildren().get( 0 ) );
        assertEquals( c,
                      and1.getChildren().get( 1 ) );
        assertEquals( e,
                      and1.getChildren().get( 2 ) );
        assertEquals( not,
                      and1.getChildren().get( 3 ) );

    }

    /**
     * This data structure is now valid
     *
     * (Not (OR (A B) ) )
     *
     * <pre>
     *             Not
     *              |
     *             or
     *            /  \
     *           a    b
     * </pre>
     *
     * Should become:
     *
     * <pre>
     *             And
     *             / \
     *           Not Not
     *            |   |
     *            a   b
     * </pre>
     *
     *
     */
    @Test
    public void testNotOrTransformation() throws InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );

        final GroupElement parent = GroupElementFactory.newNotInstance();
        final GroupElement or = GroupElementFactory.newOrInstance();
        parent.addChild( or );

        or.addChild( a );
        or.addChild( b );

        LogicTransformer.getInstance().applyOrTransformation( parent );

        assertTrue( parent.isAnd() );
        assertEquals( 2,
                      parent.getChildren().size() );

        // we must ensure order
        final GroupElement b1 = (GroupElement) parent.getChildren().get( 0 );
        final GroupElement b2 = (GroupElement) parent.getChildren().get( 1 );
        assertTrue( b1.isNot() );
        assertTrue( b2.isNot() );

        assertEquals( 1,
                      b1.getChildren().size() );
        assertEquals( a,
                      b1.getChildren().get( 0 ) );

        assertEquals( 1,
                      b2.getChildren().size() );
        assertEquals( b,
                      b2.getChildren().get( 0 ) );
    }

    /**
     * Exists inside a not is redundant and should be eliminated
     *
     * (Not (exists (A) ) )
     *
     * <pre>
     *             Not
     *              |
     *            Exists
     *              |
     *             And
     *             / \
     *            a   b
     * </pre>
     *
     * Should become:
     *
     * <pre>
     *             Not
     *              |   
     *             And
     *             / \
     *            a   b   
     * </pre>
     *
     *
     */
    @Test
    public void testNotExistsTransformation() throws InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                       type,
                                       "a" );
        final Pattern b = new Pattern( 1,
                                       type,
                                       "b" );

        final GroupElement not = GroupElementFactory.newNotInstance();
        final GroupElement exists = GroupElementFactory.newExistsInstance();
        final GroupElement and = GroupElementFactory.newAndInstance();
        not.addChild( exists );
        exists.addChild( and );
        and.addChild( a );
        and.addChild( b );

        GroupElement[] transformed = LogicTransformer.getInstance().transform( not );
        GroupElement wrapper = transformed[0]; 
        GroupElement notR = (GroupElement) wrapper.getChildren().get( 0 );

        assertTrue( notR.isNot() );
        assertEquals( 1,
                      notR.getChildren().size() );

        assertTrue( notR.getChildren().get( 0 ) instanceof GroupElement );
        GroupElement andR = (GroupElement) notR.getChildren().get( 0 );
        assertTrue( andR.isAnd() );
        
        assertEquals( 2,
                      andR.getChildren().size() );
        assertTrue( andR.getChildren().get( 0 ) instanceof Pattern );
        assertTrue( andR.getChildren().get( 1 ) instanceof Pattern );
        final Pattern a1 = (Pattern) andR.getChildren().get( 0 );
        final Pattern b1 = (Pattern) andR.getChildren().get( 1 );

        assertEquals( a,
                      a1 );
        assertEquals( b,
                      b1 );
    }

    /**
     * This data structure is now valid (Exists (OR (A B) ) )
     *
     * <pre>
     *             Exists
     *              |
     *             or
     *            /  \
     *           a    b
     * </pre>
     *
     * Should become:
     *
     * <pre>
     *              Or
     *             /  \
     *        Exists  Exists
     *            |    |
     *            a    b
     * </pre>
     */
    @Test
    public void testExistOrTransformation() throws InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );

        final GroupElement parent = GroupElementFactory.newExistsInstance();
        final GroupElement or = GroupElementFactory.newOrInstance();
        parent.addChild( or );

        or.addChild( a );
        or.addChild( b );

        LogicTransformer.getInstance().applyOrTransformation( parent );

        assertTrue( parent.isOr() );
        assertEquals( 2,
                      parent.getChildren().size() );

        // we must ensure order
        final GroupElement b1 = (GroupElement) parent.getChildren().get( 0 );
        final GroupElement b2 = (GroupElement) parent.getChildren().get( 1 );
        assertTrue( b1.isExists() );
        assertTrue( b2.isExists() );

        assertEquals( 1,
                      b1.getChildren().size() );
        assertEquals( a,
                      b1.getChildren().get( 0 ) );

        assertEquals( 1,
                      b2.getChildren().size() );
        assertEquals( b,
                      b2.getChildren().get( 0 ) );

    }

    @Test
    public void testEliminateEmptyBranchesAndDuplications() throws InvalidRuleException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );
        final Pattern c = new Pattern( 2,
                                     type,
                                     "c" );
        final Pattern d = new Pattern( 3,
                                     type,
                                     "d" );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( a );
        and1.addChild( b );

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( c );
        and2.addChild( d );

        and1.addChild( and2 );

        final GroupElement or = GroupElementFactory.newOrInstance();
        and1.addChild( or );

        final GroupElement[] result = LogicTransformer.getInstance().transform( and1 );

        assertLength( 1,
                      result );
        assertLength( 4,
                      result[0].getChildren() );
        // we must ensure order
        assertEquals( a,
                      result[0].getChildren().get( 0 ) );
        assertEquals( b,
                      result[0].getChildren().get( 1 ) );
        assertEquals( c,
                      result[0].getChildren().get( 2 ) );
        assertEquals( d,
                      result[0].getChildren().get( 3 ) );

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
     *             a  And d    e  Or        i
     *                / \        /  \
     *               b  Not     h  Exists
     *                   |           |
     *                  Not          g
     *                   |
     *                   c
     * </pre>
     *
     *   It is important to ensure that the order of
     *   the elements is not changed after transformation
     *
     * <pre>
     *                            Or
     *                           _/ \__
     *                        __/      \___
     *                       /             \__
     *                    __/                 \__
     *                   /                       \__
     *                  /                           \__
     *                 |                               \
     *                And                             And
     *            /|  |  | | | \                /|  |  | |   |    \
     *           a b Not d e h Not             a b Not d e Exists Not
     *                |         |                   |        |     |
     *               Not        i                  Not       g     i
     *                |                             |
     *                c                             c
     * </pre>
     *
     * @throws IOException
     * @throws ClassNotFoundException
     *
     *
     *
     */
    @Test
    public void testProcessTree() throws IOException,
                                 ClassNotFoundException,
                                 InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );
        final Pattern c = new Pattern( 2,
                                     type,
                                     "c" );
        final Pattern d = new Pattern( 3,
                                     type,
                                     "d" );
        final Pattern e = new Pattern( 4,
                                     type,
                                     "e" );
        final Pattern g = new Pattern( 5,
                                     type,
                                     "g" );
        final Pattern h = new Pattern( 6,
                                     type,
                                     "h" );
        final Pattern i = new Pattern( 7,
                                     type,
                                     "i" );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and1.addChild( a );
        and1.addChild( and2 );
        and2.addChild( b );
        final GroupElement not1 = GroupElementFactory.newNotInstance();
        final GroupElement not2 = GroupElementFactory.newNotInstance();
        not1.addChild( not2 );
        not2.addChild( c );
        and2.addChild( not1 );
        and1.addChild( d );

        final GroupElement and3 = GroupElementFactory.newAndInstance();
        and3.addChild( e );
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        and3.addChild( or1 );
        final GroupElement exist1 = GroupElementFactory.newExistsInstance();
        exist1.addChild( g );
        or1.addChild( h );
        or1.addChild( exist1 );

        final GroupElement not3 = GroupElementFactory.newNotInstance();
        not3.addChild( i );

        final GroupElement root = GroupElementFactory.newAndInstance();
        root.addChild( and1 );
        root.addChild( and3 );
        root.addChild( not3 );

        final GroupElement[] result = LogicTransformer.getInstance().transform( root );

        // ----------------------------------------------------------------------------------
        // Now construct the result tree so we can test root against what it
        // should look like
        // ----------------------------------------------------------------------------------

        // Get known correct tree
        // The binary stream was created from a handchecked correct output

        // Uncomment this when you need to output a new known correct tree
        // result
        writeTree( result,
                   "correct_processTree1.dat" );
        final GroupElement[] correctResultRoot =
                (GroupElement[]) DroolsStreamUtils.streamIn(
                        this.getClass().getResourceAsStream( "correct_processTree1.dat" ));

        // Make sure they are equal
        for ( int j = 0; j < correctResultRoot.length; j++ ) {
            assertEquals( correctResultRoot[j],
                          result[j] );
        }
    }

    @Test
    public void testCloneable() {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );
        final Pattern c = new Pattern( 2,
                                     type,
                                     "c" );
        final Pattern d = new Pattern( 3,
                                     type,
                                     "d" );
        final Pattern e = new Pattern( 4,
                                     type,
                                     "e" );
        final Pattern f = new Pattern( 5,
                                     type,
                                     "f" );
        final Pattern g = new Pattern( 6,
                                     type,
                                     "g" );
        final Pattern h = new Pattern( 7,
                                     type,
                                     "h" );

        // Test against a known false tree
        final GroupElement and = GroupElementFactory.newAndInstance();
        and.addChild( a );
        and.addChild( b );

        final GroupElement or = GroupElementFactory.newOrInstance();
        or.addChild( c );
        or.addChild( d );
        and.addChild( or );
        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( e );
        and2.addChild( f );
        or.addChild( and2 );

        final GroupElement not = GroupElementFactory.newNotInstance();
        and.addChild( not );
        final GroupElement or2 = GroupElementFactory.newOrInstance();
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
     *      abd Not g  abd Not Not  abe Not g  abe Not Not  acd Not g acd Not Not  ace Not g ace Not Not
     *           |          |   |        |          |   |        |        |    |       |          |   |
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
    @Test
    public void testTransform() throws IOException,
                               ClassNotFoundException,
                               InvalidPatternException {
        final ObjectType type = new ClassObjectType( String.class );
        final Pattern a = new Pattern( 0,
                                     type,
                                     "a" );
        final Pattern b = new Pattern( 1,
                                     type,
                                     "b" );
        final Pattern c = new Pattern( 2,
                                     type,
                                     "c" );
        final Pattern d = new Pattern( 3,
                                     type,
                                     "d" );
        final Pattern e = new Pattern( 4,
                                     type,
                                     "e" );
        final Pattern f = new Pattern( 5,
                                     type,
                                     "f" );
        final Pattern g = new Pattern( 6,
                                     type,
                                     "g" );
        final Pattern h = new Pattern( 7,
                                     type,
                                     "h" );

        final GroupElement and = GroupElementFactory.newAndInstance();

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( a );
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        or1.addChild( b );
        or1.addChild( c );
        and1.addChild( or1 );
        and.addChild( and1 );

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( d );
        or2.addChild( e );
        and.addChild( or2 );

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        final GroupElement not1 = GroupElementFactory.newNotInstance();
        not1.addChild( f );
        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( g );

        final GroupElement not2 = GroupElementFactory.newNotInstance();
        not2.addChild( h );
        or3.addChild( not2 );

        and2.addChild( not1 );
        and2.addChild( or3 );
        and.addChild( and2 );

        final GroupElement[] ands = LogicTransformer.getInstance().transform( and );

        // Uncomment this when you need to output a new known correct tree
        // result
        writeTree( ands,
                   "correct_transform1.dat" );

        // Now check the main tree

        // Get known correct tree
        // The binary stream was created from a handchecked correct output
        final GroupElement[] correctResultAnds =
                (GroupElement[]) DroolsStreamUtils.streamIn(
                        this.getClass().getResourceAsStream( "correct_transform1.dat" ));

        for ( int j = 0; j < ands.length; j++ ) {
            assertEquals( correctResultAnds[j],
                          ands[j] );
        }
    }

    private void writeTree(final Object object,
                           final String fileName) throws IOException {
        final String className = this.getClass().getName();

        File file = new File( this.getClass().getResource( className.substring( className.lastIndexOf( '.' ) + 1 ) + ".class" ).getFile() );

        file = new File( file.getParent().replaceAll("%20", " "), fileName );

        DroolsStreamUtils.streamOut(new FileOutputStream( file ), object);
    }

}
