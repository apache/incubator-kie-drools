/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.CityAssignment;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;
import org.drools.planner.examples.tsp.solver.move.SubTourChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class TspWorldPanel extends JPanel {

    private final TspPanel tspPanel;

    private BufferedImage canvas = null;

    public TspWorldPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
    }

    public void resetPanel() {
        TravelingSalesmanTour travelingSalesmanTour = tspPanel.getTravelingSalesmanTour();
        double srcMinimumX = Double.MAX_VALUE;
        double srcMaximumX = -Double.MAX_VALUE;
        double srcMinimumY = Double.MAX_VALUE;
        double srcMaximumY = -Double.MAX_VALUE;
        for (City city : travelingSalesmanTour.getCityList()) {
            double x = city.getX();
            if (x < srcMinimumX) {
                srcMinimumX = x;
            }
            if (x > srcMaximumX) {
                srcMaximumX = x;
            }
            double y = city.getY();
            if (y < srcMinimumY) {
                srcMinimumY = y;
            }
            if (y > srcMaximumY) {
                srcMaximumY = y;
            }
        }
        double srcWidth = srcMaximumX - srcMinimumX;
        double srcHeight = srcMaximumY - srcMinimumY;

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        g.setColor(Color.RED);
        for (City city : travelingSalesmanTour.getCityList()) {
            int x = translateCoordinate(srcMinimumX, srcWidth, width, city.getX());
            int y = translateCoordinate(srcMinimumY, srcHeight, height, city.getY());
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        g.setColor(Color.BLACK);
        if (travelingSalesmanTour.isInitialized()) {
            for (CityAssignment cityAssignment : travelingSalesmanTour.getCityAssignmentList()) {
                City city = cityAssignment.getCity();
                int x1 = translateCoordinate(srcMinimumX, srcWidth, width, city.getX());
                int y1 = translateCoordinate(srcMinimumY, srcHeight, height, city.getY());
                City nextCity = cityAssignment.getNextCityAssignment().getCity();
                int x2 = translateCoordinate(srcMinimumX, srcWidth, width, nextCity.getX());
                int y2 = translateCoordinate(srcMinimumY, srcHeight, height, nextCity.getY());
                g.drawLine(x1, y1, x2, y2);
            }
        }
        repaint();
    }

    private int translateCoordinate(double srcMinimum, double srcLength, double length, double value) {
        return (int) Math.floor((value - srcMinimum) * length / srcLength);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }
}
