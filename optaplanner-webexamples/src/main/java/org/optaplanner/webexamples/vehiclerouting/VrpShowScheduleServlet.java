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

package org.optaplanner.webexamples.vehiclerouting;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.swingui.VehicleRoutingSolutionPainter;

public class VrpShowScheduleServlet extends HttpServlet {

    private VehicleRoutingSolutionPainter schedulePainter;

    @Override
    public void init() {
        schedulePainter = new VehicleRoutingSolutionPainter();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Avoid image caching
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");
        HttpSession session = req.getSession();
        VehicleRoutingSolution shownSolution = (VehicleRoutingSolution) session.getAttribute(VrpSessionAttributeName.SHOWN_SOLUTION);
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
