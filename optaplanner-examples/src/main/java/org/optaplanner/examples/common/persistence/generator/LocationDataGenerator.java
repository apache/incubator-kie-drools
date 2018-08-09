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

    public static final LocationData[] EUROPE_BUSIEST_AIRPORTS = {
            new LocationData("BRU", 50.901389, 4.484444),
            new LocationData("LHR", 51.4775, -0.461389),
            new LocationData("CDG", 49.009722, 2.547778),
            new LocationData("AMS", 52.308056, 4.764167),
            new LocationData("FRA", 50.033333, 8.570556),
            new LocationData("IST", 40.976111, 28.814167),
            new LocationData("MAD", 40.472222, -3.560833),
            new LocationData("BCN", 41.296944, 2.078333),
            new LocationData("LGW", 51.148056, -0.190278),
            new LocationData("MUC", 48.353889, 11.786111),
            new LocationData("FCO", 41.800278, 12.238889),
            new LocationData("SVO", 55.972778, 37.414722),
            new LocationData("ORY", 48.723333, 2.379444),
            new LocationData("DME", 55.408611, 37.906111),
            new LocationData("DUB", 53.421389, -6.27),
            new LocationData("SRH", 47.464722, 8.549167),
            new LocationData("CPH", 55.618056, 12.656111),
            new LocationData("PMI", 39.551667, 2.738889),
            new LocationData("MAN", 53.353889, -2.275),
            new LocationData("OSL", 60.202778, 11.083889),
            new LocationData("LIS", 38.774167, -9.134167),
            new LocationData("ARN", 59.651944, 17.918611),
            new LocationData("STN", 51.885, 0.235),
            new LocationData("DUS", 51.289444, 6.766667),
            new LocationData("VIE", 48.110833, 16.570833),
            new LocationData("MXP", 45.63, 8.723056),
            new LocationData("ATH", 37.936389, 23.947222),
            new LocationData("TXL", 52.559722, 13.287778),
            new LocationData("HEL", 60.317222, 24.963333),
            new LocationData("AGP", 36.675, -4.499167),
            new LocationData("VKO", 55.596111, 37.2675),
            new LocationData("HAM", 53.630278, 9.991111),
            new LocationData("GVA", 46.238333, 6.109444),
            new LocationData("LED", 59.800278, 30.2625),
            new LocationData("LTN", 51.874722, -0.368333),
            new LocationData("WAW", 52.165833, 20.967222),
            new LocationData("PRG", 50.100833, 14.26),
            new LocationData("ALC", 38.282222, -0.558056),
            new LocationData("EDI", 55.95, -3.3725),
            new LocationData("NCE", 43.665278, 7.215),
            new LocationData("BUD", 47.439444, 19.261944),
            new LocationData("BHX", 52.453889, -1.748056),
            new LocationData("SXF", 52.378611, 13.520556),
            new LocationData("OTP", 44.571111, 26.085),
            new LocationData("CGN", 50.865833, 7.142778),
            new LocationData("BGY", 45.668889, 9.700278),
            new LocationData("STR", 48.69, 9.221944),
            new LocationData("OPO", 41.235556, -8.678056),
            new LocationData("KBP", 50.344722, 30.893333),
            new LocationData("VCE", 45.505278, 12.351944),
    };

    public static final LocationData[] EUROPE_CAPITALS = {
            new LocationData("Brussels", 50.797140, 4.361572),
            new LocationData("Dublin", 53.309435, -6.284180),
            new LocationData("London", 51.465872, -0.131836),
            new LocationData("Paris", 48.797698, 2.351074),
            new LocationData("Reykjavik", 64.133219, -21.886139),
            new LocationData("Luxembourg", 49.537568, 6.130371),
            new LocationData("Amsterdam", 52.320120, 4.888916),
            new LocationData("Berlin", 52.480996, 13.414307),
            new LocationData("Copenhagen", 55.620139, 12.579346),
            new LocationData("Oslo", 59.876442, 10.766602),
            new LocationData("Stockholm", 59.292446, 18.061523),
            new LocationData("Helsinki", 60.134576, 24.949951),
            new LocationData("Tallinn", 59.363808, 24.763184),
            new LocationData("Riga", 56.845768, 24.082031),
            new LocationData("Vilnius", 54.581401, 25.268555),
            new LocationData("Minsk", 53.810166, 27.553711),
            new LocationData("Warsaw", 52.129891, 21.005859),
            new LocationData("Moscow", 55.661888, 37.617188),
            new LocationData("Kiev", 50.355742, 30.541992),
            new LocationData("Chisinau", 46.916253, 28.828125),
            new LocationData("Bucharest", 44.319656, 26.059570),
            new LocationData("Sofia", 42.581130, 23.312988),
            new LocationData("Ankara", 39.943436, 32.857132),
            new LocationData("Athens", 37.852881, 23.730469),
            new LocationData("Nicosia", 35.099537, 33.365479),
            new LocationData("Tirana", 41.283861, 19.808350),
            new LocationData("Skopje", 41.949141, 21.456299),
            new LocationData("Podgorica", 42.380730, 19.281006),
            new LocationData("Belgrade", 44.752455, 20.456543),
            new LocationData("Sarajevo", 43.784843, 18.347168),
            new LocationData("Zagreb", 45.757815, 15.974121),
            new LocationData("Ljubljana", 45.994926, 14.490967),
            new LocationData("Rome", 41.842830, 12.491455),
            new LocationData("Madrid", 40.369427, -3.691406),
            new LocationData("Lisbon", 38.648910, -9.140625),
            new LocationData("Bern", 46.895737, 7.437744),
            new LocationData("Vienna", 48.142143, 16.380615),
            new LocationData("Prague", 50.066778, 14.419556),
            new LocationData("Bratislava", 48.098138, 17.105713),
            new LocationData("Budapest", 47.440969, 19.039307)
    };

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
