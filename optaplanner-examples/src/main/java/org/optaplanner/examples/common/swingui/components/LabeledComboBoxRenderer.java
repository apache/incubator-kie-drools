/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.swingui.components;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Display the user-friendly {@link Labeled#getLabel()} instead of the developer-friendly {@link Object#toString()}.
 */
public class LabeledComboBoxRenderer implements ListCellRenderer {

    public static void applyToComboBox(JComboBox comboBox) {
        comboBox.setRenderer(new LabeledComboBoxRenderer(comboBox.getRenderer()));
    }

    private final ListCellRenderer originalListCellRenderer;

    public LabeledComboBoxRenderer(ListCellRenderer originalListCellRenderer) {
        this.originalListCellRenderer = originalListCellRenderer;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        String label = (value == null) ? "" : ((Labeled) value).getLabel();
        return originalListCellRenderer.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
    }

}
