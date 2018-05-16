/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.persistence.generator;

public class LocationDataGenerator {

    public static final LocationData[] US_MAINLAND_STATE_CAPITALS = {
            new LocationData("Montgomery, Alabama", 32.377716, -86.300568),
            // new LocationData("Juneau, Alaska", 58.301598, -134.420212),
            new LocationData("Phoenix, Arizona", 33.448143, -112.096962),
            new LocationData("Little Rock, Arkansas", 34.746613, -92.288986),
            new LocationData("Sacramento, California", 38.576668, -121.493629),
            new LocationData("Denver, Colorado", 39.739227, -104.984856),
            new LocationData("Hartford, Connecticut", 41.764046, -72.682198),
            new LocationData("Dover, Delaware", 39.157307, -75.519722),
            new LocationData("Tallahassee, Florida", 30.438118, -84.281296),
            new LocationData("Atlanta, Georgia", 33.749027, -84.388229),
            // new LocationData("Honolulu, Hawaii", 21.307442, -157.857376),
            new LocationData("Boise, Idaho", 43.617775, -116.199722),
            new LocationData("Springfield, Illinois", 39.798363, -89.654961),
            new LocationData("Indianapolis, Indiana", 39.768623, -86.162643),
            new LocationData("Des Moines, Iowa", 41.591087, -93.603729),
            new LocationData("Topeka, Kansas", 39.048191, -95.677956),
            new LocationData("Frankfort, Kentucky", 38.186722, -84.875374),
            new LocationData("Baton Rouge, Louisiana", 30.457069, -91.187393),
            new LocationData("Augusta, Maine", 44.307167, -69.781693),
            new LocationData("Annapolis, Maryland", 38.978764, -76.490936),
            new LocationData("Boston, Massachusetts", 42.358162, -71.063698),
            new LocationData("Lansing, Michigan", 42.733635, -84.555328),
            new LocationData("St. Paul, Minnesota", 44.955097, -93.102211),
            new LocationData("Jackson, Mississippi", 32.303848, -90.182106),
            new LocationData("Jefferson City, Missouri", 38.579201, -92.172935),
            new LocationData("Helena, Montana", 46.585709, -112.018417),
            new LocationData("Lincoln, Nebraska", 40.808075, -96.699654),
            new LocationData("Carson City, Nevada", 39.163914, -119.766121),
            new LocationData("Concord, New Hampshire", 43.206898, -71.537994),
            new LocationData("Trenton, New Jersey", 40.220596, -74.769913),
            new LocationData("Santa Fe, New Mexico", 35.68224, -105.939728),
            new LocationData("Raleigh, North Carolina", 35.78043, -78.639099),
            new LocationData("Bismarck, North Dakota", 46.82085, -100.783318),
            new LocationData("Albany, New York", 42.652843, -73.757874),
            new LocationData("Columbus, Ohio", 39.961346, -82.999069),
            new LocationData("Oklahoma City, Oklahoma", 35.492207, -97.503342),
            new LocationData("Salem, Oregon", 44.938461, -123.030403),
            new LocationData("Harrisburg, Pennsylvania", 40.264378, -76.883598),
            new LocationData("Providence, Rhode Island", 41.830914, -71.414963),
            new LocationData("Columbia, South Carolina", 34.000343, -81.033211),
            new LocationData("Pierre, South Dakota", 44.367031, -100.346405),
            new LocationData("Nashville, Tennessee", 36.16581, -86.784241),
            new LocationData("Austin, Texas", 30.27467, -97.740349),
            new LocationData("Salt Lake City, Utah", 40.777477, -111.888237),
            new LocationData("Montpelier, Vermont", 44.262436, -72.580536),
            new LocationData("Richmond, Virginia", 37.538857, -77.43364),
            new LocationData("Olympia, Washington", 47.035805, -122.905014),
            new LocationData("Charleston, West Virginia", 38.336246, -81.612328),
            new LocationData("Madison, Wisconsin", 43.074684, -89.384445),
            new LocationData("Cheyenne, Wyoming", 41.140259, -104.820236)
    };

    public static class LocationData {
        private String name;
        protected double latitude;
        protected double longitude;

        public LocationData(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    private LocationDataGenerator() {
    }
}
