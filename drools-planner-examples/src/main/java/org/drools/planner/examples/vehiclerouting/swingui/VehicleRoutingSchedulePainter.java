/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.vehiclerouting.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.NumberFormat;

import javax.swing.ImageIcon;

import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.drools.planner.examples.vehiclerouting.domain.VrpCustomer;
import org.drools.planner.examples.vehiclerouting.domain.VrpDepot;
import org.drools.planner.examples.vehiclerouting.domain.VrpLocation;
import org.drools.planner.examples.vehiclerouting.domain.VrpSchedule;
import org.drools.planner.examples.vehiclerouting.domain.VrpVehicle;

public class VehicleRoutingSchedulePainter {

    private static final int TEXT_SIZE = 12;

    private static final String IMAGE_PATH_PREFIX = "/org/drools/planner/examples/vehiclerouting/swingui/";

    private ImageIcon depotImageIcon;
    private ImageIcon[] vehicleImageIcons;
    private NumberFormat numberFormat = NumberFormat.getInstance();

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public VehicleRoutingSchedulePainter() {
        depotImageIcon = new ImageIcon(getClass().getResource(IMAGE_PATH_PREFIX + "depot.png"));
        vehicleImageIcons = new ImageIcon[] {
                new ImageIcon(getClass().getResource(IMAGE_PATH_PREFIX + "vehicleChameleon.png")),
                new ImageIcon(getClass().getResource(IMAGE_PATH_PREFIX + "vehicleButter.png")),
                new ImageIcon(getClass().getResource(IMAGE_PATH_PREFIX + "vehicleSkyBlue.png")),
                new ImageIcon(getClass().getResource(IMAGE_PATH_PREFIX + "vehicleChocolate.png")),
                new ImageIcon(getClass().getResource(IMAGE_PATH_PREFIX + "vehiclePlum.png")),
        };
        if (vehicleImageIcons.length != TangoColors.SEQUENCE_1.length) {
            throw new IllegalStateException("The vehicleImageIcons length (" + vehicleImageIcons.length
                    + ") should be equal to the TangoColors.SEQUENCE length (" + TangoColors.SEQUENCE_1.length + ").");
        }
    }

    public BufferedImage getCanvas() {
        return canvas;
    }

    public LatitudeLongitudeTranslator getTranslator() {
        return translator;
    }

