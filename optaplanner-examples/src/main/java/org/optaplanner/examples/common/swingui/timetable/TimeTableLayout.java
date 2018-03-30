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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeTableLayout implements LayoutManager2, Serializable {

    public static final int FILL_COLLISIONS_FLAG = -1;

    private List<Column> columns;
    private List<Row> rows;
    private List<List<Cell>> cells;
    private Map<Component, ComponentSpan> spanMap;

    private boolean stale;
    private int totalColumnWidth;
    private int totalRowHeight;

    public TimeTableLayout() {
        reset();
    }

    public void reset() {
        columns = new ArrayList<>();
        rows = new ArrayList<>();
        cells = new ArrayList<>();
        spanMap = new HashMap<>();
        stale = false;
        totalColumnWidth = 0;
        totalRowHeight = 0;
    }

    public int addColumn() {
        return addColumn(true, 0);
    }

    public int addColumn(int baseWidth) {
        if (baseWidth < 0) {
            throw new IllegalArgumentException("Invalid baseWidth (" + baseWidth + ").");
        }
        return addColumn(false, baseWidth);
    }

    private int addColumn(boolean autoWidth, int baseWidth) {
        if (rows.size() > 0) {
            throw new IllegalStateException("Add all columns before adding rows");
        }
        stale = true;
        int index = columns.size();
        Column column = new Column(index, autoWidth, baseWidth);
        columns.add(column);
        cells.add(new ArrayList<>());
        return index;
    }

    public int addRow() {
        return addRow(true, 0);
    }

    public int addRow(int baseHeight) {
        if (baseHeight < 0) {
            throw new IllegalArgumentException("Invalid baseHeight (" + baseHeight + ").");
        }
        return addRow(false, baseHeight);
    }

    public int addRow(boolean autoHeight, int baseHeight) {
        stale = true;
        int index = rows.size();
        Row row = new Row(index, autoHeight, baseHeight);
        rows.add(row);
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            cells.get(i).add(new Cell(column, row));
        }
        return index;
    }

    @Override
    public void addLayoutComponent(Component component, Object o) {
        TimeTableLayoutConstraints c = (TimeTableLayoutConstraints) o;
        if (c.getXEnd() > columns.size()) {
            throw new IllegalArgumentException("The xEnd (" + c.getXEnd()
                    + ") is > columnsSize (" +  columns.size() + ").");
        }
        if (c.getYEnd() > rows.size()) {
            throw new IllegalArgumentException("The yEnd (" + c.getYEnd()
                    + ") is > rowsSize (" +  rows.size() + ").");
        }
        stale = true;
        ComponentSpan span = new ComponentSpan(component);
        spanMap.put(component, span);
        span.topLeftCell = cells.get(c.getX()).get(c.getY());
        span.bottomRightCell = cells.get(c.getXEnd() - 1).get(c.getYEnd() - 1);
        Set<Integer> occupiedCollisionIndexes = new HashSet<>();
        for (int i = c.getX(); i < c.getXEnd(); i++) {
            for (int j = c.getY(); j < c.getYEnd(); j++) {
                Cell cell = cells.get(i).get(j);
                cell.column.stale = true;
                cell.row.stale = true;
                cell.spans.add(span);
                span.cells.add(cell);
                occupiedCollisionIndexes.addAll(cell.occupiedCollisionIndexes);
            }
        }
        Integer collisionIndex = 0;
        while (occupiedCollisionIndexes.contains(collisionIndex)) {
            collisionIndex++;
        }
        if (c.isFillCollisions()) {
            if (collisionIndex != 0 || occupiedCollisionIndexes.contains(FILL_COLLISIONS_FLAG)) {
                throw new IllegalArgumentException("There is a collision in the cell range ("
                        + (c.getX() == c.getXEnd() - 1 ? c.getX() : c.getX() + "-" + (c.getXEnd() - 1))
                        + ", " + (c.getY() == c.getYEnd() - 1 ? c.getY() : c.getY() + "-" + (c.getYEnd() - 1))
                        + ").");
            }
            collisionIndex = FILL_COLLISIONS_FLAG;
        }
        span.collisionIndex = collisionIndex;
        for (Cell cell : span.cells) {
            cell.occupiedCollisionIndexes.add(collisionIndex);
        }
    }

    @Override
    public void addLayoutComponent(String name, Component component) {
        // No effect
    }

    @Override
    public void removeLayoutComponent(Component component) {
        stale = true;
        ComponentSpan span = spanMap.remove(component);
        for (Cell cell : span.cells) {
            cell.spans.remove(span);
            cell.column.stale = true;
            cell.row.stale = true;
            cell.occupiedCollisionIndexes.remove(span.collisionIndex);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        update();
        return new Dimension(totalColumnWidth, totalRowHeight);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        update();
        return new Dimension(totalColumnWidth, totalRowHeight);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        update();
        return new Dimension(totalColumnWidth, totalRowHeight);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }


    @Override
    public void invalidateLayout(Container target) {
        // No effect
    }

    @Override
    public void layoutContainer(Container parent) {
        update();
        synchronized (parent.getTreeLock()) {
            for (ComponentSpan span : spanMap.values()) {
                int x1 = span.topLeftCell.column.boundX;
                int collisionIndexStart = (span.collisionIndex == FILL_COLLISIONS_FLAG)
                        ? 0 : span.collisionIndex;
                int y1 = span.topLeftCell.row.boundY + (collisionIndexStart * span.topLeftCell.row.baseHeight);
                int x2 = span.bottomRightCell.column.boundX + span.bottomRightCell.column.baseWidth;
                int collisionIndexEnd = (span.collisionIndex == FILL_COLLISIONS_FLAG)
                        ? span.bottomRightCell.row.collisionCount : span.collisionIndex + 1;
                int y2 = span.bottomRightCell.row.boundY + (collisionIndexEnd * span.bottomRightCell.row.baseHeight);
                span.component.setBounds(x1, y1, x2 - x1, y2 - y1);
            }
        }
    }

    public void update() {
        if (!stale) {
            return;
        }
        refreshColumns();
        refreshRows();
        stale = false;
    }

    private void refreshColumns() {
        for (Column column : columns) {
            if (column.stale) {
                if (column.autoWidth) {
                    column.baseWidth = getMaxCellWidth(column);
                }
                column.stale = false;
            }
        }
        refreshColumnsBoundX();
    }

    private int getMaxCellWidth(Column column) {
        int maxCellWidth = 0;
        for (int i = 0; i < rows.size(); i++) {
            Cell cell = cells.get(column.index).get(i);
            for (ComponentSpan span : cell.spans) {
                int width = span.getPreferredWidthPerCell();
                if (width > maxCellWidth) {
                    maxCellWidth = width;
                }
            }
        }
        return maxCellWidth;
    }

    private void refreshColumnsBoundX() {
        int nextColumnBoundX = 0;
        for (Column column : columns) {
            column.boundX = nextColumnBoundX;
            nextColumnBoundX += column.baseWidth;
        }
        totalColumnWidth = nextColumnBoundX;
    }

    private void refreshRows() {
        for (Row row : rows) {
            if (row.stale) {
                if (row.autoHeight) {
                    row.baseHeight = getMaxCellHeight(row);
                }
                row.collisionCount = getMaxCollisionCount(row);
            }
            row.stale = false;
        }
        freshRowsBoundY();
    }

    private int getMaxCellHeight(Row row) {
        int maxCellHeight = 0;
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = cells.get(i).get(row.index);
            for (ComponentSpan span : cell.spans) {
                int height = span.getPreferredHeightPerCell();
                if (height > maxCellHeight) {
                    maxCellHeight = height;
                }
            }
        }
        return maxCellHeight;
    }

    private int getMaxCollisionCount(Row row) {
        int maxCollisionCount = 1;
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = cells.get(i).get(row.index);
            if (cell.occupiedCollisionIndexes.size() > maxCollisionCount) {
                maxCollisionCount = cell.occupiedCollisionIndexes.size();
            }
        }
        return maxCollisionCount;
    }

    private void freshRowsBoundY() {
        int nextRowBoundY = 0;
        for (Row row : rows) {
            row.boundY = nextRowBoundY;
            nextRowBoundY += row.baseHeight * row.collisionCount;
        }
        totalRowHeight = nextRowBoundY;
    }

    private static class Column implements Serializable {

        private final int index;
        private final boolean autoWidth;

        private boolean stale;
        private int baseWidth;
        private int boundX = -1;

        private Column(int index, boolean autoWidth, int baseWidth) {
            this.index = index;
            this.autoWidth = autoWidth;
            stale = true;
            this.baseWidth = baseWidth;
        }

    }

    private static class Row implements Serializable {

        private final int index;
        private final boolean autoHeight;

        private boolean stale;
        private int baseHeight;
        private int collisionCount = 1;
        private int boundY = -1;

        private Row(int index, boolean autoHeight, int baseHeight) {
            this.index = index;
            this.autoHeight = autoHeight;
            stale = true;
            this.baseHeight = baseHeight;
        }

    }

    private static class Cell implements Serializable {

        private Column column;
        private Row row;

        private Set<ComponentSpan> spans = new HashSet<>();
        private Set<Integer> occupiedCollisionIndexes = new HashSet<>();

        private Cell(Column column, Row row) {
            this.column = column;
            this.row = row;
        }

    }

    private static class ComponentSpan implements Serializable {

        private Component component;

        private Set<Cell> cells = new HashSet<>();
        private Cell topLeftCell;
        private Cell bottomRightCell;
        private Integer collisionIndex;

        private ComponentSpan(Component component) {
            this.component = component;
        }

        public int getPreferredWidthPerCell() {
            int width = component.getPreferredSize().width;
            int horizontalCellSize = bottomRightCell.column.index - topLeftCell.column.index + 1;
            return (width + (horizontalCellSize - 1)) / horizontalCellSize; // Ceil rounding
        }

        public int getPreferredHeightPerCell() {
            int height = component.getPreferredSize().height;
            int verticalCellSize = bottomRightCell.row.index - topLeftCell.row.index + 1;
            return (height + (verticalCellSize - 1)) / verticalCellSize; // Ceil rounding
        }

    }

}
