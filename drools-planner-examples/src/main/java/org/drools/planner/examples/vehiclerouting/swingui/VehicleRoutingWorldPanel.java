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
import org.drools.planner.examples.vehiclerouting.domain.VrpLocation;
import org.drools.planner.examples.vehiclerouting.domain.VrpSchedule;
import org.drools.planner.examples.vehiclerouting.domain.VrpVehicle;

/**
 * TODO this code is highly unoptimized
 */
public class VehicleRoutingWorldPanel extends JPanel {

    private final VehicleRoutingPanel vehicleRoutingPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public VehicleRoutingWorldPanel(VehicleRoutingPanel vehicleRoutingPanel) {
        this.vehicleRoutingPanel = vehicleRoutingPanel;
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
        g.setColor(TangoColors.ALUMINIUM_6);
        g.setFont(g.getFont().deriveFont(8.0f));
        for (VrpCustomer customer : schedule.getCustomerList()) {
            int x = translator.translateLongitudeToX(customer.getLocation().getLongitude());
            int y = translator.translateLatitudeToY(customer.getLocation().getLatitude());
            g.drawString(Integer.toString(customer.getDemand()), x + 3, y - 3);
        }
        g.setColor(TangoColors.PLUM_2);
        for (VrpLocation location : schedule.getLocationList()) {
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        g.setColor(TangoColors.SCARLET_2);
        for (VrpVehicle vehicle : schedule.getVehicleList()) {
            int x = translator.translateLongitudeToX(vehicle.getLocation().getLongitude());
            int y = translator.translateLatitudeToY(vehicle.getLocation().getLatitude());
            g.fillRect(x - 2, y - 2, 5, 5);
        }
        g.setColor(TangoColors.CHOCOLATE_1);
        for (VrpCustomer customer : schedule.getCustomerList()) {
            if (customer.getPreviousAppearance() != null) {
                VrpLocation previousLocation = customer.getPreviousAppearance().getLocation();
                int previousX = translator.translateLongitudeToX(previousLocation.getLongitude());
                int previousY = translator.translateLatitudeToY(previousLocation.getLatitude());
                VrpLocation location = customer.getLocation();
                int x = translator.translateLongitudeToX(location.getLongitude());
                int y = translator.translateLatitudeToY(location.getLatitude());
                g.drawLine(previousX, previousY, x, y);
                // Back to vehicle line
                boolean needsBackToVehicleLineDraw = true;
                for (VrpCustomer trailingCustomer : schedule.getCustomerList()) {
                    if (trailingCustomer.getPreviousAppearance() == customer) {
                        needsBackToVehicleLineDraw = false;
                        break;
                    }
                }
                if (needsBackToVehicleLineDraw) {
                    // TODO support more than 1 vehicle
                    VrpVehicle vehicle = schedule.getVehicleList().get(0);
                    VrpLocation vehicleLocation = vehicle.getLocation();
                    int vehicleX = translator.translateLongitudeToX(vehicleLocation.getLongitude());
                    int vehicleY = translator.translateLatitudeToY(vehicleLocation.getLatitude());
                    g.drawLine(x, y,vehicleX, vehicleY);
                }
            }
        }
        // Legend
        g.setFont(g.getFont().deriveFont(8.0f));
        g.setColor(TangoColors.PLUM_2);
        g.fillRect(6, (int) height - 19, 3, 3);
        g.drawString("Customer", 15, (int) height - 15);
        g.setColor(TangoColors.SCARLET_2);
        g.fillRect(5, (int) height - 10, 5, 5);
        g.drawString("Vehicle", 15, (int) height - 5);
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
