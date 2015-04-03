/*
 * Copyright 2015 JBoss Inc
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStartPoint;
import org.optaplanner.examples.coachshuttlegathering.domain.BusVisit;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.swingui.VehicleRoutingPanel;
import org.optaplanner.examples.vehiclerouting.swingui.VehicleRoutingSolutionPainter;

public class CoachShuttleGatheringWorldPanel extends JPanel {

    private final CoachShuttleGatheringPanel coachShuttleGatheringPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public CoachShuttleGatheringWorldPanel(CoachShuttleGatheringPanel coachShuttleGatheringPanel) {
        this.coachShuttleGatheringPanel = coachShuttleGatheringPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                CoachShuttleGatheringSolution solution = CoachShuttleGatheringWorldPanel.this.coachShuttleGatheringPanel.getCoachShuttleGatheringSolution();
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
        g.setColor(TangoColorFactory.ALUMINIUM_6);
        BusHub hub = solution.getHub();
        int hubX = translator.translateLongitudeToX(hub.getLocation().getLongitude());
        int hubY = translator.translateLatitudeToY(hub.getLocation().getLatitude());
        g.fillRect(hubX - 2, hubY - 2, 5, 5);
        for (BusVisit visit : solution.getVisitList()) {
            RoadLocation location = visit.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            g.setColor(TangoColorFactory.ALUMINIUM_4);
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        for (BusStartPoint startPoint : solution.getStartPointList()) {
            RoadLocation location = startPoint.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            g.setColor(TangoColorFactory.ALUMINIUM_2);
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        int colorIndex = 0;
        for (BusStartPoint startPoint : solution.getStartPointList()) {
            g.setColor(TangoColorFactory.SEQUENCE_2[colorIndex]);
            for (BusVisit visit = startPoint.getNextVisit(); visit != null; visit = visit.getNextVisit()) {
                RoadLocation previousLocation = visit.getPreviousStandstill().getLocation();
                RoadLocation location = visit.getLocation();
                translator.drawRoute(g, previousLocation.getLongitude(), previousLocation.getLatitude(),
                        location.getLongitude(), location.getLatitude(),
                        false, false);
            }
            colorIndex = (colorIndex + 1) % TangoColorFactory.SEQUENCE_2.length;
            // TODO draw to endpoint
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
