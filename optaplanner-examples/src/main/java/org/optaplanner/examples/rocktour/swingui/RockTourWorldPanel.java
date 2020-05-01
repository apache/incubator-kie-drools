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

package org.optaplanner.examples.rocktour.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.swing.impl.TangoColorFactory;

public class RockTourWorldPanel extends JPanel {

    private static final int TEXT_SIZE = 12;
    private static final int LOCATION_NAME_TEXT_SIZE = 8;
    protected static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);

    private final RockTourPanel rockTourPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public RockTourWorldPanel(RockTourPanel rockTourPanel) {
        this.rockTourPanel = rockTourPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                RockTourSolution solution = RockTourWorldPanel.this.rockTourPanel.getSolution();
                if (solution != null) {
                    resetPanel(solution);
                }
            }
        });
    }

    public void resetPanel(RockTourSolution solution) {
        translator = new LatitudeLongitudeTranslator();
        RockBus bus = solution.getBus();
        translator.addCoordinates(bus.getStartLocation().getLatitude(), bus.getStartLocation().getLongitude());
        translator.addCoordinates(bus.getEndLocation().getLatitude(), bus.getEndLocation().getLongitude());
        for (RockShow show : solution.getShowList()) {
            translator.addCoordinates(show.getLocation().getLatitude(), show.getLocation().getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics2D g = createCanvas(width, height);
        g.setFont(g.getFont().deriveFont((float) LOCATION_NAME_TEXT_SIZE));
        List<RockShow> showList = solution.getShowList();
        int maxAvailableDateSetSize = showList.stream().mapToInt(show -> show.getAvailableDateSet().size()).max().orElse(-1);
        for (RockShow show : showList) {
            RockLocation location = show.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            double percentage = (double) show.getAvailableDateSet().size() / maxAvailableDateSetSize;
            g.setColor(TangoColorFactory.buildPercentageColor(TangoColorFactory.PLUM_3, TangoColorFactory.PLUM_1, percentage));
            g.fillRect(x - 1, y - 1, 3, 3);
            if (location.getCityName() != null && showList.size() <= 500) {
                g.drawString(StringUtils.abbreviate(location.getCityName(), 20), x + 3, y - 3);
            }
            if (show.getDate() != null) {
                g.drawString(DAY_FORMATTER.format(show.getDate()), x + 3, y - 3 + LOCATION_NAME_TEXT_SIZE * 3 / 2);
            }
        }
        g.setColor(TangoColorFactory.ALUMINIUM_4);
        RockLocation busStartLocation = bus.getStartLocation();
        int domicileX = translator.translateLongitudeToX(busStartLocation.getLongitude());
        int domicileY = translator.translateLatitudeToY(busStartLocation.getLatitude());
        g.fillRect(domicileX - 2, domicileY - 2, 5, 5);
        if (busStartLocation.getCityName() != null && showList.size() <= 500) {
            g.drawString(busStartLocation.getCityName(), domicileX + 3, domicileY - 3);
        }
        Set<RockShow> needsBackToDomicileLineSet = new HashSet<>(showList);
        for (RockShow trailingShow : showList) {
            if (trailingShow.getPreviousStandstill() instanceof RockShow) {
                needsBackToDomicileLineSet.remove(trailingShow.getPreviousStandstill());
            }
        }
        g.setColor(TangoColorFactory.CHOCOLATE_1);
        for (RockShow show : showList) {
            if (show.getPreviousStandstill() != null) {
                RockLocation previousLocation = show.getPreviousStandstill().getDepartureLocation();
                RockLocation location = show.getLocation();
                translator.drawRoute(g, previousLocation.getLongitude(), previousLocation.getLatitude(),
                        location.getLongitude(), location.getLatitude(),
                        true, false);
                // Back to bus line
                if (needsBackToDomicileLineSet.contains(show)) {
                    translator.drawRoute(g, location.getLongitude(), location.getLatitude(),
                            busStartLocation.getLongitude(), busStartLocation.getLatitude(),
                            true, true);
                }
            }
        }
        g.setFont(g.getFont().deriveFont((float) TEXT_SIZE));
        // Legend
        g.setColor(TangoColorFactory.ALUMINIUM_4);
        g.fillRect(5, (int) height - 17 - TEXT_SIZE, 5, 5);
        g.drawString("Bus start", 15, (int) height - 10 - TEXT_SIZE);
        g.setColor(TangoColorFactory.PLUM_2);
        g.fillRect(6, (int) height - 11, 3, 3);
        g.drawString("Show (darker means less available)", 15, (int) height - 5);
        repaint();
    }

    public void updatePanel(RockTourSolution solution) {
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
