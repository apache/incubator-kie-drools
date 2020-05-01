/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.flightcrewscheduling.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.examples.flightcrewscheduling.domain.Airport;
import org.optaplanner.examples.flightcrewscheduling.domain.Flight;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.swing.impl.TangoColorFactory;

public class FlightCrewSchedulingWorldPanel extends JPanel {

    private static final int TEXT_SIZE = 12;
    private static final int LOCATION_NAME_TEXT_SIZE = 8;
    protected static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);

    private final FlightCrewSchedulingPanel flightCrewSchedulingPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public FlightCrewSchedulingWorldPanel(FlightCrewSchedulingPanel flightCrewSchedulingPanel) {
        this.flightCrewSchedulingPanel = flightCrewSchedulingPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                FlightCrewSolution solution = FlightCrewSchedulingWorldPanel.this.flightCrewSchedulingPanel.getSolution();
                if (solution != null) {
                    resetPanel(solution);
                }
            }
        });
    }

    public void resetPanel(FlightCrewSolution solution) {
        translator = new LatitudeLongitudeTranslator();
        for (Airport airport : solution.getAirportList()) {
            translator.addCoordinates(airport.getLatitude(), airport.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics2D g = createCanvas(width, height);
        g.setFont(g.getFont().deriveFont((float) LOCATION_NAME_TEXT_SIZE));
        g.setColor(TangoColorFactory.PLUM_2);
        for (Airport airport : solution.getAirportList()) {
            int x = translator.translateLongitudeToX(airport.getLongitude());
            int y = translator.translateLatitudeToY(airport.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
            g.drawString(StringUtils.abbreviate(airport.getCode(), 20), x + 3, y - 3);
        }
        g.setColor(TangoColorFactory.CHOCOLATE_1);
        for (Flight flight : solution.getFlightList()) {
            Airport departureAirport = flight.getDepartureAirport();
            Airport arrivalAirport = flight.getArrivalAirport();
            translator.drawRoute(g, departureAirport.getLongitude(), departureAirport.getLatitude(),
                    arrivalAirport.getLongitude(), arrivalAirport.getLatitude(),
                    true, false);
        }
        g.setFont(g.getFont().deriveFont((float) TEXT_SIZE));
        // Legend
        g.setColor(TangoColorFactory.PLUM_2);
        g.fillRect(6, (int) height - 11, 3, 3);
        g.drawString("Airport", 15, (int) height - 5);
        repaint();
    }

    public void updatePanel(FlightCrewSolution solution) {
        resetPanel(solution);
    }

    private Graphics2D createCanvas(double width, double height) {
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
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