    public void reset(VrpSchedule schedule, Dimension size, ImageObserver imageObserver) {
        translator = new LatitudeLongitudeTranslator();
        for (VrpLocation location : schedule.getLocationList()) {
            translator.addCoordinates(location.getLatitude(), location.getLongitude());
        }

        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics g = createCanvas(width, height);
        g.setColor(TangoColors.ORANGE_2);
        g.setFont(g.getFont().deriveFont((float) TEXT_SIZE));
        for (VrpCustomer customer : schedule.getCustomerList()) {
            VrpLocation location = customer.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
            g.drawString(Integer.toString(customer.getDemand()), x + 3, y - 3);
        }
        g.setColor(TangoColors.ALUMINIUM_4);
        for (VrpDepot depot : schedule.getDepotList()) {
            int x = translator.translateLongitudeToX(depot.getLocation().getLongitude());
            int y = translator.translateLatitudeToY(depot.getLocation().getLatitude());
            g.fillRect(x - 2, y - 2, 5, 5);
            g.drawImage(depotImageIcon.getImage(),
                    x - depotImageIcon.getIconWidth() / 2, y - 2 - depotImageIcon.getIconHeight(), imageObserver);
        }
        int colorIndex = 0;
        // TODO Too many nested for loops
        for (VrpVehicle vehicle : schedule.getVehicleList()) {
            g.setColor(TangoColors.SEQUENCE_2[colorIndex]);
            VrpCustomer vehicleInfoCustomer = null;
            int longestNonDepotDistance = -1;
            int load = 0;
            for (VrpCustomer customer : schedule.getCustomerList()) {
                if (customer.getPreviousAppearance() != null && customer.getVehicle() == vehicle) {
                    load += customer.getDemand();
                    VrpLocation previousLocation = customer.getPreviousAppearance().getLocation();
                    int previousX = translator.translateLongitudeToX(previousLocation.getLongitude());
                    int previousY = translator.translateLatitudeToY(previousLocation.getLatitude());
                    VrpLocation location = customer.getLocation();
                    int x = translator.translateLongitudeToX(location.getLongitude());
                    int y = translator.translateLatitudeToY(location.getLatitude());
                    g.drawLine(previousX, previousY, x, y);
                    // Determine where to draw the vehicle info
                    int distance = customer.getDistanceToPreviousAppearance();
                    if (customer.getPreviousAppearance() instanceof VrpCustomer) {
                        if (longestNonDepotDistance < distance) {
                            longestNonDepotDistance = distance;
                            vehicleInfoCustomer = customer;
                        }
                    } else if (vehicleInfoCustomer == null) {
                        // If there is only 1 customer in this chain, draw it on a line to the Depot anyway
                        vehicleInfoCustomer = customer;
                    }
                    // Line back to the vehicle depot
                    boolean needsBackToVehicleLineDraw = true;
                    for (VrpCustomer trailingCustomer : schedule.getCustomerList()) {
                        if (trailingCustomer.getPreviousAppearance() == customer) {
                            needsBackToVehicleLineDraw = false;
                            break;
                        }
                    }
                    if (needsBackToVehicleLineDraw) {
                        VrpLocation vehicleLocation = vehicle.getLocation();
                        int vehicleX = translator.translateLongitudeToX(vehicleLocation.getLongitude());
                        int vehicleY = translator.translateLatitudeToY(vehicleLocation.getLatitude());
                        g.drawLine(x, y,vehicleX, vehicleY);
                    }
                }
            }
            // Draw vehicle info
            if (vehicleInfoCustomer != null) {
                if (load > vehicle.getCapacity()) {
                    g.setColor(TangoColors.SCARLET_2);
                }
                VrpLocation previousLocation = vehicleInfoCustomer.getPreviousAppearance().getLocation();
                VrpLocation location = vehicleInfoCustomer.getLocation();
                double longitude = (previousLocation.getLongitude() + location.getLongitude()) / 2.0;
                int x = translator.translateLongitudeToX(longitude);
                double latitude = (previousLocation.getLatitude() + location.getLatitude()) / 2.0;
                int y = translator.translateLatitudeToY(latitude);
                boolean ascending = (previousLocation.getLongitude() < location.getLongitude())
                        ^ (previousLocation.getLatitude() < location.getLatitude());

                ImageIcon vehicleImageIcon = vehicleImageIcons[colorIndex];
                int vehicleInfoHeight = vehicleImageIcon.getIconHeight() + 2 + TEXT_SIZE;
                g.drawImage(vehicleImageIcon.getImage(), x + 1, (ascending ? y - vehicleInfoHeight - 1 : y + 1), imageObserver);
                g.drawString(load + " / " + vehicle.getCapacity(), x + 1, (ascending ? y - 1 : y + vehicleInfoHeight + 1));
            }
            colorIndex = (colorIndex + 1) % TangoColors.SEQUENCE_2.length;
        }

        // Legend
        g.setColor(TangoColors.ALUMINIUM_4);
        g.fillRect(5, (int) height - 12 - TEXT_SIZE - (TEXT_SIZE / 2), 5, 5);
        g.drawString("Depot", 15, (int) height - 10 - TEXT_SIZE);
        g.setColor(TangoColors.ORANGE_2);
        g.fillRect(6, (int) height - 6 - (TEXT_SIZE / 2), 3, 3);
        g.drawString("Customer demand", 15, (int) height - 5);
        // Show soft score
        g.setColor(TangoColors.SCARLET_2);
        HardAndSoftScore score = schedule.getScore();
        if (score != null) {
            String totalDistanceString;
            if (!score.isFeasible()) {
                totalDistanceString = "Not feasible";
            } else {
                totalDistanceString = numberFormat.format(- score.getSoftScore()) + " fuel";
            }
            g.setFont( g.getFont().deriveFont(Font.BOLD, (float) TEXT_SIZE * 2));
            g.drawString(totalDistanceString,
                    (int) width - g.getFontMetrics().stringWidth(totalDistanceString) - 10, (int) height - 10);
        }
    }

    public Graphics createCanvas(double width, double height) {
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        return g;
    }

}
