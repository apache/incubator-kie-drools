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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;

public class VertexFunctions
    implements
    VertexStringer,
    VertexFontFunction,
    VertexShapeFunction,
    VertexPaintFunction,
    VertexLabelPaintFunction {
    private VertexColorSet defaultColors;

    private Graphics       graphics;

    private Font           font;

    public VertexFunctions() {
        final BufferedImage image = new BufferedImage( 100,
                                                       100,
                                                       BufferedImage.TYPE_INT_RGB );
        this.graphics = image.createGraphics();
        this.defaultColors = new VertexColorSet();
        this.font = new Font( "Verdana",
                              Font.BOLD,
                              10 );
    }

    public void setDefaultColors(final Color fill,
                                 final Color stroke,
                                 final Color text) {
        this.defaultColors = new VertexColorSet( fill,
                                                 stroke,
                                                 text );
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    public Paint getLabelDrawPaint(final Vertex vertex) {
        return this.defaultColors.getText();
    }

    public Font getFont(final Vertex vertex) {
        return this.font;
    }

    public Shape getShape(final Vertex vertex) {
        final Dimension dim = getShapeDimension( vertex );

        return new RoundRectangle2D.Double( 0 - (dim.width / 2),
                                            0 - (dim.height / 2),
                                            dim.width,
                                            dim.height,
                                            10,
                                            10 );
    }

    public Dimension getShapeDimension(final Vertex vertex) {
        final String label = getLabel( vertex );

        final Font font = getFont( vertex );

        final FontMetrics fm = this.graphics.getFontMetrics( getFont( vertex ) );

        int width = fm.stringWidth( label );
        int height = fm.getHeight();

        width = (width + font.getSize());
        height = (height + font.getSize());

        return new Dimension( width,
                              height );
    }

    public Paint getFillPaint(final Vertex vertex) {
        return this.defaultColors.getFill();
    }

    public Paint getDrawPaint(final Vertex vertex) {
        return this.defaultColors.getStroke();
    }

    public String getLabel(final ArchetypeVertex vertex) {
        return "node";
    }

}