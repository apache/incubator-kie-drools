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

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.ReteooJungViewer.HtmlContent;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.Field;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.VisualizationViewer;

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

    public void visitBaseNode(BaseNode node) {
        Vertex vertex = (Vertex) this.visitedNodes.get( dotId( node ) );
        if ( vertex == null ) {
            try {
                String name = node.getClass().getName();
                name = name.substring( name.lastIndexOf( '.' ) + 1 ) + "Vertex";
                Class clazz = Class.forName( "org.drools.reteoo.ReteooToJungVisitor$" + name );
                vertex = (Vertex) clazz.getConstructor( new Class[]{node.getClass()} ).newInstance( new Object[]{node} );
            } catch ( Exception e ) {
                throw new RuntimeException( "problem visiting node " + node.getClass().getName(), e);
            }
            this.graph.addVertex( vertex );
            this.visitedNodes.put( dotId( node ),
                                   vertex );
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
            Vertex oldParentVertex = this.parentVertex;
            this.parentVertex = vertex;

            List list = null;
            if ( node instanceof ObjectSource ) {
                list = ((ObjectSource) node).getObjectSinks();
            } else if ( node instanceof TupleSource ) {
                list = ((TupleSource) node).getTupleSinks();
            }

            if ( list != null ) {
                for ( Iterator it = list.iterator(); it.hasNext(); ) {
                    Object nextNode = it.next();
                    visitNode( nextNode );
                }
            }
            this.parentVertex = oldParentVertex;
        } else {
            this.graph.addEdge( new DroolsDirectedEdge( this.parentVertex,
                                                        vertex ) );
        }
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

    static class ReteNodeVertex extends BaseNodeVertex {
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

    static class ObjectTypeNodeVertex extends BaseNodeVertex {
        private final ObjectTypeNode node;

        public ObjectTypeNodeVertex(ObjectTypeNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "ObjectTypeNode : " + this.node.getObjectType();
        }

        public String toString() {
            return "ObjectTypeNode";
        }

        public Paint getFillPaint() {
            return Color.RED;
        }
    }

    static class AlphaNodeVertex extends BaseNodeVertex {
        private final AlphaNode node;

        public AlphaNodeVertex(AlphaNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            LiteralConstraint constraint = node.getConstraint();
            Field field = constraint.getField();
            return "AlphaNode<br>field name : " + field.getName() + "<br>evaluator : " + constraint.getEvaluator() + "<br>value :  " + field.getValue();  
        }

        public String toString() {
            return "AlphaNode";
        }
        
        public Paint getFillPaint() {
            return Color.BLUE;
        }        
    }

    static class LeftInputAdapterNodeVertex extends BaseNodeVertex {
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
        
        public Paint getFillPaint() {
            return Color.YELLOW;
        }        
    }

    static class RightInputAdapterNodeVertex extends BaseNodeVertex {
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
        
        public Paint getFillPaint() {
            return Color.ORANGE;
        }
    }

    static class JoinNodeVertex extends BaseNodeVertex {
        private final JoinNode node;

        public JoinNodeVertex(JoinNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            this.node.getJoinNodeBinder().
            return "JoinNode : " + this.node.getId();
        }

        public String toString() {
            return "JoinNode";
        }
        
        public Paint getFillPaint() {
            return Color.GREEN;
        }        
    }

    static class NotNodeVertex extends BaseNodeVertex {
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
        
        public Paint getFillPaint() {
            return Color.CYAN;
        }        
    }

    static class TestNodeVertex extends BaseNodeVertex {
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

    static class TerminalNodeVertex extends BaseNodeVertex {
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
        
        public Paint getFillPaint() {
            return Color.DARK_GRAY;
        }        
    }

    public static abstract class BaseNodeVertex extends DirectedSparseVertex
        implements
        HtmlContent {
        public BaseNodeVertex() {
            super();
        }

        public String getHtml() {
            return this.getClass().getName().toString();
        }
        
        public Paint getFillPaint() {
            return Color.WHITE;
        }

        public Paint getDrawPaint() {
            return Color.BLACK;
        }        
    }
}
