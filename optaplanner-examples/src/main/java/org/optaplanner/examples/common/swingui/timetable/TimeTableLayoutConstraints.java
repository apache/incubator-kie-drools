/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.common.swingui.timetable;

final class TimeTableLayoutConstraints {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean fillCollisions;

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
