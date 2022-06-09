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
