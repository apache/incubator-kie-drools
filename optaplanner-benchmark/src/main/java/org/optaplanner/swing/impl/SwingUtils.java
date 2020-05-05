/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.swing.impl;

import static org.optaplanner.swing.impl.TangoColorFactory.ALUMINIUM_1;
import static org.optaplanner.swing.impl.TangoColorFactory.ALUMINIUM_2;
import static org.optaplanner.swing.impl.TangoColorFactory.ALUMINIUM_3;
import static org.optaplanner.swing.impl.TangoColorFactory.ALUMINIUM_4;
import static org.optaplanner.swing.impl.TangoColorFactory.ALUMINIUM_6;
import static org.optaplanner.swing.impl.TangoColorFactory.BUTTER_1;
import static org.optaplanner.swing.impl.TangoColorFactory.BUTTER_2;
import static org.optaplanner.swing.impl.TangoColorFactory.CHAMELEON_1;
import static org.optaplanner.swing.impl.TangoColorFactory.ORANGE_2;
import static org.optaplanner.swing.impl.TangoColorFactory.SCARLET_2;
import static org.optaplanner.swing.impl.TangoColorFactory.SKY_BLUE_1;
import static org.optaplanner.swing.impl.TangoColorFactory.SKY_BLUE_2;
import static org.optaplanner.swing.impl.TangoColorFactory.SKY_BLUE_3;

import java.awt.Color;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingUtils {

    private static final Logger logger = LoggerFactory.getLogger(SwingUtils.class);

    private static final UIDefaults smallButtonUIDefaults;

    static {
        smallButtonUIDefaults = new UIDefaults();
        smallButtonUIDefaults.put("Button.contentMargins", new Insets(5, 5, 5, 5));
    }

    public static void fixateLookAndFeel() {
        configureNimbusToTangoColors();
        configureLookAndFeel("Nimbus");
        //        increaseDefaultFont(1.5F);
    }

    protected static void configureNimbusToTangoColors() {
        UIManager.put("control", ALUMINIUM_1);
        UIManager.put("info", BUTTER_1);
        UIManager.put("nimbusAlertYellow", BUTTER_2);
        UIManager.put("nimbusBase", SKY_BLUE_3);
        UIManager.put("nimbusDisabledText", ALUMINIUM_4);
        UIManager.put("nimbusFocus", SKY_BLUE_1);
        UIManager.put("nimbusGreen", CHAMELEON_1);
        UIManager.put("nimbusInfoBlue", SKY_BLUE_2);
        UIManager.put("nimbusLightBackground", Color.WHITE);
        UIManager.put("nimbusOrange", ORANGE_2);
        UIManager.put("nimbusRed", SCARLET_2);
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("nimbusSelectionBackground", SKY_BLUE_2);
        UIManager.put("text", Color.BLACK);

        UIManager.put("activeCaption", ALUMINIUM_3);
        UIManager.put("background", ALUMINIUM_2);
        UIManager.put("controlDkShadow", ALUMINIUM_4);
        UIManager.put("controlHighlight", ALUMINIUM_1);
        UIManager.put("controlLHighlight", ALUMINIUM_6);
        UIManager.put("controlShadow", ALUMINIUM_2);
        UIManager.put("controlText", Color.BLACK);
        UIManager.put("desktop", SKY_BLUE_1);
        UIManager.put("inactiveCaption", ALUMINIUM_3);
        UIManager.put("infoText", Color.BLACK);
        UIManager.put("menu", ALUMINIUM_1);
        UIManager.put("menuText", Color.BLACK);
        UIManager.put("nimbusBlueGrey", ALUMINIUM_1);
        UIManager.put("nimbusBorder", ALUMINIUM_4);
        UIManager.put("nimbusSelection", SKY_BLUE_2);
        UIManager.put("scrollbar", ALUMINIUM_2);
        UIManager.put("textBackground", SKY_BLUE_1);
        UIManager.put("textForeground", Color.BLACK);
        UIManager.put("textHighlight", SKY_BLUE_3);
        UIManager.put("textHighlightText", Color.WHITE);
        UIManager.put("textInactiveText", ALUMINIUM_4);
    }

    protected static void configureLookAndFeel(String lookAndFeelName) {
        Exception lookAndFeelException;
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                }
            }
            lookAndFeelException = null;
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            lookAndFeelException = e;
        }
        if (lookAndFeelException != null) {
            logger.warn("Could not switch to lookAndFeel ({}). Layout might be incorrect.", lookAndFeelName,
                    lookAndFeelException);
        }
    }

    public static void increaseDefaultFont(float multiplier) {
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof FontUIResource) {
                FontUIResource fontUIResource = (FontUIResource) value;
                UIManager.put(key, fontUIResource.deriveFont(fontUIResource.getSize() * multiplier));
            }
        }
    }

    public static JButton makeSmallButton(JButton button) {
        button.setMargin(new Insets(0, 0, 0, 0));
        button.putClientProperty("Nimbus.Overrides", smallButtonUIDefaults);
        return button;
    }

    private SwingUtils() {
    }

}
