package org.drools.reteoo;

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

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassFieldExtractor;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldValue;
import org.drools.util.ReflectiveVisitor;
import org.drools.visualize.ReteooJungViewer.DroolsVertex;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

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

    private Graph               graph;

    private Vertex              rootVertex;

    private Vertex              parentVertex;

    /**
     * Constructor.
     */
    public ReteooToJungVisitor(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public Vertex getRootVertex() {
        return this.rootVertex;
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

        this.graph.addVertex( this.rootVertex );
        this.parentVertex = this.rootVertex;
        for ( Iterator i = rete.objectTypeNodeIterator(); i.hasNext(); ) {
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
                throw new RuntimeException( "problem visiting node " + node.getClass().getName(),
                                            e );
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
                list = ((ObjectSource) node).getObjectSinksAsList();
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
            LiteralConstraint constraint = (LiteralConstraint) node.getConstraint();
            ClassFieldExtractor extractor = ( ClassFieldExtractor ) constraint.getFieldExtractor();
            return "AlphaNode<br>field : " + extractor.getFieldName() + "<br>evaluator : " + constraint.getEvaluator() + "<br>value :  " + constraint.getField();
        }

        public String toString() {
            return this.node.toString();
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
            return "LeftInputAdapterNode<br>" + dumpConstraints( this.node.getConstraints() );
        }

        public String toString() {
            return this.node.toString();
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
            return "RightInputAdapterNode";
        }

        public String toString() {
            return "RightInputAdapterNode";
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
            return "JoinNode<br> " + dumpConstraints( this.node.getConstraints() );
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

    static class EvalConditionNodeVertex extends BaseNodeVertex {
        private final EvalConditionNode node;

        public EvalConditionNodeVertex(EvalConditionNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "EvalConditionNode : " + this.node.getId();
        }

        public String toString() {
            return "EvalConditionNode";
        }
    }

    static class TerminalNodeVertex extends BaseNodeVertex {
        private final TerminalNode node;

        public TerminalNodeVertex(TerminalNode node) {
            super();
            this.node = node;
        }

        public String getHtml() {
            return "TerminalNode : " + this.node.getId() + " : " + this.node.getRule();
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
        DroolsVertex {
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
    
    public static String dumpConstraints(FieldConstraint[] constraints) {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0, length = constraints.length; i < length; i++ ) {
            buffer.append( constraints[i].toString() + "<br>" );
        }
        return buffer.toString();
    }
}
