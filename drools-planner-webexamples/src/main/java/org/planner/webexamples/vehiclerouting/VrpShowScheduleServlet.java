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

package org.planner.webexamples.vehiclerouting;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.drools.planner.examples.vehiclerouting.domain.VrpSchedule;
import org.drools.planner.examples.vehiclerouting.swingui.VehicleRoutingSchedulePainter;

public class VrpShowScheduleServlet extends HttpServlet {

    private VehicleRoutingSchedulePainter schedulePainter;

    @Override
    public void init() {
        schedulePainter = new VehicleRoutingSchedulePainter();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        VrpSchedule shownSolution = (VrpSchedule) session.getAttribute(VrpSessionAttributeName.SHOWN_SOLUTION);
        Dimension size = new Dimension(800, 600);
        BufferedImage image;
        if (shownSolution == null) {
            schedulePainter.createCanvas(size.width, size.height);
        } else {
            schedulePainter.reset(shownSolution, size, null);
        }
        image = schedulePainter.getCanvas();
        resp.setContentType("image/png");
        ImageIO.write(image, "png", resp.getOutputStream());
    }

}
