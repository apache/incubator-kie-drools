package org.drools.verifier.solver;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.data.VerifierComponent;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class PatternSolverTest extends TestCase {

    /**
     * <pre>
     *      and
     *     /   \
     *  descr  descr2
     * </pre>
     * 
     * result:<br>
     * descr && descr2
     */
    public void testAddBasicAnd() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction );
        solver.add( literalRestriction2 );
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      list.get( 0 ).size() );
    }

    /**
     * <pre>
     *       or
     *      /  \
     *  descr descr2
     * </pre>
     * 
     * result:<br>
     * descr<br>
     * or<br>
     * descr2
     */
    public void testAddBasicOr() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction );
        solver.add( literalRestriction2 );
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 2,
                      list.size() );
        assertEquals( 1,
                      list.get( 0 ).size() );
        assertEquals( 1,
                      list.get( 1 ).size() );
    }

    /**
     * <pre>
     *       or
     *      /  \
     *  descr  and
     *         / \
     *    descr2 descr3
     * </pre>
     * 
     * result:<br>
     * descr <br>
     * or<br>
     * descr2 && descr3
     */
    public void testAddOrAnd() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();
        LiteralRestriction literalRestriction3 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction );
        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction2 );
        solver.add( literalRestriction3 );
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 2,
                      list.size() );

        assertEquals( 1,
                      list.get( 0 ).size() );
        assertTrue( list.get( 0 ).contains( literalRestriction ) );

        assertEquals( 2,
                      list.get( 1 ).size() );
        assertTrue( list.get( 1 ).contains( literalRestriction2 ) );
        assertTrue( list.get( 1 ).contains( literalRestriction3 ) );
    }

    /**
     * <pre>
     *       and
     *      /  \
     *  descr   or
     *         / \
     *    descr2 descr3
     * </pre>
     * 
     * result:<br>
     * descr && descr2 <br>
     * or<br>
     * descr && descr3
     */
    public void testAddAndOr() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();
        LiteralRestriction literalRestriction3 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction );
        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction2 );
        solver.add( literalRestriction3 );
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 2,
                      list.size() );

        assertEquals( 2,
                      list.get( 0 ).size() );
        assertTrue( list.get( 0 ).contains( literalRestriction ) );
        assertTrue( list.get( 0 ).contains( literalRestriction2 ) );

        assertEquals( 2,
                      list.get( 1 ).size() );
        assertTrue( list.get( 1 ).contains( literalRestriction ) );
        assertTrue( list.get( 1 ).contains( literalRestriction3 ) );
    }

    /**
     * <pre>
     *            and
     *         /        \
     *       or          or
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr3<br>
     * or<br>
     * descr && descr4<br>
     * or<br>
     * descr2 && descr3<br>
     * or<br>
     * descr2 && descr4
     */
    public void testAddAndOrOr() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();
        LiteralRestriction literalRestriction3 = new LiteralRestriction();
        LiteralRestriction literalRestriction4 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.AND );
        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction );
        solver.add( literalRestriction2 );
        solver.end();
        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction3 );
        solver.add( literalRestriction4 );
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 4,
                      list.size() );

        assertEquals( 2,
                      list.get( 0 ).size() );
        assertTrue( list.get( 0 ).contains( literalRestriction ) );
        assertTrue( list.get( 0 ).contains( literalRestriction3 ) );

        assertEquals( 2,
                      list.get( 1 ).size() );
        assertTrue( list.get( 1 ).contains( literalRestriction ) );
        assertTrue( list.get( 1 ).contains( literalRestriction4 ) );

        assertEquals( 2,
                      list.get( 2 ).size() );
        assertTrue( list.get( 2 ).contains( literalRestriction2 ) );
        assertTrue( list.get( 2 ).contains( literalRestriction3 ) );

        assertEquals( 2,
                      list.get( 3 ).size() );
        assertTrue( list.get( 3 ).contains( literalRestriction2 ) );
        assertTrue( list.get( 3 ).contains( literalRestriction4 ) );
    }

    /**
     * <pre>
     *             or
     *         /        \
     *       and         and
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr2<br>
     * or<br>
     * descr3 && descr4
     */
    public void testAddOrAndAnd() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();
        LiteralRestriction literalRestriction3 = new LiteralRestriction();
        LiteralRestriction literalRestriction4 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.OR );
        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction );
        solver.add( literalRestriction2 );
        solver.end();
        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction3 );
        solver.add( literalRestriction4 );
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 2,
                      list.size() );

        assertEquals( 2,
                      list.get( 0 ).size() );
        assertTrue( list.get( 0 ).contains( literalRestriction ) );
        assertTrue( list.get( 0 ).contains( literalRestriction2 ) );

        assertEquals( 2,
                      list.get( 1 ).size() );
        assertTrue( list.get( 1 ).contains( literalRestriction3 ) );
        assertTrue( list.get( 1 ).contains( literalRestriction4 ) );
    }

    /**
     * <pre>
     *             or
     *         /        \
     *       and         or
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr2<br>
     * or<br>
     * descr3<br>
     * or<br>
     * descr4
     */
    public void testAddOrAndOr() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();
        LiteralRestriction literalRestriction3 = new LiteralRestriction();
        LiteralRestriction literalRestriction4 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.OR );
        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction );
        solver.add( literalRestriction2 );
        solver.end();
        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction3 );
        solver.add( literalRestriction4 );
        solver.end();
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 3,
                      list.size() );

        assertEquals( 2,
                      list.get( 0 ).size() );
        assertTrue( list.get( 0 ).contains( literalRestriction ) );
        assertTrue( list.get( 0 ).contains( literalRestriction2 ) );

        assertEquals( 1,
                      list.get( 1 ).size() );
        assertTrue( list.get( 1 ).contains( literalRestriction3 ) );

        assertEquals( 1,
                      list.get( 2 ).size() );
        assertTrue( list.get( 2 ).contains( literalRestriction4 ) );
    }

    /**
     * <pre>
     *                   and
     *          /         |      \
     *       and         or       descr5
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     * 
     * result:<br>
     * descr && descr2 && descr3 && descr5<br>
     * or<br>
     * descr && descr2 && descr4 && descr5<br>
     */
    public void testAddOrAndOrDescr() {
        Pattern pattern = new Pattern();

        LiteralRestriction literalRestriction = new LiteralRestriction();
        LiteralRestriction literalRestriction2 = new LiteralRestriction();
        LiteralRestriction literalRestriction3 = new LiteralRestriction();
        LiteralRestriction literalRestriction4 = new LiteralRestriction();
        LiteralRestriction literalRestriction5 = new LiteralRestriction();

        PatternSolver solver = new PatternSolver( pattern );

        solver.addOperator( OperatorDescr.Type.AND );
        solver.addOperator( OperatorDescr.Type.AND );
        solver.add( literalRestriction );
        solver.add( literalRestriction2 );
        solver.end();
        solver.addOperator( OperatorDescr.Type.OR );
        solver.add( literalRestriction3 );
        solver.add( literalRestriction4 );
        solver.end();
        solver.add( literalRestriction5 );
        solver.end();

        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        assertEquals( 2,
                      list.size() );

        assertEquals( 4,
                      list.get( 0 ).size() );
        assertTrue( list.get( 0 ).contains( literalRestriction ) );
        assertTrue( list.get( 0 ).contains( literalRestriction2 ) );
        assertTrue( list.get( 0 ).contains( literalRestriction3 ) );
        assertTrue( list.get( 0 ).contains( literalRestriction5 ) );

        assertEquals( 4,
                      list.get( 1 ).size() );
        assertTrue( list.get( 1 ).contains( literalRestriction ) );
        assertTrue( list.get( 1 ).contains( literalRestriction2 ) );
        assertTrue( list.get( 1 ).contains( literalRestriction4 ) );
        assertTrue( list.get( 1 ).contains( literalRestriction4 ) );
    }
}
