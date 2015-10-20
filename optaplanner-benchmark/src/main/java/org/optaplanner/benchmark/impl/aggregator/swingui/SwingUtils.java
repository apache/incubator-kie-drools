/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.benchmark.impl.aggregator.swingui;

import java.awt.Insets;
import java.util.Enumeration;
import javax.swing.Action;
import javax.swing.Icon;
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
        // increaseDefaultFont(1.5F);
        String lookAndFeelName = "Metal"; // "Nimbus" is nicer but incompatible
        Exception lookAndFeelException;
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            lookAndFeelException = null;
        } catch (UnsupportedLookAndFeelException e) {
            lookAndFeelException = e;
        } catch (ClassNotFoundException e) {
            lookAndFeelException = e;
        } catch (InstantiationException e) {
            lookAndFeelException = e;
        } catch (IllegalAccessException e) {
            lookAndFeelException = e;
        }
        logger.warn("Could not switch to lookAndFeel (" + lookAndFeelName + "). Layout might be incorrect.",
                lookAndFeelException);
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
//        button.putClientProperty("Nimbus.Overrides", smallButtonUIDefaults);
        return button;
    }

    private SwingUtils() {
    }

}
