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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.drools.planner.examples.vehiclerouting.domain.VrpCustomer;
import org.drools.planner.examples.vehiclerouting.domain.VrpDepot;
import org.drools.planner.examples.vehiclerouting.domain.VrpLocation;
import org.drools.planner.examples.vehiclerouting.domain.VrpSchedule;
import org.drools.planner.examples.vehiclerouting.domain.VrpVehicle;

/**
 * TODO this code is highly unoptimized
 */
public class VehicleRoutingWorldPanel extends JPanel {

    private static final int TEXT_SIZE = 12;
    private static final String VEHICLE_IMAGE_PATH_PREFIX = "/org/drools/planner/examples/vehiclerouting/swingui/";

    private ImageIcon[] vehicleImageIcons;
    private final VehicleRoutingPanel vehicleRoutingPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public VehicleRoutingWorldPanel(VehicleRoutingPanel vehicleRoutingPanel) {
        this.vehicleRoutingPanel = vehicleRoutingPanel;
        vehicleImageIcons = new ImageIcon[] {
                new ImageIcon(getClass().getResource(VEHICLE_IMAGE_PATH_PREFIX + "vehicleChameleon.png")),
                new ImageIcon(getClass().getResource(VEHICLE_IMAGE_PATH_PREFIX + "vehicleButter.png")),
                new ImageIcon(getClass().getResource(VEHICLE_IMAGE_PATH_PREFIX + "vehicleSkyBlue.png")),
                new ImageIcon(getClass().getResource(VEHICLE_IMAGE_PATH_PREFIX + "vehicleChocolate.png")),
                new ImageIcon(getClass().getResource(VEHICLE_IMAGE_PATH_PREFIX + "vehiclePlum.png")),
        };
        if (vehicleImageIcons.length != TangoColors.SEQUENCE_1.length) {
            throw new IllegalStateException("The vehicleImageIcons length (" + vehicleImageIcons.length
                    + ") should be equal to the TangoColors.SEQUENCE length (" + TangoColors.SEQUENCE_1.length + ").");
        }
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                VrpSchedule schedule = VehicleRoutingWorldPanel.this.vehicleRoutingPanel.getSchedule();
                if (schedule != null) {
                    resetPanel(schedule);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (translator != null) {
                    double longitude = translator.translateXToLongitude(e.getX());
                    double latitude = translator.translateYToLatitude(e.getY());
                    VehicleRoutingWorldPanel.this.vehicleRoutingPanel.insertLocationAndCustomer(longitude, latitude);
                }
            }
        });
    }

    public void resetPanel(VrpSchedule schedule) {
        translator = new LatitudeLongitudeTranslator();
        for (VrpLocation location : schedule.getLocationList()) {
            translator.addCoordinates(location.getLatitude(), location.getLongitude());
        }

        Dimension size = getSize();
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

                Image vehicleImage = vehicleImageIcons[colorIndex].getImage();
                int vehicleInfoHeight = vehicleImage.getHeight(this) + 2 + TEXT_SIZE;
                g.drawImage(vehicleImage, x, (ascending ? y - vehicleInfoHeight : y), this);
                g.drawString(load + " / " + vehicle.getCapacity(), x, (ascending ? y : y + vehicleInfoHeight));
            }
            colorIndex = (colorIndex + 1) % TangoColors.SEQUENCE_2.length;
        }

        // Legend
        g.setColor(TangoColors.ALUMINIUM_4);
        g.fillRect(5, (int) height - 17 - (2 * TEXT_SIZE) - (TEXT_SIZE / 2), 5, 5);
        g.drawString("Depot", 15, (int) height - 15 - (2 * TEXT_SIZE));
        g.setColor(TangoColors.ORANGE_2);
        g.fillRect(6, (int) height - 11 - TEXT_SIZE - (TEXT_SIZE / 2), 3, 3);
        g.drawString("Customer demand", 15, (int) height - 10 - TEXT_SIZE);
        g.setColor(TangoColors.SCARLET_2);
        g.drawString(schedule.getVehicleList().size() + " vehicles: load / capacity", 15, (int) height - 5);
        repaint();
    }

    public void updatePanel(VrpSchedule schedule) {
        resetPanel(schedule);
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
