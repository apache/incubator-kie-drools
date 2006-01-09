package org.drools.reteoo;

/*
 * $Id: ReteooToJungVisitor.java,v 1.11 2005/02/02 00:23:22 mproctor Exp $
 *
 * Copyright 2004-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.drools.ReteooJungViewer.HtmlContent;

/**
 * Produces a graph in GraphViz DOT format.
 *
 * @see http://www.research.att.com/sw/tools/graphviz/
 * @see http://www.pixelglow.com/graphviz/
 *
 * @author Andy Barnett
 */
public class ReteooToJungVisitor extends ReflectiveVisitor {
    /** String displayed for Null values. */
    private static final String NULL_STRING  = "<NULL>";

    /** Amount of indention for Node and Edge lines. */
    private static final String INDENT       = "    ";

    /**
     * Keeps track of visited JoinNode DOT IDs. This mapping allows the visitor
     * to recognize JoinNodes it has already visited and as a consequence link
     * existing nodes back together. This is vital to the Visitor being able to
     * link two JoinNodeInputs together through their common JoinNode.
     */
    private Map                 visitedNodes = new HashMap();

    Graph                       graph;

    VisualizationViewer         vv;

    Vertex                      rootVertex;

    Vertex                      parentVertex;

    /**
     * Constructor.
     */
    public ReteooToJungVisitor() {
    }

    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Default visitor if an unknown object is visited.
     */
    public void visitObject(Object object) {
        Vertex vertex = this.graph.addVertex( new UnkownVertex() );
        this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                    vertex ) );
    }

    /**
     * Null visitor if a NULL object gets visited. Unique String objects are
     * generated to ensure every NULL object is distinct.
     */
    public void visitNull() {
        Vertex vertex = this.graph.addVertex( new UnkownVertex() );
        this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                    vertex ) );
    }

    /**
     * RuleBaseImpl visits its Rete.
     */
    public void visitRuleBaseImpl(RuleBaseImpl ruleBase) {
        visit( ((RuleBaseImpl) ruleBase).getRete() );
    }

    /**
     * Rete visits each of its ObjectTypeNodes.
     */
    public void visitRete(Rete rete) {
        this.rootVertex = (ReteNodeVertex) this.visitedNodes.get( dotId( rete ) );
        if ( this.rootVertex == null ) {
            this.rootVertex = new ReteNodeVertex( rete );
            this.visitedNodes.put( dotId( rete ),
                                   this.rootVertex );
        }

        this.graph = new DirectedSparseGraph();
        this.graph.addVertex( this.rootVertex );
        this.parentVertex = this.rootVertex;
        for ( Iterator i = rete.getObjectTypeNodeIterator(); i.hasNext(); ) {
            Object nextNode = i.next();
            visitNode( nextNode );
        }
    }

    /**
     * ObjectTypeNode displays its objectType and then visits each of its
     * ParameterNodes.
     */
    public void visitObjectTypeNode(ObjectTypeNode node) {
        Vertex vertex = (ObjectTypeNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new ObjectTypeNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            //makeNode( node,
            //          "ObjectTypeNode",
            //          "objectType: " + node.getObjectType( ) );
            for ( Iterator i = node.getObjectSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }

    public void visitAlphaNode(AlphaNode node) {
        Vertex vertex = (AlphaNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new AlphaNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            for ( Iterator i = node.getObjectSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }

    public void visitRightInputAdapterNode(RightInputAdapterNode node) {
        Vertex vertex = (RightInputAdapterNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new RightInputAdapterNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            for ( Iterator i = node.getObjectSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }
    
    public void visitLeftInputAdapterNode(LeftInputAdapterNode node) {
        Vertex vertex = (LeftInputAdapterNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new  LeftInputAdapterNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            for ( Iterator i = node.getTupleSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }

    public void visitJoinNode(JoinNode node) {
        Vertex vertex = (JoinNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new  JoinNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            for ( Iterator i = node.getTupleSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }

    public void visitNotNode(NotNode node) {
        Vertex vertex = (NotNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new  NotNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            for ( Iterator i = node.getTupleSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }

    public void visitTestNode(TestNode node) {
        Vertex vertex = (TestNodeVertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            vertex = new  TestNodeVertex( node );
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            for ( Iterator i = node.getTupleSinks().iterator(); i.hasNext(); ) {
                Object nextNode = i.next();
                visitNode( nextNode );
            }
            this.parentVertex = oldParentVertex;            
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );            
        }
    }

    //    /**
    //     * ConditionNode displays its condition and tuple Declarations and then
    //     * visits its TupleSink.
    //     */
    //    public void visitConditionNode(ConditionNode node)
    //    {
    //        makeTupleSourceNode( node,
    //                  "ConditionNode",
    //                  "TupleSource/TupleSink",
    //                  "condition: " + node.getCondition( ) + newline + format( node.getTupleDeclarations( ),
    //                                                                           "tuple" ) );
    //    }

    /**
     * TerminalNode displays its rule.
     */
    public void visitTerminalNode(TerminalNode node) {
        Vertex vertex = this.graph.addVertex( new TerminalNodeVertex( node ) );
        this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                    vertex ) );

        //        makeNode( node,
        //                  "TerminalNode",
        //                  "TupleSink",
        //                  "rule: " + node.getRule( ).getName( ) );
    }

    /**
     * Helper method to ensure nodes are not visited more than once.
     */
    private void visitNode(Object node) {
        visit( node );
    }

    /**
     * The identity hashCode for the given object is used as its unique DOT
     * identifier.
     */
    private static String dotId(Object object) {
        return Integer.toHexString( System.identityHashCode( object ) ).toUpperCase();
    }

    class DroolsDirectedEdge extends DirectedSparseEdge {
        public DroolsDirectedEdge(Vertex v1,
                                  Vertex v2) {
            super( v1,
                   v2 );
        }

        //        public String toString() {
        //            return null;
        //        }
    }

    class ReteNodeVertex extends ReteooNodeVertex {
        private final Rete node;

        public ReteNodeVertex(Rete node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "Rete : " + this.node.getId();
        }

        public String toString() {
            return "Rete";
        }
    }

    class ObjectTypeNodeVertex extends ReteooNodeVertex {
        private final ObjectTypeNode node;

        public ObjectTypeNodeVertex(ObjectTypeNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "ObjectTypeNode : " + this.node.getId();
        }

        public String toString() {
            return "ObjectTypeNode";
        }
    }

    class AlphaNodeVertex extends ReteooNodeVertex {
        private final AlphaNode node;

        public AlphaNodeVertex(AlphaNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "AlphaNode : " + this.node.getId();
        }

        public String toString() {
            return "AlphaNode";
        }
    }

    class LeftInputAdapterNodeVertex extends ReteooNodeVertex {
        private final LeftInputAdapterNode node;

        public LeftInputAdapterNodeVertex(LeftInputAdapterNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "LeftInputAdapterNode : " + this.node.getId();
        }

        public String toString() {
            return "leftInputAdapter";
        }
    }

    class RightInputAdapterNodeVertex extends ReteooNodeVertex {
        private final RightInputAdapterNode node;

        public RightInputAdapterNodeVertex(RightInputAdapterNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "RightInputAdapterNodeVertex : " + this.node.getId();
        }

        public String toString() {
            return "RightInputAdapterNodeVertex";
        }
    }

    class JoinNodeVertex extends ReteooNodeVertex {
        private final JoinNode node;

        public JoinNodeVertex(JoinNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "JoinNode : " + this.node.getId();
        }

        public String toString() {
            return "JoinNode";
        }
    }

    class NotNodeVertex extends ReteooNodeVertex {
        private final NotNode node;

        public NotNodeVertex(NotNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "NotNode : " + this.node.getId();
        }

        public String toString() {
            return "NotNode";
        }
    }

    class TestNodeVertex extends ReteooNodeVertex {
        private final TestNode node;

        public TestNodeVertex(TestNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "TestNode : " + this.node.getId();
        }

        public String toString() {
            return "TestNode";
        }
    }

    class TerminalNodeVertex extends ReteooNodeVertex {
        private final TerminalNode node;

        public TerminalNodeVertex(TerminalNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "TerminalNode : " + this.node.getId();
        }

        public String toString() {
            return "TerminalNode";
        }
    }

    class UnkownVertex extends ReteooNodeVertex {

        public UnkownVertex() {
            super();
        }

        public String getHtml() {
            return "Uknown";
        }

        public String toString() {
            return "Unknown";
        }
    }

    public static abstract class ReteooNodeVertex extends DirectedSparseVertex
        implements
        HtmlContent {
        public ReteooNodeVertex() {
            super();
        }

        public abstract String getHtml();
    }
}
