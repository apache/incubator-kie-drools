/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.swingui.timetable;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.optaplanner.examples.common.swingui.SolutionPanel;

public class TimeTablePanel<XObject, YObject> extends JPanel implements Scrollable {

    private TimeTableLayout layout = new TimeTableLayout();
    private Map<Object, Integer> xMap = new HashMap<>();
    private Map<Object, Integer> yMap = new HashMap<>();

    public TimeTablePanel() {
        setLayout(layout);
    }

    public void reset() {
        removeAll();
        layout.reset();
        xMap.clear();
        yMap.clear();
    }

    // ************************************************************************
    // Define methods
    // ************************************************************************

    public void defineColumnHeaderByKey(HeaderColumnKey xObject) {
        int x = layout.addColumn();
        xMap.put(xObject, x);
    }

    public void defineColumnHeader(XObject xObject) {
        int x = layout.addColumn();
        xMap.put(xObject, x);
    }

    public void defineColumnHeader(XObject xObject, int width) {
        int x = layout.addColumn(width);
        xMap.put(xObject, x);
    }

    public void defineRowHeaderByKey(HeaderRowKey yObject) {
        int y = layout.addRow();
        yMap.put(yObject, y);
    }

    public void defineRowHeader(YObject yObject) {
        int y = layout.addRow();
        yMap.put(yObject, y);
    }

    public void defineRowHeader(YObject yObject, int height) {
        int y = layout.addRow(height);
        yMap.put(yObject, y);
    }

    // ************************************************************************
    // Add methods
    // ************************************************************************

    public void addCornerHeader(HeaderColumnKey xObject, HeaderRowKey yObject, JComponent component) {
        int x = xMap.get(xObject);
        int y = yMap.get(yObject);
        add(component, new TimeTableLayoutConstraints(x, y, true));
    }

    public void addColumnHeader(XObject xObject, HeaderRowKey yObject, JComponent component) {
        int x = xMap.get(xObject);
        int y = yMap.get(yObject);
        add(component, new TimeTableLayoutConstraints(x, y, true));
    }

    public void addColumnHeader(XObject xObject1, HeaderRowKey yObject1, XObject xObject2, HeaderRowKey yObject2,
            JComponent component) {
        int x1 = xMap.get(xObject1);
        int y1 = yMap.get(yObject1);
        int x2 = xMap.get(xObject2);
        int y2 = yMap.get(yObject2);
        add(component, new TimeTableLayoutConstraints(x1, y1, x2 - x1 + 1, y2 - y1 + 1, true));
    }

    public void addRowHeader(HeaderColumnKey xObject, YObject yObject, JComponent component) {
        int x = xMap.get(xObject);
        int y = yMap.get(yObject);
        add(component, new TimeTableLayoutConstraints(x, y, true));
    }

    public void addRowHeader(HeaderColumnKey xObject1, YObject yObject1, HeaderColumnKey xObject2, YObject yObject2,
            JComponent component) {
        int x1 = xMap.get(xObject1);
        int y1 = yMap.get(yObject1);
        int x2 = xMap.get(xObject2);
        int y2 = yMap.get(yObject2);
        add(component, new TimeTableLayoutConstraints(x1, y1, x2 - x1 + 1, y2 - y1 + 1, true));
    }

    public void addCell(XObject xObject, YObject yObject, JComponent component) {
        int x = xMap.get(xObject);
        int y = yMap.get(yObject);
        add(component, new TimeTableLayoutConstraints(x, y));
    }

    public void addCell(XObject xObject1, YObject yObject1, XObject xObject2, YObject yObject2, JComponent component) {
        int x1 = xMap.get(xObject1);
        int y1 = yMap.get(yObject1);
        int x2 = xMap.get(xObject2);
        int y2 = yMap.get(yObject2);
        add(component, new TimeTableLayoutConstraints(x1, y1, x2 - x1 + 1, y2 - y1 + 1));
    }

    // ************************************************************************
    // Scrollable methods
    // ************************************************************************

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return SolutionPanel.PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

    public enum HeaderColumnKey {
        HEADER_COLUMN_GROUP2,
        HEADER_COLUMN_GROUP1,
        HEADER_COLUMN,
        HEADER_COLUMN_EXTRA_PROPERTY_1,
        HEADER_COLUMN_EXTRA_PROPERTY_2,
        HEADER_COLUMN_EXTRA_PROPERTY_3,
        HEADER_COLUMN_EXTRA_PROPERTY_4,
        HEADER_COLUMN_EXTRA_PROPERTY_5,
        TRAILING_HEADER_COLUMN;
    }

    public enum HeaderRowKey {
        HEADER_ROW_GROUP2,
        HEADER_ROW_GROUP1,
        HEADER_ROW,
        TRAILING_HEADER_ROW;
    }

}
