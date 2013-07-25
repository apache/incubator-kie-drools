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

package org.optaplanner.examples.common.swingui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

public class TangoColorFactory {

    public static final Color CHAMELEON_1 = new Color(138, 226, 52);
    public static final Color CHAMELEON_2 = new Color(115, 210, 22);
    public static final Color CHAMELEON_3 = new Color(78, 154, 6);
    public static final Color BUTTER_1 = new Color(252, 233, 79);
    public static final Color BUTTER_2 = new Color(237, 212, 0);
    public static final Color BUTTER_3 = new Color(196, 160, 0);
    public static final Color SKY_BLUE_1 = new Color(114, 159, 207);
    public static final Color SKY_BLUE_2 = new Color(52, 101, 164);
    public static final Color SKY_BLUE_3 = new Color(32, 74, 135);
    public static final Color CHOCOLATE_1 = new Color(233, 185, 110);
    public static final Color CHOCOLATE_2 = new Color(193, 125, 17);
    public static final Color CHOCOLATE_3 = new Color(143, 89, 2);
    public static final Color PLUM_1 = new Color(173, 127, 168);
    public static final Color PLUM_2 = new Color(117, 80, 123);
    public static final Color PLUM_3 = new Color(92, 53, 102);

    public static final Color SCARLET_1 = new Color(239, 41, 41);
    public static final Color SCARLET_2 = new Color(204, 0, 0);
    public static final Color SCARLET_3 = new Color(164, 0, 0);
    public static final Color ORANGE_1 = new Color(252, 175, 62);
    public static final Color ORANGE_2 = new Color(245, 121, 0);
    public static final Color ORANGE_3 = new Color(206, 92, 0);

    public static final Color ALUMINIUM_1 = new Color(238, 238, 236);
    public static final Color ALUMINIUM_2 = new Color(211, 215, 207);
    public static final Color ALUMINIUM_3 = new Color(186, 189, 182);
    public static final Color ALUMINIUM_4 = new Color(136, 138, 133);
    public static final Color ALUMINIUM_5 = new Color(85, 87, 83);
    public static final Color ALUMINIUM_6 = new Color(46, 52, 54);

    // Scarlet and orange are reserved for hard and soft constraints
    public static final Color[] SEQUENCE_1 = {
            TangoColorFactory.CHAMELEON_1, TangoColorFactory.BUTTER_1, TangoColorFactory.SKY_BLUE_1, TangoColorFactory.CHOCOLATE_1,
            TangoColorFactory.PLUM_1
    };
    public static final Color[] SEQUENCE_2 = {
            TangoColorFactory.CHAMELEON_2, TangoColorFactory.BUTTER_2, TangoColorFactory.SKY_BLUE_2, TangoColorFactory.CHOCOLATE_2,
            TangoColorFactory.PLUM_2
    };
    public static final Color[] SEQUENCE_3 = {
            TangoColorFactory.CHAMELEON_3, TangoColorFactory.BUTTER_3, TangoColorFactory.SKY_BLUE_3, TangoColorFactory.CHOCOLATE_3,
            TangoColorFactory.PLUM_3
    };

    public static final Stroke THICK_STROKE = new BasicStroke(2.0f);
    public static final Stroke NORMAL_STROKE = new BasicStroke();

    public static final Stroke FAT_DASHED_STROKE = new BasicStroke(
            1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {7.0f, 3.0f}, 0.0f);

    public static final Stroke DASHED_STROKE = new BasicStroke(
            1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {4.0f, 4.0f}, 0.0f);

    public static final Stroke LIGHT_DASHED_STROKE = new BasicStroke(
            1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {3.0f, 7.0f}, 0.0f);
    
    private Map<Object, Color> colorMap;
    private int nextColorCount;

    public TangoColorFactory() {
        colorMap = new HashMap<Object, Color>();
        nextColorCount = 0;
    }
    
    public Color pickColor(Object o) {
        Color color = colorMap.get(o);
        if (color == null) {
            color = nextColor();
            colorMap.put(o, color);
        }
        return color;
    }

    private Color nextColor() {
        Color color;
        int colorIndex = nextColorCount % SEQUENCE_1.length;
        int shadeIndex = nextColorCount / SEQUENCE_1.length;
        if (shadeIndex == 0) {
            color = SEQUENCE_1[colorIndex];
        } else if (shadeIndex == 1) {
            color = SEQUENCE_2[colorIndex]; // TODO
        } else if (shadeIndex == 2) {
            color = SEQUENCE_3[colorIndex];
        } else {
            shadeIndex -= 3;
            Color floorColor;
            Color ceilColor;
            if (shadeIndex % 2 == 0) {
                floorColor = SEQUENCE_2[colorIndex];
                ceilColor = SEQUENCE_1[colorIndex];
            } else {
                floorColor = SEQUENCE_3[colorIndex];
                ceilColor = SEQUENCE_2[colorIndex];
            }
            int base = (shadeIndex / 2) + 1;
            int divisor = 2;
            while (base >= divisor) {
                divisor *= 2;
            }
            base = (base * 2) - divisor + 1;
            double shadePercentage = ((double) base) / (double) divisor;
            color = new Color(
                    floorColor.getRed() + (int) (shadePercentage * (ceilColor.getRed() - floorColor.getRed())),
                    floorColor.getGreen() + (int) (shadePercentage * (ceilColor.getGreen() - floorColor.getGreen())),
                    floorColor.getBlue() + (int) (shadePercentage * (ceilColor.getBlue() - floorColor.getBlue())));
        }
        nextColorCount++;
        return color;
    }

}
