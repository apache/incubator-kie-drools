package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.core.util.bitmask.OpenBitSet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.GraphTest.assertLinkedNode;
import static org.drools.beliefs.bayes.GraphTest.assertLinkedVertex;
import static org.drools.beliefs.bayes.GraphTest.bitSet;
import static org.drools.beliefs.bayes.GraphTest.connectChildToParents;
import static org.drools.beliefs.bayes.GraphTest.connectParentToChildren;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JunctionTreeBuilderTest {
    @Test
    public void testOpenBitSet() {
        OpenBitSet b1 = bitSet("00000111");
        OpenBitSet b2 = bitSet("00000111");
        OpenBitSet b3 = bitSet("00000110");
        OpenBitSet b4 = bitSet("00001110");

        assertEquals(0, OpenBitSet.andNotCount(b1, b2)); // b1 and b3 are equal

        assertEquals( 1,  OpenBitSet.andNotCount( b2, b3 ) ); // b2 is not a subset of b3
        assertEquals(0, OpenBitSet.andNotCount(b3, b2)); // b3 is a subset of b2

        assertEquals(1, OpenBitSet.andNotCount(b2, b4)); // b2 is not a subset of b4
        assertEquals(1, OpenBitSet.andNotCount(b4, b2)); // b4 is not a subset of b3
    }

    @Test
    public void testMoralize1() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        connectParentToChildren(x2, x1);
        connectParentToChildren(x3, x1);
        connectParentToChildren(x4, x1);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );

        assertLinkedNode(jtBuilder, new int[]{1, 2, 3, 4});
        assertLinkedNode(jtBuilder, new int[]{2, 1});
        assertLinkedNode(jtBuilder, new int[]{3, 1});
        assertLinkedNode(jtBuilder, new int[]{4, 1});

        jtBuilder.moralize();

        assertLinkedNode(jtBuilder, new int[]{1, 2, 3, 4});
        assertLinkedNode(jtBuilder, new int[]{2, 1, 3, 4});
        assertLinkedNode(jtBuilder, new int[]{3, 1, 2, 4});
        assertLinkedNode(jtBuilder, new int[]{4, 1, 2, 3});
    }

    @Test
    public void testMoralize2() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);

        connectParentToChildren(x1, x2, x3);
        connectParentToChildren(x2, x4);
        connectParentToChildren(x4, x5);
        connectParentToChildren(x3, x5);
        connectParentToChildren(x6, x5);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        jtBuilder.moralize();

        assertLinkedNode(jtBuilder, new int[]{x1.getId(), 2, 3});

        assertLinkedNode(jtBuilder, new int[]{x2.getId(), 1, 4});

        assertLinkedNode(jtBuilder, new int[]{x3.getId(), 1, 4, 5, 6});

        assertLinkedNode(jtBuilder, new int[]{x4.getId(), 2, 3, 5, 6});

        assertLinkedNode(jtBuilder, new int[]{x5.getId(), 3, 4, 6});

        assertLinkedNode(jtBuilder, new int[]{x6.getId(), 3, 4, 5});

    }

    @Test
    public void testEliminationCandidate1() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        connectParentToChildren(x1, x2, x3, x4);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        jtBuilder.moralize();

        EliminationCandidate vt1 = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1 );

        assertEquals(3, vt1.getNewEdgesRequired() );

        assertEquals( bitSet("11110"), vt1.getCliqueBitSit());
    }

    @Test
    public void testEliminationCandidate2() {
        Graph graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        connectParentToChildren(x1, x2, x3, x4);
        connectParentToChildren(x3, x4);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        jtBuilder.moralize();

        EliminationCandidate vt1 = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1 );

        assertEquals(2, vt1.getNewEdgesRequired());
        assertEquals(bitSet("11110"), vt1.getCliqueBitSit());
    }

    @Test
    public void testCreateClique() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode dX0 = addNode(graph);
        GraphNode dX1 = addNode(graph);
        GraphNode dX2 = addNode(graph);
        GraphNode dX3 = addNode(graph);
        GraphNode dX4 = addNode(graph);
        GraphNode dX5 = addNode(graph);

        connectParentToChildren(dX1, dX2, dX3, dX4);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        // do not moralize, as we want to test just the clique creation through elimination of the provided vertices

        Set<Integer> vertices = new HashSet<Integer>();
        boolean[] adjList = new boolean[] { false, false, true, true, true, false };

        boolean[][] clonedAdjMatrix = JunctionTreeBuilder.cloneAdjacencyMarix(jtBuilder.getAdjacencyMatrix());
        jtBuilder.createClique(dX1.getId(), clonedAdjMatrix, vertices, adjList );

        assertEquals( 3, vertices.size() );
        assertTrue( vertices.containsAll(Arrays.asList(new Integer[]{2, 3, 4})) );

        assertLinkedNode(jtBuilder, 1, 2, 3, 4);
        assertLinkedNode(jtBuilder, 2, 1, 3, 4);
        assertLinkedNode(jtBuilder, 3, 1, 2, 4);
        assertLinkedNode(jtBuilder, 4, 1, 2, 3);
    }

    @Test
    public void testCliqueSuperSet() {
        Graph<BayesVariable> graph = new BayesNetwork();
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );

        List<OpenBitSet> cliques = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = bitSet("00011110");
        jtBuilder.updateCliques(cliques, OpenBitSet1);
        assertEquals( 1, cliques.size() );

        // ignore subset
        OpenBitSet OpenBitSet2 = bitSet("00000110");
        jtBuilder.updateCliques(cliques, OpenBitSet2);
        assertEquals( 1, cliques.size() );
        assertEquals( OpenBitSet1, cliques.get(0) );

        // add overlapping, as not a pure subset
        OpenBitSet OpenBitSet3 = bitSet("01000110");
        jtBuilder.updateCliques(cliques, OpenBitSet3);
        assertEquals( 2, cliques.size() );
        assertEquals( OpenBitSet1, cliques.get(0) );
        assertEquals( OpenBitSet3, cliques.get(1) );
    }

    @Test
    public void testPriorityQueueWithMinimalNewEdges() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);
        GraphNode x8 = addNode(graph);

        GraphNode x9 = addNode(graph);
        GraphNode x10 = addNode(graph);
        GraphNode x11 = addNode(graph);
        GraphNode x12 = addNode(graph);

        // 3 new edges
        connectParentToChildren(x2, x1);
        connectParentToChildren(x3, x1);
        connectParentToChildren(x4, x1);

        // 1 new edge
        // we give this a high weight, to show required new edges is compared first
        connectParentToChildren(x6, x5);
        connectParentToChildren(x7, x5);
        x5.setContent(new BayesVariable<String>("x5", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));
        x6.setContent(new BayesVariable<String>("x6", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));
        x7.setContent(new BayesVariable<String>("x7", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));

        // 6 new edges
        connectParentToChildren(x9, x8);
        connectParentToChildren(x10, x8);
        connectParentToChildren(x11, x8);
        connectParentToChildren(x12, x8);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        //jtBuilder.moralize(); // don't moralize, as we want to force a simpler construction for required edges, for the purposes of testing

        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());

        EliminationCandidate elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1);
        p.add( elmCandVert );

        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x5);
        p.add( elmCandVert );

        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x8);
        p.add( elmCandVert );

        EliminationCandidate v = p.remove();
        int id = v.getV().getId();
        assertEquals( 5, id );
        assertEquals( 1, v.getNewEdgesRequired() );

        v = p.remove();
        id = v.getV().getId();
        assertEquals( 1, id );
        assertEquals( 3, v.getNewEdgesRequired() );

        v = p.remove();
        id = v.getV().getId();
        assertEquals( 8, id );
        assertEquals( 6, v.getNewEdgesRequired() );

        assertEquals( 0, p.size() );
    }

    @Test
    public void testPriorityQueueWithMaximalCliqueWeight() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);
        GraphNode x8 = addNode(graph);
        GraphNode x9 = addNode(graph);
        GraphNode x10 = addNode(graph);
        GraphNode x11 = addNode(graph);
        GraphNode x12 = addNode(graph);

        connectParentToChildren(x2, x1);
        connectParentToChildren(x3, x1);
        connectParentToChildren(x4, x1);
        x1.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x3.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x4.setContent(new BayesVariable<String>("x4", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));

        connectParentToChildren(x6, x5);
        connectParentToChildren(x7, x5);
        connectParentToChildren(x8, x5);
        x5.setContent(new BayesVariable<String>("x5", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));
        x6.setContent(new BayesVariable<String>("x6", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));
        x7.setContent(new BayesVariable<String>("x7", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));
        x8.setContent(new BayesVariable<String>("x8", x0.getId(), new String[]{"a", "b", "c"}, new double[][]{{0.1, 0.1, 0.1}}));


        connectParentToChildren(x10, x9);
        connectParentToChildren(x11, x9);
        connectParentToChildren(x12, x9);
        x9.setContent(new BayesVariable<String>("x9", x0.getId(), new String[]{"a"}, new double[][]{{0.1}}));
        x10.setContent(new BayesVariable<String>("x10", x0.getId(), new String[]{"a"}, new double[][]{{0.1}}));
        x11.setContent(new BayesVariable<String>("x11", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x12.setContent(new BayesVariable<String>("x12", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        //jtBuilder.moralize(); // don't moralize, as we want to force a simpler construction for required edges, for the purposes of testing

        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());

        EliminationCandidate elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1);
        p.add( elmCandVert );

        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x5);
        p.add( elmCandVert );

        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x9);
        p.add( elmCandVert );

        EliminationCandidate v = p.remove();
        int id = v.getV().getId();
        assertEquals( 9, id );
        assertEquals( 4, v.getWeightRequired() );

        v = p.remove();
        id = v.getV().getId();
        assertEquals( 1, id );
        assertEquals( 16, v.getWeightRequired() );

        v = p.remove();
        id = v.getV().getId();
        assertEquals( 5, id );
        assertEquals( 81, v.getWeightRequired() );

        assertEquals( 0, p.size() );
    }

    @Test
    public void testIterativeEliminationUsingEdgeAndWeight() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);

        //          *
        //        / | \
        //       *  | *
        //       |  | |
        //       *  | *
        //        \  /
        //         *

        connectParentToChildren(x1, x2);
        connectParentToChildren(x1, x3);
        connectParentToChildren(x1, x6);

        connectParentToChildren(x2, x4);
        connectParentToChildren(x3, x5);

        connectParentToChildren(x4, x6);
        connectParentToChildren(x5, x6);

        // need to ensure x5 followed by x4 are removed first
        x1.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{"a", "b", "c", "d", "e", "f"}, new double[][]{{0.1, 0.1, 0.1, 0.1, 0.1, 0.1}}));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x3.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x4.setContent(new BayesVariable<String>("x4", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x5.setContent(new BayesVariable<String>("x5", x0.getId(), new String[]{"a"}, new double[][]{{0.1 }}));
        x6.setContent(new BayesVariable<String>("x6", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));


        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        //jtBuilder.moralize(); // don't moralize, as we want to force a simpler construction for vertex elimination order and updates

        boolean[][] clonedAdjMatrix = jtBuilder.cloneAdjacencyMarix(jtBuilder.getAdjacencyMatrix());
        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());
        Map<Integer, EliminationCandidate> elmVertMap = new HashMap<Integer, EliminationCandidate>();

        for ( GraphNode<BayesVariable> v : graph ) {
            if   ( v.getId() == 0 ) {
                continue;
            }
            EliminationCandidate elmCandVert = new EliminationCandidate(graph, clonedAdjMatrix, v);
            p.add( elmCandVert );
            elmVertMap.put( v.getId(), elmCandVert );
        }

        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 1, 2, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 2, 1, 4 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 3, 1, 5 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 4, 2, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 5, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 6, 1, 4, 5 });

        assertEquals( 3, elmVertMap.get( 1 ).getNewEdgesRequired() );
        assertEquals( 1, elmVertMap.get( 2 ).getNewEdgesRequired() );
        assertEquals( 1, elmVertMap.get( 3 ).getNewEdgesRequired() );
        assertEquals( 1, elmVertMap.get( 4 ).getNewEdgesRequired() );
        assertEquals( 1, elmVertMap.get( 5 ).getNewEdgesRequired() );
        assertEquals( 3, elmVertMap.get( 6 ).getNewEdgesRequired() );

        // 5 has the lowest new edges and weight
        EliminationCandidate v = p.remove();
        int id = v.getV().getId();
        assertEquals( 5, id );
        Set<Integer> verticesToUpdate = new HashSet<Integer>();
        boolean[] adjList = clonedAdjMatrix[ id  ];
        jtBuilder.createClique(5, clonedAdjMatrix, verticesToUpdate, adjList);
        assertEquals(4, verticesToUpdate.size());
        assertTrue(verticesToUpdate.containsAll(Arrays.asList(new Integer[]{1, 3,  6})));
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v );

        // assert all new edges
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 1, 2, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 2, 1, 4 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 3, 1, 5, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 4, 2, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 5, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 6, 1, 3, 4, 5 });

        // assert new edges were correctly recalculated
        assertEquals( 2, elmVertMap.get( 1 ).getNewEdgesRequired() );
        assertEquals( 1, elmVertMap.get( 2 ).getNewEdgesRequired() );
        assertEquals( 0, elmVertMap.get( 3 ).getNewEdgesRequired() );
        assertEquals( 2, elmVertMap.get( 6 ).getNewEdgesRequired() );
        assertEquals( 1, elmVertMap.get( 4 ).getNewEdgesRequired() );

        // 3 next as it has no new edges now, after recalculation
        v = p.remove();
        id = v.getV().getId();
        assertEquals( 3, id );
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(3, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v );

        // 4 is next
        v = p.remove();
        id = v.getV().getId();
        assertEquals( 4, id );
        verticesToUpdate = new HashSet<Integer>();
        adjList = clonedAdjMatrix[ id  ];
        jtBuilder.createClique(4, clonedAdjMatrix, verticesToUpdate, adjList);
        assertEquals(3, verticesToUpdate.size());
        assertTrue(verticesToUpdate.containsAll(Arrays.asList(new Integer[]{1, 2, 6}))); // don't forget 3 and 5 were already eliminated
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v );

        // assert all new edges
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 1, 2, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 2, 1, 4, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 3, 1, 5, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 4, 2, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 5, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 6, 1, 2, 3, 4, 5 });

        // assert new edges were correctly recalculated
        assertEquals( 0, elmVertMap.get( 1 ).getNewEdgesRequired() );
        assertEquals( 0, elmVertMap.get( 2 ).getNewEdgesRequired() );
        assertEquals( 0, elmVertMap.get( 6 ).getNewEdgesRequired() );


        // 1, 2 and 6 all have no new edges, and same cluster, so it uses id to ensure arbitrary is deterministic
        v = p.remove();
        id = v.getV().getId();
        assertEquals( 1, id );
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(1, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v );


        v = p.remove();
        id = v.getV().getId();
        assertEquals( 2, id );
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(2, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);

        v = p.remove();
        id = v.getV().getId();
        assertEquals( 6, id );
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(6, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v );

        assertEquals( 0, p.size() );
    }

    @Test
    public void testTriangulate1() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);

        //          *
        //        / | \
        //       *  | *
        //       |  | |
        //       *  | *
        //        \  /
        //         *

        connectParentToChildren(x1, x2);
        connectParentToChildren(x1, x3);
        connectParentToChildren(x1, x6);

        connectParentToChildren(x2, x4);
        connectParentToChildren(x3, x5);

        connectParentToChildren(x4, x6);
        connectParentToChildren(x5, x6);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        jtBuilder.moralize();
        jtBuilder.triangulate();

        // assert all new edges
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 1, 2, 3, 4, 5, 6 });
        assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{2, 1, 4});
        assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{3, 1, 5});
        assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{4, 1, 2, 5, 6});
        assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{5, 1, 3, 4, 6});
        assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{6, 1, 4, 5});
    }

    @Test
    public void testTriangulate2() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);

        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);

        //            *
        //           /
        //          *
        //        /  \
        //       *   *
        //       |   |
        //       *   |
        //        \  /
        //         *

        connectParentToChildren(x1, x2);
        connectParentToChildren(x1, x3);


        connectParentToChildren(x2, x4);
        connectParentToChildren(x2, x6);

        connectParentToChildren(x3, x5);

        connectParentToChildren(x5, x6);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        jtBuilder.moralize();
        List<OpenBitSet> cliques =  jtBuilder.triangulate();

        // assert all new edges
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 1, 2, 3 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 2, 1, 3, 4, 5, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 3, 1, 2, 5 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 4, 2 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 5, 2, 3, 6 });
        assertLinkedVertex( jtBuilder.getAdjacencyMatrix(), new int[] { 6, 2, 5 });

        assertEquals( 5, cliques.size() ); // 5th is 0, which is just a dummy V to get numbers aligned
        assertTrue( cliques.contains( bitSet("1110" ) ) ); // x1, x2, x3 //a, b, c
        assertTrue( cliques.contains( bitSet("10100" ) ) ); // x2, x4
        assertTrue( cliques.contains( bitSet("1100100" ) ) ); // x2, x5, x6
        assertTrue( cliques.contains( bitSet("101100" ) ) ); // x2, x3, x5
    }

    @Test
    public void testSepSetCompareWithDifferentMass() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);


        OpenBitSet OpenBitSet1_1 = bitSet("00001110");
        OpenBitSet OpenBitSet1_2 = bitSet("01101100");
        SeparatorSet s1 = new SeparatorSet( OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);

        OpenBitSet OpenBitSet2_1 = bitSet("00001110");
        OpenBitSet OpenBitSet2_2 = bitSet("00100100");
        SeparatorSet s2 = new SeparatorSet( OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);

        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        list.add( s1 );
        list.add( s2 );
        Collections.sort(list);

        assertEquals(s1, list.get( 0 ) );
    }

    @Test
    public void testSepSetCompareWithDifferentCost() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);

        x1.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{"a"}, new double[][]{{0.1 }}));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x3.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));

        OpenBitSet OpenBitSet1_1 = bitSet("00001110");
        OpenBitSet OpenBitSet1_2 = bitSet("01101100");
        SeparatorSet s1 = new SeparatorSet( OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);

        OpenBitSet OpenBitSet2_1 = bitSet("00001110");
        OpenBitSet OpenBitSet2_2 = bitSet("00100110");
        SeparatorSet s2 = new SeparatorSet( OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);

        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        list.add( s1 );
        list.add( s2 );
        Collections.sort( list );

        assertEquals(s1, list.get( 0 ) );

        // repeat, reversing the costs, to be sure no other factor is in play.
        x1.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{"a", "b"}, new double[][]{{0.1, 0.1}}));
        x3.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{"a"}, new double[][]{{0.1 }}));

        s1 = new SeparatorSet( OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);
        s2 = new SeparatorSet( OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);

        list = new ArrayList<SeparatorSet>();
        list.add( s1 );
        list.add( s2 );
        Collections.sort( list );

        assertEquals(s2, list.get( 0 ) ); // was s1 before
    }

    @Test
    public void testSepSetCompareWithSameIntersect() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);


        OpenBitSet OpenBitSet1_1 = bitSet("00001110");
        OpenBitSet OpenBitSet1_2 = bitSet("01000010");
        SeparatorSet s1 = new SeparatorSet( OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);

        OpenBitSet OpenBitSet2_1 = bitSet("00001110");
        OpenBitSet OpenBitSet2_2 = bitSet("01000100");
        SeparatorSet s2 = new SeparatorSet( OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);

        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        list.add( s1 );
        list.add( s2 );
        Collections.sort( list );

        assertEquals(s1, list.get( 0 ) );

        // reverse the bits, to show the arbitrary is deterministic
        OpenBitSet1_2 = bitSet("01000100");
        s1 = new SeparatorSet( OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);

        OpenBitSet2_2 = bitSet("01000010");
        s2 = new SeparatorSet( OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);

        list = new ArrayList<SeparatorSet>();
        list.add( s1 );
        list.add( s2 );
        Collections.sort( list );

        assertEquals(s2, list.get( 0 ) ); // was s1 before
    }


    @Test
    public void testJunctionTree() {
        Graph<BayesVariable> graph = new BayesNetwork();

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);

        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);

        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);

        OpenBitSet OpenBitSet1 = bitSet("00001110");
        OpenBitSet OpenBitSet2 = bitSet("00011100");
        OpenBitSet OpenBitSet3 = bitSet("00110000");
        OpenBitSet OpenBitSet4 = bitSet("01110000");

        List<OpenBitSet> cliques = new ArrayList<OpenBitSet>();
        cliques.add ( OpenBitSet1 );
        cliques.add ( OpenBitSet2 );
        cliques.add ( OpenBitSet3 );
        cliques.add ( OpenBitSet4 );

        List<SeparatorSet> separatorSets = new ArrayList<SeparatorSet>();
        for ( int i = 0; i < cliques.size(); i++ ) {
            OpenBitSet ci = cliques.get(i);
            for ( int j = i+1; j < cliques.size(); j++ ) {
                OpenBitSet cj = cliques.get(j);
                if ( ci.intersects( cj ) ) {
                    SeparatorSet separatorSet = new SeparatorSet( ci, 0, cj, 0, graph );
                }
            }
        }
        Collections.sort(separatorSets);
    }

    @Test
    public void testJunctionTreeNoPruning() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);

        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = bitSet("00001111");
        OpenBitSet OpenBitSet2 = bitSet("00111100");
        OpenBitSet OpenBitSet3 = bitSet("11100000"); // linear

        OpenBitSet intersect1And2 = ((OpenBitSet)OpenBitSet2.clone());
        intersect1And2.and(OpenBitSet1);

        OpenBitSet intersect2And3 = ((OpenBitSet)OpenBitSet2.clone());
        intersect2And3.and(OpenBitSet3);

        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();


        assertEquals( OpenBitSet1, jtNode.getBitSet() );
        assertEquals( 1,  jtNode.getChildren().size() );
        JunctionTreeSeparator sep =  jtNode.getChildren().get(0);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet2, sep.getChild().getBitSet() );
        assertEquals( intersect1And2, sep.getBitSet() );

        jtNode = sep.getChild();
        assertEquals( OpenBitSet2, jtNode.getBitSet() );
        assertEquals( 1,  jtNode.getChildren().size() );
        sep =   jtNode.getChildren().get(0);
        assertEquals( OpenBitSet2, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet3, sep.getChild().getBitSet() );
        assertEquals( intersect2And3, sep.getBitSet() );
    }


    @Test
    public void testJunctionWithPruning1() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);

        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = bitSet("00001111");
        OpenBitSet OpenBitSet2 = bitSet("00111100");
        OpenBitSet OpenBitSet3 = bitSet("11100001"); // links to 2 and 1, but should still result in a single path. As the 3 -> 1 link, gets pruned

        OpenBitSet intersect1And2 = ((OpenBitSet)OpenBitSet2.clone());
        intersect1And2.and(OpenBitSet1);

        OpenBitSet intersect2And3 = ((OpenBitSet)OpenBitSet2.clone());
        intersect2And3.and(OpenBitSet3);

        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();


        assertEquals( OpenBitSet1, jtNode.getBitSet() );
        assertEquals( 2,  jtNode.getChildren().size() );
        JunctionTreeSeparator sep =  jtNode.getChildren().get(0);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet2, sep.getChild().getBitSet() );
        assertEquals(0, sep.getChild().getChildren().size());

        sep =  jtNode.getChildren().get(1);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet3, sep.getChild().getBitSet() );
        assertEquals( 0, sep.getChild().getChildren().size() );

    }

    @Test
    public void testJunctionWithPruning2() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);

        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = bitSet("00001111");
        OpenBitSet OpenBitSet2 = bitSet("00111100");
        OpenBitSet OpenBitSet3 = bitSet("11100000");
        OpenBitSet OpenBitSet4 = bitSet("00100001");

        OpenBitSet intersect1And2 = ((OpenBitSet)OpenBitSet2.clone());
        intersect1And2.and(OpenBitSet1);

        OpenBitSet intersect2And3 = ((OpenBitSet)OpenBitSet2.clone());
        intersect2And3.and(OpenBitSet3);

        OpenBitSet intersect1And4 = ((OpenBitSet)OpenBitSet1.clone());
        intersect1And4.and(OpenBitSet4);

        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);
        list.add(OpenBitSet4);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();
        JunctionTreeClique root = jtNode;


        assertEquals( OpenBitSet1, root.getBitSet() );
        assertEquals( 2,  root.getChildren().size() );
        JunctionTreeSeparator sep =  root.getChildren().get(0);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet2, sep.getChild().getBitSet() );
        assertEquals(1, sep.getChild().getChildren().size());

        jtNode = sep.getChild();
        assertEquals( OpenBitSet2, jtNode.getBitSet() );
        assertEquals( 1,  jtNode.getChildren().size() );
        sep =   jtNode.getChildren().get(0);
        assertEquals( OpenBitSet2, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet3, sep.getChild().getBitSet() );
        assertEquals( intersect2And3, sep.getBitSet() );
        assertEquals( 0, sep.getChild().getChildren().size());

        sep =  root.getChildren().get(1);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet4, sep.getChild().getBitSet() );
        assertEquals( intersect1And4, sep.getBitSet() );
        assertEquals( 0, sep.getChild().getChildren().size() );
    }

    @Test
    public void testJunctionWithPruning3() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);

        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = bitSet("00001111");
        OpenBitSet OpenBitSet2 = bitSet("00011110");
        OpenBitSet OpenBitSet3 = bitSet("11100000");
        OpenBitSet OpenBitSet4 = bitSet("01100001");

        OpenBitSet intersect1And2 = ((OpenBitSet)OpenBitSet2.clone());
        intersect1And2.and(OpenBitSet1);

        OpenBitSet intersect2And3 = ((OpenBitSet)OpenBitSet2.clone());
        intersect2And3.and(OpenBitSet3);

        OpenBitSet intersect1And4 = ((OpenBitSet)OpenBitSet1.clone());
        intersect1And4.and(OpenBitSet4);

        OpenBitSet intersect3And4 = ((OpenBitSet)OpenBitSet3.clone());
        intersect3And4.and(OpenBitSet4);

        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);
        list.add(OpenBitSet4);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();;
        JunctionTreeClique root = jtNode;

        assertEquals( OpenBitSet1, root.getBitSet() );
        assertEquals( 2,  root.getChildren().size() );
        JunctionTreeSeparator sep =  root.getChildren().get(0);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet2, sep.getChild().getBitSet() );
        assertEquals(0, sep.getChild().getChildren().size());

        sep =  root.getChildren().get(1);
        assertEquals( OpenBitSet1, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet4, sep.getChild().getBitSet() );
        assertEquals( intersect1And4, sep.getBitSet() );
        assertEquals( 1, sep.getChild().getChildren().size() );

        jtNode = sep.getChild();
        assertEquals( OpenBitSet4, jtNode.getBitSet() );
        assertEquals( 1,  jtNode.getChildren().size() );
        sep =   jtNode.getChildren().get(0);
        assertEquals( OpenBitSet4, sep.getParent().getBitSet() );
        assertEquals( OpenBitSet3, sep.getChild().getBitSet() );
        assertEquals( intersect3And4, sep.getBitSet() );
        assertEquals( 0, sep.getChild().getChildren().size() );
    }


    @Test
    public void testMapNodeToCliques() {
        Graph<BayesVariable> graph = new BayesNetwork();
        JunctionTreeBuilder tbuilder = new JunctionTreeBuilder(graph);

        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);
        GraphNode x4 = addNode(graph);
        GraphNode x5 = addNode(graph);
        GraphNode x6 = addNode(graph);
        GraphNode x7 = addNode(graph);

        OpenBitSet clique0 = bitSet("01010101");
        OpenBitSet clique1 = bitSet("10010001");
        OpenBitSet clique2 = bitSet("10111010");

        OpenBitSet[] nodeToCliques = new OpenBitSet[8];
        tbuilder.mapVarNodeToCliques(nodeToCliques, 0, clique0);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 1, clique1);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 2, clique2);

        assertEquals( bitSet("011"), nodeToCliques[0] );
        assertEquals( bitSet("100"), nodeToCliques[1] );
        assertEquals( bitSet("001"), nodeToCliques[2] );
        assertEquals( bitSet("100"), nodeToCliques[3] );
        assertEquals( bitSet("111"), nodeToCliques[4] );
        assertEquals( bitSet("100"), nodeToCliques[5] );
        assertEquals( bitSet("001"), nodeToCliques[6] );
        assertEquals( bitSet("110"), nodeToCliques[7] );
    }


    @Test
    public void testMapNodeToClique() {
        Graph<BayesVariable> graph = new BayesNetwork();
        JunctionTreeBuilder tbuilder = new JunctionTreeBuilder(graph);

        GraphNode<BayesVariable> x0 = addNode(graph);
        GraphNode<BayesVariable> x1 = addNode(graph);
        GraphNode<BayesVariable> x2 = addNode(graph);
        GraphNode<BayesVariable> x3 = addNode(graph);
        GraphNode<BayesVariable> x4 = addNode(graph);
        GraphNode<BayesVariable> x5 = addNode(graph);
        GraphNode<BayesVariable> x6 = addNode(graph);
        GraphNode<BayesVariable> x7 = addNode(graph);

        connectChildToParents(x1, x2, x3);
        connectChildToParents(x3, x6, x7);

        OpenBitSet clique0 = bitSet("01001110");
        OpenBitSet clique1 = bitSet("11001110");
        OpenBitSet clique2 = bitSet("11101000");
        OpenBitSet clique3 = bitSet("00010011");

        JunctionTreeClique jtNode0 = new JunctionTreeClique(0, graph, clique0);
        JunctionTreeClique jtNode1 = new JunctionTreeClique(1, graph, clique1);
        JunctionTreeClique jtNode2 = new JunctionTreeClique(2, graph, clique2);
        JunctionTreeClique jtNode3 = new JunctionTreeClique(3, graph, clique3);
        JunctionTreeClique[] jtNodes  = new JunctionTreeClique[] { jtNode0, jtNode1, jtNode2, jtNode3};

        OpenBitSet[] nodeToCliques = new OpenBitSet[8];
        tbuilder.mapVarNodeToCliques(nodeToCliques, 0, clique0);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 1, clique1);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 2, clique2);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 3, clique3);

        //int[] nodeToClique = new int[8];
        tbuilder.mapNodeToCliqueFamily(nodeToCliques, jtNodes);

        assertEquals( 3, x0.getContent().getFamily() );
        assertEquals( 0, x1.getContent().getFamily() );
        assertEquals( 0, x2.getContent().getFamily() );
        assertEquals( 2, x3.getContent().getFamily() );
        assertEquals( 3, x4.getContent().getFamily() );
        assertEquals( 2, x5.getContent().getFamily() );
        assertEquals( 0, x6.getContent().getFamily() );
        assertEquals( 2, x7.getContent().getFamily() );
    }

    @Test
    public void testFullExample1() {
        // from "Bayesian Belief Network Propagation Engine In Java"
        // the result here is slightly different, due to ordering, but it's still correct.
        // http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.7921&rep=rep1&type=pdf
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode xa = addNode(graph);
        GraphNode xb = addNode(graph);
        GraphNode xc = addNode(graph);
        GraphNode xd = addNode(graph);
        GraphNode xe = addNode(graph);
        GraphNode xf = addNode(graph);
        GraphNode xg = addNode(graph);
        GraphNode xh = addNode(graph);

        connectParentToChildren(xa, xb, xc);
        connectParentToChildren(xb, xd);
        connectParentToChildren(xc, xe, xg);
        connectParentToChildren(xd, xf);
        connectParentToChildren(xe, xf, xh);
        connectParentToChildren(xg, xh);

        OpenBitSet clique1 = bitSet("00111000"); // d, e, f
        OpenBitSet clique2 = bitSet("00011100"); // c, d, e
        OpenBitSet clique3 = bitSet("01010100"); // c, e, g
        OpenBitSet clique4 = bitSet("11010000"); // e, g, h
        OpenBitSet clique5 = bitSet("00001110"); // b, c, d
        OpenBitSet clique6 = bitSet("00000111"); // a, b, c

        OpenBitSet clique1And2 = bitSet("00011000"); // d, e
        OpenBitSet clique2And3 = bitSet("00010100"); // c, e
        OpenBitSet clique2And5 = bitSet("00001100"); // c, d
        OpenBitSet clique3And4 = bitSet("01010000"); // e, g
        OpenBitSet clique5And6 = bitSet("00000110"); // b, c

        // clique1
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTreeClique root = jtBuilder.build(false).getRoot();
        assertEquals( clique1, root.getBitSet() );
        assertEquals(1, root.getChildren().size());

        // clique2
        JunctionTreeSeparator sep =  root.getChildren().get(0);
        assertEquals( clique1And2, sep.getBitSet() );
        JunctionTreeClique jtNode2 = sep.getChild();
        assertEquals( clique1, sep.getParent().getBitSet() );
        assertEquals( clique2, jtNode2.getBitSet() );
        assertEquals(2, jtNode2.getChildren().size());

        // clique3
        sep =  jtNode2.getChildren().get(0);
        assertEquals( clique2And3, sep.getBitSet() );
        JunctionTreeClique jtNode3 =sep.getChild();
        assertEquals( clique2, sep.getParent().getBitSet() );
        assertEquals( clique3, jtNode3.getBitSet() );
        assertEquals( 1, jtNode3.getChildren().size());

        // clique4
        sep =  jtNode3.getChildren().get(0);
        assertEquals( clique3And4, sep.getBitSet() );
        JunctionTreeClique jtNode4 = sep.getChild();
        assertEquals( clique3, sep.getParent().getBitSet() );
        assertEquals( clique4, jtNode4.getBitSet() );
        assertEquals( 0, jtNode4.getChildren().size());

        // clique5
        sep =  jtNode2.getChildren().get(1);
        assertEquals( clique2And5, sep.getBitSet() );
        JunctionTreeClique jtNode5 = sep.getChild();
        assertEquals( clique2, sep.getParent().getBitSet() );
        assertEquals( clique5, jtNode5.getBitSet() );
        assertEquals( 1, jtNode5.getChildren().size());

        //clique 6
        sep =  jtNode5.getChildren().get(0);
        assertEquals( clique5And6, sep.getBitSet() );
        JunctionTreeClique jtNode6 = sep.getChild();
        assertEquals( clique5, sep.getParent().getBitSet() );
        assertEquals( clique6, jtNode6.getBitSet() );
        assertEquals( 0, jtNode6.getChildren().size());
    }

    @Test
    public void testFullExample2() {
        // Bayesian Networks -  A Self-contained introduction with implementation remarks
        // http://www.mathcs.emory.edu/~whalen/Papers/BNs/Intros/BayesianNetworksTutorial.pdf
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode xElectricity = addNode(graph);   // 0
        GraphNode xTelecom = addNode(graph);       // 1
        GraphNode xRail = addNode(graph);          // 2
        GraphNode xAirTravel = addNode(graph);     // 3
        GraphNode xTransportation = addNode(graph);// 4
        GraphNode xUtilities = addNode(graph);     // 5
        GraphNode xUSBanks = addNode(graph);       // 6
        GraphNode xUSStocks = addNode(graph);      // 7

        connectParentToChildren( xElectricity, xRail, xAirTravel, xUtilities, xTelecom );
        connectParentToChildren( xTelecom, xUtilities, xUSBanks );
        connectParentToChildren( xRail, xTransportation );
        connectParentToChildren( xAirTravel, xTransportation );
        connectParentToChildren( xUtilities, xUSStocks );
        connectParentToChildren( xUSBanks, xUSStocks );
        connectParentToChildren( xTransportation, xUSStocks );


        OpenBitSet clique1 = bitSet("11110000"); // Utilities, Transportation, USBanks, UStocks
        OpenBitSet clique2 = bitSet("01110001"); // Electricity, Transportation, Utilities, USBanks
        OpenBitSet clique3 = bitSet("01100011"); // Electricity, Telecom, Utilities, USBanks
        OpenBitSet clique4 = bitSet("00011101"); // Electricity, Rail, AirTravel, Transportation

        OpenBitSet clique1And2 = bitSet("01110000"); // Utilities, Transportation, USBanks
        OpenBitSet clique2And3 = bitSet("01100001"); // Electricity, Utilities, USBanks
        OpenBitSet clique2And4 = bitSet("00010001"); // Electricity, Transportation


        xElectricity.setContent(new BayesVariable<String>("Electricity", xElectricity.getId(),
                                                          new String[]{"Working", "Reduced", "NotWorking"}, new double[][]{{0.6, 0.3, 0.099}}));

        xTelecom.setContent(new BayesVariable<String>("Telecom", xTelecom.getId(),
                                                      new String[]{"Working", "Reduced", "NotWorking"}, new double[][]{{0.544, 0.304, 0.151}}));

        xRail.setContent(new BayesVariable<String>("Rail", xRail.getId(),
                                                   new String[]{"Working", "Reduced", "NotWorking"}, new double[][]{{0.579, 0.230, 0.190}}));

        xAirTravel.setContent(new BayesVariable<String>("AirTravel", xAirTravel.getId(),
                                                        new String[]{"Working", "Reduced", "NotWorking"}, new double[][]{{0.449, 0.330, 0.219}}));

        xTransportation.setContent(new BayesVariable<String>("Transportation", xTransportation.getId(),
                                                             new String[]{"Working", "Moderate", "Severe", "Failure"}, new double[][]{{0.658, 0.167, 0.097, 0.077}}));

        xUtilities.setContent(new BayesVariable<String>("Utilities", xUtilities.getId(),
                                                        new String[]{"Working", "Moderate", "Severe", "Failure"}, new double[][]{{0.541, 0.272, 0.097, 0.088}}));

        xUSBanks.setContent(new BayesVariable<String>("USBanks", xUSBanks.getId(),
                                                      new String[]{"Working", "Reduced", "NotWorking"}, new double[][]{{0.488, 0.370, 0.141}}));

        xUSStocks.setContent(new BayesVariable<String>("USStocks", xUSStocks.getId(),
                                                       new String[]{"Up", "Down", "Crash"}, new double[][]{{0.433, 0.386, 0.179}}));

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTreeClique root = jtBuilder.build(false).getRoot();

        // clique1
        assertEquals( clique1, root.getBitSet() );
        assertEquals( 1, root.getChildren().size() );

        // clique2
        JunctionTreeSeparator sep =  root.getChildren().get(0);
        assertEquals( clique1And2, sep.getBitSet() );
        JunctionTreeClique jtNode2 = sep.getChild();
        assertEquals( clique1, sep.getParent().getBitSet() );
        assertEquals( clique2, jtNode2.getBitSet() );
        assertEquals(2, jtNode2.getChildren().size());

        // clique3
        assertSame( sep, jtNode2.getParentSeparator() );
        sep =  jtNode2.getChildren().get(0);
        assertEquals( clique2And3, sep.getBitSet() );
        JunctionTreeClique jtNode3 = sep.getChild();
        assertEquals( clique2, sep.getParent().getBitSet() );
        assertEquals( clique3, jtNode3.getBitSet() );
        assertEquals(0, jtNode3.getChildren().size());

        // clique4
        sep =  jtNode2.getChildren().get(1);
        assertEquals( clique2And4, sep.getBitSet() );
        JunctionTreeClique jtNode4 = sep.getChild();
        assertEquals( clique2, sep.getParent().getBitSet() );
        assertEquals( clique4, jtNode4.getBitSet() );
        assertEquals(0, jtNode4.getChildren().size());
    }

}
