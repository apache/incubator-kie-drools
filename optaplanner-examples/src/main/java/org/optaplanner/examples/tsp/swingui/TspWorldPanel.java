/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tsp.swingui;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TspWorldPanel extends JPanel {

    private static final int TEXT_SIZE = 12;
    private static final int LOCATION_NAME_TEXT_SIZE = 8;
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00");

    private final TspPanel tspPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    private Standstill dragSourceStandstill = null;
    private Standstill dragTargetStandstill = null;

    private ImageIcon europaBackground;

    public TspWorldPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                TspSolution tspSolution = TspWorldPanel.this.tspPanel.getSolution();
                if (tspSolution != null) {
                    resetPanel(tspSolution);
                }
            }
        });
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (translator == null) {
                    return;
                }
                double longitude = translator.translateXToLongitude(e.getX());
                double latitude = translator.translateYToLatitude(e.getY());
                if (isLeftClick(e)) {
                    dragSourceStandstill = TspWorldPanel.this.tspPanel.findNearestStandstill(
                            new AirLocation(-1L, latitude, longitude));
                    TspSolution tspSolution = TspWorldPanel.this.tspPanel.getSolution();
                    dragTargetStandstill = tspSolution.getDomicile();
                    resetPanel(tspSolution);
                } else if (isRightClick(e)) {
                    TspWorldPanel.this.tspPanel.insertLocationAndVisit(longitude, latitude);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (translator != null) {
                    if (dragSourceStandstill != null) {
                        double longitude = translator.translateXToLongitude(e.getX());
                        double latitude = translator.translateYToLatitude(e.getY());
                        dragTargetStandstill = TspWorldPanel.this.tspPanel.findNearestStandstill(
                                new AirLocation(-1L, latitude, longitude));
                        TspSolution tspSolution = TspWorldPanel.this.tspPanel.getSolution();
                        if (dragSourceStandstill == dragTargetStandstill) {
                            dragTargetStandstill = tspSolution.getDomicile();
                        }
                        resetPanel(tspSolution);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (translator == null || !isLeftClick(e)) {
                    return;
                }
                double longitude = translator.translateXToLongitude(e.getX());
                double latitude = translator.translateYToLatitude(e.getY());
                dragTargetStandstill = TspWorldPanel.this.tspPanel.findNearestStandstill(
                        new AirLocation(-1L, latitude, longitude));
                TspSolution tspSolution = TspWorldPanel.this.tspPanel.getSolution();
                if (dragSourceStandstill == dragTargetStandstill) {
                    dragTargetStandstill = tspSolution.getDomicile();
                }
                Standstill sourceStandstill = TspWorldPanel.this.dragSourceStandstill;
                Standstill targetStandstill = TspWorldPanel.this.dragTargetStandstill;
                TspWorldPanel.this.dragSourceStandstill = null;
                TspWorldPanel.this.dragTargetStandstill = null;
                // connectStandstills() will call resetPanel()
                TspWorldPanel.this.tspPanel.connectStandstills(sourceStandstill, targetStandstill);
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        europaBackground = new ImageIcon(getClass().getResource("europaBackground.png"));
    }

    private static boolean isLeftClick(MouseEvent e) {
        // Mac OS X users may be using Ctrl+LeftClick to emulate right button
        return SwingUtilities.isLeftMouseButton(e) && !e.isControlDown();
    }

    private static boolean isRightClick(MouseEvent e) {
        // Mac OS X users may be using Ctrl+LeftClick to emulate right button
        return SwingUtilities.isRightMouseButton(e) || e.isControlDown();
    }

    public void resetPanel(TspSolution tspSolution) {
        translator = new LatitudeLongitudeTranslator();
        for (Location location : tspSolution.getLocationList()) {
            translator.addCoordinates(location.getLatitude(), location.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics2D g = createCanvas(width, height);
        if (tspSolution.getName().startsWith("europe")) {
            g.drawImage(europaBackground.getImage(), 0, 0, translator.getImageWidth(), translator.getImageHeight(), this);
        }
        g.setFont(g.getFont().deriveFont((float) LOCATION_NAME_TEXT_SIZE));
        g.setColor(TangoColorFactory.PLUM_2);
        List<Visit> visitList = tspSolution.getVisitList();
        for (Visit visit : visitList) {
            Location location = visit.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
            if (location.getName() != null && visitList.size() <= 500) {
                g.drawString(StringUtils.abbreviate(location.getName(), 20), x + 3, y - 3);
            }
        }
        g.setColor(TangoColorFactory.ALUMINIUM_4);
        Domicile domicile = tspSolution.getDomicile();
        Location domicileLocation = domicile.getLocation();
        int domicileX = translator.translateLongitudeToX(domicileLocation.getLongitude());
        int domicileY = translator.translateLatitudeToY(domicileLocation.getLatitude());
        g.fillRect(domicileX - 2, domicileY - 2, 5, 5);
        if (domicileLocation.getName() != null && visitList.size() <= 500) {
            g.drawString(domicileLocation.getName(), domicileX + 3, domicileY - 3);
        }
        Set<Visit> needsBackToDomicileLineSet = new HashSet<>(visitList);
        for (Visit trailingVisit : visitList) {
            if (trailingVisit.getPreviousStandstill() instanceof Visit) {
                needsBackToDomicileLineSet.remove(trailingVisit.getPreviousStandstill());
            }
        }
        g.setColor(TangoColorFactory.CHOCOLATE_1);
        for (Visit visit : visitList) {
            if (visit.getPreviousStandstill() != null) {
                Location previousLocation = visit.getPreviousStandstill().getLocation();
                Location location = visit.getLocation();
                translator.drawRoute(g, previousLocation.getLongitude(), previousLocation.getLatitude(),
                        location.getLongitude(), location.getLatitude(),
                        location instanceof AirLocation, false);
                // Back to domicile line
                if (needsBackToDomicileLineSet.contains(visit)) {
                    translator.drawRoute(g, location.getLongitude(), location.getLatitude(),
                            domicileLocation.getLongitude(), domicileLocation.getLatitude(),
                            location instanceof AirLocation, true);
                }
            }
        }
        // Drag
        if (dragSourceStandstill != null) {
            g.setColor(TangoColorFactory.CHOCOLATE_2);
            Location sourceLocation = dragSourceStandstill.getLocation();
            Location targetLocation = dragTargetStandstill.getLocation();
            translator.drawRoute(g, sourceLocation.getLongitude(), sourceLocation.getLatitude(),
                    targetLocation.getLongitude(), targetLocation.getLatitude(),
                    sourceLocation instanceof AirLocation, dragTargetStandstill instanceof Domicile);
        }
        // Legend
        g.setColor(TangoColorFactory.ALUMINIUM_4);
        g.fillRect(5, (int) height - 15 - TEXT_SIZE, 5, 5);
        g.drawString("Domicile", 15, (int) height - 10 - TEXT_SIZE);
        g.setColor(TangoColorFactory.PLUM_2);
        g.fillRect(6, (int) height - 9, 3, 3);
        g.drawString("Visit", 15, (int) height - 5);
        g.setColor(TangoColorFactory.ALUMINIUM_5);
        String locationsSizeString = tspSolution.getLocationList().size() + " locations";
        g.drawString(locationsSizeString,
                ((int) width - g.getFontMetrics().stringWidth(locationsSizeString)) / 2, (int) height - 5);
        if (tspSolution.getDistanceType() == DistanceType.AIR_DISTANCE) {
            String leftClickString = "Left click and drag between 2 locations to connect them.";
            g.drawString(leftClickString, (int) width - 5 - g.getFontMetrics().stringWidth(leftClickString), (int) height - 10 - TEXT_SIZE);
            String rightClickString = "Right click anywhere on the map to add a visit.";
            g.drawString(rightClickString, (int) width - 5 - g.getFontMetrics().stringWidth(rightClickString), (int) height - 5);
        }
        // Show soft score
        g.setColor(TangoColorFactory.ORANGE_3);
        SimpleLongScore score = tspSolution.getScore();
        if (score != null) {
            String distanceString = tspSolution.getDistanceString(NUMBER_FORMAT);
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float) TEXT_SIZE * 2));
            g.drawString(distanceString,
                    (int) width - g.getFontMetrics().stringWidth(distanceString) - 10, (int) height - 15 - 2 * TEXT_SIZE);
        }
        repaint();
    }

    public void updatePanel(TspSolution tspSolution) {
        resetPanel(tspSolution);
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
