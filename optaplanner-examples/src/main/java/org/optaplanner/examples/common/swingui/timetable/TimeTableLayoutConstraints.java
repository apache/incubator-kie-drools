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

public class TimeTableLayoutConstraints {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean fillCollisions;

    public TimeTableLayoutConstraints(int x, int y) {
        this(x, y, 1, 1, false);
    }

    public TimeTableLayoutConstraints(int x, int y, int width, int height) {
        this(x, y, width, height, false);
    }

    public TimeTableLayoutConstraints(int x, int y, boolean fillCollisions) {
        this(x, y, 1, 1, fillCollisions);
    }

    public TimeTableLayoutConstraints(int x, int y, int width, int height, boolean fillCollisions) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillCollisions = fillCollisions;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFillCollisions() {
        return fillCollisions;
    }

    public int getXEnd() {
        return x + width;
    }

    public int getYEnd() {
        return y + height;
    }

}
