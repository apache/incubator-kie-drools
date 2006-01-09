package org.drools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.drools.reteoo.ReteooToJungVisitor;
import org.drools.reteoo.ReteooToJungVisitor.BaseNodeVertex;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseTree;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.DAGLayout;
import edu.uci.ics.jung.visualization.contrib.TreeLayout;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.CrossoverScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ViewScalingGraphMousePlugin;

import samples.graph.TreeLayoutDemo;

public class ReteooJungViewer extends JFrame {

    /**
     * the graph
     */
    Graph               graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;

    public static void createAndShowGUI(RuleBase ruleBase) {
        ReteooJungViewer viewer = new ReteooJungViewer( ruleBase );;
    }

    public ReteooJungViewer(RuleBase ruleBase) {
        // Setup a standard left/right splitPane
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                               leftPanel,
                                               rightPanel );
        splitPane.setDividerLocation( 0.75 );
        splitPane.setResizeWeight( 1 );
        getContentPane().add( splitPane );

        // Create the graph and parse it to the visitor where it will parse the rulebase and attach the vertices
        this.graph = new DirectedSparseGraph();
        ReteooToJungVisitor visitor = new ReteooToJungVisitor( this.graph );
        visitor.visit( ruleBase );

        final PluggableRenderer pr = new PluggableRenderer();
        
        pr.setEdgeShapeFunction( new EdgeShape.QuadCurve() );
        
        pr.setVertexPaintFunction( new VertexPaintFunction() {
            public Paint getFillPaint(Vertex v) {
                return ((DroolsVertex) v).getFillPaint();
            }

            public Paint getDrawPaint(Vertex v) {
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

        Layout layout = new DAGLayout( this.graph );

        this.vv = new VisualizationViewer( layout,
                                           pr,
                                           new Dimension( 600,
                                                          600 ) );

        this.vv.setBackground( Color.white );
        this.vv.setPickSupport( new ShapePickSupport() );
        this.vv.setToolTipFunction( new DefaultToolTipFunction() );

        final PluggableGraphMouse graphMouse = new PluggableGraphMouse();
        graphMouse.add( new PickingGraphMousePlugin() );
        graphMouse.add( new ViewScalingGraphMousePlugin() );
        graphMouse.add( new CrossoverScalingGraphMousePlugin() );

        this.vv.setGraphMouse( graphMouse );

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton( "+" );
        plus.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale( ReteooJungViewer.this.vv,
                              1.1f,
                              ReteooJungViewer.this.vv.getCenter() );
            }
        } );
        JButton minus = new JButton( "-" );
        minus.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale( ReteooJungViewer.this.vv,
                              0.9f,
                              ReteooJungViewer.this.vv.getCenter() );
            }
        } );

        final GraphZoomScrollPane graphPanel = new GraphZoomScrollPane( this.vv );
        leftPanel.add( graphPanel );

        // Add the zoom controls
        JPanel scaleGrid = new JPanel( new GridLayout( 1,
                                                       0 ) );
        scaleGrid.setBorder( BorderFactory.createTitledBorder( "Zoom" ) );
        JPanel controls = new JPanel();
        scaleGrid.add( plus );
        scaleGrid.add( minus );
        controls.add( scaleGrid );
        leftPanel.add( controls,
                       BorderLayout.SOUTH );

        final JEditorPane infoPane = new JEditorPane();
        infoPane.setEditable( false );
        infoPane.setContentType( "text/html" );

        //        Put the editor pane in a scroll pane.
        JScrollPane infoScrollPane = new JScrollPane( infoPane );
        infoScrollPane.setPreferredSize( new Dimension( 250,
                                                        600 ) );
        infoScrollPane.setMinimumSize( new Dimension( 50,
                                                      600 ) );

        // Add a mouse listener to update the info panel when a node is clicked
        this.vv.addGraphMouseListener( new GraphMouseListener() {

            public void graphClicked(Vertex vertex,
                                     MouseEvent e) {
                System.out.println( vertex );
                infoPane.setText( ((DroolsVertex) vertex).getHtml() );
            }

            public void graphPressed(Vertex vertex,
                                     MouseEvent e) {
            }

            public void graphReleased(Vertex vertex,
                                      MouseEvent e) {
            }

        } );

        rightPanel.add( infoScrollPane );

        pack();
        setVisible( true );
    }

    public interface DroolsVertex {
        public String getHtml();

        public Paint getFillPaint();

        public Paint getDrawPaint();
    }
}
