/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.swingui.latitudelongitude;

import java.awt.Graphics2D;
import java.awt.geom.CubicCurve2D;

import org.optaplanner.swing.impl.TangoColorFactory;

public class LatitudeLongitudeTranslator {

    public static final double MARGIN_RATIO = 0.04;

    private double minimumLatitude = Double.MAX_VALUE;
    private double maximumLatitude = -Double.MAX_VALUE;
    private double minimumLongitude = Double.MAX_VALUE;
    private double maximumLongitude = -Double.MAX_VALUE;
    private double latitudeLength = 0.0;
    private double longitudeLength = 0.0;

    private double innerWidth = 0.0;
    private double innerHeight = 0.0;
    private double innerWidthMargin = 0.0;
    private double innerHeightMargin = 0.0;
    private int imageWidth = -1;
    private int imageHeight = -1;

    public void addCoordinates(double latitude, double longitude) {
        if (latitude < minimumLatitude) {
            minimumLatitude = latitude;
        }
        if (latitude > maximumLatitude) {
            maximumLatitude = latitude;
        }
        if (longitude < minimumLongitude) {
            minimumLongitude = longitude;
        }
        if (longitude > maximumLongitude) {
            maximumLongitude = longitude;
        }
    }

    public void prepareFor(double width, double height) {
        latitudeLength = maximumLatitude - minimumLatitude;
        longitudeLength = maximumLongitude - minimumLongitude;
        innerWidthMargin = width * MARGIN_RATIO;
        innerHeightMargin = height * MARGIN_RATIO;
        innerWidth = width - (2.0 * innerWidthMargin);
        innerHeight = height - (2.0 * innerHeightMargin);
        // Keep ratio visually correct
        if (innerWidth > innerHeight * longitudeLength / latitudeLength) {
            innerWidth = innerHeight * longitudeLength / latitudeLength;
        } else {
            innerHeight = innerWidth * latitudeLength / longitudeLength;
        }
        imageWidth = (int) Math.floor((2.0 * innerWidthMargin) + innerWidth);
        imageHeight = (int) Math.floor((2.0 * innerHeightMargin) + innerHeight);
    }

    public int translateLongitudeToX(double longitude) {
        return (int) Math.floor(((longitude - minimumLongitude) * innerWidth / longitudeLength) + innerWidthMargin);
    }

    public int translateLatitudeToY(double latitude) {
        return (int) Math.floor(((maximumLatitude - latitude) * innerHeight / latitudeLength) + innerHeightMargin);
    }

    public double translateXToLongitude(int x) {
        return minimumLongitude + ((((double) x) - innerWidthMargin) * longitudeLength / innerWidth);
    }

    public double translateYToLatitude(double y) {
        return maximumLatitude - ((((double) y) - innerHeightMargin) * latitudeLength / innerHeight);
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void drawSquare(Graphics2D g, double lon, double lat, int diameter) {
        drawSquare(g, lon, lat, diameter, null);
    }

    public void drawSquare(Graphics2D g, double lon, double lat, int diameter, String label) {
        int x = translateLongitudeToX(lon);
        int y = translateLatitudeToY(lat);
        g.fillRect(x - (diameter / 2), y - (diameter / 2), diameter, diameter);
        if (label != null) {
            g.drawString(label, x + diameter, y - diameter);
        }
    }

    public void drawRoute(Graphics2D g, double lon1, double lat1, double lon2, double lat2, boolean straight, boolean dashed) {
        int x1 = translateLongitudeToX(lon1);
        int y1 = translateLatitudeToY(lat1);
        int x2 = translateLongitudeToX(lon2);
        int y2 = translateLatitudeToY(lat2);
        if (dashed) {
            g.setStroke(TangoColorFactory.FAT_DASHED_STROKE);
        }
        if (straight) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            double xDistPart = (x2 - x1) / 3.0;
            double yDistPart = (y2 - y1) / 3.0;
            double ctrlx1 = x1 + xDistPart + yDistPart;
            double ctrly1 = y1 - xDistPart + yDistPart;
            double ctrlx2 = x2 - xDistPart - yDistPart;
            double ctrly2 = y2 + xDistPart - yDistPart;
            g.draw(new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
        }
        if (dashed) {
            g.setStroke(TangoColorFactory.NORMAL_STROKE);
        }
    }

}
