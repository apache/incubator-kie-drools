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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
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
    private LatitudeLongitudeTranslator translator = null;

    private ImageIcon europaBackground;

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
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (translator != null) {
                    double longitude = translator.translateXToLongitude(e.getX());
                    double latitude = translator.translateYToLatitude(e.getY());
                    TspWorldPanel.this.tspPanel.insertCityAndJourney(longitude, latitude);
                }
            }
        });
        europaBackground = new ImageIcon(getClass().getResource(
                "/org/drools/planner/examples/tsp/swingui/europaBackground.png"));
    }

    public void resetPanel(Solution solution) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) solution;
        translator = new LatitudeLongitudeTranslator();
        for (City city : travelingSalesmanTour.getCityList()) {
            translator.addCoordinates(city.getLatitude(), city.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics g = createCanvas(width, height);
        String tourName = travelingSalesmanTour.getName();
        if (tourName.startsWith("europe")) {
            g.drawImage(europaBackground.getImage(), 0, 0, translator.getImageWidth(), translator.getImageHeight(), this);
        }
        g.setColor(TangoColors.PLUM_2);
        for (City city : travelingSalesmanTour.getCityList()) {
            int x = translator.translateLongitudeToX(city.getLongitude());
            int y = translator.translateLatitudeToY(city.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        g.setColor(TangoColors.SCARLET_2);
        for (Depot depot : travelingSalesmanTour.getDepotList()) {
            int x = translator.translateLongitudeToX(depot.getCity().getLongitude());
            int y = translator.translateLatitudeToY(depot.getCity().getLatitude());
            g.fillRect(x - 2, y - 2, 5, 5);
        }
        g.setColor(TangoColors.CHOCOLATE_1);
        for (Journey journey : travelingSalesmanTour.getJourneyList()) {
            if (journey.getPreviousTerminal() != null) {
                City previousCity = journey.getPreviousTerminal().getCity();
                int previousX = translator.translateLongitudeToX(previousCity.getLongitude());
                int previousY = translator.translateLatitudeToY(previousCity.getLatitude());
                City city = journey.getCity();
                int x = translator.translateLongitudeToX(city.getLongitude());
                int y = translator.translateLatitudeToY(city.getLatitude());
                g.drawLine(previousX, previousY, x, y);
                // Back to depot line
                boolean needsBackToDepotLineDraw = true;
                for (Journey chainedJourney : travelingSalesmanTour.getJourneyList()) {
                    if (chainedJourney.getPreviousTerminal() == journey) {
                        needsBackToDepotLineDraw = false;
                        break;
                    }
                }
                if (needsBackToDepotLineDraw) {
                    // TODO support more than 1 depot
                    Depot depot = travelingSalesmanTour.getDepotList().get(0);
                    City depotCity = depot.getCity();
                    int depotX = translator.translateLongitudeToX(depotCity.getLongitude());
                    int depotY = translator.translateLatitudeToY(depotCity.getLatitude());
                    g.drawLine(x, y,depotX, depotY);
                }
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
