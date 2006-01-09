package org.drools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drools.reteoo.ReteooToJungVisitor;
import org.drools.reteoo.ReteooToJungVisitor.ReteooNodeVertex;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.PickableVertexPaintFunction;
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
    Graph          graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;

    //    public static void main(String[] args) {
    //        javax.swing.SwingUtilities.invokeLater(new Runnable() {
    //            public void run() {
    //                createAndShowGUI();
    //            }
    //        });
    //    }

    public static void createAndShowGUI(RuleBase ruleBase) {
        ReteooJungViewer viewer = new ReteooJungViewer(ruleBase);;
    }

    public ReteooJungViewer(RuleBase ruleBase) {
        ReteooToJungVisitor visitor = new ReteooToJungVisitor();
        visitor.visit( ruleBase );
        this.graph = visitor.getGraph();
        this.graph.g

        final PluggableRenderer pr = new PluggableRenderer();

        pr.setVertexPaintFunction( new PickableVertexPaintFunction( pr,
                                                                    Color.black,
                                                                    Color.white,
                                                                    Color.yellow ) );
        pr.setEdgePaintFunction( new PickableEdgePaintFunction( pr,
                                                                Color.black,
                                                                Color.cyan ) );
        pr.setGraphLabelRenderer( new DefaultGraphLabelRenderer( Color.cyan,
                                                                 Color.cyan ) );

        pr.setVertexShapeFunction( new EllipseVertexShapeFunction() );

        Layout layout = new DAGLayout( this.graph );

        this.vv = new VisualizationViewer( layout,
                                      pr,
                                      new Dimension( 600,
                                                     600 ) );
        this.vv.setPickSupport( new ShapePickSupport() );
        pr.setEdgeShapeFunction( new EdgeShape.QuadCurve() );
        this.vv.setBackground( Color.white );

        // add a listener for ToolTips
        this.vv.setToolTipFunction( new DefaultToolTipFunction() );

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane( this.vv );
        content.add( panel );

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

        JPanel scaleGrid = new JPanel( new GridLayout( 1,
                                                       0 ) );
        scaleGrid.setBorder( BorderFactory.createTitledBorder( "Zoom" ) );

        JPanel controls = new JPanel();
        scaleGrid.add( plus );
        scaleGrid.add( minus );
        controls.add( scaleGrid );
        content.add( controls,
                     BorderLayout.SOUTH );

        final JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable( false );
        editorPane.setContentType( "text/html" );

        this.vv.addGraphMouseListener( new GraphMouseListener() {

            public void graphClicked(Vertex vertex,
                                     MouseEvent e) {
                System.out.println( vertex );
                editorPane.setText( ((HtmlContent)vertex).getHtml() );
            }

            public void graphPressed(Vertex vertex,
                                     MouseEvent e) {
            }

            public void graphReleased(Vertex vertex,
                                      MouseEvent e) {
            }

        } );

        //        Put the editor pane in a scroll pane.
        JScrollPane editorScrollPane = new JScrollPane( editorPane );
        editorScrollPane.setPreferredSize( new Dimension( 250,
                                                          145 ) );
        editorScrollPane.setMinimumSize( new Dimension( 10,
                                                        10 ) );
        content.add( editorScrollPane,
                     BorderLayout.EAST );
        
        pack();
        setVisible( true ); 
    }
    
    public interface HtmlContent {
        public String getHtml();
    }
}
