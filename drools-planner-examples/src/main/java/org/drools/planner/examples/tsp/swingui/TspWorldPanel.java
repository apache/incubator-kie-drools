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
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.CityAssignment;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

/**
 * TODO this code is highly unoptimized
 */
public class TspWorldPanel extends JPanel {

    private final TspPanel tspPanel;

    private BufferedImage canvas = null;

    public TspWorldPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
    }

    public void resetPanel(Solution solution) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) solution;
        LatitudeLongitudeTranslator translator = new LatitudeLongitudeTranslator();
        for (City city : travelingSalesmanTour.getCityList()) {
            translator.addCoordinates(city.getLatitude(), city.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics g = createCanvas(width, height);
        g.setColor(Color.RED);
        for (City city : travelingSalesmanTour.getCityList()) {
            int x = translator.translateLongitude(city.getLongitude());
            int y = translator.translateLatitude(city.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        g.setColor(Color.BLACK);
        for (CityAssignment cityAssignment : travelingSalesmanTour.getCityAssignmentList()) {
            if (cityAssignment.getNextCityAssignment() != null) {
                City city1 = cityAssignment.getCity();
                int x1 = translator.translateLongitude(city1.getLongitude());
                int y1 = translator.translateLatitude(city1.getLatitude());
                City city2 = cityAssignment.getNextCityAssignment().getCity();
                int x2 = translator.translateLongitude(city2.getLongitude());
                int y2 = translator.translateLatitude(city2.getLatitude());
                g.drawLine(x1, y1, x2, y2);
            }
        }
        repaint();
    }

    private static class LatitudeLongitudeTranslator {

        private double minimumLatitude = Double.MAX_VALUE;
        private double maximumLatitude = -Double.MAX_VALUE;
        private double minimumLongitude = Double.MAX_VALUE;
        private double maximumLongitude = -Double.MAX_VALUE;
        private double latitudeLength = 0.0;
        private double longitudeLength = 0.0;

        private double width = 0.0;
        private double height = 0.0;

        public void addCoordinates(double latitude, double longitude) {
            if (latitude < minimumLatitude) {
                minimumLatitude = latitude;
            }
            if (latitude > maximumLatitude) {
                maximumLatitude = latitude;
            }
            if (longitude < minimumLongitude) {
                minimumLongitude = longitude;
            }
            if (longitude > maximumLongitude) {
                maximumLongitude = longitude;
            }
        }

        public void prepareFor(double width, double height) {
            this.width = width;
            this.height = height;
            latitudeLength = maximumLatitude - minimumLatitude;
            longitudeLength = maximumLongitude - minimumLongitude;
        }

        public int translateLongitude(double value) {
            return (int) Math.floor((value - minimumLongitude) * width / longitudeLength);
        }

        public int translateLatitude(double value) {
            return (int) Math.floor((maximumLatitude - value) * height / latitudeLength);
        }

    }

    private Graphics createCanvas(double width, double height) {
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        return g;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }
}
