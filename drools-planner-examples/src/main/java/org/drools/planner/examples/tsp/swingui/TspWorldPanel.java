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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.Depot;
import org.drools.planner.examples.tsp.domain.Journey;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

/**
 * TODO this code is highly unoptimized
 */
public class TspWorldPanel extends JPanel {

    private final TspPanel tspPanel;

    private BufferedImage canvas = null;

    public TspWorldPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                TravelingSalesmanTour travelingSalesmanTour = TspWorldPanel.this.tspPanel.getTravelingSalesmanTour();
                if (travelingSalesmanTour != null) {
                    resetPanel(travelingSalesmanTour);
                }
            }
        });
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
        g.setColor(TangoColors.SKY_BLUE_1);
        for (City city : travelingSalesmanTour.getCityList()) {
            int x = translator.translateLongitude(city.getLongitude());
            int y = translator.translateLatitude(city.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        g.setColor(TangoColors.SCARLET_1);
        for (Depot depot : travelingSalesmanTour.getDepotList()) {
            int x = translator.translateLongitude(depot.getCity().getLongitude());
            int y = translator.translateLatitude(depot.getCity().getLatitude());
            g.fillRect(x - 2, y - 2, 5, 5);
        }
        g.setColor(TangoColors.CHOCOLATE_1);
        for (Journey journey : travelingSalesmanTour.getJourneyList()) {
            if (journey.getPreviousTerminal() != null) {
                City city1 = journey.getPreviousTerminal().getCity();
                int x1 = translator.translateLongitude(city1.getLongitude());
                int y1 = translator.translateLatitude(city1.getLatitude());
                City city2 = journey.getCity();
                int x2 = translator.translateLongitude(city2.getLongitude());
                int y2 = translator.translateLatitude(city2.getLatitude());
                g.drawLine(x1, y1, x2, y2);
            }
        }
        repaint();
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
