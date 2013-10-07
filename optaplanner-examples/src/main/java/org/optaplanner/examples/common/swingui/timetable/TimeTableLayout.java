/*
 * Copyright 2013 JBoss Inc
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

    private int totalColumnWidth;
    private int totalRowHeight;

    public TimeTableLayout() {
        reset();
    }

    public void reset() {
        columns = new ArrayList<Column>();
        rows = new ArrayList<Row>();
        cells = new ArrayList<List<Cell>>();
        spanMap = new HashMap<Component, ComponentSpan>();
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
        int index = columns.size();
        Column column = new Column(index, autoWidth, baseWidth);
        columns.add(column);
        if (!autoWidth) {
            totalColumnWidth += baseWidth;
        }
        cells.add(new ArrayList<Cell>());
        return index;
    }

    public int addRow(int baseHeight) {
        if (baseHeight < 0) {
            throw new IllegalArgumentException("Invalid baseHeight (" + baseHeight + ").");
        }
        int index = rows.size();
        Row row = new Row(index, baseHeight);
        rows.add(row);
        totalRowHeight += baseHeight;
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            cells.get(i).add(new Cell(column, row));
        }
        return index;
    }

    public void addLayoutComponent(Component component, Object o) {
        TimeTableLayoutConstraints c = (TimeTableLayoutConstraints) o;
        if (c.getXEnd() > columns.size()) {
            throw new IllegalArgumentException("The xEnd (" + c.getXEnd()
                    + ") is > columnsSize (" +  columns.size() +").");
        }
        if (c.getYEnd() > rows.size()) {
            throw new IllegalArgumentException("The yEnd (" + c.getYEnd()
                    + ") is > rowsSize (" +  rows.size() +").");
        }
        ComponentSpan span = new ComponentSpan(component);
        spanMap.put(component, span);
        span.topLeftCell = cells.get(c.getX()).get(c.getY());
        span.bottomRightCell = cells.get(c.getXEnd() - 1).get(c.getYEnd() - 1);
        Set<Integer> occupiedCollisionIndexes = new HashSet<Integer>();
        for (int i = c.getX(); i < c.getXEnd(); i++) {
            for (int j = c.getY(); j < c.getYEnd(); j++) {
                Cell cell = cells.get(i).get(j);
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
                throw new IllegalArgumentException("There is a collision with fillCollisions ("
                        + c.isFillCollisions() + ").");
            }
            collisionIndex = FILL_COLLISIONS_FLAG;
        }
        span.collisionIndex = collisionIndex;
        int componentWidth =  span.getPreferredWidthPerCell();
        for (Cell cell : span.cells) {
            cell.occupiedCollisionIndexes.add(collisionIndex);
            Column column = cell.column;
            if (column.autoWidth) {
                if (column.baseWidth < componentWidth) {
                    totalColumnWidth += componentWidth - column.baseWidth;
                    column.baseWidth = componentWidth;
                }
            }
            int collisionCount = cell.occupiedCollisionIndexes.size();
            Row row = cell.row;
            if (row.collisionCount < collisionCount) {
                row.collisionCount = collisionCount;
                totalRowHeight += row.baseHeight;
            }
        }
    }

    public void addLayoutComponent(String name, Component component) {
        // No effect
    }

    public void removeLayoutComponent(Component component) {
        ComponentSpan span = spanMap.remove(component);
        Set<Column> refreshColumns = new HashSet<Column>(columns.size());
        Set<Row> refreshRows = new HashSet<Row>(rows.size());
        for (Cell cell : span.cells) {
            cell.spans.remove(span);
            int collisionCount = cell.occupiedCollisionIndexes.size();
            Column column = cell.column;
            if (column.autoWidth) {
                refreshColumns.add(column);
            }
            Row row = cell.row;
            if (row.collisionCount == collisionCount) {
                refreshRows.add(row);
            }
            cell.occupiedCollisionIndexes.remove(span.collisionIndex);
        }
        refreshColumns(refreshColumns);
        refreshRows(refreshRows);
    }

    private void refreshColumns(Set<Column> refreshColumns) {
        for (Column column : refreshColumns) {
            if (column.autoWidth) {
                int maxBaseWidth = 0;
                for (int i = 0; i < rows.size(); i++) {
                    Cell cell = cells.get(column.index).get(i);
                    for (ComponentSpan span : cell.spans) {
                        int componentWidth = span.getPreferredWidthPerCell();
                        if (componentWidth > maxBaseWidth) {
                            maxBaseWidth = componentWidth;
                        }
                    }
                }
                column.baseWidth = maxBaseWidth;
            }
        }
    }

    private void refreshRows(Set<Row> refreshRows) {
        for (Row row : refreshRows) {
            int maxCollisionCount = 1;
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = cells.get(i).get(row.index);
                if (cell.occupiedCollisionIndexes.size() > maxCollisionCount) {
                    maxCollisionCount = cell.occupiedCollisionIndexes.size();
                }
            }
            if (row.collisionCount > maxCollisionCount) {
                row.collisionCount = maxCollisionCount;
                totalRowHeight -= row.baseHeight;
            }
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(totalColumnWidth, totalRowHeight);
    }

    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(totalColumnWidth, totalRowHeight);
    }

    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(totalColumnWidth, totalRowHeight);
    }

    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }


    public void invalidateLayout(Container target) {
        // No effect
    }

    public void layoutContainer(Container parent) {
        freshColumnsBoundX();
        freshRowsBoundY();
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

    private void freshColumnsBoundX() {
        int nextColumnBoundX = 0;
        for (Column column : columns) {
            column.boundX = nextColumnBoundX;
            nextColumnBoundX += column.baseWidth;
        }
        if (nextColumnBoundX != totalColumnWidth) {
            throw new IllegalArgumentException("The nextColumnBoundX (" + nextColumnBoundX
                    + ") is not totalColumnWidth (" + totalColumnWidth + ").");
        }
    }

    private void freshRowsBoundY() {
        int nextRowBoundY = 0;
        for (Row row : rows) {
            row.boundY = nextRowBoundY;
            nextRowBoundY += row.baseHeight * row.collisionCount;
        }
        if (nextRowBoundY != totalRowHeight) {
            throw new IllegalArgumentException("The nextRowBoundY (" + nextRowBoundY
                    + ") is not totalRowHeight (" + totalRowHeight + ").");
        }
    }

    private static class Column {

        private final int index;
        private final boolean autoWidth;
        private int baseWidth;

        private int boundX = -1;

        private Column(int index, boolean autoWidth, int baseWidth) {
            this.index = index;
            this.autoWidth = autoWidth;
            this.baseWidth = baseWidth;
        }
    }

    private static class Row {

        private final int index;
        private int baseHeight;

        private int collisionCount = 1;

        private int boundY = -1;

        private Row(int index, int baseHeight) {
            this.index = index;
            this.baseHeight = baseHeight;
        }
    }

    private static class Cell {

        private Column column;
        private Row row;

        private Set<ComponentSpan> spans = new HashSet<ComponentSpan>();
        private Set<Integer> occupiedCollisionIndexes = new HashSet<Integer>();

        private Cell(Column column, Row row) {
            this.column = column;
            this.row = row;
        }
    }

    private static class ComponentSpan {

        private Component component;

        private Set<Cell> cells = new HashSet<Cell>();
        private Cell topLeftCell;
        private Cell bottomRightCell;
        private Integer collisionIndex;

        private ComponentSpan(Component component) {
            this.component = component;
        }

        public int getPreferredWidthPerCell() {
            int width = component.getPreferredSize().width;
            return (width + (cells.size() - 1)) / cells.size(); // Ceil rounding
        }

    }

}
