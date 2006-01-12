package org.drools.visualize;

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
	implements VertexStringer, VertexFontFunction, VertexShapeFunction, VertexPaintFunction, VertexLabelPaintFunction {
    private VertexColorSet defaultColors;
    
    private Graphics graphics;
    
    private Font font;
    
    public VertexFunctions() {
        BufferedImage image = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB );
        this.graphics   = image.createGraphics();
        this.defaultColors = new VertexColorSet();
        this.font = new Font( "Verdana", Font.BOLD, 10 );
    }
    
    public void setDefaultColors(Color fill, Color stroke, Color text) {
        this.defaultColors = new VertexColorSet( fill, stroke, text );
    }
    
    public void setFont(Font font) {
        this.font = font;
    }

    public Paint getLabelDrawPaint( Vertex vertex )
    {
        return defaultColors.getText();
    }

    public Font getFont( Vertex vertex ) {
        return this.font;
    }

    public Shape getShape( Vertex vertex ) {
        Dimension dim = getShapeDimension( vertex );
        
        return new RoundRectangle2D.Double( 0-(dim.width/2), 0-(dim.height/2), dim.width, dim.height, 10, 10 );
    }
    
    public Dimension getShapeDimension(Vertex vertex) {
        String label = getLabel( vertex );
        
        Font font = getFont( vertex );
        
        FontMetrics fm = graphics.getFontMetrics( getFont( vertex ) );
        
        int width = fm.stringWidth( label );
        int height = fm.getHeight();
        
        width = ( int ) (width + font.getSize());
        height = ( int ) (height + font.getSize());
        
        return new Dimension( width, height );
    }
    
    public Paint getFillPaint( Vertex vertex ) {
        return defaultColors.getFill();
    }

    public Paint getDrawPaint( Vertex vertex ) {
        return defaultColors.getStroke();
    }

	public String getLabel(ArchetypeVertex vertex) {
		return "node";
	}

}
