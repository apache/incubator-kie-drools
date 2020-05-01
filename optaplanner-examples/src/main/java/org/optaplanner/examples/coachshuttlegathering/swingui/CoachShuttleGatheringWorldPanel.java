/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.swing.impl.TangoColorFactory;

public class CoachShuttleGatheringWorldPanel extends JPanel {

    private static final int LOCATION_NAME_TEXT_SIZE = 12;

    private final CoachShuttleGatheringPanel coachShuttleGatheringPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public CoachShuttleGatheringWorldPanel(CoachShuttleGatheringPanel coachShuttleGatheringPanel) {
        this.coachShuttleGatheringPanel = coachShuttleGatheringPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                CoachShuttleGatheringSolution solution = CoachShuttleGatheringWorldPanel.this.coachShuttleGatheringPanel
                        .getSolution();
                if (solution != null) {
                    resetPanel(solution);
                }
            }
        });
    }

    public void resetPanel(CoachShuttleGatheringSolution solution) {
        translator = new LatitudeLongitudeTranslator();
        for (RoadLocation location : solution.getLocationList()) {
            translator.addCoordinates(location.getLatitude(), location.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics2D g = createCanvas(width, height);
        g.setColor(TangoColorFactory.ORANGE_3);
        RoadLocation hubLocation = solution.getHub().getLocation();
        translator.drawSquare(g, hubLocation.getLongitude(), hubLocation.getLatitude(), 5);
        for (BusStop stop : solution.getStopList()) {
            RoadLocation location = stop.getLocation();
            g.setColor((stop.getPassengerQuantity() <= 0) ? TangoColorFactory.ALUMINIUM_4
                    : (stop.getTransportTimeToHub() == null) ? TangoColorFactory.ORANGE_2
                            : (stop.getTransportTimeRemainder() < 0) ? TangoColorFactory.SCARLET_2
                                    : TangoColorFactory.ORANGE_2);
            translator.drawSquare(g, location.getLongitude(), location.getLatitude(), 3,
                    stop.getTransportLabel());
        }
        List<Bus> busList = solution.getBusList();
        g.setColor(TangoColorFactory.ALUMINIUM_2);
        g.setFont(g.getFont().deriveFont((float) LOCATION_NAME_TEXT_SIZE));
        for (Bus bus : busList) {
            RoadLocation location = bus.getLocation();
            g.setColor(bus instanceof Coach ? TangoColorFactory.ORANGE_1 : TangoColorFactory.ALUMINIUM_2);
            translator.drawSquare(g, location.getLongitude(), location.getLatitude(), 3,
                    StringUtils.abbreviate(bus.getName(), 20));
        }
        int colorIndex = 0;
        for (Bus bus : busList) {
            g.setColor(TangoColorFactory.SEQUENCE_2.get(colorIndex));
            BusStop lastStop = null;
            for (BusStop stop = bus.getNextStop(); stop != null; stop = stop.getNextStop()) {
                RoadLocation previousLocation = stop.getPreviousBusOrStop().getLocation();
                RoadLocation location = stop.getLocation();
                translator.drawRoute(g, previousLocation.getLongitude(), previousLocation.getLatitude(),
                        location.getLongitude(), location.getLatitude(),
                        false, false);
                lastStop = stop;
            }
            if (lastStop != null || bus instanceof Coach) {
                RoadLocation lastStopLocation = lastStop == null ? bus.getLocation() : lastStop.getLocation();
                StopOrHub destination = bus.getDestination();
                if (destination != null) {
                    RoadLocation destinationLocation = destination.getLocation();
                    translator.drawRoute(g, lastStopLocation.getLongitude(), lastStopLocation.getLatitude(),
                            destinationLocation.getLongitude(), destinationLocation.getLatitude(),
                            false, true);
                }
            }
            colorIndex = (colorIndex + 1) % TangoColorFactory.SEQUENCE_2.size();
        }
        repaint();
    }

    public void updatePanel(CoachShuttleGatheringSolution solution) {
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
