package org.drools.visualize;

import java.awt.Color;

public class VertexColorSet {

    private Color fill;
    private Color stroke;
    private Color text;

    public VertexColorSet(Color fill,
                          Color stroke,
                          Color text) {
        this.fill = fill;
        this.stroke = stroke;
        this.text = text;
    }

    public VertexColorSet() {
        this.fill = Color.white;
        this.stroke = Color.black;
        this.text = Color.black;
    }

    public Color getFill() {
        return this.fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public Color getStroke() {
        return this.stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke = stroke;
    }

    public Color getText() {
        return this.text;
    }

    public void setText(Color text) {
        this.text = text;
    }

}
