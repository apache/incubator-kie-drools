package org.drools.visualize;

import java.awt.Paint;

import edu.uci.ics.jung.graph.Vertex;

public interface VertexLabelPaintFunction {
    Paint getLabelDrawPaint(Vertex vertex);
}
