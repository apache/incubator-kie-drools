package org.drools.visualize;

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.drools.RuleBase;
import org.drools.reteoo.ReteooToJungVisitor;
import org.drools.visualize.ReteooJungViewer.DroolsVertex;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.CrossoverScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ViewScalingGraphMousePlugin;

public class ReteooJungViewerPanel extends JPanel {

    private static final long   serialVersionUID = 73294554831916314L;

    /**
     * the graph
     */
    private Graph               graph;

    /**
     * the visual component and renderer for the graph
     */
    private VisualizationViewer vv;

    public ReteooJungViewerPanel(final RuleBase ruleBase) {
        setLayout( new BorderLayout() );
        // Setup a standard left/right splitPane
        final JPanel leftPanel = new JPanel( new BorderLayout() );
        final JPanel rightPanel = new JPanel( new BorderLayout() );
        final JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                                     leftPanel,
                                                     rightPanel );
        splitPane.setDividerLocation( 0.75 );
        splitPane.setResizeWeight( 1 );
        add( splitPane );

        // Create the graph and parse it to the visitor where it will parse the rulebase and attach the vertices
        this.graph = new DirectedSparseGraph();
        final ReteooToJungVisitor visitor = new ReteooToJungVisitor( this.graph );
        visitor.visit( ruleBase );

        final PluggableRenderer pr = new PluggableRenderer();

        pr.setEdgeShapeFunction( new EdgeShape.QuadCurve() );

        pr.setVertexPaintFunction( new VertexPaintFunction() {
            public Paint getFillPaint(final Vertex v) {
                return ((DroolsVertex) v).getFillPaint();
            }

            public Paint getDrawPaint(final Vertex v) {
                return ((DroolsVertex) v).getDrawPaint();
            }
        } );

        pr.setEdgePaintFunction( new PickableEdgePaintFunction( pr,
                                                                Color.black,
                                                                Color.cyan ) );
        pr.setGraphLabelRenderer( new DefaultGraphLabelRenderer( Color.cyan,
                                                                 Color.cyan ) );

        // Sets the size of the nodes
        pr.setVertexShapeFunction( new EllipseVertexShapeFunction( new ConstantVertexSizeFunction( 14 ),
                                                                   new ConstantVertexAspectRatioFunction( 1.0f ) ) );

        final ReteooLayoutSolver solver = new ReteooLayoutSolver( visitor.getRootVertex() );

        final Layout layout = new ReteooLayout( this.graph,
                                                new VertexFunctions(),
                                                solver.getRowList() );
        /*
         Layout layout = new DAGLayout( this.graph );
         */

        this.vv = new VisualizationViewer( layout,
                                           pr );

        this.vv.setBackground( Color.white );
        this.vv.setPickSupport( new ShapePickSupport() );
        this.vv.setToolTipFunction( new DefaultToolTipFunction() );

        final PluggableGraphMouse graphMouse = new PluggableGraphMouse();
        graphMouse.add( new PickingGraphMousePlugin() );
        graphMouse.add( new ViewScalingGraphMousePlugin() );
        graphMouse.add( new CrossoverScalingGraphMousePlugin() );
        graphMouse.add( new RotatingGraphMousePlugin() );

        this.vv.setGraphMouse( graphMouse );

        final ScalingControl scaler = new CrossoverScalingControl();

        final JButton plus = new JButton( "+" );
        plus.addActionListener( new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                scaler.scale( ReteooJungViewerPanel.this.vv,
                              1.1f,
                              ReteooJungViewerPanel.this.vv.getCenter() );
            }
        } );
        final JButton minus = new JButton( "-" );
        minus.addActionListener( new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                scaler.scale( ReteooJungViewerPanel.this.vv,
                              0.9f,
                              ReteooJungViewerPanel.this.vv.getCenter() );
            }
        } );

        final GraphZoomScrollPane graphPanel = new GraphZoomScrollPane( this.vv );
        graphPanel.scrollRectToVisible( new Rectangle( 1,
                                                       1,
                                                       1,
                                                       1 ) );
        leftPanel.add( graphPanel );

        // Add the zoom controls
        final JPanel scaleGrid = new JPanel( new GridLayout( 1,
                                                             0 ) );
        scaleGrid.setBorder( BorderFactory.createTitledBorder( "Zoom" ) );
        final JPanel controls = new JPanel();
        scaleGrid.add( plus );
        scaleGrid.add( minus );
        controls.add( scaleGrid );
        leftPanel.add( controls,
                       BorderLayout.SOUTH );

        final JEditorPane infoPane = new JEditorPane();
        infoPane.setEditable( false );
        infoPane.setContentType( "text/html" );

        //        Put the editor pane in a scroll pane.
        final JScrollPane infoScrollPane = new JScrollPane( infoPane );
        infoScrollPane.setPreferredSize( new Dimension( 150,
                                                        10 ) );
        infoScrollPane.setMinimumSize( new Dimension( 150,
                                                      10 ) );

        // Add a mouse listener to update the info panel when a node is clicked
        this.vv.addGraphMouseListener( new GraphMouseListener() {

            public void graphClicked(final Vertex vertex,
                                     final MouseEvent e) {
                infoPane.setText( ((DroolsVertex) vertex).getHtml() );
            }

            public void graphPressed(final Vertex vertex,
                                     final MouseEvent e) {
            }

            public void graphReleased(final Vertex vertex,
                                      final MouseEvent e) {
            }

        } );

        rightPanel.add( infoScrollPane );
    }
}