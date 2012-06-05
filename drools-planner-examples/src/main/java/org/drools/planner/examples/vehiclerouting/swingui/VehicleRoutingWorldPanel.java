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
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
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
    private final VehicleRoutingPanel vehicleRoutingPanel;

    private VehicleRoutingSchedulePainter schedulePainter = new VehicleRoutingSchedulePainter();

    public VehicleRoutingWorldPanel(VehicleRoutingPanel vehicleRoutingPanel) {
        this.vehicleRoutingPanel = vehicleRoutingPanel;
        schedulePainter = new VehicleRoutingSchedulePainter();
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
                LatitudeLongitudeTranslator translator = schedulePainter.getTranslator();
                if (translator != null) {
                    double longitude = translator.translateXToLongitude(e.getX());
                    double latitude = translator.translateYToLatitude(e.getY());
                    VehicleRoutingWorldPanel.this.vehicleRoutingPanel.insertLocationAndCustomer(longitude, latitude);
                }
            }
        });
    }

    public void resetPanel(VrpSchedule schedule) {
        schedulePainter.reset(schedule, getSize(), this);
        repaint();
    }

    public void updatePanel(VrpSchedule schedule) {
        resetPanel(schedule);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage canvas = schedulePainter.getCanvas();
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }

}
