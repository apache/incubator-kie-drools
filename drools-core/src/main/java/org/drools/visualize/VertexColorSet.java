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