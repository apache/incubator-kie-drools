package org.drools.compiler.xpath;

import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecursiveQueryBenchmark {

    private static final String RELATIONAL_DRL =
            "import " + Node.class.getCanonicalName() + ";\n" +
            "import " + Edge.class.getCanonicalName() + ";\n" +
            "import " + List.class.getCanonicalName() + ";\n" +
            "query findNodesWithValue( int $id, int $value, List list )\n" +
            "  $n: Node( id == $id, $v : value ) " +
            "  eval( $v != $value || ( $v == $value && list.add( $n ) ) )\n" +
            "  Edge( fromId == $id, $toId : toId ) " +
            "  findNodesWithValue( $toId, $value, list; )\n" +
            "end\n";

    private static final String RELATIONAL_DRL_OLD =
            "import " + Node.class.getCanonicalName() + ";\n" +
            "import " + Edge.class.getCanonicalName() + ";\n" +
            "query findNodesWithValue( int $fromId, int $toId, int $value )\n" +
            "  ( Edge( fromId == $fromId, toId == $toId ) and Node( id == $toId, value == $value ) )\n" +
            "  or\n" +
            "  ( Edge( fromId == $fromId, $childId : toId ) and findNodesWithValue( $childId, $toId, $value; ) )\n" +
            "end\n" +
            "\n" +
            "rule R when\n" +
            "  Node( root == true, $rootId : id )\n" +
            "  accumulate( findNodeWithValue($rootId, $nodeId, 0;) ; $result : count($nodeId) )\n" +
            "then\n" +
            "  System.out.println( $result );\n" +
            "end\n";

    private static final String FROM_DRL =
            "import " + Node.class.getCanonicalName() + ";\n" +
            "import " + Edge.class.getCanonicalName() + ";\n" +
            "import " + List.class.getCanonicalName() + ";\n" +
            "query findNodesWithValue( Node $from, int $value, List list )\n" +
            "  Edge( $n : to, $v : to.value ) from $from.outEdges\n" +
            "  eval( $v != $value || ( $v == $value && list.add( $n ) ) )\n" +
            "  findNodesWithValue( $n, $value, list; )\n" +
            "end\n";

    private static final String FROM_DRL_OLD =
            "import " + Node.class.getCanonicalName() + ";\n" +
            "import " + Edge.class.getCanonicalName() + ";\n" +
            "query findNodesWithValue( Node $from, Node $to, int $value )\n" +
            "  Edge( to.value == $value, $to := to ) from $from.outEdges\n" +
            "  or\n" +
            "  ( Edge( $child : to ) from $from.outEdges and findNodesWithValue( $child, $to, $value; ) )\n" +
            "end\n" +
            "\n" +
            "rule R when\n" +
            "  $root: Node( root == true )\n" +
            "  accumulate( findNodeWithValue($root, $node, 0;) ; $result : count($node) )\n" +
            "then\n" +
            "  System.out.println( $result );\n" +
            "end\n";

    private static final String XPATH_DRL =
            "import " + Node.class.getCanonicalName() + ";\n" +
            "import " + Edge.class.getCanonicalName() + ";\n" +
            "import " + List.class.getCanonicalName() + ";\n" +
            "query findNodesWithValue( Node $from, int $value, List list )\n" +
            "  Node( id == $from.id, $n: /outEdges/to )\n" +
            "  eval( $n.getValue() != $value || ( $n.getValue() == $value && list.add( $n ) ) )\n" +
            "  findNodesWithValue( $n, $value, list; )\n" +
            "end\n";

    private static final String XPATH_DRL_OLD =
            "import " + Node.class.getCanonicalName() + ";\n" +
            "import " + Edge.class.getCanonicalName() + ";\n" +
            "query findNodesWithValue( Node $from, Node $to, int $value )\n" +
            "  Node( this == $from, $to := /outEdges/to[value == $value] )\n" +
            "  or\n" +
            "  ( Node( this == $from, $child : /outEdges/to ) and findNodesWithValue( $child, $to, $value; ) )\n" +
            "end\n" +
            "\n" +
            "rule R when\n" +
            "  $root: Node( root == true )\n" +
            "  accumulate( findNodeWithValue($root, $node, 0;) ; $result : count($node) )\n" +
            "then\n" +
            "  System.out.println( $result );\n" +
            "end\n";

    public static void main( String[] args ) {
        int n = 1000;
        for (int i = 0; i < 5; i++) {
            System.out.println( "-------------------------------------" );
            System.out.println( "Running with " + n  + " nodes" );
            System.out.println( "Relational version" );
            runTest( new RelationalTest(), n );
            System.out.println( "From version" );
            runTest( new FromTest(), n );

            n *= 2;
            System.gc();
            try {
                Thread.sleep( 5000L );
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }
            System.gc();
        }
    }

    private static void runTest(Test test, int n) {
        KieBase kbase = getKieBase(test.getDrl());

        // warmup
        for (int i = 0; i < 3; i++) {
            test.runTest(kbase, n);
            System.gc();
        }

        BenchmarkResult batch = new BenchmarkResult("Batch");
        for (int i = 0; i < 10; i++) {
            long[] result = test.runTest(kbase, n);
            batch.accumulate(result[0]);
            System.gc();
        }

        System.out.println(batch);
    }

    private static KieBase getKieBase(String drl) {
        return new KieHelper().addContent(drl, ResourceType.DRL).build();
    }

    interface Test {
        long[] runTest(KieBase kbase, int n);
        String getDrl();
    }

    private static class RelationalTest implements Test {
        @Override
        public long[] runTest(KieBase kbase, int n) {
            return execTest(kbase, n, true);
        }

        @Override
        public String getDrl() {
            return RELATIONAL_DRL;
        }
    }

    private static class FromTest implements Test {
        @Override
        public long[] runTest(KieBase kbase, int n) {
            return execTest( kbase, n, false );
        }

        @Override
        public String getDrl() {
            return FROM_DRL;
        }
    }

    public static long[] execTest(KieBase kbase, int n, boolean isRelational) {
        KieSession ksession = kbase.newKieSession();

        Node root = generateTree( ksession, n, isRelational );

        List list = new ArrayList();
        long start = System.nanoTime();
        ksession.getQueryResults( "findNodesWithValue", isRelational ? root.getId() : root, 0, list );
        ksession.fireAllRules();
        long[] result = new long[]{ (System.nanoTime() - start) };
        //System.out.println( list.size() );

        ksession.dispose();

        return result;
    }

    private static Node generateTree( KieSession ksession, int n, boolean insertAll ) {
        final Random RANDOM = new Random(0);

        Node root = new Node(1);
        root.setRoot( true );
        ksession.insert( root );

        List<Node> nodes = new ArrayList<Node>(n);
        for (int i = 0; i < n; i++) {
            Node node = new Node(i / 10);
            nodes.add( node );
        }

        List<Node> nodesInTree = new ArrayList<Node>(n);
        nodesInTree.add( root );

        while (!nodes.isEmpty()) {
            Node parent = nodesInTree.get( RANDOM.nextInt( nodesInTree.size() ) );
            Node node = nodes.remove( RANDOM.nextInt( nodes.size() ) );
            Edge edge = new Edge( parent, node );
            parent.addOutEdge( edge );
            nodesInTree.add( node );

            if ( insertAll ) {
                ksession.insert( edge );
                ksession.insert( node );
            }
        }

        return root;
    }

    public static class Node {

        private static int ID_GENERATOR = 0;

        private final int id = ID_GENERATOR++;

        private final List<Edge> outEdges = new ArrayList<Edge>();

        private final int value;

        private boolean root;

        public Node( int value ) {
            this.value = value;
        }

        public List<Edge> getOutEdges() {
            return outEdges;
        }

        public void addOutEdge( Edge edge) {
            outEdges.add(edge);
        }

        public int getValue() {
            return value;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            Node node = (Node) o;

            return id == node.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        public boolean isRoot() {
            return root;
        }

        public void setRoot( boolean root ) {
            this.root = root;
        }

        @Override
        public String toString() {
            return "Node: " + id;
        }
    }

    public static class Edge {
        public final Node from;
        public final Node to;

        public Edge( Node from, Node to ) {
            this.from = from;
            this.to = to;
        }

        public Node getFrom() {
            return from;
        }

        public int getFromId() {
            return from.getId();
        }

        public Node getTo() {
            return to;
        }

        public int getToId() {
            return to.getId();
        }

        @Override
        public String toString() {
            return "Edge[" + getFromId() + ", " + getToId() + "]";
        }
    }

    public static class BenchmarkResult {
        private final String name;

        private long min = Long.MAX_VALUE;
        private long max = 0;
        private long sum = 0;
        private int counter = 0;

        public BenchmarkResult(String name) {
            this.name = name;
        }

        public void accumulate(long result) {
            if (result < min) {
                min = result;
            }
            if (result > max) {
                max = result;
            }
            sum += result;
            counter++;
        }

        private long getAverage() {
            return (sum - min - max) / (counter - 2);
        }

        @Override
        public String toString() {
            return name + " results: min = " + min + "; max = " + max + "; avg = " + getAverage();
        }
    }
}
